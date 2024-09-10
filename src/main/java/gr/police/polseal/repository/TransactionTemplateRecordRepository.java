package gr.police.polseal.repository;

import gr.police.polseal.model.TransactionTemplateRecord;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionTemplateRecordRepository implements PanacheRepository<TransactionTemplateRecord> {

}