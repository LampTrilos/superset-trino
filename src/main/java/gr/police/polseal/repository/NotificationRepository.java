package gr.police.polseal.repository;

import gr.police.polseal.model.Notification;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotificationRepository implements PanacheRepository<Notification> {

}