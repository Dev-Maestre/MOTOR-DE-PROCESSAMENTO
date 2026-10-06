package com.motor.demo.controllers;


import com.motor.demo.dtos.EventRequestDTO;
import com.motor.demo.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Void> receiveEvent(@Valid @RequestBody EventRequestDTO dto) {
        eventService.processIngestion(dto);

        // Retornado 202 ACCEPTED para indicar que o evento foi aceito para processamento assíncrono futuro.
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}
