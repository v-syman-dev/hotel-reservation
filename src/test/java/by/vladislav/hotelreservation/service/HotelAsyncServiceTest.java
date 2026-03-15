package by.vladislav.hotelreservation.service;

import static org.mockito.Mockito.verify;

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
public class HotelAsyncServiceTest {
  @Mock
  RoomService roomService;
  @Mock
  TaskStatusService taskStatusService;
  @InjectMocks
  RoomAsyncService hotelAsyncService;

  @Test
  void processBulkSaveSuccessUpdatesStatusToCompleted() throws Exception {
    UUID taskId = UUID.randomUUID();
    RoomDto roomDto = new RoomDto(null, 0, null, null);
    List<RoomDto> dtos = List.of(roomDto);

    CompletableFuture<Void> future = hotelAsyncService.processBulkSave(taskId, dtos, 0L);

    future.get();

    verify(taskStatusService).updateStatus(taskId, "PROCESSING");
    verify(roomService).saveBulk(0L, dtos);
    verify(taskStatusService).updateStatus(taskId, "COMPLETED");
  }

}
