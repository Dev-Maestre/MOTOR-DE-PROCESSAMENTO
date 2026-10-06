package com.motor.demo.controllers;

import com.motor.demo.dtos.EventMetricsDTO;
import com.motor.demo.entities.Event;
import com.motor.demo.enums.EventStatus;
import com.motor.demo.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/events")
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

    public ResponseEntity<Map<String,String>> redriveFailedEvents() {

    }

}
