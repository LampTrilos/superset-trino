package gr.police.polseal.schedulers;


import gr.police.polseal.restclients.HumanResourceClient;
import gr.police.polseal.restclients.OuDtoClient;
import gr.police.polseal.service.OuService;
import io.quarkus.scheduler.Scheduled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class OuScheduler {

  @Inject
  @RestClient
  HumanResourceClient humanResourceClient;

  @Inject
  OuService ouService;

  // schedule task every day at 18:30
  @Scheduled(cron = "0 30 18 * * ?")
  void syncOus() {
    log.info("Scheduler start syncOus");
    List<OuDtoClient> list = humanResourceClient.getYpiresia();
    for (OuDtoClient dto : list) {
      ouService.persistOrUpdateOuHistFromRest(dto);
    }
    for (OuDtoClient dto : list) {
      ouService.persistOrUpdateOuFromRest(dto);
    }
    log.info("Scheduler end syncOus");
  }

}
