package com.motor.demo.services;

import com.motor.demo.dtos.EventRequestDTO;
import com.motor.demo.entities.Event;
import com.motor.demo.enums.EventStatus;
import com.motor.demo.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public void processIngestion (EventRequestDTO dto) {

        if (eventRepository.existsByExternalId(dto.eventId())) {
            log.info("externalId={} ja existe || EVENTO IGNORADO", dto.eventId());
            return;
        }

        Event event = Event.builder()
                .externalId(dto.eventId())
                .type(dto.type())
                .source(dto.source())
                .status(EventStatus.PENDING)
                .attempts(0)
                .payload(dto.payload().toString())
                .occurredAt(dto.occurredAt())
                .build();

        try {
            eventRepository.save(event);
            log.info("Evento registrado com sucesso | id={} ; externalId={}", event.getId(), event.getExternalId());
        } catch (DataIntegrityViolationException e) {
            log.warn("Colisão de concorrência detectada | externalId={} | Evento considerado já processado.", dto.eventId());
        }

    }
}
