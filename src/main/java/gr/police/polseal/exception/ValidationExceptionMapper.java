package gr.police.polseal.exception;

import eu.ubitech.bitt.core.exceptions.ErrorMessageModel;
import eu.ubitech.bitt.core.exceptions.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Slf4j
//@Provider
@RequiredArgsConstructor
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

  @Override
  public Response toResponse(ValidationException e) {
    return Response
        .status(Response.Status.BAD_REQUEST)
        .entity(new ErrorMessageModel(ErrorType.CLIENT, e.getMessage()))
        .build();
  }
}
