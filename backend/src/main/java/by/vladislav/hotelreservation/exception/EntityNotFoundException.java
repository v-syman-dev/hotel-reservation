package by.vladislav.hotelreservation.exception;

import by.vladislav.hotelreservation.entity.constant.EntityType;

public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(long id) {
    super("Entity with id: " + id + " not found");
  }

  public EntityNotFoundException(String name) {
    super("Entity with name: " + name + " not found");
  }

  public EntityNotFoundException(EntityType entityType, String fieldName, Object fieldValue) {
    super(String.format("%s with %s : %s not found", entityType.getName(), fieldName, fieldValue));
  }
}
