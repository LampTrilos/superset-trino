package gr.police.polseal.repository;

import gr.police.polseal.model.logs.LogTransaction;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LogTransactionRepository implements PanacheRepository<LogTransaction> {

}