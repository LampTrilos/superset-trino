package gr.police.polseal.service;

import gr.police.polseal.dto.LogItemDto;
import gr.police.polseal.dto.LogTransactionDto;
import gr.police.polseal.dto.mapper.LogMapper;
import gr.police.polseal.dto.options.extensions.ToOptionsLogTransaction;
import gr.police.polseal.dto.page.Page;
//import gr.police.polseal.model.CompoundTransaction_;
import gr.police.polseal.model.User;
import gr.police.polseal.model.logs.*;
import gr.police.polseal.repository.LogItemRepository;
import gr.police.polseal.repository.LogSyndesiKinitouRepository;
import gr.police.polseal.repository.LogTransactionRepository;
import gr.police.polseal.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class LogService {

  private final LogMapper logMapper;

  private final LogItemRepository logItemRepository;

  private final LogSyndesiKinitouRepository logSyndesiKinitouRepository;

  private final LogTransactionRepository logTransactionRepository;

  private final EntityManager em;

//  @Traced
//  @Transactional
//  public Page<LogItem> getLogItems(ToOptionsLogItem options) {
//
//    CriteriaBuilder cb = em.getCriteriaBuilder();
//    // Results
//    CriteriaQuery<LogItem> lcq = cb.createQuery(LogItem.class);
//    Root<LogItem> root = lcq.from(LogItem.class);
//
//    // Filter
//    List<Predicate> predicates = new ArrayList<>();
//
//    predicates.add(cb.equal(root.get(LogItem_.itemId), options.itemId));
//
//    ServiceUtils<LogItem> serviceUtils = new ServiceUtils<>();
//    Predicate predicate = serviceUtils.getAndPredicate(cb, predicates);
//    // End Filter
//
//    // Order
//    List<Order> orders = serviceUtils.getSort(cb, root, options.sort);
//    // Default ordering
//    orders.add(cb.desc(root.get(LogItem_.createdTimestamp)));
//    // End Order
//
//    lcq.select(root).where(predicate).orderBy(orders);
//    Query query = em.createQuery(lcq);
//
//    List<LogItem> results;
//    if (options.size != -1) {
//      results = query.setFirstResult(options.size * options.index).setMaxResults(options.size).getResultList();
//      // End Results
//
//      // Total count results
//      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//      cq.select(cb.count(cq.from(LogItem.class)));
//      cq.where(predicate);
//      Long total = em.createQuery(cq).getSingleResult();
//      // End  Total count results
//
//      return new Page<>(results, total, options.index, options.size);
//    } else {
//      results = query.getResultList();
//      return new Page<>(results, results == null ? 0L : results.size(), options.index, options.size);
//    }
//  }
//
//  @Transactional
//  @Traced
//  public Page<LogItemDto> getLogItemsResponse(ToOptionsLogItem options) {
//    Page<LogItem> results = getLogItems(options);
//    return new Page<>(logMapper.toLogItemDto(results.content), results.total, options.index, options.size);
//  }

  @Traced
  @Transactional
  public void persistLogItem(Long itemId, Long userId, LogItem.Action action) {
    User user = new User();
    user.setId(userId);
    LogItem logItem = LogItem.builder()
        .itemId(itemId)
        .user(user)
        .action(action)
        .build();
    logItemRepository.persistAndFlush(logItem);
  }

  @Traced
  @Transactional
  public void persistLogItem(Long itemId, Long userId, LogItem.Action action, String comments) {
    User user = new User();
    user.setId(userId);
    LogItem logItem = LogItem.builder()
        .itemId(itemId)
        .user(user)
        .action(action)
        .comments(comments)
        .build();
    logItemRepository.persistAndFlush(logItem);
  }

//  @Traced
//  @Transactional
//  public Page<LogTransaction> getLogTransactions(ToOptionsLogTransaction options) {
//
//    CriteriaBuilder cb = em.getCriteriaBuilder();
//    // Results
//    CriteriaQuery<LogTransaction> lcq = cb.createQuery(LogTransaction.class);
//    Root<LogTransaction> root = lcq.from(LogTransaction.class);
//
//    // Filter
//    List<Predicate> predicates = new ArrayList<>();
//
//    predicates.add(cb.equal(root.get(LogTransaction_.cTransaction).get(CompoundTransaction_.id), options.transactionId));
//
//    ServiceUtils<LogTransaction> serviceUtils = new ServiceUtils<>();
//    Predicate predicate = serviceUtils.getAndPredicate(cb, predicates);
//    // End Filter
//
//    // Order
//    List<Order> orders = serviceUtils.getSort(cb, root, options.sort);
//    // Default ordering
//    orders.add(cb.desc(root.get(LogTransaction_.createdTimestamp)));
//    // End Order
//
//    lcq.select(root).where(predicate).orderBy(orders);
//    Query query = em.createQuery(lcq);
//
//    List<LogTransaction> results;
//    if (options.size != -1) {
//      results = query.setFirstResult(options.size * options.index).setMaxResults(options.size).getResultList();
//      // End Results
//
//      // Total count results
//      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//      cq.select(cb.count(cq.from(LogTransaction.class)));
//      cq.where(predicate);
//      Long total = em.createQuery(cq).getSingleResult();
//      // End  Total count results
//
//      return new Page<>(results, total, options.index, options.size);
//    } else {
//      results = query.getResultList();
//      return new Page<>(results, results == null ? 0L : results.size(), options.index, options.size);
//    }
//  }

//  @Transactional
//  @Traced
//  public Page<LogTransactionDto> getLogTransactionsResponse(ToOptionsLogTransaction options) {
//    Page<LogTransaction> results = getLogTransactions(options);
//    return new Page<>(logMapper.toLogTransactionDto(results.content), results.total, options.index, options.size);
//  }

////  @Traced
////  @Transactional
////  public void persistLogTransaction(Long transactionId, Long userId, LogTransaction.Action action) {
////    CompoundTransaction transaction = new CompoundTransaction();
////    transaction.setId(transactionId);
////    User user = new User();
////    user.setId(userId);
////    LogTransaction logTransaction = LogTransaction.builder()
////        .cTransaction(transaction)
////        .user(user)
////        .action(action)
////        .build();
////    logTransactionRepository.persistAndFlush(logTransaction);
////  }
//
//  @Traced
//  @Transactional
//  public void deleteByTransactionId(Long transactionId) {
//    logTransactionRepository.delete("cTransaction.id", transactionId);
//  }
//
//  @Traced
//  @Transactional
//  public Page<LogSyndesiKinitou> getLogSyndesiKinitous(ToOptionsLogItem options) {
//
//    CriteriaBuilder cb = em.getCriteriaBuilder();
//    // Results
//    CriteriaQuery<LogSyndesiKinitou> lcq = cb.createQuery(LogSyndesiKinitou.class);
//    Root<LogSyndesiKinitou> root = lcq.from(LogSyndesiKinitou.class);
//
//    // Filter
//    List<Predicate> predicates = new ArrayList<>();
//
//    predicates.add(cb.equal(root.get(LogSyndesiKinitou_.itemId), options.itemId));
//
//    ServiceUtils<LogSyndesiKinitou> serviceUtils = new ServiceUtils<>();
//    Predicate predicate = serviceUtils.getAndPredicate(cb, predicates);
//    // End Filter
//
//    // Order
//    List<Order> orders = serviceUtils.getSort(cb, root, options.sort);
//    // Default ordering
//    orders.add(cb.desc(root.get(LogSyndesiKinitou_.createdTimestamp)));
//    // End Order
//
//    lcq.select(root).where(predicate).orderBy(orders);
//    Query query = em.createQuery(lcq);
//
//    List<LogSyndesiKinitou> results;
//    if (options.size != -1) {
//      results = query.setFirstResult(options.size * options.index).setMaxResults(options.size).getResultList();
//      // End Results
//
//      // Total count results
//      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//      cq.select(cb.count(cq.from(LogSyndesiKinitou.class)));
//      cq.where(predicate);
//      Long total = em.createQuery(cq).getSingleResult();
//      // End  Total count results
//
//      return new Page<>(results, total, options.index, options.size);
//    } else {
//      results = query.getResultList();
//      return new Page<>(results, results == null ? 0L : results.size(), options.index, options.size);
//    }
//  }
//
//  @Transactional
//  @Traced
//  public Page<LogItemDto> getLogSyndesiKinitousResponse(ToOptionsLogItem options) {
//    Page<LogSyndesiKinitou> results = getLogSyndesiKinitous(options);
//    return new Page<>(logMapper.toLogSyndesiKinitouDto(results.content), results.total, options.index, options.size);
//  }

  @Traced
  @Transactional
  public void persistLogSyndesiKinitou(Long itemId, Long userId, LogSyndesiKinitou.Action action) {
    User user = new User();
    user.setId(userId);
    LogSyndesiKinitou logSyndesiKinitou = LogSyndesiKinitou.builder()
        .itemId(itemId)
        .user(user)
        .action(action)
        .build();
    logSyndesiKinitouRepository.persistAndFlush(logSyndesiKinitou);
  }

  @Traced
  @Transactional
  public void persistLogSyndesiKinitou(Long itemId, Long userId, LogSyndesiKinitou.Action action, String comments) {
    User user = new User();
    user.setId(userId);
    LogSyndesiKinitou logSyndesiKinitou = LogSyndesiKinitou.builder()
        .itemId(itemId)
        .user(user)
        .action(action)
        .comments(comments)
        .build();
    logSyndesiKinitouRepository.persistAndFlush(logSyndesiKinitou);
  }

}
