package gr.police.polseal.repository;

import gr.police.polseal.model.TransactionTemplate;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionTemplateRepository implements PanacheRepository<TransactionTemplate> {

}