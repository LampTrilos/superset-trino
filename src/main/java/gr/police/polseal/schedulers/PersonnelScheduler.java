package gr.police.polseal.schedulers;
//A cron job that runs every morning and updates the users and the email accounts

import gr.police.polseal.resource.UserResource;
import gr.police.polseal.restclients.HumanResourceClient;
import gr.police.polseal.restclients.OuDtoClient;
import gr.police.polseal.service.OuService;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
@Slf4j

@ApplicationScoped
public class PersonnelScheduler {


    @Inject
    @RestClient
    HumanResourceClient humanResourceClient;

//    @Inject
//    OuService ouService;

    @Inject
    UserResource userResource;

    // Sync all users and their emails, schedule task every day at 19:00
    @Scheduled(cron = "0 30 19 * * ?", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    //@Scheduled(cron = "*/4 * * * * ?", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void syncPersonnel() {
        log.info("Scheduler start sync of users and emails");
        //Sync all users
        userResource.syncUsers();
        //Now sync their emails
        userResource.syncEmails();
        log.info("Scheduler end of sync of users and emails");
    }

}
