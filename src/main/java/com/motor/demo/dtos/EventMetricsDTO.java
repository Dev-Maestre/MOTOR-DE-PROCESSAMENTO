package com.motor.demo.dtos;

public record EventMetricsDTO(
         long countPending,
         long countProcessing,
         long countProcessed,
         long countRetry_Pending,
         long countDead_Letter
) {}
