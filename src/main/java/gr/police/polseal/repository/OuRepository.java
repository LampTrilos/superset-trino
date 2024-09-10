package gr.police.polseal.repository;

import gr.police.polseal.model.Ou;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OuRepository implements PanacheRepository<Ou> {

}