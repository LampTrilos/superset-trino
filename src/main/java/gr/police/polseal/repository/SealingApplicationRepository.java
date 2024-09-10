package gr.police.polseal.repository;

import gr.police.polseal.model.SealingApplication;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SealingApplicationRepository implements PanacheRepository<SealingApplication> {

}