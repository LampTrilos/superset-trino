package gr.police.polseal.service;

import gr.police.polseal.dto.ChallengeChannelDto;
import gr.police.polseal.dto.SealingApplicationDto;
import gr.police.polseal.dto.SealingTemplateDto;
import gr.police.polseal.dto.SignatureMetadataDto;
import gr.police.polseal.dto.mapper.ChallengeChannelMapper;
import gr.police.polseal.dto.mapper.SealingApplicationMapper;
import gr.police.polseal.exception.NotFoundAlertException;
import gr.police.polseal.model.ChallengeChannel;
import gr.police.polseal.model.SealingApplication;
import gr.police.polseal.model.SealingTemplate;
import gr.police.polseal.model.SignatureMetadata;
import gr.police.polseal.repository.*;
import io.quarkus.panache.common.Sort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class SealingApplicationService {

    private final EntityManager em;

    private final SealingApplicationMapper sealingApplicationMapper;
    private final ChallengeChannelMapper challengeChannelMapper;

    private final SealingApplicationRepository sealingApplicationRepository;
    private final ChallengeChannelRepository challengeChannelRepository;
    private final SealingTemplateRepository sealingTemplateRepository;
    private final SignatureMetadataRepository signatureMetadataRepository;

    private final SealingTemplateService sealingTemplateService;

    private final TransactionTemplateRecordRepository transactionTemplateRecordRepository;


    @Transactional
    @Traced
    public List<SealingApplicationDto> getSealingApps() {
//   fetching all apps from db
        List<SealingApplication> results = sealingApplicationRepository.findAll(Sort.by("active").ascending()).list();
//        transforming results to dtos and returning them
        return sealingApplicationMapper.toSealingApplicationDto(results);
    }

    @Transactional
    @Traced
    public SealingApplicationDto getSealingAppById(String id) {
//   fetching app from db
        SealingApplication app = sealingApplicationRepository.findById(Long.valueOf(id));
//        transforming results to dtos and returning them
        return sealingApplicationMapper.toSealingApplicationDto(app);
    }


    @Transactional
    @Traced
    public void persistOrUpdateSealingAppsFromRest(SealingApplicationDto appDto) {
        Long id = null;
        SealingApplication persistItem = null;
        //If we are creating new
        if (appDto.getId() == null) {
            persistItem = new SealingApplication();
            persistItem.setAppCode(appDto.getAppCode());
            persistItem.setDescription(appDto.getDescription());
        }
        //If we are editing
        else {
            id = Long.valueOf(appDto.getId());
            persistItem = sealingApplicationRepository.findByIdOptional(id).orElse(new SealingApplication());
        }
        persistItem.id = id;
        persistItem.setActive(appDto.isActive());

        //Now to update the ChallengeChannels
        List<ChallengeChannel> updatedChannels = new ArrayList<ChallengeChannel>();
        for (ChallengeChannelDto channelDto : appDto.getChallengeChannels()) {
            Long channelId = Long.valueOf(channelDto.getId());
            ChallengeChannel channelToPersist = challengeChannelRepository.findByIdOptional(channelId).orElse(new ChallengeChannel());
            channelToPersist.id = channelId;
            //Lets add the channels to our temp List
            updatedChannels.add(channelToPersist);
        }
        // We use a set to protect the list from identical challenge channels
        Set<ChallengeChannel> channelSet = new HashSet<>(updatedChannels);
        List<ChallengeChannel> finalChannels = new ArrayList<>(channelSet);
        //Now to link the list of channels to our Item
        persistItem.setChallengeChannels(finalChannels);

        //Now to set or update the Templates
        persistItem.templates.clear();
        //Only execute template logic if there are templates attached to the appDTO
        if (appDto.getTemplates().size() > 0) {
            //Hibernate really doesnt like the new Array list and causes orphan trouble, we need to add/remove
            List<SealingTemplate> updatedTemplates = new ArrayList<>();
            for (SealingTemplateDto templateDto : appDto.getTemplates()) {
                SealingTemplate template = null;
                //If the Template doesnt exist, it doesnt have an id yet
                if (templateDto.getId() != null) {
                    template = sealingTemplateRepository.findByIdOptional(Long.valueOf(templateDto.getId())).orElse(new SealingTemplate());
                }
                //If we dont have a template id, make a new one
                else {
                    template = new SealingTemplate();
                }
                template.setDescription(templateDto.getDescription());
                template.setMaxSignatures(Integer.parseInt(templateDto.getMaxSignatures()));
                template.setSealOnEveryPage(templateDto.isSealOnEveryPage());
                template.setCode(templateDto.getCode());
                template.setEmailMessage(templateDto.getEmailMessage());
                //todo not sure that the following works
                //template.setFileData(templateDto.getFileData());
                template.setSealingApplication(persistItem);

                //If there are no existing Signature Metadata, add the signature metadata for this template
//                if (!templateDto.getSignaturesMetadata().isEmpty()) {
//                    for (SignatureMetadataDto metadataDto : templateDto.getSignaturesMetadata()) {
//                        template.getSignaturesMetadata().add(new SignatureMetadata(metadataDto.getIndex(), metadataDto.getPosition()));
//                    }
//                }
                //Hibernate really doesnt like the new Array list and causes orphan trouble, we need to add/remove
                List<SignatureMetadata> updatedSignaturesMetadata = new ArrayList<>();
                template.signaturesMetadata.clear();
                for (SignatureMetadataDto signatureMetadataDto : templateDto.getSignaturesMetadata()) {
                    SignatureMetadata signatureMetadata = null;
                    //If the signatureMetadata doesnt exist, it doesnt have an id yet
                    if (signatureMetadataDto.getId() != null) {
                        signatureMetadata = signatureMetadataRepository.findByIdOptional(Long.valueOf(templateDto.getId())).orElse(new SignatureMetadata());
                    }
                    //If we dont have a template id, make a new one
                    else {
                        signatureMetadata = new SignatureMetadata();
                    }
                    signatureMetadata.setIndex(signatureMetadataDto.getIndex());
                    signatureMetadata.setSealingTemplate(template);
                    signatureMetadata.setPosition(signatureMetadataDto.getPosition());
                    updatedSignaturesMetadata.add(signatureMetadata);
                }
                template.getSignaturesMetadata().addAll(updatedSignaturesMetadata);
                updatedTemplates.add(template);
            }
            //Now to link the list of templates to our App
            //persistItem.setTemplates(updatedTemplates); //Hibernate really doesnt like this
            persistItem.templates.addAll(updatedTemplates);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:mm:ss'Z[UTC]'");
        LocalDateTime start = appDto.getStartDate() != null ? LocalDateTime.parse(appDto.getStartDate(), formatter) : null;
        persistItem.setStartDate(start);
        LocalDateTime end = appDto.getEndDate() != null ? LocalDateTime.parse(appDto.getEndDate(), formatter) : null;
        persistItem.setEndDate(end);
        sealingApplicationRepository. persistAndFlush(persistItem);
//        we need to persist the App in order for the Templates to take Ids and then set the TemplateCode accordingly
        for (SealingTemplate template : persistItem.getTemplates()){
            template.setCode(persistItem.getAppCode().substring(3)+"_"+template.id);
        }
        sealingApplicationRepository. persistAndFlush(persistItem);
    }

    @Transactional
    @Traced
    public boolean delete(List<Long> appsIds) {
        boolean successfullyDeletedAllApps = true;
        if (!appsIds.isEmpty()) {
//            List for all the valid apps that can be deleted. Apps with associated templates won't be deleted
            List<Long> validAppsForDeletion = new ArrayList<>();
            for (Long id : appsIds) {
                SealingApplication sa = sealingApplicationRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(SealingApplication.class.getName()));
                if (sa.getTemplates().isEmpty()) {
                    validAppsForDeletion.add(sa.id);
                } else {
                    successfullyDeletedAllApps = false;
                }
            }
            for (Long id : validAppsForDeletion) {
                sealingApplicationRepository.deleteById(id);
            }
        }
        return successfullyDeletedAllApps;
    }


//  @Transactional
//  @Traced
//  public TransactionTemplateResDto getTransactionTemplate(Long id) {
//    return transactionTemplateMapper.toTransactionSealingTemplateDto(transactionTemplateRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(TransactionTemplate.class.getName())));
//  }

//  @Transactional
//  @Traced
//  public Page<TransactionTemplateResDto> getTransactionTemplates(ToOptionsTransactionTemplate options) {
//    CriteriaBuilder cb = em.getCriteriaBuilder();
//    // Results
//    CriteriaQuery<TransactionTemplate> lcq = cb.createQuery(TransactionTemplate.class);
//    Root<TransactionTemplate> root = lcq.from(TransactionTemplate.class);
//
//    // Filter
//    List<Predicate> predicates = new ArrayList<>();
//
//    predicates.add(cb.equal(root.get(TransactionTemplate_.user).get(User_.id), options.user));
//
//    if (options.id != null) {
//      CriteriaBuilder.In<Long> inClause = cb.in(root.get(TransactionTemplate_.id));
//      for (String item : options.id.list) {
//        inClause.value(Long.valueOf(item));
//      }
//      predicates.add(inClause);
//    }
//
//    if (options.description != null) {
//      predicates.add(cb.like(cb.upper(root.get(TransactionTemplate_.description)), "%" + options.description.toUpperCase() + "%"));
//    }
//
//    ServiceUtils<TransactionTemplate> serviceUtils = new ServiceUtils<>();
//    Predicate predicate = serviceUtils.getAndPredicate(cb, predicates);
//    // End Filter
//
//    // Order
//    List<Order> orders = serviceUtils.getSort(cb, root, options.sort);
//    // Default ordering
//    orders.add(cb.asc(root.get(TransactionTemplate_.description)));
//    // End Order
//
//    lcq.select(root).where(predicate).orderBy(orders);
//    Query query = em.createQuery(lcq);
//
//    List<TransactionTemplate> results;
//    if (options.size != -1) {
//      results = query.setFirstResult(options.size * options.index).setMaxResults(options.size).getResultList();
//      // End Results
//
//      // Total results
//      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//      cq.select(cb.count(cq.from(TransactionTemplate.class)));
//      cq.where(predicate);
//      Long total = em.createQuery(cq).getSingleResult();
//      // End  Total results
//
//      return new Page<>(transactionTemplateMapper.toTransactionSealingTemplateDto(results), total, options.index, options.size);
//    } else {
//      results = query.getResultList();
//      return new Page<>(transactionTemplateMapper.toTransactionSealingTemplateDto(results), results == null ? 0L : results.size(), options.index, options.size);
//    }
//  }
//
//    @Transactional
//    @Traced
//    public EntityDto persistOrUpdate(TransactionTemplateReqDto transactionSealingTemplateDto) {
//        TransactionTemplate transactionTemplate = transactionTemplateMapper.toTransactionTemplate(transactionSealingTemplateDto);
//        if (transactionTemplate.getId() != null) {
//            TransactionTemplate oldTransactionTemplate = transactionTemplateRepository.findByIdOptional(transactionTemplate.getId()).orElseThrow(() -> new NotFoundAlertException(TransactionTemplate.class.getName()));
//            oldTransactionTemplate.setDescription(transactionTemplate.getDescription());
//            for (TransactionTemplateRecord record : oldTransactionTemplate.getRecords()) {
//                transactionTemplateRecordRepository.delete(record);
//            }
//            for (TransactionTemplateRecord record : transactionTemplate.getRecords()) {
//                record.setTransactionTemplate(oldTransactionTemplate);
//                transactionTemplateRecordRepository.persist(record);
//            }
//        } else {
//            transactionTemplateRepository.persistAndFlush(transactionTemplate);
//            for (TransactionTemplateRecord record : transactionTemplate.getRecords()) {
//                record.setTransactionTemplate(transactionTemplate);
//                transactionTemplateRecordRepository.persist(record);
//            }
//        }
//        return new EntityDto(transactionTemplate.getId(), transactionTemplate.getDescription());
//    }


//    @Transactional
//    @Traced
//    public boolean hasPermissions(Long id, Long userId) {
//        TransactionTemplate transactionTemplate = transactionTemplateRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(TransactionTemplate.class.getName()));
//        return transactionTemplate.getUser().getId().equals(userId);
//    }

}
