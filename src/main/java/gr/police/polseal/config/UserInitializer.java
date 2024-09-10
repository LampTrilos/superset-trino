package gr.police.polseal.config;

import eu.ubitech.bitt.core.data.AppData;
import eu.ubitech.bitt.core.domain.auth.model.entity.KeycloakUser;
import eu.ubitech.bitt.core.domain.auth.service.UserManagementService;
import gr.police.polseal.dto.SignUpRequest;
import gr.police.polseal.service.UserRegistrationService;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.StartupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.representations.idm.UserRepresentation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.List;

import static org.keycloak.common.util.CollectionUtil.isEmpty;

@Slf4j
@ApplicationScoped
@IfBuildProfile("create-users")
@RequiredArgsConstructor
public class UserInitializer {

  private final AppData appData;
  private final UserManagementService userManagementService;
  private final UserRegistrationService userRegistrationService;

  public void createUsers(@Observes StartupEvent startupEvent) {

    // required for smtp configuration.
    userManagementService.setMasterEmail();

    if (isEmpty(appData.users())) {
      return;
    }

    log.info("Creating Users Data...");
    boolean keycloakOnly = appData.keycloakOnly().orElse(true);
    if (keycloakOnly) {
      appData.users().forEach(this::registerUserKeycloakOnly);
    } else {
      appData.users().forEach(this::registerUser);
    }
  }

  private UserRepresentation registerUser(AppData.User configUser) {
    SignUpRequest signUpRequest = new SignUpRequest();
    signUpRequest.setEmail(configUser.email());
    signUpRequest.setRealmRoles(configUser.roles());
    signUpRequest.setUsername(configUser.username());
    signUpRequest.setFirstName(configUser.firstname());
    signUpRequest.setLastName(configUser.lastname());
    return userRegistrationService.registerUser(signUpRequest);
  }

  private UserRepresentation registerUserKeycloakOnly(AppData.User configUser) {
    List<UserRepresentation> userExists = userManagementService.userRepresentationNoErrorHandling(configUser.realm(), configUser.username());
    if (CollectionUtil.isNotEmpty(userExists)) {
      return userExists.get(0);
    }

    KeycloakUser keycloakUser = new KeycloakUser();
    keycloakUser.setEmail(configUser.email());
    keycloakUser.setUsername(configUser.username());
    keycloakUser.setFirstName(configUser.firstname());
    keycloakUser.setLastName(configUser.lastname());
    keycloakUser.setRealm(configUser.realm());
    keycloakUser.setRoles(configUser.roles());
    if (configUser.clientId().isPresent()) {
      keycloakUser.setClientId(configUser.clientId().get());
    }
    UserRepresentation userRepresentation = userManagementService.addUser(keycloakUser);
    log.info(String.format("User [%s] with email: [%s]", userRepresentation.getId(), userRepresentation.getEmail()));
    return userRepresentation;
  }
}
