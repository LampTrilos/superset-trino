package gr.police.polseal.service;
//
//import com.google.gson.Gson;
//import gr.police.polseal.dto.EntityDto;
//import gr.police.polseal.dto.UserDto;
//import gr.police.polseal.dto.UserRoleDto;
//import gr.police.polseal.dto.mapper.UserMapper;
//import gr.police.polseal.dto.options.extensions.ToOptionsUser;
//import gr.police.polseal.dto.page.Page;
//import gr.police.polseal.dto.page.PageList;
//import gr.police.polseal.exception.BadRequestAlertException;
//import gr.police.polseal.exception.NotFoundAlertException;
//import gr.police.polseal.model.Ou;
//import gr.police.polseal.model.User;
//import gr.police.polseal.model.permissions.Role;
////import gr.police.polseal.model.permissions.UserRole;
//import gr.police.polseal.repository.OuRepository;
//import gr.police.polseal.repository.UserRepository;
////import gr.police.polseal.repository.UserRoleRepository;
//import gr.police.polseal.restclients.EmailDtoClient;
//import gr.police.polseal.restclients.ProsopikoDtoClient;
//import gr.police.polseal.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.eclipse.microprofile.opentracing.Traced;
//
import javax.enterprise.context.ApplicationScoped;
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
//import javax.persistence.criteria.*;
//import javax.transaction.Transactional;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class UserService {
//
//  private final EntityManager em;
//
//  private final UserMapper userMapper;
//
//  private final UserRepository userRepository;
//
//  //private final UserRoleRepository userRoleRepository;
//
////  private final DepUnitRepository depUnitRepository;
////
////  private final DepartmentService departmentService;
//
//  private final OuRepository ouRepository;
//
////  private final VathmosService vathmosService;
//


  //@Transactional
  //@Traced
//  public EntityDto updateUser(UserDto userDto) {
//    User user = userMapper.toUser(userDto);
//    if (user.getId() != null) {
//      User oldUser = userRepository.findByIdOptional(user.getId()).orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
//      if (oldUser.getPhone() == null || !oldUser.getPhone().equals(user.getPhone())) {
//        // TODO send sms to confirm mobile phone
//      }
//      oldUser.setPhone(user.getPhone());
//    }
//    return new EntityDto(user.getId(), user.getLastName());
//  }
//  @Transactional
//  @Traced
//  public UserDto getUser(Long id) {
//    return userMapper.toUserDto(userRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(User.class.getName())));
//  }
//
//  @Transactional
//  @Traced
//  public UserDto getMyUser(Long id) {
//    User user = userRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
//    UserDto userDto = userMapper.toUserDto(user);
////    userDto.setVathmosDescription(vathmosService.getVathmosDescription(user.getIdVathmou()));
//    return userDto;
//  }
//
////  @Transactional
////  @Traced
////  public Page<UserDto> getUsers(ToOptionsUser options) {
////    CriteriaBuilder cb = em.getCriteriaBuilder();
////    // Results
////    CriteriaQuery<User> lcq = cb.createQuery(User.class);
////    Root<User> root = lcq.from(User.class);
////
////    // Filter
////    List<Predicate> predicates = new ArrayList<Predicate>();
////
////    // the id
////    if (options.id != null) {
////      CriteriaBuilder.In<Long> inClause = cb.in(root.get(User_.id));
////      for (String item : options.id.list) {
////        inClause.value(Long.valueOf(item));
////      }
////      predicates.add(inClause);
////    }
//
//
////    if (options.lastName != null) {
////      predicates.add(cb.like(cb.upper(root.get(User_.lastName)), "%" + options.lastName.toUpperCase() + "%"));
////    }
////
////    if (options.firstName != null) {
////      predicates.add(cb.like(cb.upper(root.get(User_.firstName)), "%" + options.firstName.toUpperCase() + "%"));
////    }
////
////    /*    if (options.name != null) {
////      predicates.add(cb.or(cb.like(cb.upper(root.get(User_.lastName)), "%" + options.name.toUpperCase() + "%"),
////          cb.like(cb.upper(root.get(User_.firstName)), "%" + options.name.toUpperCase() + "%"),
////          cb.like(root.get(User_.arithmosMitroou).as(String.class), "%" + options.name + "%")));
////    }*/
////
////    if (options.name != null) {
////      predicates.add(cb.or(cb.equal(cb.upper(root.get(User_.arithmosMitroou)).as(String.class), options.name.toUpperCase())));
////    }
////
////    if (options.id == null && options.name == null) {
////      options.size = 0;
////    }
////
////    ServiceUtils<User> serviceUtils = new ServiceUtils<>();
////    Predicate predicate = serviceUtils.getAndPredicate(cb, predicates);
////    // End Filter
////
////    // Order
////    List<Order> orders = serviceUtils.getSort(cb, root, options.sort);
////    // Default ordering
////    orders.add(cb.asc(root.get(User_.lastName)));
////    // End Order
////
////    lcq.select(root).where(predicate).orderBy(orders);
////    Query query = em.createQuery(lcq);
////
////    List<User> results;
////    if (options.size != -1) {
////      results = query.setFirstResult(options.size * options.index).setMaxResults(options.size).getResultList();
////      // End Results
////
////      // Total count results
////      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
////      cq.select(cb.count(cq.from(User.class)));
////      cq.where(predicate);
////      Long total = em.createQuery(cq).getSingleResult();
////      // End  Total count results
////
////      return new Page<>(userMapper.toUserDto(results), total, options.index, options.size);
////    } else {
////      results = query.getResultList();
////      return new Page<>(userMapper.toUserDto(results), results == null ? 0L : results.size(), options.index, options.size);
////    }
////  }
//
//  @Transactional
//  @Traced
//  public Page<UserDto> getUsersByRole(ToOptionsUser options) {
//    CriteriaBuilder cb = em.getCriteriaBuilder();
//    // Results
//    CriteriaQuery<UserRole> lcq = cb.createQuery(UserRole.class);
//    Root<UserRole> root = lcq.from(UserRole.class);
//
//    // Filter
//    List<Predicate> predicates = new ArrayList<Predicate>();
////    if (options.role != null) {
////      predicates.add(cb.equal(root.get(UserRole_.role), Role.valueOf(options.role)));
////    }
////
////    if (options.ouId != null) {
////      predicates.add(cb.equal(root.get(UserRole_.ou).get(Ou_.id), options.ouId));
////    }
////
////    if (options.depUnitId != null) {
////      predicates.add(cb.equal(root.get(UserRole_.depUnit).get(DepUnit_.id), options.depUnitId));
////    }
//
//    ServiceUtils<UserRole> serviceUtils = new ServiceUtils<>();
//    Predicate predicate = serviceUtils.getAndPredicate(cb, predicates);
//    // End Filter
//
//    // Order
//    List<Order> orders = serviceUtils.getSort(cb, root, options.sort);
//    // Default ordering
//    //orders.add(cb.asc(root.get(UserRole_.lastName)));
//    // End Order
//
//    lcq.select(root).where(predicate).orderBy(orders);
//    Query query = em.createQuery(lcq);
//
//    List<UserRole> results;
//    if (options.size != -1) {
//      results = query.setFirstResult(options.size * options.index).setMaxResults(options.size).getResultList();
//      // End Results
//
//      // Total count results
//      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//      cq.select(cb.count(cq.from(UserRole.class)));
//      cq.where(predicate);
//      Long total = em.createQuery(cq).getSingleResult();
//      // End  Total count results
//
//      return new Page<>(userMapper.toUserDto(results.stream().map(userRole -> userRole.getUser()).collect(Collectors.toList())), total, options.index, options.size);
//    } else {
//      results = query.getResultList();
//      return new Page<>(userMapper.toUserDto(results.stream().map(userRole -> userRole.getUser()).collect(Collectors.toList())), results == null ? 0L : results.size(), options.index, options.size);
//    }
//  }
//
//  @Transactional
//  @Traced
//  public EntityDto persistOrUpdateUser(ProsopikoDtoClient item) {
//    User persistItem = userRepository.findByIdOptional(item.idProsopiko).orElse(new User());
//    persistItem.setId(item.idProsopiko);
//    persistItem.setUsername(String.valueOf(item.idProsopiko));
//    persistItem.setLastName(item.eponymo);
//    persistItem.setFirstName(item.onoma);
//    persistItem.setApolitiIerarchia(item.apolitiIerarchia);
//    persistItem.setArithmosMitroou(item.arithmosMitroou);
//    persistItem.setIdApospasis(item.idApospasis);
//    persistItem.setIdOrganikis(item.idOrganikis);
//    persistItem.setIdParousias(item.idParousias);
//    persistItem.setIdVathmou(item.idVathmou);
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:mm:ss'Z[UTC]'");
//    LocalDate localDate = item.imerominiaGennisis != null ? LocalDate.parse(item.imerominiaGennisis, formatter) : null;
//    persistItem.setImerominiaGennisis(localDate);
//    persistItem.setKatigoriaProsopikoy(item.katigoriaProsopikoy);
//    persistItem.setMitroo(item.mitroo);
//    persistItem.setPatronymo(item.patronymo);
//    // emails are sync by another service
//    persistItem.setEmail(persistItem.getEmail() != null ? persistItem.getEmail() : item.idProsopiko + "@astynomia.gr");
//    userRepository.persistAndFlush(persistItem);
//    return new EntityDto(persistItem.getId(), persistItem.getLastName());
//  }
//
//  @Transactional
//  @Traced
//  public void persistOrUpdateUserEmailFromRest(EmailDtoClient item) {
//    User persistItem = userRepository.findByIdOptional(item.idProsopiko).orElse(null);
//    if (persistItem == null) {
//      log.info("User not found: " + new Gson().toJson(item));
//      return;
//    }
//    persistItem.setEmail(item.account);
//    userRepository.persistAndFlush(persistItem);
//  }
//
//  @Transactional
//  @Traced
//  public EntityDto updateUser(UserDto userDto) {
//    User user = userMapper.toUser(userDto);
//    if (user.getId() != null) {
//      User oldUser = userRepository.findByIdOptional(user.getId()).orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
//      if (oldUser.getPhone() == null || !oldUser.getPhone().equals(user.getPhone())) {
//        // TODO send sms to confirm mobile phone
//      }
//      oldUser.setPhone(user.getPhone());
//    }
//    return new EntityDto(user.getId(), user.getLastName());
//  }
//
//  @Transactional
//  @Traced
//  public void deleteUser(Long id) {
//    userRepository.findByIdOptional(id).ifPresent(user -> {
//      //user.delete();
//    });
//  }
//
//  @Transactional
//  @Traced
//  public PageList<UserRoleDto> getUsersByOuAndRole(Long oudId, Role role, Long depUnitId) {
//    List<UserRole> roles = oudId == null ? userRoleRepository.list("role = ?1 and depUnit.id = ?2", role, depUnitId) : userRoleRepository.list("ou.id = ?1 and role = ?2 and depUnit.id = ?3", oudId, role, depUnitId);
//    return new PageList<>(roles.stream().map(rl -> userMapper.toUserRoleDto(rl)).collect(Collectors.toList()));
//  }
//
//  @Transactional
//  @Traced
//  public PageList<UserRoleDto> getUsersByDepUnitAndRole(Long depUnitId, Role role) {
//    List<UserRole> roles = depUnitId == null ? userRoleRepository.list("role = ?1", role) : userRoleRepository.list("depUnit.id = ?1 and role = ?2", depUnitId, role);
//    return new PageList<>(roles.stream().map(rl -> userMapper.toUserRoleDto(rl)).collect(Collectors.toList()));
//  }
//
////  @Transactional
////  @Traced
////  public PageList<UserRoleDto> getUsersByManageDepUnitAndRole(Long depUnitId, Role role) {
////    List<Long> managedDepUnits = departmentService.getManagedDepUnits(depUnitId);
////    List<UserRole> roles = depUnitId == null ? userRoleRepository.list("role = ?1", role) : userRoleRepository.list("depUnit.id in ?1 and role = ?2", managedDepUnits, role);
////    return new PageList<>(roles.stream().map(rl -> userMapper.toUserRoleDto(rl)).collect(Collectors.toList()));
////  }
//
//  @Transactional
//  @Traced
//  public void persistUserToDepUnitAndRole(Long userId, Long depUnitId, Role role, Long ouId) {
//    if (!role.equals(Role.TOPIKOS_YPEYTHYNOS)) {
//      ouId = null;
//    }
//    if (depUnitId == null && (!role.equals(Role.SUPER_ADMIN) && !role.equals(Role.DIACHEIRISTIS_KODIKOLOGIOU))) {
//      throw new BadRequestAlertException("Ο συγκεκριμένος ρόλος πρέπει να ανήκει σε διαχείριση");
//    }
//    if (ouId == null && role.equals(Role.TOPIKOS_YPEYTHYNOS)) {
//      throw new BadRequestAlertException("Ο συγκεκριμένος ρόλος πρέπει να ανήκει σε υπηρεσία");
//    }
//    if (depUnitId != null ? (ouId != null ? userRoleRepository.find("user.id = ?1 and role = ?2 and ou.id = ?3", userId, role, ouId).firstResultOptional().isPresent()
//        : userRoleRepository.find("user.id = ?1 and depUnit.id = ?2 and role = ?3", userId, depUnitId, role).firstResultOptional().isPresent())
//        : userRoleRepository.find("user.id = ?1 and role = ?2", userId, role).firstResultOptional().isPresent()) {
//      throw new BadRequestAlertException("Ο χρήστης έχει ήδη τον συγκεκριμένο ρόλο");
//    }
//    // TODO αλληλοαποκλειομενοι ρόλοι
//    if (depUnitId != null) {
//      if (userRoleRepository.find("user.id = ?1 and role = ?2", userId, Role.TOPIKOS_YPEYTHYNOS).firstResultOptional().isPresent()) {
//        throw new BadRequestAlertException("Ο χρήστης είναι τοπικός υπεύθυνος και δεν μπορεί να ορισθεί εκ νέου");
//      }
//    }
//    if (ouId != null) {
//      if (userRoleRepository.find("user.id = ?1 and depUnit.id is not null", userId).firstResultOptional().isPresent()) {
//        throw new BadRequestAlertException("Ο χρήστης είναι σε διαχείριση και δεν μπορεί να οριστεί τοπικός υπεύθυνος");
//      }
//    }
//    UserRole userRole = UserRole.builder()
//        .user(userRepository.findByIdOptional(userId).orElseThrow(() -> new NotFoundAlertException(User.class.getName())))
////        .depUnit(depUnitId == null ? null : depUnitRepository.findByIdOptional(depUnitId).orElseThrow(() -> new NotFoundAlertException(DepUnit.class.getName())))
//        .ou(ouId == null ? null : ouRepository.findByIdOptional(ouId).orElseThrow(() -> new NotFoundAlertException(Ou.class.getName())))
//        .role(role)
//        .allDepUnits(depUnitId == null ? true : null)
//        .build();
//    userRoleRepository.persistAndFlush(userRole);
//  }
//
//  @Transactional
//  @Traced
//  public void removeUserRoleFromDepUnit(Long userId, Long depUnitId, Role role, Long ouId) {
//    if (depUnitId == null) {
//      if (ouId != null) {
//        userRoleRepository.delete("user.id = ?1 and role = ?2 and ou.id = ?3", userId, role, ouId);
//      } else {
//        userRoleRepository.delete("user.id = ?1 and role = ?2", userId, role);
//      }
//    } else {
//      userRoleRepository.delete("user.id = ?1 and depUnit.id = ?2 and role = ?3", userId, depUnitId, role);
//    }
//  }
//
//  @Transactional
//  @Traced
//  public boolean harUserRoleInDepUnit(Long userId, Long depUnitId, Role role) {
//    return userRoleRepository.count("user.id = ?1 and depUnit.id = ?2 and role = ?3", userId, depUnitId, role) > 0;
//  }
//
//  @Transactional
//  @Traced
//  public boolean isTopikosInDepUnitAndOu(Long userId, Long depUnitId, Long ouId) {
//    return userRoleRepository.count("user.id = ?1 and depUnit.id = ?2 and ou.id = ?3 and role = 'TOPIKOS_YPEYTHYNOS'", userId, depUnitId, ouId) > 0;
//  }
//
}
