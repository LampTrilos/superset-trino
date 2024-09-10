package gr.police.polseal.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class InternalServerErrorException extends WebApplicationException {
  private static final long serialVersionUID = 1L;

  public InternalServerErrorException() {
    this("HTTP 500 - Internal server error. Please contact site admin.");
  }

  public InternalServerErrorException(String message) {
    super(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build());
  }
}
