package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class TaskStatusServiceTest {

  @Test
  void createTaskShouldStoreAcceptedStatus() {
    TaskStatusService taskStatusService = new TaskStatusService();

    UUID id = taskStatusService.createTask();

    assertNotNull(id);
    assertEquals("ACCEPTED", taskStatusService.getStatus(id));
  }

  @Test
  void updateStatusShouldOverrideStoredValue() {
    TaskStatusService taskStatusService = new TaskStatusService();
    UUID id = taskStatusService.createTask();

    taskStatusService.updateStatus(id, "COMPLETED");

    assertEquals("COMPLETED", taskStatusService.getStatus(id));
  }

  @Test
  void getStatusShouldReturnNotFoundForUnknownTask() {
    TaskStatusService taskStatusService = new TaskStatusService();

    assertEquals("NOT_FOUND", taskStatusService.getStatus(UUID.randomUUID()));
  }
}
