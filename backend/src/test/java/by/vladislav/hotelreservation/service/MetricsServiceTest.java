package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import by.vladislav.hotelreservation.entity.dto.MetricsDto;

class MetricsServiceTest {

  @Test
  void metricsShouldTrackProcessedAndActiveTasks() {
    MetricsService metricsService = new MetricsService();

    metricsService.incrementActiveTasks();
    metricsService.addProcessed(5);
    metricsService.decrementActiveTasks();

    MetricsDto metrics = metricsService.getMetrics();

    assertEquals(5, metrics.totalProcessed());
    assertEquals(5, metrics.totalProcessedUnsafe());
    assertEquals(0, metrics.activeTasksCount());
    assertEquals(0, metrics.activeTasksCountUnsafe());
    assertNotNull(metrics.timestamp());
  }
}
