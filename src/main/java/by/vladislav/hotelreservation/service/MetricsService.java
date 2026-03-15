package by.vladislav.hotelreservation.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import by.vladislav.hotelreservation.entity.dto.MetricsDto;

@Service
public class MetricsService {
  private AtomicLong totalProcessed = new AtomicLong(0);
  private long totalProcessedUnsafe = 0;
  private AtomicLong activeTasks = new AtomicLong(0);
  private long activeTasksUnsafe = 0;

  public void incrementActiveTasks() {
    activeTasks.incrementAndGet();
    activeTasksUnsafe++;
  }

  public void decrementActiveTasks() {
    activeTasks.decrementAndGet();
    activeTasksUnsafe--;
  }

  public void addProcessed(int value) {
    totalProcessed.addAndGet(value);
    totalProcessedUnsafe += value;
  }

  public MetricsDto getMetrics() {
    return new MetricsDto(
        totalProcessed.get(),
        totalProcessedUnsafe,
        activeTasks.get(),
        activeTasksUnsafe,
        System.currentTimeMillis());
  }
}
