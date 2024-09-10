package gr.police.polseal.exception;

import eu.ubitech.bitt.core.exceptions.ErrorMessageModel;
import eu.ubitech.bitt.core.exceptions.ErrorType;
import eu.ubitech.bitt.core.exceptions.StackException;
import eu.ubitech.bitt.core.exceptions.StackValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Map;
import java.util.function.Function;

@Slf4j
//@Provider
@RequiredArgsConstructor
public class WebServiceExceptionMapper implements ExceptionMapper<WebApplicationException> {

  private static final Map<Class<?>, Function<WebApplicationException, Response>> KNOWN_EXCEPTION_MAP = Map.of(
      StackException.class, (e) -> toStackException((StackException) e),
      StackValidationException.class, (e) -> toStackValidationException((StackValidationException) e)
  );

  @Override
  public Response toResponse(WebApplicationException exception) {
    //log.error(exception.getMessage());
    return KNOWN_EXCEPTION_MAP.getOrDefault(exception.getClass(), this::toDefaultHandler).apply(exception);
  }

  private static Response toStackValidationException(StackValidationException exception) {
    return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(new ErrorMessageModel(ErrorType.CLIENT, exception.getMessage()))
            .build();
  }

  private static Response toStackException(StackException exception) {
    return Response
      .status(Response.Status.INTERNAL_SERVER_ERROR)
      .entity(new ErrorMessageModel(ErrorType.SERVER, exception.getMessage()))
      .build();
  }

  private Response toDefaultHandler(WebApplicationException exception) {
    return Response
      .status(Response.Status.INTERNAL_SERVER_ERROR)
      .entity(new ErrorMessageModel(ErrorType.SERVER, exception.getMessage()))
      .build();
  }
}
