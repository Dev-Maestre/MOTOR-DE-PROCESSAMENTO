package com.motor.demo.workers;

import com.motor.demo.entities.Event;
import com.motor.demo.repositories.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class EventSweeper {

    private static final Logger log = LoggerFactory.getLogger(EventSweeper.class);
    private final EventRepository eventRepository;


    public EventSweeper(EventRepository eventRepository) { this.eventRepository = eventRepository; }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void runSweeper() {

        int recoveredEvents = eventRepository.recoverStuckEvents();

        if (recoveredEvents > 0) {
            log.warn("SWEEPER: {} evento(s) fantasma(s) em PROCESSING foram recuperados para RETRY_PENDING!", recoveredEvents);
        }

    }

}
