package gr.police.polseal.resource;

import eu.ubitech.bitt.core.domain.auth.model.AuthToken;
import eu.ubitech.bitt.core.domain.auth.model.request.RefreshRequest;
import eu.ubitech.bitt.core.domain.auth.model.request.UsernamePasswordRequest;
import eu.ubitech.bitt.core.domain.auth.service.BearerTokenAuthorizationService;
import gr.police.polseal.dto.SignUpRequest;
import gr.police.polseal.exception.NotFoundAlertException;
import gr.police.polseal.model.User;
import gr.police.polseal.repository.UserRepository;
import gr.police.polseal.service.AuthorizationService;
import gr.police.polseal.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Singleton
@SecuritySchemes(value = {
  @SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.HTTP, scheme = "Bearer")
})
@SecurityRequirement(name = "apiKey")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationResource {

  private final BearerTokenAuthorizationService bearerTokenAuthorizationService;
  private final UserRegistrationService userRegistrationService;
  private final AuthorizationService authorizationService;
  private final UserRepository userRepository;

  @POST
  @Path("/sign-up")
  public Response signUp(@Valid SignUpRequest signUpRequest) {
    userRegistrationService.registerUser(signUpRequest);
    return Response.noContent().build();
  }

  @POST
  @Path("/login")
  public Response login(@Valid UsernamePasswordRequest usernamePasswordRequest) {
    AuthToken token = bearerTokenAuthorizationService.login(usernamePasswordRequest);
    //For the purposes of this demo, we must not create new users via autologin, so we just pre-register the users in Keycloak and then do a simple login
    //User user = userRepository.find("id", Long.valueOf(usernamePasswordRequest.getUsername())).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
    //AuthToken token = authorizationService.autologin(user.getArithmosMitroou(), null);
    if (token == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    return Response.ok(token).build();
  }

  @POST
  @Path("/refresh")
  public Response refresh(@Valid RefreshRequest refresh) {
    AuthToken authToken = bearerTokenAuthorizationService.refresh(refresh);
    if (authToken == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    return Response.ok(authToken).build();
  }

  @POST
  @Path("/forgot-password")
  public Response forgotPassword(@QueryParam("username") String username) {
    userRegistrationService.resetUserPassword(username);
    return Response.ok().build();
  }

  @POST
  @Path("/logout")
  public Response logout(@Valid RefreshRequest refresh) {
    bearerTokenAuthorizationService.logout(refresh);
    return Response.noContent().build();
  }

  @POST
  @Path("/autologin")
  public Response autologin(@NotNull(message = "Δεν έχετε header iv-user") @HeaderParam("IV-USER") String arithmosMitroou) {
    log.info("IV-USER: " + arithmosMitroou);
    AuthToken token = authorizationService.autologin(arithmosMitroou, null);
    if (token == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    return Response.ok(token).build();
  }

  @POST
  @Path("/autologin/{depunitid}")
  public Response autoLoginWithPreferredDepUnit(@NotNull(message = "Δεν έχετε header iv-user") @HeaderParam("IV-USER") String arithmosMitroou, @NotNull @PathParam("depunitid") String depUnitId) {
    log.info("IV-USER: " + arithmosMitroou);
    AuthToken token = authorizationService.autologin(arithmosMitroou, depUnitId);
    if (token == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    return Response.ok(token).build();
  }

}

