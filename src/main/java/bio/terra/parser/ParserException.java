package bio.terra.parser;

public class ParserException extends RuntimeException {
  public ParserException() {
    super();
  }

  public ParserException(String message) {
    super(message);
  }

  public ParserException(String message, Throwable cause) {
    super(message, cause);
  }
}
