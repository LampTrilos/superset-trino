package gr.police.polseal.service;

import gr.police.polseal.dto.ChallengeChannelDto;
import gr.police.polseal.dto.mapper.ChallengeChannelMapper;
import gr.police.polseal.model.ChallengeChannel;
import gr.police.polseal.repository.ChallengeChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class ChallengeChannelService {

    private final EntityManager em;

    private final ChallengeChannelMapper challengeChannelMapper;

    private final ChallengeChannelRepository challengeChannelRepository;



    @Transactional
    @Traced
    public List<ChallengeChannelDto> getAllChannels() {
//   fetching all challengeChannels from db
        List<ChallengeChannel> results = challengeChannelRepository.findAll().list();
//        transforming results to dtos and returning them
        return challengeChannelMapper.toChallengeChannelDto(results);
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
