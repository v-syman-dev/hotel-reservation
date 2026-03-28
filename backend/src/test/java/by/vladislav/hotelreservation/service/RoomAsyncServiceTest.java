package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import by.vladislav.hotelreservation.entity.dto.RoomDto;

@ExtendWith(MockitoExtension.class)
class RoomAsyncServiceTest {
  @Mock
  private TaskStatusService taskStatusService;
  @Mock
  private RoomService roomService;
  @Mock
  private MetricsService taskMetricsService;

  @InjectMocks
  private RoomAsyncService roomAsyncService;

  @Test
  void processBulkSaveShouldCompleteSuccessfully() {
    UUID taskId = UUID.randomUUID();
    List<RoomDto> rooms = List.of(new RoomDto(null, 1, "Type", BigDecimal.TEN));

    CompletableFuture<Void> result = roomAsyncService.processBulkSave(taskId, rooms, 10L);

    assertNotNull(result);
    verify(taskMetricsService).incrementActiveTasks();
    verify(taskStatusService).updateStatus(taskId, "PROCESSING");
    verify(roomService).saveBulk(10L, rooms);
    verify(taskMetricsService).addProcessed(1);
    verify(taskStatusService).updateStatus(taskId, "COMPLETED");
    verify(taskMetricsService).decrementActiveTasks();
  }

  @Test
  void processBulkSaveShouldMarkTaskFailedWhenExceptionOccurs() {
    UUID taskId = UUID.randomUUID();
    List<RoomDto> rooms = List.of(new RoomDto(null, 1, "Type", BigDecimal.TEN));

    doThrow(new IllegalArgumentException("boom")).when(roomService).saveBulk(10L, rooms);

    CompletableFuture<Void> result = roomAsyncService.processBulkSave(taskId, rooms, 10L);

    assertNotNull(result);
    verify(taskStatusService).updateStatus(taskId, "FAILED boom");
    verify(taskMetricsService).decrementActiveTasks();
  }
}
