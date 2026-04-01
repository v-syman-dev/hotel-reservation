package by.vladislav.hotelreservation.exception;

public class EntityAlreadyExistsException extends RuntimeException {
  public EntityAlreadyExistsException(long id) {
    super("Entity with id: " + id + " not found");
  }

  public EntityAlreadyExistsException(String name) {
    super("Entity with name: " + name + " not found");
  }
}
