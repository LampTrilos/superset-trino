package gr.police.polseal.service;

import eu.ubitech.bitt.core.domain.auth.model.AuthToken;
import eu.ubitech.bitt.core.domain.auth.model.RequiredActionsEnum;
import eu.ubitech.bitt.core.domain.auth.model.request.UsernamePasswordRequest;
import eu.ubitech.bitt.core.domain.auth.service.BearerTokenAuthorizationService;
import eu.ubitech.bitt.core.exceptions.StackException;
import gr.police.polseal.dto.KeycloakUser;
import gr.police.polseal.exception.NotFoundAlertException;
import gr.police.polseal.model.User;
import gr.police.polseal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.opentracing.Traced;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

  private final Keycloak keycloakAdmin;

  private final BearerTokenAuthorizationService bearerTokenAuthorizationService;

  private final UserRepository userRepository;

  private final PermissionService permissionService;

  @Traced
  public AuthToken autologin(String arithmosMitroou, String depUnitId) {
    //User user = userRepository.findByIdOptional(Long.valueOf(userid)).orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
//    User user = userRepository.find("arithmosMitroou", arithmosMitroou).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
    User user = userRepository.find("arithmosMitroou", arithmosMitroou).firstResult();
//    List<DepUnitDto> depUnits = permissionService.getUserDepartments(user.getId());
//    if (depUnitId == null || !depUnits.stream().map(depUnitDto -> depUnitDto.getId()).collect(Collectors.toList()).contains(depUnitId)) {
//      depUnitId = (depUnits != null && depUnits.size() > 0) ? depUnits.get(0).getId() : null;
//    }
    Map<String, String> attributeMap = new HashMap<>();
    attributeMap.put("ivuser", arithmosMitroou);
    attributeMap.put("userid", String.valueOf(user.getId()));
    if (depUnitId != null) {
      attributeMap.put("depunitid", depUnitId);
    }
    KeycloakUser kuser = KeycloakUser.builder()
        .username(user.getUsername())
        .email(user.getEmail())
        .enabled("true")
        .emailVerified(true)
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .attributes(attributeMap)
        .realmRoles(Collections.emptyList())
        .build();
    try {
      adduser(kuser);
      String userid = String.valueOf(user.getId());
      UsernamePasswordRequest req = UsernamePasswordRequest.builder().username(userid).password(getUserPassword(userid)).build();
      return bearerTokenAuthorizationService.login(req);
    } catch (Exception e) {
      log.error("Autologin failed", e);
      throw new StackException("Autologin failed.");
    }
  }

  private KeycloakUser adduser(KeycloakUser user) {
    List<UserRepresentation> userRepresentationList = keycloakAdmin.realm("quarkus").users().search(user.getUsername(), true);
    if (!CollectionUtil.isEmpty(userRepresentationList)) {
      //log.info(String.format("Username: [%s] already exists:", user.getUsername()));
      keycloakAdmin.realm("quarkus").users().delete(userRepresentationList.get(0).getId());
    }
    //---Step 2 Request to add User and reset password -----------------
    //log.info(new Gson().toJson(user));
    requestToAddUser(user);
    return user;
  }

  public void resetUserPassword(String userId, String password) {
    UserResource userResource = keycloakAdmin.realm("quarkus").users().get(userId);
    userResource.resetPassword(toCredentialPresentation(password));
    userResource.toRepresentation().getRequiredActions().removeIf(p -> p.equalsIgnoreCase(RequiredActionsEnum.UPDATE_PASSWORD.getDescription()));
  }

  private void requestToAddUser(KeycloakUser user) {
    UserRepresentation currentRepresentation = toUserRepresentation(user, false);
    String userId = addUserWithRole(user, currentRepresentation);
    resetUserPassword(userId, getUserPassword(user.getUsername()));
  }

  private String getUserPassword(String userId) {
    return "!" + userId + "!";
  }

  private UserRepresentation toUserRepresentation(KeycloakUser user, boolean pendingVerification) {
    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setUsername(user.getUsername());
    userRepresentation.setEmail(user.getEmail());
    userRepresentation.setFirstName(user.getFirstName());
    userRepresentation.setLastName(user.getLastName());
    userRepresentation.setEnabled(true);
    userRepresentation.setEmailVerified(!pendingVerification);
    Map<String, List<String>> newAttributes = new HashMap<>();
    for (Map.Entry entry : user.attributes.entrySet()) {
      newAttributes.put(entry.getKey().toString(), List.of(entry.getValue().toString()));
    }
    userRepresentation.setAttributes(newAttributes);
    if (pendingVerification) {
      userRepresentation.setRequiredActions(List.of("VERIFY_EMAIL", "UPDATE_PASSWORD"));
    }
    userRepresentation.setCredentials(Collections.singletonList(toCredentialPresentation(user.getUsername())));
    userRepresentation.setRealmRoles(user.getRealmRoles());
    return userRepresentation;
  }

  private CredentialRepresentation toCredentialPresentation(String password) {
    CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
    credentialRepresentation.setTemporary(false);
    credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
    credentialRepresentation.setValue(password);
    return credentialRepresentation;
  }

  private String addUserWithRole(KeycloakUser user, UserRepresentation currentRepresentation) {
    Response response = keycloakAdmin.realm("quarkus").users().create(currentRepresentation);
    String userId = CreatedResponseUtil.getCreatedId(response);
    UserResource userResource = keycloakAdmin.realm("quarkus").users().get(userId);

    List<RoleRepresentation> roleRepresentations = new ArrayList<>();
    for (String roleName : currentRepresentation.getRealmRoles()) {
      roleRepresentations.add(keycloakAdmin.realm("quarkus").roles().get(roleName).toRepresentation());
    }
    userResource.roles().realmLevel().add(roleRepresentations);
    return userId;
  }

}
