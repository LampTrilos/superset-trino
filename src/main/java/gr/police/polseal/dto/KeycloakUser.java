package gr.police.polseal.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@RegisterForReflection
@Builder
public class KeycloakUser {
  public String firstName;
  public String lastName;
  public String email;
  public String username;
  public String enabled;
  public Map<String, String> attributes;
  public List<String> realmRoles;
  public Boolean emailVerified;
  public List<String> requiredActions;

  @Override
  public String toString() {
    return "KeycloakUser{" + "lastName=" + lastName + ", email=" + email + ", username=" + username + ", enabled=" + enabled + ", attributes=" + attributes + ", realmRoles=" + realmRoles + '}';
  }

}

