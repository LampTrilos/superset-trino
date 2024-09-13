package gr.police.polseal.service;

import gr.police.polseal.dto.options.ToOptions;
import gr.police.polseal.dto.page.Page;
import gr.police.polseal.exception.NotFoundAlertException;
import gr.police.polseal.model.Vathmos;
//import gr.police.polseal.model.Vathmos_;
import gr.police.polseal.restclients.VathmosDtoClient;
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

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class VathmosService {

  private final EntityManager em;


  @Transactional
  @Traced
  public Page<Vathmos> getVathmos(ToOptions options) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    // Results
    CriteriaQuery<Vathmos> lcq = cb.createQuery(Vathmos.class);
    Root<Vathmos> root = lcq.from(Vathmos.class);
    List<Predicate> predicates = new ArrayList<Predicate>();

    ServiceUtils<Vathmos> serviceUtils = new ServiceUtils<>();
    Predicate predicate = serviceUtils.getAndPredicate(cb, predicates);
    // End Filter
    // Order
    List<Order> orders = serviceUtils.getSort(cb, root, options.sort);
    // Default ordering
    //orders.add(cb.asc(root.get(Vathmos_.id)));
    // End Order
    lcq.select(root).where(predicate).orderBy(orders);
    Query query = em.createQuery(lcq);
    List<Vathmos> results;
    if (options.size != -1) {
      results = query.setFirstResult(options.size * options.index).setMaxResults(options.size).getResultList();
      // End Results
      // Total count results
      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      cq.select(cb.count(cq.from(Vathmos.class)));
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
  public Vathmos getVathmos(Long id) {
    return (Vathmos) Vathmos.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(Vathmos.class.getName()));
  }

  @Transactional
  @Traced
  public String getVathmosDescription(Long id) {
    if (id == null) {
      return null;
    }
    Vathmos vathmos = (Vathmos) Vathmos.findByIdOptional(id).orElse(null);
    return vathmos == null ? null : vathmos.getVathmos();
  }

  @Transactional
  @Traced
  public void persistOrUpdateVathmosFromRest(VathmosDtoClient vathmosDto) {
    Vathmos persistItem = (Vathmos) Vathmos.findByIdOptional(vathmosDto.getId()).orElse(new Vathmos());
    persistItem.setId(vathmosDto.getId());
    persistItem.setVathmos(vathmosDto.getVathmos());
    persistItem.setGroupVathmos(vathmosDto.getGroupVathmos());
    persistItem.setEnergos(vathmosDto.getEnergos());
    persistItem.setSntm(vathmosDto.getSntm());
    persistItem.setIerarchia(vathmosDto.getIerarchia());
    persistItem.persistAndFlush();
  }

}
