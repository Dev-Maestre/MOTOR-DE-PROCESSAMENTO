package com.motor.demo.workers;

import com.motor.demo.entities.Event;
import com.motor.demo.enums.EventStatus;
import com.motor.demo.repositories.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class EventWorker {

    private static final Logger log = LoggerFactory.getLogger(EventWorker.class);
    private static final int MAX_ATTEMPTS = 3;
    private static final int FIND_CHUNKS = 3;

    private final EventRepository eventRepository;

    public EventWorker(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(fixedDelay = 5000) // <- argumento para agendamento
    @Transactional
    public void execute() {

        List<Event> events = eventRepository.findPendingEvents(FIND_CHUNKS); // LIMITA BUSCA DE 3 EM 3 chunks p/ EVENTO PARA CADA WORKER

        // Sem evento pendente
        if (events.isEmpty()) { log.info("Worker encontrou {} evento(s) pendentes", events.size()); return; }

        log.info("Worker encontrou {} evento(s) pendentes", events.size());

        for (Event event: events) {
            try{
                processEvent(event);
            } catch (Exception e) {
                handleFailure(event, e);
            }
        }

        eventRepository.saveAll(events);
    }

    private void processEvent(Event event){
        log.info("Processando evento: externalId={} | type={}", event.getExternalId(), event.getType());

        if ("FAIL_TEST".equals(event.getType())){
            throw new RuntimeException("Simulação de erro na integração externa!");
        }

        event.setStatus(EventStatus.PROCESSED);
        event.setUpdatedAt(OffsetDateTime.now());
        log.info("Evento externalId={} processado com SUCESSO!", event.getExternalId());
    }

    private void handleFailure(Event event, Exception e) {
        int nextAttempt = event.getAttempts() + 1;
        event.setAttempts(nextAttempt);
        event.setUpdatedAt(OffsetDateTime.now());

        if (nextAttempt >= MAX_ATTEMPTS) { // Se ja tiver passado de 3 tentativas no worker
            event.setStatus(EventStatus.DEAD_LETTER); // seta dead letter  p liberar fila
        } else {
            event.setStatus(EventStatus.RETRY_PENDING);

            long delayInSeconds = 15L * nextAttempt;
            event.setNextAttemptAt(OffsetDateTime.now().plusSeconds(delayInSeconds));

            log.warn("Falha no evento externalId={}. Agendada tentativa {}/{} para daqui a {} segundos.",
                    event.getExternalId(), nextAttempt, MAX_ATTEMPTS, delayInSeconds);
        }

    }

}
