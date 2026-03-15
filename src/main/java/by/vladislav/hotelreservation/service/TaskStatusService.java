package by.vladislav.hotelreservation.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TaskStatusService {
  private final Map<UUID, String> taskStatuses = new ConcurrentHashMap<>();

  public UUID createTask() {
    UUID id = UUID.randomUUID();
    taskStatuses.put(id, "ACCEPTED");
    return id;
  }

  public void updateStatus(UUID id, String status) {
    taskStatuses.put(id, status);
  }

  public String getStatus(UUID id) {
    return taskStatuses.getOrDefault(id, "NOT_FOUND");
  }
}
