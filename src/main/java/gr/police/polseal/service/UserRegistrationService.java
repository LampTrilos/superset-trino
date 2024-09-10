package gr.police.polseal.service;

import eu.ubitech.bitt.core.domain.auth.config.AuthConfig;
import eu.ubitech.bitt.core.domain.auth.model.entity.KeycloakUser;
import eu.ubitech.bitt.core.domain.auth.model.entity.KeycloakUser_;
import eu.ubitech.bitt.core.domain.auth.service.UserManagementService;
import eu.ubitech.bitt.core.exceptions.StackValidationException;
import gr.police.polseal.dto.SignUpRequest;
import gr.police.polseal.dto.mapper.UserMapper;
import gr.police.polseal.model.User;
import gr.police.polseal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.opentracing.Traced;
import org.keycloak.representations.idm.UserRepresentation;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UserRegistrationService {

  private final UserManagementService userManagementService;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final AuthConfig authConfig;

  @Traced
  @Transactional
  public UserRepresentation registerUser(@Valid SignUpRequest signUpRequest) {
    if (checkIfUserExists(signUpRequest) != null) {
      log.error(String.format("User already exists. email: [%s]", signUpRequest.getEmail()));
      throw new StackValidationException("User already exists");
    }

    User user = userMapper.toUser(signUpRequest, authConfig.realm());
    userRepository.persist(user);
    return userManagementService.addUser(userMapper.toKeycloakUser(user));
  }

  private KeycloakUser checkIfUserExists(SignUpRequest signUpRequest) {
    KeycloakUser tempAbstractUser = userMapper.toKeycloakUser(userRepository.find(KeycloakUser_.USERNAME, signUpRequest.getUsername()).firstResult());
    if (tempAbstractUser != null) {
      return tempAbstractUser;
    }
    tempAbstractUser = userMapper.toKeycloakUser(userRepository.find(KeycloakUser_.EMAIL, signUpRequest.getEmail()).firstResult());
    return tempAbstractUser;
  }

  @Traced
  @Transactional
  public void resetUserPassword(String username) {
    User user = userRepository.find(KeycloakUser_.USERNAME, username).firstResult();
    if (user == null) {
      log.error(String.format("User: [%s] not found", username));
      throw new StackValidationException("User not found");
    }
    userManagementService.sendResetPassword(username);
  }

}
