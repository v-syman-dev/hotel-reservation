package by.vladislav.hotelreservation.exception;

import by.vladislav.hotelreservation.entity.constant.EntityType;

public class EntityAlreadyExistsException extends RuntimeException {
  public EntityAlreadyExistsException(long id) {
    super("Entity with id: " + id + " already exists");
  }

  public EntityAlreadyExistsException(String name) {
    super("Entity with name: " + name + " already exists");
  }

  public EntityAlreadyExistsException(String entityName, String fieldName, Object fieldValue) {
    super(entityName + " with " + fieldName + ": " + fieldValue + " already exists");
  }

  public EntityAlreadyExistsException(EntityType entityType, String fieldName, Object fieldValue) {
    super(String.format("%s with %s : %s already exists", entityType.getName(), fieldName, fieldValue));
  }
}
