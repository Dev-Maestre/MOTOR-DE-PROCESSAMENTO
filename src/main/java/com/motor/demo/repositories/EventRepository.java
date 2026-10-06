package com.motor.demo.repositories;

import com.motor.demo.entities.Event;
import com.motor.demo.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


// CRIA OS METODOS QUE VAO SER UTILIZADOS NO SERVICE E WORKER
@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    boolean existsByExternalId(String externalId);
    Optional<Event> findByExternalId (String externalId);
    long countByStatus(EventStatus status);

    @Query(value = """
            SELECT * FROM public.event
                WHERE status = 'PENDING' OR (status = 'RETRY_PENDING' AND (next_attempt_at IS NULL OR next_attempt_at <= CURRENT_TIMESTAMP))
            ORDER BY created_at ASC
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true) // SKIP LOCKED NA QUERY PARA PULAR LINHAS QUE JA FORAM LOCKADAS POR OUTRO WORKER
    List<Event> findPendingEvents(@Param("limit") int limit);

}
