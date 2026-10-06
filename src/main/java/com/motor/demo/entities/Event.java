package com.motor.demo.entities;

import com.motor.demo.enums.EventStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity (name = "event")
@Table (name = "event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Event {
    @Version
    private Long version;/* A cada UPDATE, o Hibernate incrementa esse contador no banco (version = version + 1).
    Se outro processo tentar atualizar um registro que já teve a versão alterada, o Spring lança uma ObjectOptimisticLockingFailureException*/


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // Id interno gravado pela aplicação
    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId; // Ex: event-123 vindo do cliente (Garante Idempotência)
    @Column(nullable = false)
    private String type; // Ex: "PAYMENT_RECEIVED"
    @Column(nullable = false)
    private String source;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    EventStatus status;
    @Column(nullable = false)
    private Integer attempts;
    @Column(name = "next_attempt_at")
    private OffsetDateTime nextAttemptAt;
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;
    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
