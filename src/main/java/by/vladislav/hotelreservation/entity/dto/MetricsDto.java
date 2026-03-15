package by.vladislav.hotelreservation.entity.dto;

public record MetricsDto(
    long totalProcessed,
    long totalProcessedUnsafe,
    long activeTasksCount,
    long activeTasksCountUnsafe,
    long timestamp) {
}
