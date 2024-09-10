package gr.police.polseal.repository;

import gr.police.polseal.model.SealingTemplate;
import gr.police.polseal.model.SignatureMetadata;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SignatureMetadataRepository implements PanacheRepository<SignatureMetadata> {

}