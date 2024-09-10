package gr.police.polseal.repository;

import gr.police.polseal.model.OuHist;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OuHistRepository implements PanacheRepository<OuHist> {

}