package gr.police.polseal.repository;

import gr.police.polseal.model.ChallengeChannel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChallengeChannelRepository implements PanacheRepository<ChallengeChannel> {

}