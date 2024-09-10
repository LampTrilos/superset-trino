package gr.police.polseal.service;

import io.quarkus.security.UnauthorizedException;
import io.quarkus.security.identity.SecurityIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.inject.Alternative;

@Alternative
@RequiredArgsConstructor
@Slf4j
public class JwtService {

  private final SecurityIdentity securityIdentity;

  public Long getUserId() {
    if (!((JsonWebToken) this.securityIdentity.getPrincipal()).containsClaim(JwtClaim.USER_ID.getDescription())) {
      log.warn("User does not have attribute " + JwtClaim.USER_ID.getDescription() + " Check keycloak");
      throw new UnauthorizedException();
    } else {
      return Long.parseLong(((JsonWebToken) this.securityIdentity.getPrincipal()).getClaim(JwtClaim.USER_ID.getDescription()).toString());
    }
  }

  public String getDepUnitId() {
    if (!((JsonWebToken) this.securityIdentity.getPrincipal()).containsClaim(JwtClaim.DEP_UNIT_ID.getDescription())) {
      return null;
    } else {
      return ((JsonWebToken) this.securityIdentity.getPrincipal()).getClaim(JwtClaim.DEP_UNIT_ID.getDescription()).toString();
    }
  }

  public void checkPermissions() {
  }

}
