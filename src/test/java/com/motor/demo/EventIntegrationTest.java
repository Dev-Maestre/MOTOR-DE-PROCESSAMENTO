package com.motor.demo;

import com.motor.demo.entities.Event;
import com.motor.demo.enums.EventStatus;
import com.motor.demo.repositories.EventRepository;
import com.motor.demo.workers.EventWorker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class EventIntegrationTest {

	@Container
	@ServiceConnection // test-containers dependency notation
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private EventWorker eventWorker;

	@Test
	@DisplayName("Deve buscar eventos pendentes")
	void shouldProcessPendingEventsSuccessfully() {
		// Cria um evento pendente no banco real
		Event event = Event.builder()
				.externalId("evt-test-001")
				.type("PAYMENT_RECEIVED")
				.source("integration-test")
				.status(EventStatus.PENDING)
				.attempts(0)
				.payload("{\"amount\": 100.00}")
				.occurredAt(OffsetDateTime.now())
				.updatedAt(OffsetDateTime.now())
				.build();

		eventRepository.save(event);

		// Busca o evento criado com a query do metodo [SKIP LOCK]
		List<Event> pendingEvents = eventRepository.findPendingEvents(10);
		assertThat(pendingEvents).hasSize(1);

		eventWorker.execute(); // Executa o ciclo do worker

		// Valida que o evento mudou para PROCESSED no banco
		Event processedEvent = eventRepository.findByExternalId("evt-test-001").orElseThrow();

		assertThat(processedEvent.getStatus()).isEqualTo(EventStatus.PROCESSED);
		assertThat(processedEvent.getVersion()).isNotNull(); // Valida que o Optimistic Locking funcionou
	}

}
