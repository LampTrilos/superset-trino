package gr.police.polseal.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CONFLICT;

public class FailAlertException extends WebApplicationException {
  private static final long serialVersionUID = 1L;

  public FailAlertException(String message) {
    super(Response.status(CONFLICT).entity(message).build());
  }

  public FailAlertException(String message, String errorKey) {
    super(Response.status(CONFLICT).entity(message).header("message", "error." + errorKey).build());
  }
}
