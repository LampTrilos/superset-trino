package gr.police.polseal.repository;

import gr.police.polseal.model.SealingTemplate;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class SealingTemplateRepository implements PanacheRepository<SealingTemplate> {
    public Optional<SealingTemplate> findByCode(String code){
        return find("code", code).firstResultOptional();
    }

}