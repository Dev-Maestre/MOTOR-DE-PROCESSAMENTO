package com.motor.demo.enums;

public enum EventStatus {
    PENDING,
    PROCESSING,
    PROCESSED,
    RETRY_PENDING,
    DEAD_LETTER // -> RECUSA AUTOMATICAMENTE
}
