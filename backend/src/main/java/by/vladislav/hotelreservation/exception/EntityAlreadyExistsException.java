package by.vladislav.hotelreservation.exception;

public class EntityAlreadyExistsException extends RuntimeException {
  public EntityAlreadyExistsException(long id) {
    super("Entity with id: " + id + " already exists");
  }

  public EntityAlreadyExistsException(String name) {
    super("Entity with name: " + name + " already exists");
  }

  public EntityAlreadyExistsException(String entityName, String field, Object value) {
    super(entityName + " with " + field + ": " + value + " already exists");
  }
}
