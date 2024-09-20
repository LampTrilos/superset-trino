//package gr.police.polseal.service;
//
//import gr.police.polseal.dto.OuDto;
//import gr.police.polseal.dto.mapper.OuMapper;
//import gr.police.polseal.exception.NotFoundAlertException;
//import gr.police.polseal.model.Ou;
//import gr.police.polseal.model.OuHist;
//import gr.police.polseal.repository.OuHistRepository;
//import gr.police.polseal.repository.OuRepository;
//import gr.police.polseal.restclients.OuDtoClient;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.eclipse.microprofile.opentracing.Traced;
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.persistence.EntityManager;
//import javax.transaction.Transactional;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@ApplicationScoped
//@RequiredArgsConstructor
//@Slf4j
//public class OuService {
//
//  private final EntityManager em;
//
//  private final OuMapper ouMapper;
//
//  private final OuRepository ouRepository;
//
//  private final OuHistRepository ouHistRepository;
//
//  @Transactional
//  @Traced
//  public OuDto getOu(Long id) {
//    return ouMapper.toOuDto(ouRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(Ou.class.getName())));
//  }
//
////  @Transactional
////  @Traced
////  public Page<OuDto> getOus(ToOptionsOu options) {
////    CriteriaBuilder cb = em.getCriteriaBuilder();
////    // Results
////    CriteriaQuery<Ou> lcq = cb.createQuery(Ou.class);
////    Root<Ou> root = lcq.from(Ou.class);
////
////    // Filter
////    List<Predicate> predicates = new ArrayList<>();
////
////    // the id
////    if (options.id != null) {
////      CriteriaBuilder.In<Long> inClause = cb.in(root.get(Ou_.id));
////      for (String item : options.id.list) {
////        inClause.value(Long.valueOf(item));
////      }
////      predicates.add(inClause);
////    }
////
////    if (options.name != null) {
////      predicates.add(cb.or(cb.like(cb.upper(root.get(Ou_.name)), "%" + options.name.toUpperCase() + "%"),
////          cb.like(cb.upper(root.get(Ou_.mitroo)), "%" + options.name.toUpperCase() + "%")));
////    }
////
////    ServiceUtils<Ou> serviceUtils = new ServiceUtils<>();
////    Predicate predicate = serviceUtils.getAndPredicate(cb, predicates);
////    // End Filter
////
////    // Order
////    List<Order> orders = serviceUtils.getSort(cb, root, options.sort);
////    // Default ordering
////    orders.add(cb.asc(root.get(Ou_.name)));
////    // End Order
////
////    lcq.select(root).where(predicate).orderBy(orders);
////    Query query = em.createQuery(lcq);
////
////    List<Ou> results;
////    if (options.size != -1) {
////      results = query.setFirstResult(options.size * options.index).setMaxResults(options.size).getResultList();
////      // End Results
////
////      // Total count results
////      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
////      cq.select(cb.count(cq.from(Ou.class)));
////      cq.where(predicate);
////      Long total = em.createQuery(cq).getSingleResult();
////      // End  Total count results
////
////      return new Page<>(ouMapper.toOuDto(results), total, options.index, options.size);
////    } else {
////      results = query.getResultList();
////      return new Page<>(ouMapper.toOuDto(results), results == null ? 0L : results.size(), options.index, options.size);
////    }
////  }
//
//
//  @Transactional
//  @Traced
//  public void persistOrUpdateOuFromRest(OuDtoClient ouDto) {
//    Long id = ouDto.getId_ypiresia();
//    Ou persistItem = ouRepository.findByIdOptional(id).orElse(new Ou());
//    boolean persist = true;
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:mm:ss'Z[UTC]'");
//    if (ouDto.getEnarxi() == null) {
//      return;
//    }
//    LocalDateTime enarxi = LocalDateTime.parse(ouDto.getEnarxi(), formatter);
//    if (persistItem.getId() == null) {
//      persist = true;
//    } else {
//      if (enarxi.isBefore(persistItem.getEnarxi())) {
//        persist = false;
//      }
//    }
//    if (persist) {
//      persistItem.setId(id);
//      persistItem.setIdHist(ouDto.getId());
//      persistItem.setName(ouDto.getTitlosPliris());
//      persistItem.setId_ad(ouDto.getId_ad());
//      persistItem.setId_energis(ouDto.getId_energis());
//      persistItem.setId_gad(ouDto.getId_gad());
//      persistItem.setTaxinomisi(ouDto.getTaxinomisi());
//      persistItem.setId_sfragidas(ouDto.getId_sfragidas());
//      persistItem.setId_ypiresia(ouDto.getId_ypiresia());
//      persistItem.setId_diakrisi_organotika(ouDto.getId_diakrisi_organotika());
//      persistItem.setTitlos(ouDto.getTitlos());
//      persistItem.setEnarxi(enarxi);
//      persistItem.setMitroo(ouDto.getMitroo());
//      LocalDateTime lixi = ouDto.getLixi() != null ? LocalDateTime.parse(ouDto.getLixi(), formatter) : null;
//      persistItem.setLixi(lixi);
//      ouRepository.persistAndFlush(persistItem);
//    }
//  }
//
//  @Transactional
//  @Traced
//  public void persistOrUpdateOuHistFromRest(OuDtoClient ouDto) {
//    Long id = ouDto.getId();
//    OuHist persistItem = ouHistRepository.findByIdOptional(id).orElse(new OuHist());
//    persistItem.setId(id);
//    persistItem.setTitlosPliris(ouDto.getTitlosPliris());
//    persistItem.setId_ad(ouDto.getId_ad());
//    persistItem.setId_energis(ouDto.getId_energis());
//    persistItem.setId_gad(ouDto.getId_gad());
//    persistItem.setTaxinomisi(ouDto.getTaxinomisi());
//    persistItem.setId_sfragidas(ouDto.getId_sfragidas());
//    persistItem.setId_ypiresia(ouDto.getId_ypiresia());
//    persistItem.setId_diakrisi_organotika(ouDto.getId_diakrisi_organotika());
//    persistItem.setTitlos(ouDto.getTitlos());
//    persistItem.setMitroo(ouDto.getMitroo());
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:mm:ss'Z[UTC]'");
//    LocalDateTime enarxi = ouDto.getEnarxi() != null ? LocalDateTime.parse(ouDto.getEnarxi(), formatter) : null;
//    persistItem.setEnarxi(enarxi);
//    LocalDateTime lixi = ouDto.getLixi() != null ? LocalDateTime.parse(ouDto.getLixi(), formatter) : null;
//    persistItem.setLixi(lixi);
//    ouHistRepository.persistAndFlush(persistItem);
//  }
//
////  @Transactional
////  @Traced
////  public Long getOuIdFromDepUnit(DepUnit depUnit) {
////    if (depUnit.getAmu() == null) {
////      log.error("getOuIdFromDepUnit depUnit: " + depUnit.getId() + " amu empty");
////      return null;
////    }
////    List<Ou> ous = ouRepository.find("mitroo = ?1 and lixi is null", depUnit.getAmu()).list();
////    if (ous.size() != 1) {
////      log.error("getOuIdFromDepUnit depUnit: " + depUnit.getId() + " ous.size: " + ous.size());
////      return null;
////    } else {
////      return ous.get(0).getId_energis();
////    }
////  }
////
//}
