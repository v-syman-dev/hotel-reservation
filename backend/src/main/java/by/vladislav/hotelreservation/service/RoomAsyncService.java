package by.vladislav.hotelreservation.service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import by.vladislav.hotelreservation.entity.dto.RoomDto;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RoomAsyncService {

  private final TaskStatusService taskStatusService;
  private final RoomService roomService;
  private final MetricsService taskMetricsService;

  @Async("basicExecutor")
  public CompletableFuture<Void> processBulkSave(UUID taskId, List<RoomDto> dtos, Long hotelId) {
    taskMetricsService.incrementActiveTasks();
    try {
      taskStatusService.updateStatus(taskId, "PROCESSING");

      roomService.saveBulk(hotelId, dtos);

      taskMetricsService.addProcessed(dtos.size());

      taskStatusService.updateStatus(taskId, "COMPLETED");
    } catch (Exception exc) {
      taskStatusService.updateStatus(taskId, "FAILED " + exc.getMessage());
    } finally {
      taskMetricsService.decrementActiveTasks();
    }

    return CompletableFuture.completedFuture(null);
  }

  @Async("basicExecutor")
  public CompletableFuture<Void> processBulkSaveWithTimer(UUID taskId, List<RoomDto> dtos, Long hotelId) {
    taskMetricsService.incrementActiveTasks();
    try {
      taskStatusService.updateStatus(taskId, "PROCESSING");

      roomService.saveBulk(hotelId, dtos);

      taskMetricsService.addProcessed(dtos.size());
      Thread.sleep(Duration.ofSeconds(5));
      taskStatusService.updateStatus(taskId, "COMPLETED");
    } catch (Exception exc) {
      taskStatusService.updateStatus(taskId, "FAILED " + exc.getMessage());
    } finally {
      taskMetricsService.decrementActiveTasks();
    }

    return CompletableFuture.completedFuture(null);
  }
}
