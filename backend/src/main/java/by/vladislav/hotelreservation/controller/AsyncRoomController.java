package by.vladislav.hotelreservation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import by.vladislav.hotelreservation.entity.dto.MetricsDto;
import by.vladislav.hotelreservation.entity.dto.RoomDto;
import by.vladislav.hotelreservation.service.RoomAsyncService;
import by.vladislav.hotelreservation.service.MetricsService;
import by.vladislav.hotelreservation.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@RestController
@RequestMapping("/async")
public class AsyncRoomController {
  private final RoomAsyncService hotelAsyncService;
  private final TaskStatusService taskStatusService;
  private final MetricsService hotelMetricsService;

  @PostMapping("hotels/{hotelId}/rooms/bulk")
  public ResponseEntity<UUID> bulkPostHotels(@RequestParam boolean timer, @PathVariable Long hotelId,
      @Valid @RequestBody List<RoomDto> dtos) {
    UUID uuid = taskStatusService.createTask();

    if (timer) {
      hotelAsyncService.processBulkSaveWithTimer(uuid, dtos, hotelId);
    }

    hotelAsyncService.processBulkSave(uuid, dtos, hotelId);

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(uuid);
  }

  @GetMapping("/status/{taskId}")
  public ResponseEntity<String> getStatus(@PathVariable UUID taskId) {
    return ResponseEntity.ok().body(taskStatusService.getStatus(taskId));
  }

  @GetMapping("/metrics")
  public ResponseEntity<MetricsDto> getMetrics() {
    return ResponseEntity.status(HttpStatus.OK).body(hotelMetricsService.getMetrics());
  }

}
