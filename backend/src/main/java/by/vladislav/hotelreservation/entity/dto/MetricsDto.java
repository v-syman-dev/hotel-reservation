package by.vladislav.hotelreservation.entity.dto;

import java.time.LocalDateTime;

public record MetricsDto(
    long totalProcessed,
    long totalProcessedUnsafe,
    long activeTasksCount,
    long activeTasksCountUnsafe,
    LocalDateTime timestamp) {
}
