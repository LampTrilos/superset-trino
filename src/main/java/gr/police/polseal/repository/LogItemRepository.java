package gr.police.polseal.repository;

import gr.police.polseal.model.logs.LogItem;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LogItemRepository implements PanacheRepository<LogItem> {

}