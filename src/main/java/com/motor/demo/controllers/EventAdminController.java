package com.motor.demo.controllers;

import com.motor.demo.dtos.EventMetricsDTO;
import com.motor.demo.entities.Event;
import com.motor.demo.enums.EventStatus;
import com.motor.demo.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

import static java.time.OffsetTime.now;

@RestController
@RequestMapping("/api/admin/events") // TODO -> AUTH ADMIN
public class EventAdminController {

    EventRepository eventRepository;

    public EventAdminController(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    // GET /api/admin/events/metrics -> Exibe a saúde e o acúmulo (lag) do motor
    @GetMapping("/metrics")
    public ResponseEntity<EventMetricsDTO> getMetrics(){
        long pending = eventRepository.countByStatus(EventStatus.PENDING);
        long processing = eventRepository.countByStatus(EventStatus.PROCESSING);
        long processed = eventRepository.countByStatus(EventStatus.PROCESSED);
        long retry = eventRepository.countByStatus(EventStatus.RETRY_PENDING);
        long dead_letter = eventRepository.countByStatus(EventStatus.DEAD_LETTER);

        return ResponseEntity.ok(new EventMetricsDTO(pending, processing, processed, retry, dead_letter));
    }

    @PostMapping("/redrive")
    @Transactional
    public ResponseEntity<Map<String,String>> redriveFailedEvents() {
        var failedEvents = eventRepository.findAll().stream()
                .filter(event -> event.getStatus() == EventStatus.DEAD_LETTER)
                .toList();

        for (var event : failedEvents){
            event.setStatus(EventStatus.PENDING);
            event.setAttempts(0);
            event.setNextAttemptAt(null);
            event.setUpdatedAt(OffsetDateTime.now());
        }

        eventRepository.saveAll(failedEvents);

        return ResponseEntity.ok(Map.of("message","Redrive executado com sucesso",
                                        "reprocessedCount", String.valueOf(failedEvents.size())));
    }

}
