package gr.police.polseal.config;

import eu.ubitech.bitt.core.domain.auth.health.AuthReadinessCheck;
import eu.ubitech.bitt.core.domain.auth.service.UserManagementService;
import eu.ubitech.bitt.core.domain.health.ApplicationHealthManager;
import eu.ubitech.bitt.core.domain.mailer.health.MailReadinessCheck;
import eu.ubitech.bitt.core.domain.mailer.service.SendMailService;
import eu.ubitech.bitt.core.domain.storage.health.StorageReadinessCheck;
import eu.ubitech.bitt.core.domain.storage.service.StorageService;
import io.quarkus.arc.All;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.eclipse.microprofile.health.HealthCheck;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class ApplicationHealthBeanFactory {

  @Produces
  @ApplicationScoped
  public AuthReadinessCheck authReadinessCheck(UserManagementService userManagementService) {
    return new AuthReadinessCheck(userManagementService);
  }

  @Produces
  @ApplicationScoped
  public StorageReadinessCheck storageReadinessCheck(StorageService storageService) {
    return new StorageReadinessCheck(storageService);
  }

  @Produces
  @ApplicationScoped
  public MailReadinessCheck mailReadinessCheck(SendMailService sendMailService) {
    return new MailReadinessCheck(sendMailService);
  }

  /*  @Produces
  @ApplicationScoped
  public MassIndexService massIndexService(SearchSession searchSession) {
    return new MassIndexService(searchSession);
  }

  @Produces
  @ApplicationScoped
  public SearchReadinessCheck searchReadinessCheck(MassIndexService massIndexService) {
    return new SearchReadinessCheck(massIndexService);
  }*/

  @Produces
  @ApplicationScoped
  @IfBuildProfile("test")
  public ApplicationHealthManager applicationHealthManagerTest() {
    return new ApplicationHealthManager(Collections.emptyList());
  }

  @Produces
  @ApplicationScoped
  @UnlessBuildProfile("test")
  public ApplicationHealthManager applicationHealthManager(@All List<InstanceHandle<HealthCheck>> healthCheckList) {
    return new ApplicationHealthManager(healthCheckList);
  }

}
