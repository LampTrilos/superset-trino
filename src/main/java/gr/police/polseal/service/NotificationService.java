package gr.police.polseal.service;

import gr.police.polseal.dto.NotificationDto;
import gr.police.polseal.dto.UserDto;
import gr.police.polseal.dto.mapper.NotificationMapper;
import gr.police.polseal.dto.options.extensions.ToOptionsNotification;
import gr.police.polseal.dto.page.Page;
import gr.police.polseal.exception.NotFoundAlertException;
import gr.police.polseal.model.*;
import gr.police.polseal.model.permissions.Role;
import gr.police.polseal.repository.NotificationRepository;
import gr.police.polseal.repository.UserRepository;
import gr.police.polseal.service.mailer.Templates;
import gr.police.polseal.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationMapper notificationMapper;

  private final NotificationRepository notificationRepository;

  private final EntityManager em;

  //private final UserService userService;

  private final UserRepository userRepository;

  @Traced
  @Transactional
  public Page<Notification> getNotifications(ToOptionsNotification options) {

    CriteriaBuilder cb = em.getCriteriaBuilder();
    // Results
    CriteriaQuery<Notification> lcq = cb.createQuery(Notification.class);
    Root<Notification> root = lcq.from(Notification.class);

    // Filter
    List<Predicate> predicates = new ArrayList<>();

    predicates.add(cb.equal(root.get(Notification_.user).get(User_.id), options.userId));

    if (options.read != null) {
      predicates.add(cb.equal(root.get(Notification_.read), options.read));
    }

    ServiceUtils<Notification> serviceUtils = new ServiceUtils<>();
    Predicate predicate = serviceUtils.getAndPredicate(cb, predicates);
    // End Filter

    // Order
    List<Order> orders = serviceUtils.getSort(cb, root, options.sort);
    // Default ordering
    orders.add(cb.desc(root.get(Notification_.createdTimestamp)));
    // End Order

    lcq.select(root).where(predicate).orderBy(orders);
    Query query = em.createQuery(lcq);

    List<Notification> results;
    if (options.size != -1) {
      results = query.setFirstResult(options.size * options.index).setMaxResults(options.size).getResultList();
      // End Results

      // Total count results
      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      cq.select(cb.count(cq.from(Notification.class)));
      cq.where(predicate);
      Long total = em.createQuery(cq).getSingleResult();
      // End  Total count results

      return new Page<>(results, total, options.index, options.size);
    } else {
      results = query.getResultList();
      return new Page<>(results, results == null ? 0L : results.size(), options.index, options.size);
    }
  }

  @Transactional
  @Traced
  public Page<NotificationDto> getNotificationsResponse(ToOptionsNotification options) {
    Page<Notification> results = getNotifications(options);
    return new Page<>(notificationMapper.toNotificationDto(results.content), results.total, options.index, options.size);
  }

  @Transactional
  @Traced
  public void markNotificationAsRead(Long id) {
    Notification notification = notificationRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(Notification.class.getName()));
    notification.setRead(true);
  }

  @Transactional
  @Traced
  public void markNotificationAsUnread(Long id) {
    Notification notification = notificationRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(Notification.class.getName()));
    notification.setRead(false);
  }


//  @Traced
//  @Transactional
//  public void persistNotificationSignature(CompoundTransaction compTransaction, DepUnit depUnit, Role role) {
//    List<UserDto> users = userService.getUsersByDepUnitAndRole(depUnit.getId(), role).content.stream().map(userRoleDto -> userRoleDto.getUser()).collect(Collectors.toList());
//    for (UserDto userDto : users) {
//      Map<String, Object> attributes = new HashMap<>();
//      attributes.put("compTransactionUdc", compTransaction.getUdc());
//      attributes.put("compTransactionId", compTransaction.getId());
//      attributes.put("compTransactionType", compTransaction.getType().name());
//      User user = userRepository.findByIdOptional(Long.valueOf(userDto.getId())).orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
//      Notification notification = Notification.builder()
//          //.message("Υπηρεσία: " + depUnit.getName()  + ". Συναλλαγή Προς Υπογραφή: " + compTransaction.getUdc() + ". Κωδικός Επαλήθευσης: " + compTransaction.getSignatureVerifyCode())
//          .message("Υπηρεσία: " + depUnit.getName()  + ". Συναλλαγή Προς Υπογραφή: " + compTransaction.getUdc() + ".")
//          .type("TRANSACTION_SIGN")
//          .attributes(attributes)
//          .read(false)
//          .user(user)
//          .build();
//      notificationRepository.persist(notification);
//      Templates.sign_code(compTransaction.getSignatureVerifyCode(), compTransaction.getUdc())
//          .to(user.getEmail()).subject("Συναλλαγή προς υπογραφή: " + compTransaction.getUdc() + " " + compTransaction.getType().getDescription()).send()
//          .subscribeAsCompletionStage()
//          .thenApply(x -> Response.accepted().build());
//    }
//    notificationRepository.flush();
//  }

//  @Traced
//  @Transactional
//  public void persistNotificationSignature(CompoundTransaction compTransaction, DepUnit depUnit, Role role) {
//    List<UserDto> users = userService.getUsersByDepUnitAndRole(depUnit.getId(), role).content.stream().map(userRoleDto -> userRoleDto.getUser()).collect(Collectors.toList());
//    for (UserDto userDto : users) {
//      Map<String, Object> attributes = new HashMap<>();
//      attributes.put("compTransactionUdc", compTransaction.getUdc());
//      attributes.put("compTransactionId", compTransaction.getId());
//      attributes.put("compTransactionType", compTransaction.getType().name());
//      User user = userRepository.findByIdOptional(Long.valueOf(userDto.getId())).orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
//      Notification notification = Notification.builder()
//          //.message("Υπηρεσία: " + depUnit.getName()  + ". Συναλλαγή Προς Υπογραφή: " + compTransaction.getUdc() + ". Κωδικός Επαλήθευσης: " + compTransaction.getSignatureVerifyCode())
//          .message("Υπηρεσία: " + depUnit.getName()  + ". Συναλλαγή Προς Υπογραφή: " + compTransaction.getUdc() + ".")
//          .type("TRANSACTION_SIGN")
//          .attributes(attributes)
//          .read(false)
//          .user(user)
//          .build();
//      notificationRepository.persist(notification);
//      Templates.sign_code(compTransaction.getSignatureVerifyCode(), compTransaction.getUdc())
//          .to(user.getEmail()).subject("Συναλλαγή προς υπογραφή: " + compTransaction.getUdc() + " " + compTransaction.getType().getDescription()).send()
//          .subscribeAsCompletionStage()
//          .thenApply(x -> Response.accepted().build());
//    }
//    notificationRepository.flush();
//  }

//  @Traced
//  @Transactional
//  public void persistNotificationSignature(CompoundTransaction compTransaction, Long userId) {
//    Map<String, Object> attributes = new HashMap<>();
//    attributes.put("compTransactionUdc", compTransaction.getUdc());
//    attributes.put("compTransactionId", compTransaction.getId());
//    attributes.put("compTransactionType", compTransaction.getType().name());
//    User user = userRepository.findByIdOptional(userId).orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
//    Notification notification = Notification.builder()
//        //.message("Συναλλαγή Προς Υπογραφή. Συναλλαγή: " + compTransaction.getUdc() + " Κωδικός Επαλήθευσης: " + compTransaction.getSignatureVerifyCode())
//        .message("Συναλλαγή Προς Υπογραφή. Συναλλαγή: " + compTransaction.getUdc() + ".")
//        .type("TRANSACTION_SIGN")
//        .attributes(attributes)
//        .read(false)
//        .user(user)
//        .build();
//    notificationRepository.persistAndFlush(notification);
//    Templates.sign_code(compTransaction.getSignatureVerifyCode(), compTransaction.getUdc())
//        .to(user.getEmail()).subject("Συναλλαγή προς υπογραφή: " + compTransaction.getUdc() + " " + compTransaction.getType().getDescription()).send()
//        .subscribeAsCompletionStage()
//        .thenApply(x -> Response.accepted().build());
//  }
//
//  @Traced
//  @Transactional
//  public void persistNotificationSignature(CompoundTransaction compTransaction, Long userId, boolean myCompoundTransaction) {
//    Map<String, Object> attributes = new HashMap<>();
//    attributes.put("compTransactionUdc", compTransaction.getUdc());
//    attributes.put("compTransactionId", compTransaction.getId());
//    attributes.put("compTransactionType", compTransaction.getType().name());
//    attributes.put("myCompoundTransaction", myCompoundTransaction);
//    User user = userRepository.findByIdOptional(userId).orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
//    Notification notification = Notification.builder()
//        //.message("Συναλλαγή Προς Υπογραφή. Συναλλαγή: " + compTransaction.getUdc() + " Κωδικός Επαλήθευσης: " + compTransaction.getSignatureVerifyCode())
//        .message("Συναλλαγή Προς Υπογραφή. Συναλλαγή: " + compTransaction.getUdc() + ".")
//        .type("TRANSACTION_SIGN")
//        .attributes(attributes)
//        .read(false)
//        .user(user)
//        .build();
//    notificationRepository.persistAndFlush(notification);
//    Templates.sign_code(compTransaction.getSignatureVerifyCode(), compTransaction.getUdc())
//        .to(user.getEmail()).subject("Συναλλαγή προς υπογραφή: " + compTransaction.getUdc()  + " " + compTransaction.getType().getDescription()).send()
//        .subscribeAsCompletionStage()
//        .thenApply(x -> Response.accepted().build());
//  }

//  @Traced
//  @Transactional
//  public void persistNotificationItemCodeTicket(ItemCodeTicket itemCodeTicket) {
//    List<UserDto> users = userService.getUsersByDepUnitAndRole(null, Role.DIACHEIRISTIS_KODIKOLOGIOU).content.stream().map(userRoleDto -> userRoleDto.getUser()).collect(Collectors.toList());
//    log.info(users.size() + "");
//    for (UserDto userDto : users) {
//      Map<String, Object> attributes = new HashMap<>();
//      attributes.put("ticketId", itemCodeTicket.getId());
//      User user = new User();
//      user.setId(Long.valueOf(userDto.getId()));
//      Notification notification = Notification.builder()
//          .message("Αίτημα για νέο κωδικό από: " + itemCodeTicket.getUser().getLastName() + " " + itemCodeTicket.getUser().getFirstName())
//          .type("ITEM_CODE_TICKET")
//          .attributes(attributes)
//          .read(false)
//          .user(user)
//          .build();
//      notificationRepository.persist(notification);
//    }
//    notificationRepository.flush();
//  }

//  @Traced
//  @Transactional
//  public void persistNotificationTransactionCompleted(CompoundTransaction compTransaction, Long userId, boolean myCompoundTransaction) {
//    Map<String, Object> attributes = new HashMap<>();
//    attributes.put("compTransactionUdc", compTransaction.getUdc());
//    attributes.put("compTransactionId", compTransaction.getId());
//    attributes.put("compTransactionType", compTransaction.getType().name());
//    attributes.put("myCompoundTransaction", myCompoundTransaction);
//    User user = userRepository.findByIdOptional(userId).orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
//    Notification notification = Notification.builder()
//        //.message("Συναλλαγή Προς Υπογραφή. Συναλλαγή: " + compTransaction.getUdc() + " Κωδικός Επαλήθευσης: " + compTransaction.getSignatureVerifyCode())
//        .message("Συναλλαγή Ολοκληρώθηκε. Συναλλαγή: " + compTransaction.getUdc() + ".")
//        .type("TRANSACTION_COMPLETED")
//        .attributes(attributes)
//        .read(false)
//        .user(user)
//        .build();
//    notificationRepository.persistAndFlush(notification);
//    Templates.transaction_completed(compTransaction.getUdc())
//        .to(user.getEmail()).subject("Συναλλαγή Ολοκληρώθηκε: " + compTransaction.getUdc() + " " + compTransaction.getType().getDescription()).send()
//        .subscribeAsCompletionStage()
//        .thenApply(x -> Response.accepted().build());
//  }

}
