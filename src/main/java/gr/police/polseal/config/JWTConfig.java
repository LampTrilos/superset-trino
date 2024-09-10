package gr.police.polseal.config;


import gr.police.polseal.service.JwtService;
import io.quarkus.security.identity.SecurityIdentity;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class JWTConfig {

  @Produces
  @RequestScoped
  public JwtService jwtService(SecurityIdentity securityIdentity) {
    return new JwtService(securityIdentity);
  }
}
