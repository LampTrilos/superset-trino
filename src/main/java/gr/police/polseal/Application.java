package gr.police.polseal;

import eu.ubitech.bitt.core.domain.health.ApplicationHealthManager;
import eu.ubitech.bitt.core.exceptions.StackException;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class Application {

  private final ApplicationHealthManager applicationHealthManager;

  @Transactional
  void onStart(@Observes StartupEvent ev) {
    log.info(String.format("The application starting up with profile [%s]", ProfileManager.getActiveProfile()));
//    System.out.println("Current profile is");
//    System.out.println(ProfileManager.getActiveProfile());
    //Do not run Readiness check while developing
    if (!ProfileManager.getActiveProfile().equalsIgnoreCase("dev")) {
//      readinessCheck();
    }
  }

  private void readinessCheck() {
    try {
      applicationHealthManager.check();
    } catch (StackException e) {
      log.error(e.getMessage(), e);
      Quarkus.asyncExit();
    }
  }
}
