package gr.police.polseal.repository;

import gr.police.polseal.model.logs.LogSyndesiKinitou;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LogSyndesiKinitouRepository implements PanacheRepository<LogSyndesiKinitou> {

}