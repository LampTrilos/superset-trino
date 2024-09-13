package gr.police.polseal.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import eu.ubitech.bitt.core.domain.storage.service.StorageService;
import gr.police.polseal.dto.*;
import gr.police.polseal.dto.options.extensions.ToOptionsCompoundTransaction;
import gr.police.polseal.dto.page.Page;
import gr.police.polseal.dto.page.PageList;
import gr.police.polseal.exception.BadRequestAlertException;
import gr.police.polseal.exception.ErrorMessage;
import gr.police.polseal.exception.InternalServerErrorException;
import gr.police.polseal.exception.NotFoundAlertException;
import gr.police.polseal.model.*;
import gr.police.polseal.model.logs.LogTransaction;
import gr.police.polseal.model.permissions.Role;
import gr.police.polseal.model.projections.ItemPr;
import gr.police.polseal.repository.*;
import gr.police.polseal.service.utils.PDFUtils;
import gr.police.polseal.service.utils.ServiceUtils;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static gr.police.polseal.service.utils.GeneralUtils.transformCodeWithLeadingZeros;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

  private final EntityManager em;

//  private final TransactionMapper transactionMapper;

//  private final CompoundTransactionRepository compoundTransactionRepository;
//
//  private final CompoundTransactionFileRepository compoundTransactionFileRepository;
//
//  private final ITransactionRepository iTransactionRepository;
//
//  private final GenAccountBalanceRepository genAccountBalanceRepository;
//
//  private final ParAccountBalanceRepository parAccountBalanceRepository;
//
//  private final UserAccountBalanceRepository userAccountBalanceRepository;
//
//  private final UserDepUnitAccountBalanceRepository userDepUnitAccountBalanceRepository;
//
//  private final OuManagerAccountBalanceRepository ouManagerAccountBalanceRepository;
//
//  private final OuAccountBalanceRepository ouAccountBalanceRepository;
//
//  private final BalanceService balanceService;
//
//  private final ItemCodeRepository itemCodeRepository;
//
//  private final DepUnitRepository depUnitRepository;

  private final StorageService storageService;

//  private final ItemRepository itemRepository;
//
//  private final ItemService itemService;

  private final NotificationService notificationService;

//  private final GenDepUnitRepository genDepUnitRepository;
//
//  private final ParDepUnitRepository parDepUnitRepository;

  private final UserRepository userRepository;

  //private final PermissionService permissionService;

//  private final DepartmentService departmentService;

  private final LogService logService;

//  private final VathmosService vathmosService;

  //private final UserRoleRepository userRoleRepository;

//  private final AssetPliroforikiRepository assetPliroforikiRepository;
//
//  private final GunRepository gunRepository;
//
//  private final AvlRepository avlRepository;
//
//  private final AvlPdaRepository avlPdaRepository;
//
//  private final AntivalistikoRepository antivalistikoRepository;
//
//  private final KeraiaRepository keraiaRepository;
//
//  private final KinitoRepository kinitoRepository;
//
//  private final ParelkomenoPompodektiRepository parelkomenoPompodektiRepository;
//
//  private final PompodektisRepository pompodektisRepository;
//
//  private final OchimaRepository ochimaRepository;
//
//  private final RouxismosRepository rouxismosRepository;
//
//  private final ItemCodeMapper itemCodeMapper;
//
//  private final ItemFileRepository itemFileRepository;
//
//  private final SyndesiKinitouRepository syndesiKinitouRepository;

  //private final UserService userService;

//  private final OchimaTransactionRepository ochimaTransactionRepository;
//
//  private final PliroforikiTransactionService pliroforikiTransactionService;
//
//  private final OchimaTransactionService ochimaTransactionService;

  @ConfigProperty(name = "mode.not.verify.code", defaultValue = "false")
  boolean modeNotVerifyCode;

  @Inject
  SigningService signingservice;


//  @Transactional
//  @Traced
//  public void sendCompoundTransactionForSign(Long id, Long userId) {
//    CompoundTransaction compTransaction = compoundTransactionRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    Long inTransactionId = compTransaction.getParentTransaction() == null ? compTransaction.getId() : compTransaction.getParentTransaction().getId();
//    compTransaction.setStatus(CompoundTransactionStatus.FOR_SIGNATURE);
//    LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);
//    compTransaction.setSendForSignTimestamp(timestamp);
//    compTransaction.setSignatureVerifyCode(generateRandomString());
//    compoundTransactionRepository.persist(compTransaction);
//    logService.persistLogTransaction(id, userId, LogTransaction.Action.FOR_SIGN);
//    // TODO send email and sms
//    List<ITransaction> itransactions = iTransactionRepository.list("cTransaction.id = ?1 and groupParentTransaction.id IS NULL", id);
//    if (compTransaction.getType().equals(TransactionType.T00)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      persistCompoundTransactionFromT00(compTransaction);
//    }
//    if (compTransaction.getType().equals(TransactionType.T01)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      for (ITransaction itransaction : itransactions) {
//        GenAccountBalance genAccountBalance = genAccountBalanceRepository.find("genDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        genAccountBalance.setTransactionsBalance(genAccountBalance.getTransactionsBalance().add(itransaction.getAmount()));
//        genAccountBalance.setAvailableBalance(genAccountBalance.getAvailableBalance().subtract(itransaction.getAmount()));
//        if (genAccountBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }


//  @Transactional
//  @Traced
//  public CompoundTransactionResDto getCompoundTransaction(Long id) {
//    CompoundTransactionResDto compoundTransactionResDto = transactionMapper.toCompoundTransactionDto(compoundTransactionRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName())));
//    compoundTransactionResDto.setItransactions(new ArrayList<>());
//    compoundTransactionResDto.getItransactions().addAll(
//        transactionMapper.toITransactionDto(iTransactionRepository.list("cTransaction.id = ?1 and groupParentTransaction.id IS NULL", id)));
//    for (ITransactionResDto transactionResDto : compoundTransactionResDto.getItransactions()) {
//      populateItem(transactionResDto, compoundTransactionResDto.getStatus().equals(CompoundTransactionStatus.DRAFT));
//    }
//    for (ITransactionResDto transactionResDto : compoundTransactionResDto.getItransactions().stream().filter(tr -> Boolean.TRUE.equals(tr.getIsGroupParent())).collect(Collectors.toList())) {
//      transactionResDto.setChildrenTransactions(new ArrayList<>());
//      transactionResDto.getChildrenTransactions().addAll(
//          transactionMapper.toITransactionDto(iTransactionRepository.list("groupParentTransaction.id = ?1", Long.valueOf(transactionResDto.getId()))));
//      for (ITransactionResDto chiltransactionResDto : transactionResDto.getChildrenTransactions()) {
//        populateItem(chiltransactionResDto, compoundTransactionResDto.getStatus().equals(CompoundTransactionStatus.DRAFT));
//      }
//    }
//    /* Αν περιέχει οχήματα γέμισε τη συνναλαγή με επιπλέον στοιχεία για το πληροφορικό σύστημα οχήματα*/
//    Optional<OchimaTransaction> ochimaTransaction = ochimaTransactionRepository.find("cTransaction.id", id).firstResultOptional();
//    if (ochimaTransaction.isPresent()) {
//      OchimaTransaction och = ochimaTransaction.get();
//      compoundTransactionResDto.setChangeDecision(och.getChangeDecision());
//      compoundTransactionResDto.setChangeDecisionSubmitDate(och.getChangeDecisionSubmitDate());
//      compoundTransactionResDto.setCompletionDecision(och.getCompletionDecision());
//      compoundTransactionResDto.setCompletionDecisionSubmitDate(och.getCompletionDecisionSubmitDate());
//    }
//    return compoundTransactionResDto;
//  }
//
//  private void populateItem(ITransactionResDto transactionResDto, boolean fullItemJson) {
//    Gson gson = new GsonBuilder()
//        .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (localDate, type, jsonSerializationContext) -> new JsonPrimitive(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)))
//        .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (localDateTime, type, jsonSerializationContext) -> new JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
//        .create();
//    if (transactionResDto.getItemId() != null) {
//      if (fullItemJson) {
//        String json = null;
//        if (transactionResDto.getItemCodeEntity().getTemplate().getItemClass().equals(ItemClass.ASSETPLIROFORIKI)) {
//          AssetPliroforiki itemProjection = assetPliroforikiRepository.findEntityById(transactionResDto.getItemId()).orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//          json = gson.toJson(itemProjection);
//        } else if (transactionResDto.getItemCodeEntity().getTemplate().getItemClass().equals(ItemClass.GUN)) {
//          Gun itemProjection = gunRepository.findEntityById(transactionResDto.getItemId()).orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//          json = gson.toJson(itemProjection);
//        } else if (transactionResDto.getItemCodeEntity().getTemplate().getItemClass().equals(ItemClass.AVL)) {
//          Avl itemProjection = avlRepository.findEntityById(transactionResDto.getItemId()).orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//          json = gson.toJson(itemProjection);
//        } else if (transactionResDto.getItemCodeEntity().getTemplate().getItemClass().equals(ItemClass.KERAIA)) {
//          Keraia itemProjection = keraiaRepository.findEntityById(transactionResDto.getItemId()).orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//          json = gson.toJson(itemProjection);
//        } else if (transactionResDto.getItemCodeEntity().getTemplate().getItemClass().equals(ItemClass.AVLPDA)) {
//          AvlPda itemProjection = avlPdaRepository.findEntityById(transactionResDto.getItemId()).orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//          json = gson.toJson(itemProjection);
//        } else if (transactionResDto.getItemCodeEntity().getTemplate().getItemClass().equals(ItemClass.KINITO)) {
//          Kinito itemProjection = kinitoRepository.findEntityById(transactionResDto.getItemId()).orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//          json = gson.toJson(itemProjection);
//        } else if (transactionResDto.getItemCodeEntity().getTemplate().getItemClass().equals(ItemClass.ANTIVALISTIKO)) {
//          Antivalistiko itemProjection = antivalistikoRepository.findEntityById(transactionResDto.getItemId()).orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//          json = gson.toJson(itemProjection);
//        } else if (transactionResDto.getItemCodeEntity().getTemplate().getItemClass().equals(ItemClass.POMPODEKTIS)) {
//          Pompodektis itemProjection = pompodektisRepository.findEntityById(transactionResDto.getItemId()).orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//          json = gson.toJson(itemProjection);
//        } else if (transactionResDto.getItemCodeEntity().getTemplate().getItemClass().equals(ItemClass.PARELKOMENOPOMPODEKTI)) {
//          ParelkomenoPompodekti itemProjection = parelkomenoPompodektiRepository.findEntityById(transactionResDto.getItemId()).orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//          json = gson.toJson(itemProjection);
//        } else if (transactionResDto.getItemCodeEntity().getTemplate().getItemClass().equals(ItemClass.OCHIMA)) {
//          Ochima itemProjection = ochimaRepository.findEntityById(transactionResDto.getItemId()).orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//          json = gson.toJson(itemProjection);
//        } else if (transactionResDto.getItemCodeEntity().getTemplate().getItemClass().equals(ItemClass.ROUXISMOS)) {
//          Rouxismos itemProjection = rouxismosRepository.findEntityById(transactionResDto.getItemId()).orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//          json = gson.toJson(itemProjection);
//        } else if (transactionResDto.getItemCodeEntity().getTemplate().getItemClass().equals(ItemClass.ΙΤΕΜ)) {
//          ItemPr itemProjection = itemRepository.find("select i.id, i.serialNumber from Item i where i.id = ?1", transactionResDto.getItemId()).project(ItemPr.class).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//          json = gson.toJson(itemProjection);
//        } else {
//          throw new BadRequestAlertException("Ο τυπος υλικού δεν υπαέρχει");
//        }
//        Map<String, Object> result = gson.fromJson(json, Map.class);
//        transactionResDto.setItemJson(result);
//      } else {
//        ItemPr itemProjection = itemRepository.find("select i.id, i.serialNumber from Item i where i.id = ?1", transactionResDto.getItemId()).project(ItemPr.class).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(Item.class.getName()));
//        String json = gson.toJson(itemProjection);
//        Map<String, Object> result = gson.fromJson(json, Map.class);
//        transactionResDto.setItemJson(result);
//      }
//    }
//  }
//
//  @Transactional
//  @Traced
//  public Page<CompoundTransactionResDto> getCompoundTransactions(ToOptionsCompoundTransaction options) {
//    CriteriaBuilder cb = em.getCriteriaBuilder();
//    // Results
//    CriteriaQuery<CompoundTransaction> lcq = cb.createQuery(CompoundTransaction.class);
//    Root<CompoundTransaction> root = lcq.from(CompoundTransaction.class);
//    root.fetch(CompoundTransaction_.fromAccountEntity, JoinType.LEFT);
//    root.fetch(CompoundTransaction_.toAccountEntity, JoinType.LEFT);
//    // Filter
//    List<Predicate> predicates = new ArrayList<Predicate>();
//
//    if (options.udc != null) {
//      predicates.add(cb.like(cb.upper(root.get(CompoundTransaction_.udc)), "%" + options.udc.toUpperCase() + "%"));
//    }
//
//    if (options.transactionType != null) {
//      CriteriaBuilder.In<TransactionType> inClause = cb.in(root.get(CompoundTransaction_.type));
//      for (String item : options.transactionType.list) {
//        inClause.value(TransactionType.valueOf(item));
//      }
//      predicates.add(inClause);
//    }
//
//    if (options.fromAccount != null) {
//      CriteriaBuilder.In<Long> inClause = cb.in(root.get(CompoundTransaction_.fromAccountEntity).get(DepUnit_.id));
//      for (String item : options.fromAccount.list) {
//        inClause.value(Long.valueOf(item));
//      }
//      predicates.add(inClause);
//    }
//
//    if (options.toAccount != null) {
//      CriteriaBuilder.In<Long> inClause = cb.in(root.get(CompoundTransaction_.toAccountEntity).get(DepUnit_.id));
//      for (String item : options.toAccount.list) {
//        inClause.value(Long.valueOf(item));
//      }
//      predicates.add(inClause);
//    }
//
//    if (options.user != null) {
//      CriteriaBuilder.In<Long> toInClause = cb.in(root.get(CompoundTransaction_.toUser).get(User_.id));
//      for (String item : options.user.list) {
//        toInClause.value(Long.valueOf(item));
//      }
//      CriteriaBuilder.In<Long> fromInClause = cb.in(root.get(CompoundTransaction_.fromUser).get(User_.id));
//      for (String item : options.user.list) {
//        fromInClause.value(Long.valueOf(item));
//      }
//      predicates.add(cb.or(toInClause, fromInClause));
//    }
//
//    if (options.dateFrom != null) {
//      predicates.add(cb.greaterThanOrEqualTo(root.get(CompoundTransaction_.actualDate), options.dateFrom));
//    }
//
//    if (options.dateTo != null) {
//      predicates.add(cb.lessThanOrEqualTo(root.get(CompoundTransaction_.actualDate), options.dateTo));
//    }
//
//    if (options.fromAccountOrToAccount != null) {
//      CriteriaBuilder.In<Long> toInClause = cb.in(root.get(CompoundTransaction_.toAccountEntity).get(DepUnit_.id));
//      for (String item : options.fromAccountOrToAccount.list) {
//        toInClause.value(Long.valueOf(item));
//      }
//      CriteriaBuilder.In<Long> fromInClause = cb.in(root.get(CompoundTransaction_.fromAccountEntity).get(DepUnit_.id));
//      for (String item : options.fromAccountOrToAccount.list) {
//        fromInClause.value(Long.valueOf(item));
//      }
//      predicates.add(cb.or(toInClause, fromInClause));
//    }
//
//    if (options.fromOuOrToOu != null) {
//      CriteriaBuilder.In<Long> toInClause = cb.in(root.get(CompoundTransaction_.toOu).get(Ou_.id));
//      for (String item : options.fromOuOrToOu.list) {
//        toInClause.value(Long.valueOf(item));
//      }
//      CriteriaBuilder.In<Long> fromInClause = cb.in(root.get(CompoundTransaction_.fromOu).get(Ou_.id));
//      for (String item : options.fromOuOrToOu.list) {
//        fromInClause.value(Long.valueOf(item));
//      }
//      predicates.add(cb.or(toInClause, fromInClause));
//    }
//
//    if (options.managedAccounts != null) {
//      CriteriaBuilder.In<Long> toInClause = cb.in(root.get(CompoundTransaction_.toAccountEntity).get(DepUnit_.id));
//      for (String item : options.managedAccounts.list) {
//        toInClause.value(Long.valueOf(item));
//      }
//      CriteriaBuilder.In<Long> fromInClause = cb.in(root.get(CompoundTransaction_.fromAccountEntity).get(DepUnit_.id));
//      for (String item : options.managedAccounts.list) {
//        fromInClause.value(Long.valueOf(item));
//      }
//      predicates.add(cb.or(toInClause, fromInClause));
//    }
//
//    if (options.toOrFromOu != null) {
//      predicates.add(cb.or(cb.isNotNull(root.get(CompoundTransaction_.toOu)), cb.isNotNull(root.get(CompoundTransaction_.fromOu))));
//    }
//
//    if (options.status != null) {
//      if (options.status.equals(CompoundTransactionStatus.REJECTED)) {
//        predicates.add(cb.or(cb.equal(root.get(CompoundTransaction_.status), CompoundTransactionStatus.REJECTED), cb.equal(root.get(CompoundTransaction_.status), CompoundTransactionStatus.FAILED)));
//      } else {
//        predicates.add(cb.equal(root.get(CompoundTransaction_.status), options.status));
//      }
//    } else if (options.pending != null && options.pending) {
//      if (options.roles.contains(Role.SUPER_ADMIN)) {
//        predicates.add(cb.or(cb.equal(root.get(CompoundTransaction_.status), CompoundTransactionStatus.FOR_SIGNATURE)));
//      } else if (options.roles.contains(Role.TOPIKOS_YPEYTHYNOS)) {
//        predicates.add(cb.or(cb.equal(root.get(CompoundTransaction_.status), CompoundTransactionStatus.FOR_SIGNATURE)));
//      } else {
//        predicates.add(cb.or(cb.equal(root.get(CompoundTransaction_.status), CompoundTransactionStatus.FOR_SIGNATURE)
//        ));
//      }
//    } else {
//      predicates.add(cb.equal(root.get(CompoundTransaction_.status), CompoundTransactionStatus.COMPLETED));
//    }
//
//    ServiceUtils<CompoundTransaction> serviceUtils = new ServiceUtils<>();
//    if (options.status != null && options.status.equals(CompoundTransactionStatus.FOR_SIGNATURE)) {
//      List<Predicate> predicatesSignature = new ArrayList<Predicate>();
//      if (options.roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU)) {
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T00), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T01), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T03), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T04), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0), cb.equal(root.get(CompoundTransaction_.fromAccountEntity).get(DepUnit_.id), Long.valueOf(options.managedAccount))));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T04), cb.equal(root.get(CompoundTransaction_.signaturesCount), 3), cb.isTrue(root.get(CompoundTransaction_.partiallyCompleted)), cb.equal(root.get(CompoundTransaction_.toAccountEntity).get(DepUnit_.id), Long.valueOf(options.managedAccount))));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T05), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T15), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T16), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T17), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T18), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T19), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//      }
//      if (options.roles.contains(Role.TMIMATARCHIS)) {
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T00), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T01), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T03), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T04), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1), cb.equal(root.get(CompoundTransaction_.fromAccountEntity).get(DepUnit_.id), Long.valueOf(options.managedAccount))));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T04), cb.equal(root.get(CompoundTransaction_.signaturesCount), 4), cb.equal(root.get(CompoundTransaction_.toAccountEntity).get(DepUnit_.id), Long.valueOf(options.managedAccount))));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T05), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T19), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1)));
//      }
//      if (options.roles.contains(Role.DIEFTHYNTIS) || options.roles.contains(Role.ANAPLIROTIS_DIEFTHYNTIS)) {
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T00), cb.equal(root.get(CompoundTransaction_.signaturesCount), 2)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T01), cb.equal(root.get(CompoundTransaction_.signaturesCount), 2)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T03), cb.equal(root.get(CompoundTransaction_.signaturesCount), 2)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T04), cb.equal(root.get(CompoundTransaction_.signaturesCount), 2), cb.equal(root.get(CompoundTransaction_.fromAccountEntity).get(DepUnit_.id), Long.valueOf(options.managedAccount))));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T04), cb.equal(root.get(CompoundTransaction_.signaturesCount), 5), cb.equal(root.get(CompoundTransaction_.toAccountEntity).get(DepUnit_.id), Long.valueOf(options.managedAccount))));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T05), cb.equal(root.get(CompoundTransaction_.signaturesCount), 2)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T19), cb.equal(root.get(CompoundTransaction_.signaturesCount), 2)));
//      }
//      if (options.roles.contains(Role.MERIKOS_DIACHEIRISTIS_YLIKOU)) {
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T01), cb.equal(root.get(CompoundTransaction_.signaturesCount), 3)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T03), cb.equal(root.get(CompoundTransaction_.signaturesCount), 3), cb.equal(root.get(CompoundTransaction_.fromAccountEntity).get(DepUnit_.id), Long.valueOf(options.managedAccount))));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T03), cb.equal(root.get(CompoundTransaction_.signaturesCount), 4), cb.equal(root.get(CompoundTransaction_.toAccountEntity).get(DepUnit_.id), Long.valueOf(options.managedAccount))));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T06), cb.equal(root.get(CompoundTransaction_.signaturesCount), 3)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T07), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T08), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T09), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T13), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T19), cb.equal(root.get(CompoundTransaction_.signaturesCount), 3)));
//      }
//      if (options.roles.contains(Role.TOPIKOS_YPEYTHYNOS)) {
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T09), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1), cb.equal(root.get(CompoundTransaction_.toUser).get(User_.id), options.ouUserId)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T12), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0), cb.equal(root.get(CompoundTransaction_.fromUser).get(User_.id), options.ouUserId)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T13), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1), cb.equal(root.get(CompoundTransaction_.fromUser).get(User_.id), options.ouUserId)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T14), cb.equal(root.get(CompoundTransaction_.signaturesCount), 0), cb.equal(root.get(CompoundTransaction_.toUser).get(User_.id), options.ouUserId)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T17), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1), cb.equal(root.get(CompoundTransaction_.toUser).get(User_.id), options.ouUserId)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T18), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1), cb.equal(root.get(CompoundTransaction_.fromUser).get(User_.id), options.ouUserId)));
//      }
//      predicates.add(serviceUtils.getOrPredicate(cb, predicatesSignature));
//    }
//
//    if (options.status != null && options.status.equals(CompoundTransactionStatus.DRAFT)) {
//      List<Predicate> predicatesSignature = new ArrayList<Predicate>();
//      if (options.roles.contains(Role.SYNTAKTIS_DAPANON_PROMITHEIAS)) {
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T00)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T01)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T03)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T04), cb.equal(root.get(CompoundTransaction_.fromAccountEntity).get(DepUnit_.id), Long.valueOf(options.managedAccount))));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T05)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T15)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T16)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T17)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T18)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T19)));
//      }
//      if (options.roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU)) {
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T00)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T01)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T03)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T04), cb.equal(root.get(CompoundTransaction_.fromAccountEntity).get(DepUnit_.id), Long.valueOf(options.managedAccount))));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T05)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T15)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T16)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T17)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T18)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T19)));
//      }
//      if (options.roles.contains(Role.MERIKOS_DIACHEIRISTIS_YLIKOU)) {
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T07)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T08)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T09)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T13)));
//      }
//      if (options.roles.contains(Role.TOPIKOS_YPEYTHYNOS)) {
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T12)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T14)));
//      }
//      predicates.add(serviceUtils.getOrPredicate(cb, predicatesSignature));
//    }
//
//    Predicate predicate = serviceUtils.getAndPredicate(cb, predicates);
//    // End Filter
//
//    // Order
//    List<Order> orders = serviceUtils.getSort(cb, root, options.sort);
//    // Default ordering
//    orders.add(cb.desc(root.get(CompoundTransaction_.updatedTimestamp)));
//    orders.add(cb.desc(root.get(CompoundTransaction_.actualDate)));
//    // End Order
//
//    lcq.select(root).where(predicate).orderBy(orders);
//    Query query = em.createQuery(lcq);
//
//    List<CompoundTransaction> results;
//    if (options.size != -1) {
//      results = query.setFirstResult(options.size * options.index).setMaxResults(options.size).getResultList();
//      // End Results
//
//      // Total count results
//      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//      cq.select(cb.count(cq.from(CompoundTransaction.class)));
//      cq.where(predicate);
//      Long total = em.createQuery(cq).getSingleResult();
//      // End  Total count results
//
//      return new Page<>(transactionMapper.toCompoundTransactionForListDto(results), total, options.index, options.size);
//    } else {
//      results = query.getResultList();
//      return new Page<>(transactionMapper.toCompoundTransactionForListDto(results), results == null ? 0L : results.size(), options.index, options.size);
//    }
//  }
//
//  @Transactional
//  @Traced
//  public Page<CompoundTransactionResDto> getUserCompoundTransactions(ToOptionsMyCompoundTransaction options) {
//    CriteriaBuilder cb = em.getCriteriaBuilder();
//    // Results
//    CriteriaQuery<CompoundTransaction> lcq = cb.createQuery(CompoundTransaction.class);
//    Root<CompoundTransaction> root = lcq.from(CompoundTransaction.class);
//    root.fetch(CompoundTransaction_.fromAccountEntity, JoinType.LEFT);
//    root.fetch(CompoundTransaction_.toAccountEntity, JoinType.LEFT);
//    // Filter
//    List<Predicate> predicates = new ArrayList<Predicate>();
//
//    if (options.udc != null) {
//      predicates.add(cb.like(cb.upper(root.get(CompoundTransaction_.udc)), "%" + options.udc.toUpperCase() + "%"));
//    }
//
//    if (options.transactionType != null) {
//      CriteriaBuilder.In<TransactionType> inClause = cb.in(root.get(CompoundTransaction_.type));
//      for (String item : options.transactionType.list) {
//        inClause.value(TransactionType.valueOf(item));
//      }
//      predicates.add(inClause);
//    }
//
//    if (options.fromAccount != null) {
//      CriteriaBuilder.In<Long> inClause = cb.in(root.get(CompoundTransaction_.fromAccountEntity).get(DepUnit_.id));
//      for (String item : options.fromAccount.list) {
//        inClause.value(Long.valueOf(item));
//      }
//      predicates.add(inClause);
//    }
//
//    if (options.toAccount != null) {
//      CriteriaBuilder.In<Long> inClause = cb.in(root.get(CompoundTransaction_.toAccountEntity).get(DepUnit_.id));
//      for (String item : options.toAccount.list) {
//        inClause.value(Long.valueOf(item));
//      }
//      predicates.add(inClause);
//    }
//
//    if (options.dateFrom != null) {
//      predicates.add(cb.greaterThanOrEqualTo(root.get(CompoundTransaction_.actualDate), options.dateFrom));
//    }
//
//    if (options.dateTo != null) {
//      predicates.add(cb.lessThanOrEqualTo(root.get(CompoundTransaction_.actualDate), options.dateTo));
//    }
//
//    if (options.fromAccountOrToAccount != null) {
//      CriteriaBuilder.In<Long> toInClause = cb.in(root.get(CompoundTransaction_.toAccountEntity).get(DepUnit_.id));
//      for (String item : options.fromAccountOrToAccount.list) {
//        toInClause.value(Long.valueOf(item));
//      }
//      CriteriaBuilder.In<Long> fromInClause = cb.in(root.get(CompoundTransaction_.fromAccountEntity).get(DepUnit_.id));
//      for (String item : options.fromAccountOrToAccount.list) {
//        fromInClause.value(Long.valueOf(item));
//      }
//      predicates.add(cb.or(toInClause, fromInClause));
//    }
//
//    ServiceUtils<CompoundTransaction> serviceUtils = new ServiceUtils<>();
//
//    if (options.status != null) {
//      if (options.status.equals(CompoundTransactionStatus.REJECTED)) {
//        predicates.add(cb.or(cb.equal(root.get(CompoundTransaction_.status), CompoundTransactionStatus.REJECTED), cb.equal(root.get(CompoundTransaction_.status), CompoundTransactionStatus.FAILED)));
//      } else {
//        predicates.add(cb.equal(root.get(CompoundTransaction_.status), options.status));
//      }
//      if (options.status.equals(CompoundTransactionStatus.COMPLETED)) {
//        predicates.add(cb.or(cb.and(cb.or(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T07), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T12), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T15)), cb.equal(root.get(CompoundTransaction_.toUser).get(User_.id), options.user)),
//            cb.and(cb.or(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T08), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T14), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T16)), cb.equal(root.get(CompoundTransaction_.fromUser).get(User_.id), options.user))));
//      } else if (options.status.equals(CompoundTransactionStatus.REJECTED)) {
//        predicates.add(cb.or(cb.and(cb.or(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T07), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T12), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T15)), cb.equal(root.get(CompoundTransaction_.toUser).get(User_.id), options.user)),
//            cb.and(cb.or(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T08), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T14), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T16)), cb.equal(root.get(CompoundTransaction_.fromUser).get(User_.id), options.user))));
//      } else if (options.status.equals(CompoundTransactionStatus.FOR_SIGNATURE)) {
//        List<Predicate> predicatesSignature = new ArrayList<Predicate>();
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T07), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1), cb.equal(root.get(CompoundTransaction_.toUser).get(User_.id), options.user)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T08), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1), cb.equal(root.get(CompoundTransaction_.fromUser).get(User_.id), options.user)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T12), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1), cb.equal(root.get(CompoundTransaction_.toUser).get(User_.id), options.user)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T14), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1), cb.equal(root.get(CompoundTransaction_.fromUser).get(User_.id), options.user)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T15), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1), cb.equal(root.get(CompoundTransaction_.toUser).get(User_.id), options.user)));
//        predicatesSignature.add(cb.and(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T16), cb.equal(root.get(CompoundTransaction_.signaturesCount), 1), cb.equal(root.get(CompoundTransaction_.fromUser).get(User_.id), options.user)));
//        predicates.add(serviceUtils.getOrPredicate(cb, predicatesSignature));
//      }
//    } else if (options.pending != null && options.pending) {
//      predicates.add(cb.equal(root.get(CompoundTransaction_.status), CompoundTransactionStatus.FOR_SIGNATURE));
//      predicates.add(cb.or(cb.and(cb.or(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T07), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T12), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T15)), cb.equal(root.get(CompoundTransaction_.toUser).get(User_.id), options.user)),
//          cb.and(cb.or(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T08), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T14), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T16)), cb.equal(root.get(CompoundTransaction_.fromUser).get(User_.id), options.user))));
//    } else {
//      predicates.add(cb.equal(root.get(CompoundTransaction_.status), CompoundTransactionStatus.COMPLETED));
//      predicates.add(cb.or(cb.and(cb.or(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T07), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T12), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T15)), cb.equal(root.get(CompoundTransaction_.toUser).get(User_.id), options.user)),
//          cb.and(cb.or(cb.equal(root.get(CompoundTransaction_.type), TransactionType.T08), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T14), cb.equal(root.get(CompoundTransaction_.type), TransactionType.T16)), cb.equal(root.get(CompoundTransaction_.fromUser).get(User_.id), options.user))));
//    }
//
//    Predicate predicate = serviceUtils.getAndPredicate(cb, predicates);
//    // End Filter
//
//    // Order
//    List<Order> orders = serviceUtils.getSort(cb, root, options.sort);
//    // Default ordering
//    orders.add(cb.desc(root.get(CompoundTransaction_.updatedTimestamp)));
//    orders.add(cb.desc(root.get(CompoundTransaction_.actualDate)));
//    // End Order
//
//    lcq.select(root).where(predicate).orderBy(orders);
//    Query query = em.createQuery(lcq);
//
//    List<CompoundTransaction> results;
//    if (options.size != -1) {
//      results = query.setFirstResult(options.size * options.index).setMaxResults(options.size).getResultList();
//      // End Results
//
//      // Total count results
//      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//      cq.select(cb.count(cq.from(CompoundTransaction.class)));
//      cq.where(predicate);
//      Long total = em.createQuery(cq).getSingleResult();
//      // End  Total count results
//
//      return new Page<>(transactionMapper.toCompoundTransactionForListDto(results), total, options.index, options.size);
//    } else {
//      results = query.getResultList();
//      return new Page<>(transactionMapper.toCompoundTransactionForListDto(results), results == null ? 0L : results.size(), options.index, options.size);
//    }
//  }
//
//  @Transactional(Transactional.TxType.REQUIRED) // TODO required is needed?
//  @Traced
//  public CompoundTransactionResDto persistCompoundTransaction(CompoundTransactionReqDto compoundTransactionDto) {
//    CompoundTransaction compTransaction = transactionMapper.toCompoundTransaction(compoundTransactionDto);
//    validateCompoundTransaction(compTransaction);
//    compTransaction.setStatus(CompoundTransactionStatus.DRAFT);
//    if (compTransaction.getId() != null) {
//      CompoundTransaction oldCompoundTransaction = compoundTransactionRepository.findByIdOptional(compTransaction.getId()).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//      if (!oldCompoundTransaction.getStatus().equals(CompoundTransactionStatus.DRAFT)) {
//        throw new BadRequestAlertException("Δεν μπορείτε να επεξεργαστείτε συναλλαγή που δεν είναι πρόχειρη");
//      }
//      if (!oldCompoundTransaction.getType().equals(compTransaction.getType())) {
//        throw new BadRequestAlertException("Δεν μπορείτε να αλλάξετε τον τύπο της συναλλαγής");
//      }
//      if (oldCompoundTransaction.getType().equals(TransactionType.T08) && !oldCompoundTransaction.getAuthor().equals(compTransaction.getAuthor())) {
//        throw new BadRequestAlertException("Δεν μπορείτε να επεξεργαστείτε συναλλαγή 'Αποχρέωση' που δεν είναι δική σας");
//      }
//      oldCompoundTransaction.setUdc(compTransaction.getUdc());
//      oldCompoundTransaction.setActualDate(compTransaction.getActualDate());
//      oldCompoundTransaction.setFromAccountEntity(compTransaction.getFromAccountEntity());
//      oldCompoundTransaction.setToAccountEntity(compTransaction.getToAccountEntity());
//      oldCompoundTransaction.setToUser(compTransaction.getToUser());
//      oldCompoundTransaction.setFromUser(compTransaction.getFromUser());
//      oldCompoundTransaction.setToOu(compTransaction.getToOu());
//      oldCompoundTransaction.setFromOu(compTransaction.getFromOu());
//      oldCompoundTransaction.setAuthor(compTransaction.getAuthor());
//      oldCompoundTransaction.setComments(compTransaction.getComments());
//      compoundTransactionRepository.persistAndFlush(oldCompoundTransaction);
//      log.info("Delete old start");
//      List<Long> itemdeleteIds = null;
//      if (oldCompoundTransaction.getType().equals(TransactionType.T00)) {
//        itemdeleteIds = em.createQuery("select t.item.id from ITransaction t where cTransaction.id = " + compTransaction.getId()).getResultList();
//        //log.info(new Gson().toJson(itemdeleteIds));
//      }
//      int up = em.createQuery("update ITransaction t set t.item.id = null where cTransaction.id = " + compTransaction.getId()).executeUpdate();
//      log.info(up + "");
//      int up2 = em.createQuery("delete from ITransaction t where cTransaction.id = " + compTransaction.getId()).executeUpdate();
//      log.info(up2 + "");
//      if (itemdeleteIds != null) {
//        int up3 = em.createQuery("delete from Item t where id in :ids").setParameter("ids", itemdeleteIds).executeUpdate();
//        log.info(up3 + "");
//      }
//      log.info("Delete old end");
//      compTransaction.setCreatedTimestamp(oldCompoundTransaction.getCreatedTimestamp());
//      compTransaction.setUpdatedTimestamp(LocalDateTime.now(ZoneOffset.UTC));
//      compTransaction.setId(compTransaction.getId());
//    } else {
//      compoundTransactionRepository.persistAndFlush(compTransaction);
//    }
//    /* OchimaTransaction */
//    OchimaTransaction ochimaTransaction = ochimaTransactionRepository.find("cTransaction.id", compTransaction.getId()).firstResultOptional().orElse(new OchimaTransaction());
//    ochimaTransaction.setCTransaction(compTransaction);
//    ochimaTransaction.setChangeDecision(compoundTransactionDto.getChangeDecision());
//    ochimaTransaction.setCompletionDecision(compoundTransactionDto.getCompletionDecision());
//    ochimaTransactionRepository.persist(ochimaTransaction);
//    /* OchimaTransaction end */
//
//    Set<ITransaction> itransactions = compTransaction.getItransactions();
//    int  i = 0;
//    for (ITransaction itransaction : itransactions) {
//      validateITransactions(compTransaction, itransaction);
//      itransaction.setCTransaction(compTransaction);
//      iTransactionRepository.persist(itransaction);
//      i++;
//      if (i % 100 == 0) {
//        em.flush();
//        em.clear();
//      }
//    } //for each transaction
//    logService.persistLogTransaction(compTransaction.getId(), compTransaction.getAuthor().getId(), compoundTransactionDto.getId() == null ? LogTransaction.Action.ADD : LogTransaction.Action.EDIT);
//    return CompoundTransactionResDto.builder().id(String.valueOf(compTransaction.getId())).createdTimestamp(compTransaction.getCreatedTimestamp()).updatedTimestamp(compTransaction.getUpdatedTimestamp()).status(compTransaction.getStatus()).signaturesCount(compTransaction.getSignaturesCount()).author(UserDto.builder().firstName(compTransaction.getAuthor().getFirstName()).lastName(compTransaction.getAuthor().getLastName()).build()).build();
//  }
//
//  private void validateCompoundTransaction(CompoundTransaction compTransaction) {
//    compTransaction.setAuthor(userRepository.findByIdOptional(Long.valueOf(compTransaction.getAuthor().getId())).orElseThrow(() -> new NotFoundAlertException(User.class.getName())));
//    DepUnit fromAccountEntity = null;
//    if (compTransaction.getFromAccountEntity() != null) {
//      fromAccountEntity = depUnitRepository.findByIdOptional(compTransaction.getFromAccountEntity().getId()).orElseThrow(() -> new NotFoundAlertException(GenDepUnit.class.getName()));
//    }
//    String fromAccount = fromAccountEntity == null ? null :  compTransaction.getFromAccountEntity().getCode();
//
//    DepUnit toAccountEntity = null;
//    if (compTransaction.getToAccountEntity() != null) {
//      toAccountEntity = depUnitRepository.findByIdOptional(compTransaction.getToAccountEntity().getId()).orElseThrow(() -> new NotFoundAlertException(GenDepUnit.class.getName()));
//    }
//    String toAccount = toAccountEntity == null ? null : compTransaction.getToAccountEntity().getCode();
//
//    TransactionType transactionType = compTransaction.getType();
//
//    // Check for TransactionType.T00
//    if (transactionType.equals(TransactionType.T00)) {
//      // 'fromAccount' must be null
//      compTransaction.setFromAccountEntity(null);
//      // Check if 'toAccount' is null
//      if (toAccount == null) {
//        throw new BadRequestAlertException("Προς λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // 'from/to' user must be null
//      compTransaction.setFromUser(null);
//      compTransaction.setToUser(null);
//      // null ou
//      compTransaction.setFromOu(null);
//      compTransaction.setToOu(null);
//    }
//
//    // Check for TransactionType.T01
//    if (transactionType.equals(TransactionType.T01)) {
//      // Check if 'fromAccount' is not null
//      if (fromAccount == null) {
//        throw new BadRequestAlertException("Από λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'toAccount' is null
//      if (toAccount == null) {
//        throw new BadRequestAlertException("Προς λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'fromAccount' and 'toAccount' are the same
//      if (fromAccount.equals(toAccount)) {
//        throw new BadRequestAlertException("Οι λογαριασμοί από και προς δεν μπορούν να ταυτίζονται");
//      }
//      // 'toAccount' must start with 'fromAccount'
//      if (!toAccount.startsWith(fromAccount)) {
//        throw new BadRequestAlertException("Από λογαριασμός πρέπει να είναι η γενική της μερικής");
//      }
//      // 'from/to' user must be null
//      compTransaction.setFromUser(null);
//      compTransaction.setToUser(null);
//      // null ou
//      compTransaction.setFromOu(null);
//      compTransaction.setToOu(null);
//    }
//
//    // Check for TransactionType.T03
//    if (transactionType.equals(TransactionType.T03)) {
//      // Check if 'fromAccount' is not null
//      if (fromAccount == null) {
//        throw new BadRequestAlertException("Από λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'toAccount' is null
//      if (toAccount == null) {
//        throw new BadRequestAlertException("Προς λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'fromAccount' and 'toAccount' are the same
//      if (fromAccount.equals(toAccount)) {
//        throw new BadRequestAlertException("Οι λογαριασμοί από και προς δεν μπορούν να ταυτίζονται");
//      }
//      // 'fromAccount' must start with the general account of 'toAccount'
//      if (!toAccount.startsWith(fromAccount.split(" ")[0])) {
//        throw new BadRequestAlertException("Οι λογαριασμοί πρέπει να ανήκουν στην ίδια γενική");
//      }
//      // 'from/to' user must be null
//      compTransaction.setFromUser(null);
//      compTransaction.setToUser(null);
//      // null ou
//      compTransaction.setFromOu(null);
//      compTransaction.setToOu(null);
//    }
//
//    // Check for TransactionType.T04
//    if (transactionType.equals(TransactionType.T04)) {
//      // Check if 'toAccount' is null
//      if (fromAccount == null) {
//        throw new BadRequestAlertException("Από λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'toAccount' is null
//      if (toAccount == null) {
//        throw new BadRequestAlertException("Προς λογαριασμός δεν μπορεί να είναι κενός");
//      }
//
//      if (toAccount.equals(fromAccount)) {
//        throw new BadRequestAlertException("Από και προς λογαριασμός δεν μπορεί να είναι ίδιος");
//      }
//      // 'from/to' user must be null
//      compTransaction.setFromUser(null);
//      compTransaction.setToUser(null);
//      // null ou
//      compTransaction.setFromOu(null);
//      compTransaction.setToOu(null);
//    }
//
//    // Check for TransactionType.T03
//    if (transactionType.equals(TransactionType.T06)) {
//      // Check if 'fromAccount' is not null
//      if (fromAccount == null) {
//        throw new BadRequestAlertException("Από λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // 'toAccount' must be null
//      compTransaction.setToAccountEntity(null);
//      // 'from/to' user must be null
//      compTransaction.setFromUser(null);
//      compTransaction.setToUser(null);
//      // null ou
//      compTransaction.setFromOu(null);
//      compTransaction.setToOu(null);
//    }
//
//    // Check for TransactionType.T07
//    if (transactionType.equals(TransactionType.T07)) {
//      // 'toAccount' must be null
//      compTransaction.setToAccountEntity(null);
//      // Check if 'fromAccount' is not null
//      if (fromAccount == null) {
//        throw new BadRequestAlertException("Από λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'toUser' is null
//      if (compTransaction.getToUser().getId() == null) {
//        throw new BadRequestAlertException("Ο παραλήπτης δεν μπορεί να είναι κενός");
//      }
//      // 'from' user must be null
//      compTransaction.setFromUser(null);
//      // null ou
//      compTransaction.setFromOu(null);
//      compTransaction.setToOu(null);
//    }
//
//    // Check for TransactionType.T08
//    if (transactionType.equals(TransactionType.T08)) {
//      // 'fromAccount' must be null
//      compTransaction.setFromAccountEntity(null);
//      // Check if 'toAccount' is not null
//      if (toAccount == null) {
//        throw new BadRequestAlertException("Προς λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'fromUser' is null
//      if (compTransaction.getFromUser().getId() == null) {
//        throw new BadRequestAlertException("Ο αποστολέας δεν μπορεί να είναι κενός");
//      }
//      // 'to' user must be null
//      compTransaction.setToUser(null);
//      // null ou
//      compTransaction.setFromOu(null);
//      compTransaction.setToOu(null);
//    }
//
//    // Check for TransactionType.T09
//    if (transactionType.equals(TransactionType.T09)) {
//      // 'toAccount' must be null
//      compTransaction.setToAccountEntity(null);
//      // Check if 'fromAccount' is not null
//      if (fromAccount == null) {
//        throw new BadRequestAlertException("Από λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'toUser' is null
//      if (compTransaction.getToUser().getId() == null) {
//        throw new BadRequestAlertException("Ο τοπικός υπεύθυνος υπηρεσίας δεν μπορεί να είναι κενός");
//      }
//      // 'from' user must be null
//      compTransaction.setFromUser(null);
//
//      // Check if 'toUser' is null
//      if (compTransaction.getToOu().getId() == null) {
//        throw new BadRequestAlertException("Η υπηρεσίας δεν μπορεί να είναι κενή");
//      }
//      compTransaction.setFromOu(null);
//      // 'from' user must be null
//      compTransaction.setFromUser(null);
//    }
//
//    // Check for TransactionType.T12
//    if (transactionType.equals(TransactionType.T12)) {
//      //
//      compTransaction.setToAccountEntity(null);
//      compTransaction.setFromAccountEntity(null);
//      // user
//      if (compTransaction.getToUser().getId() == null) {
//        throw new BadRequestAlertException("Ο παραλήπτης δεν μπορεί να είναι κενός");
//      }
//      if (compTransaction.getFromUser().getId() == null) {
//        throw new BadRequestAlertException("Ο τοπικός υπεύθυνος υπηρεσίας δεν μπορεί να είναι κενός");
//      }
//      // ou
//      if (compTransaction.getFromOu().getId() == null) {
//        throw new BadRequestAlertException("Η υπηρεσίας δεν μπορεί να είναι κενή");
//      }
//      compTransaction.setToOu(null);
//    }
//
//    // Check for TransactionType.T13
//    if (transactionType.equals(TransactionType.T13)) {
//      // depunit
//      if (toAccount == null) {
//        throw new BadRequestAlertException("Προς λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      compTransaction.setFromAccountEntity(null);
//      // user
//      if (compTransaction.getFromUser().getId() == null) {
//        throw new BadRequestAlertException("Ο παραλήπτης δεν μπορεί να είναι κενός");
//      }
//      compTransaction.setToUser(null);
//      // ou
//      if (compTransaction.getFromOu().getId() == null) {
//        throw new BadRequestAlertException("Η υπηρεσία δεν μπορεί να είναι κενή");
//      }
//      compTransaction.setToOu(null);
//    }
//
//    // Check for TransactionType.T14
//    if (transactionType.equals(TransactionType.T14)) {
//      // depunit
//      compTransaction.setToAccountEntity(null);
//      compTransaction.setFromAccountEntity(null);
//      // user
//      if (compTransaction.getFromUser().getId() == null) {
//        throw new BadRequestAlertException("Ο αποστολέας δεν μπορεί να είναι κενός");
//      }
//      if (compTransaction.getToUser().getId() == null) {
//        throw new BadRequestAlertException("Ο τοπικός υπεύθυνος υπηρεσίας δεν μπορεί να είναι κενός");
//      }
//      if (!compTransaction.getToUser().getId().equals(compTransaction.getAuthor().getId())) {
//        throw new BadRequestAlertException("Δεν μπορείτε να συντάξετε αυτή την συναλλαγή για άλλο χρήστη");
//      }
//      // ou
//      if (compTransaction.getToOu().getId() == null) {
//        throw new BadRequestAlertException("Η υπηρεσία δεν μπορεί να είναι κενή");
//      }
//      compTransaction.setFromOu(null);
//    }
//
//    // Check for TransactionType.T15
//    if (transactionType.equals(TransactionType.T15)) {
//      // 'toAccount' must be null
//      compTransaction.setToAccountEntity(null);
//      // Check if 'fromAccount' is not null
//      if (fromAccount == null) {
//        throw new BadRequestAlertException("Από λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'toUser' is null
//      if (compTransaction.getToUser().getId() == null) {
//        throw new BadRequestAlertException("Ο παραλήπτης δεν μπορεί να είναι κενός");
//      }
//      // 'from' user must be null
//      compTransaction.setFromUser(null);
//      // null ou
//      compTransaction.setFromOu(null);
//      compTransaction.setToOu(null);
//    }
//
//    // Check for TransactionType.T16
//    if (transactionType.equals(TransactionType.T16)) {
//      // 'fromAccount' must be null
//      compTransaction.setFromAccountEntity(null);
//      // Check if 'toAccount' is not null
//      if (toAccount == null) {
//        throw new BadRequestAlertException("Προς λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'fromUser' is null
//      if (compTransaction.getFromUser().getId() == null) {
//        throw new BadRequestAlertException("Ο αποστολέας δεν μπορεί να είναι κενός");
//      }
//      // 'to' user must be null
//      compTransaction.setToUser(null);
//      // null ou
//      compTransaction.setFromOu(null);
//      compTransaction.setToOu(null);
//    }
//
//    // Check for TransactionType.T17
//    if (transactionType.equals(TransactionType.T17)) {
//      // 'toAccount' must be null
//      compTransaction.setToAccountEntity(null);
//      // Check if 'fromAccount' is not null
//      if (fromAccount == null) {
//        throw new BadRequestAlertException("Από λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'toUser' is null
//      if (compTransaction.getToUser().getId() == null) {
//        throw new BadRequestAlertException("Ο τοπικός υπεύθυνος υπηρεσίας δεν μπορεί να είναι κενός");
//      }
//      // 'from' user must be null
//      compTransaction.setFromUser(null);
//
//      // Check if 'toUser' is null
//      if (compTransaction.getToOu().getId() == null) {
//        throw new BadRequestAlertException("Η υπηρεσίας δεν μπορεί να είναι κενή");
//      }
//      compTransaction.setFromOu(null);
//      // 'from' user must be null
//      compTransaction.setFromUser(null);
//    }
//
//    // Check for TransactionType.T18
//    if (transactionType.equals(TransactionType.T18)) {
//      // depunit
//      if (toAccount == null) {
//        throw new BadRequestAlertException("Προς λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      compTransaction.setFromAccountEntity(null);
//      // user
//      if (compTransaction.getFromUser().getId() == null) {
//        throw new BadRequestAlertException("Ο παραλήπτης δεν μπορεί να είναι κενός");
//      }
//      compTransaction.setToUser(null);
//      // ou
//      if (compTransaction.getFromOu().getId() == null) {
//        throw new BadRequestAlertException("Η υπηρεσία δεν μπορεί να είναι κενή");
//      }
//      compTransaction.setToOu(null);
//    }
//
//    // Check for TransactionType.T19
//    if (transactionType.equals(TransactionType.T19)) {
//      // Check if 'fromAccount' is not null
//      if (fromAccount == null) {
//        throw new BadRequestAlertException("Από λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'toAccount' is null
//      if (toAccount == null) {
//        throw new BadRequestAlertException("Προς λογαριασμός δεν μπορεί να είναι κενός");
//      }
//      // Check if 'fromAccount' and 'toAccount' are the same
//      if (fromAccount.equals(toAccount)) {
//        throw new BadRequestAlertException("Οι λογαριασμοί από και προς δεν μπορούν να ταυτίζονται");
//      }
//      // 'fromAccount' must start with 'toAccount'
//      if (!fromAccount.startsWith(toAccount)) {
//        throw new BadRequestAlertException("Προς λογαριασμός πρέπει να είναι η γενική της μερικής");
//      }
//      // 'from/to' user must be null
//      compTransaction.setFromUser(null);
//      compTransaction.setToUser(null);
//      // null ou
//      compTransaction.setFromOu(null);
//      compTransaction.setToOu(null);
//    }
//
//  }
//
//  private void validateITransactions(CompoundTransaction compTransaction, ITransaction itransaction) {
//    // Force transactions to have the same from/to account, actual date, type, udc with compound transaction
//    if (compTransaction.getType().equals(TransactionType.T00)) {
//      itransaction.setFromAccountEntity(null);
//      if (itransaction.getToAccountEntity() == null) {
//        itransaction.setToAccountEntity(compTransaction.getToAccountEntity());
//      }
//    } else if (compTransaction.getType().equals(TransactionType.T05)) {
//      if (itransaction.getFromAccountEntity() == null) {
//        itransaction.setFromAccountEntity(compTransaction.getFromAccountEntity());
//      }
//      itransaction.setToAccountEntity(compTransaction.getToAccountEntity());
//    } else if (compTransaction.getType().equals(TransactionType.T04)) {
//      if (itransaction.getFromAccountEntity() == null) {
//        itransaction.setFromAccountEntity(compTransaction.getFromAccountEntity());
//      }
//      itransaction.setToAccountEntity(compTransaction.getToAccountEntity());
//    } else {
//      itransaction.setFromAccountEntity(compTransaction.getFromAccountEntity());
//      itransaction.setToAccountEntity(compTransaction.getToAccountEntity());
//    }
//    ItemCode itemCode = itemCodeRepository.findByIdOptional(itransaction.getItemCodeEntity().getId()).orElseThrow(() -> new NotFoundAlertException(ItemCode.class.getName()));
//    itransaction.setItemCode(itemCode.getCode());
//    itransaction.setItemCodeEntity(itemCode);
//    itransaction.setActualDate(compTransaction.getActualDate());
//    itransaction.setType(compTransaction.getType());
//    itransaction.setUdc(compTransaction.getUdc());
//    itransaction.setToUser(compTransaction.getToUser());
//    itransaction.setFromUser(compTransaction.getFromUser());
//    itransaction.setToOu(compTransaction.getToOu());
//    itransaction.setFromOu(compTransaction.getFromOu());
//
//    if (Boolean.TRUE.equals(itransaction.getIsGroupParent()) && !itransaction.getType().equals(TransactionType.T00)) {
//      itransaction.setItem(null);
//      return;
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T09) || compTransaction.getType().equals(TransactionType.T17)) {
//      if (ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2 and depUnit.id != ?3", itransaction.getToOu().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getFromAccountEntity().getId()).firstResultOptional().isPresent()) {
//        throw new BadRequestAlertException("Η υπηρεσία λαμβάνει τον συγκεκριμένο κωδικό υλικού από άλλη διαχείριση. Κωδικός Υλικού: " + itransaction.getItemCodeEntity().getCode());
//      }
//    }
//
//    if (itemCode.isDiscreet()) {
//      TransactionType transactionType = itransaction.getType();
//      // set amount to 1 when discreet transactions
//      if (!Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//        itransaction.setAmount(BigDecimal.valueOf(1));
//      }
//      // get item id - if TOO first persist item
//      Long itemId;
//      if (transactionType.equals(TransactionType.T00)) {
//        if (itransaction.getItemJson() == null) {
//          itransaction.setItemJson(Collections.emptyMap());
//        }
//        itransaction.getItemJson().remove("id");
//        EntityDto persistedItem = itemService.persistOrUpdateItem(itransaction.getItemJson(), itransaction.getItemCodeEntity(), compTransaction.getAuthor().getId());
//        itemId = Long.valueOf(persistedItem.getId());
//      } else {
//        itemId = Long.valueOf(String.valueOf(itransaction.getItemJson().get("id")));
//        if (itemId == null) {
//          throw new BadRequestAlertException("Δεν έχετε επιλέξει υλικό για τη συναλλαγή");
//        }
//      }
//      Item item = itemService.getItemEntity(itemId);
//      if (compTransaction.getType().equals(TransactionType.T00)) {
//        item.setDraft(true);
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          item.setGroupParent(true);
//        }
//        itemRepository.persist(item);
//      }
//      itransaction.setItem(item);
//      if (!item.getItemCode().getId().equals(itransaction.getItemCodeEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν αντιστοιχεί στον κωδικό υλικού της συναλλαγής");
//      }
//      validateDiscreetItems(itransaction, transactionType, item);
//    } else {
//      itransaction.setItem(null);
//    }
//  }
//
//  private void validateDiscreetItems(ITransaction itransaction, TransactionType transactionType, Item item) {
//    if (transactionType.equals(TransactionType.T00)) {
//      if (!item.isDraft()) {
//        throw new BadRequestAlertException("Το υλικό έχει ήδη εισαχθεί");
//      }
//      if (item.getAssigneeDepUnit() != null) {
//        throw new BadRequestAlertException("Το υλικό έχει ήδη ανατεθεί σε υπηρεσία");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T01)) {
//      log.info(item.getItemCode().getCode() + " " + item.getId() + " " + item.getSerialNumber());
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getFromAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//      if (item.getAssigneeUser() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπάλληλο");
//      }
//      if (item.getAssigneeOu() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπηρεσία");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T03)) {
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getFromAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//      if (item.getAssigneeUser() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπάλληλο");
//      }
//      if (item.getAssigneeOu() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπηρεσία");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T04)) {
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getFromAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//      if (item.getAssigneeUser() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπάλληλο");
//      }
//      if (item.getAssigneeOu() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπηρεσία");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T05)) {
//      if (itransaction.getProxyDepUnitFrom() == null) {
//        if (!item.getAssigneeDepUnit().getId().equals(itransaction.getFromAccountEntity().getId())) {
//          throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//        }
//        if (item.getAssigneeUser() != null) {
//          throw new BadRequestAlertException("Το υλικό ανήκει σε υπάλληλο");
//        }
//        if (item.getAssigneeOu() != null) {
//          throw new BadRequestAlertException("Το υλικό ανήκει σε υπηρεσία");
//        }
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T06)) {
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getFromAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//      if (item.getAssigneeUser() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπάλληλο");
//      }
//      if (item.getAssigneeOu() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπηρεσία");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T07)) {
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getFromAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//      if (item.getAssigneeUser() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπάλληλο");
//      }
//      if (item.getAssigneeOu() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπηρεσία");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T08)) {
//      if (!item.getAssigneeUser().getId().equals(itransaction.getFromUser().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτόν τον χρήστη");
//      }
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getToAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//      if (item.getAssigneeOu() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπηρεσία");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T09)) {
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getFromAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//      if (item.getAssigneeUser() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπάλληλο");
//      }
//      if (item.getAssigneeOu() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπηρεσία");
//      }
//      if (!userService.isTopikosInDepUnitAndOu(itransaction.getToUser().getId(), itransaction.getFromAccountEntity().getId(), itransaction.getToOu().getId())) {
//        throw new BadRequestAlertException("Ο τοπικός υπεύθυνος που επιλέξατε δεν ανήκει στη συγκεκριμένη διαχείριση ή υπηρεσία");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T12)) {
//      if (!item.getAssigneeOu().getId().equals(itransaction.getFromOu().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή την υπηρεσία");
//      }
//      if (!item.getAssigneeUser().getId().equals(itransaction.getFromUser().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει στον συγκεκριμένο τοπικό υπεύθυνο υπηρεσίας");
//      }
//      if (!item.isAssignedToTopikosYpeythynos()) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε τοπικό υπεύθυνο");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T13)) {
//      if (!item.getAssigneeOu().getId().equals(itransaction.getFromOu().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή την υπηρεσία");
//      }
//      if (!item.getAssigneeUser().getId().equals(itransaction.getFromUser().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει στον συγκεκριμένο τοπικό υπεύθυνο υπηρεσίας");
//      }
//      if (!item.isAssignedToTopikosYpeythynos()) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε τοπικό υπεύθυνο");
//      }
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getToAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T14)) {
//      if (!item.getAssigneeOu().getId().equals(itransaction.getToOu().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή την υπηρεσία");
//      }
//      if (!item.getAssigneeUser().getId().equals(itransaction.getFromUser().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτόν τον χρήστη");
//      }
//      if (item.isAssignedToTopikosYpeythynos()) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε τοπικό υπεύθυνο");
//      }
//      if (!userService.isTopikosInDepUnitAndOu(itransaction.getToUser().getId(), item.getAssigneeDepUnit().getId(), itransaction.getToOu().getId())) {
//        throw new BadRequestAlertException("Ο τοπικός υπεύθυνος που επιλέξατε δεν ανήκει στη συγκεκριμένη διαχείριση ή υπηρεσία");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T15)) {
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getFromAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//      if (item.getAssigneeUser() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπάλληλο");
//      }
//      if (item.getAssigneeOu() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπηρεσία");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T16)) {
//      if (!item.getAssigneeUser().getId().equals(itransaction.getFromUser().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτόν τον χρήστη");
//      }
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getToAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T17)) {
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getFromAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//      if (item.getAssigneeUser() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπάλληλο");
//      }
//      if (item.getAssigneeOu() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπηρεσία");
//      }
//      if (!userService.isTopikosInDepUnitAndOu(itransaction.getToUser().getId(), itransaction.getFromAccountEntity().getId(), itransaction.getToOu().getId())) {
//        throw new BadRequestAlertException("Ο τοπικός υπεύθυνος που επιλέξατε δεν ανήκει στη συγκεκριμένη διαχείριση ή υπηρεσία");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T18)) {
//      if (!item.getAssigneeOu().getId().equals(itransaction.getFromOu().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή την υπηρεσία");
//      }
//      if (!item.getAssigneeUser().getId().equals(itransaction.getFromUser().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει στον συγκεκριμένο τοπικό υπεύθυνο υπηρεσίας");
//      }
//      if (!item.isAssignedToTopikosYpeythynos()) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε τοπικό υπεύθυνο");
//      }
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getToAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//    }
//
//    if (transactionType.equals(TransactionType.T19)) {
//      if (!item.getAssigneeDepUnit().getId().equals(itransaction.getFromAccountEntity().getId())) {
//        throw new BadRequestAlertException("Το υλικό δεν ανήκει σε αυτή τη διαχείριση");
//      }
//      if (item.getAssigneeUser() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπάλληλο");
//      }
//      if (item.getAssigneeOu() != null) {
//        throw new BadRequestAlertException("Το υλικό ανήκει σε υπηρεσία");
//      }
//    }
//  }
//
//  @Transactional
//  @Traced
//  public void sendCompoundTransactionForSign(Long id, Long userId) {
//    CompoundTransaction compTransaction = compoundTransactionRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    Long inTransactionId = compTransaction.getParentTransaction() == null ? compTransaction.getId() : compTransaction.getParentTransaction().getId();
//    compTransaction.setStatus(CompoundTransactionStatus.FOR_SIGNATURE);
//    LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);
//    compTransaction.setSendForSignTimestamp(timestamp);
//    compTransaction.setSignatureVerifyCode(generateRandomString());
//    compoundTransactionRepository.persist(compTransaction);
//    logService.persistLogTransaction(id, userId, LogTransaction.Action.FOR_SIGN);
//    // TODO send email and sms
//    List<ITransaction> itransactions = iTransactionRepository.list("cTransaction.id = ?1 and groupParentTransaction.id IS NULL", id);
//    if (compTransaction.getType().equals(TransactionType.T00)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      persistCompoundTransactionFromT00(compTransaction);
//    }
//    if (compTransaction.getType().equals(TransactionType.T01)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      for (ITransaction itransaction : itransactions) {
//        GenAccountBalance genAccountBalance = genAccountBalanceRepository.find("genDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        genAccountBalance.setTransactionsBalance(genAccountBalance.getTransactionsBalance().add(itransaction.getAmount()));
//        genAccountBalance.setAvailableBalance(genAccountBalance.getAvailableBalance().subtract(itransaction.getAmount()));
//        if (genAccountBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T03)) {
//      GenDepUnit genDepUnit = parDepUnitRepository.findByIdOptional(compTransaction.getFromAccountEntity().getId()).orElseThrow(() -> new NotFoundAlertException(ParDepUnit.class.getName())).getGenDepUnit();
//      notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      for (ITransaction itransaction : itransactions) {
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//        parAccountBalanceFrom.setAvailableBalance(parAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//        if (parAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T04)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      persistCompoundTransactionFromT04FromAccount(compTransaction);
//      for (ITransaction itransaction : itransactions) {
//        if (!Boolean.TRUE.equals(itransaction.getProxyTransactionFrom())) {
//          GenAccountBalance genAccountBalance = genAccountBalanceRepository.find("genDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//          genAccountBalance.setTransactionsBalance(genAccountBalance.getTransactionsBalance().add(itransaction.getAmount()));
//          genAccountBalance.setAvailableBalance(genAccountBalance.getAvailableBalance().subtract(itransaction.getAmount()));
//          if (genAccountBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//            throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//          }
//        } else {
//          ParAccountBalance parAccountBalance = parAccountBalanceRepository.find("parDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getProxyDepUnitFrom().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//          parAccountBalance.setTransactionsBalance(parAccountBalance.getTransactionsBalance().add(itransaction.getAmount()));
//          parAccountBalance.setAvailableBalance(parAccountBalance.getAvailableBalance().subtract(itransaction.getAmount()));
//          if (parAccountBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//            throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//          }
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T05)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      persistCompoundTransactionFromT05(compTransaction);
//      for (ITransaction itransaction : itransactions) {
//        if (!Boolean.TRUE.equals(itransaction.getProxyTransactionFrom())) {
//          GenAccountBalance genAccountBalance = genAccountBalanceRepository.find("genDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//          genAccountBalance.setTransactionsBalance(genAccountBalance.getTransactionsBalance().add(itransaction.getAmount()));
//          genAccountBalance.setAvailableBalance(genAccountBalance.getAvailableBalance().subtract(itransaction.getAmount()));
//          if (genAccountBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//            throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//          }
//        } else {
//          ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getProxyDepUnitFrom().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//          parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//          parAccountBalanceFrom.setAvailableBalance(parAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//          if (parAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//            throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//          }
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T06)) {
//      throw new InternalServerErrorException("Δεν μπορεί να εκκινηθεί T06 χωρίς T05");
//    }
//    if (compTransaction.getType().equals(TransactionType.T07)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      for (ITransaction itransaction : itransactions) {
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//        parAccountBalanceFrom.setAvailableBalance(parAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//        if (parAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T08)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      for (ITransaction itransaction : itransactions) {
//        UserAccountBalance userAccountBalance = userAccountBalanceRepository.find("user.id = ?1 and itemCode.id = ?2", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserAccountBalance.class.getName()));
//        UserDepUnitAccountBalance userDepUnitAccountBalance = userDepUnitAccountBalanceRepository.find("user.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3 and ou.id is null", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getToAccountEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserDepUnitAccountBalance.class.getName()));
//        userAccountBalance.setTransactionsBalance(userAccountBalance.getTransactionsBalance().add(itransaction.getAmount()));
//        userAccountBalance.setAvailableBalance(userAccountBalance.getAvailableBalance().subtract(itransaction.getAmount()));
//        userDepUnitAccountBalance.setTransactionsBalance(userDepUnitAccountBalance.getTransactionsBalance().add(itransaction.getAmount()));
//        userDepUnitAccountBalance.setAvailableBalance(userDepUnitAccountBalance.getAvailableBalance().subtract(itransaction.getAmount()));
//        if (userAccountBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (userDepUnitAccountBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T09)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      for (ITransaction itransaction : itransactions) {
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//        parAccountBalanceFrom.setAvailableBalance(parAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//        if (parAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T12)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromUser().getId());
//      for (ITransaction itransaction : itransactions) {
//        OuAccountBalance ouAccountBalanceFrom = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2", itransaction.getFromOu().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        OuManagerAccountBalance ouManagerAccountBalanceFrom = ouManagerAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2 and manager.id = ?3", itransaction.getFromOu().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getFromUser().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuManagerAccountBalance.class.getName()));
//        ouAccountBalanceFrom.setAvailableBalance(ouAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//        ouAccountBalanceFrom.setTransactionsBalance(ouAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//        ouManagerAccountBalanceFrom.setAvailableBalance(ouManagerAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//        ouManagerAccountBalanceFrom.setTransactionsBalance(ouManagerAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//        if (ouAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (ouManagerAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T13)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      for (ITransaction itransaction : itransactions) {
//        OuAccountBalance ouAccountBalanceFrom = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3", itransaction.getFromOu().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getToAccountEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuManagerAccountBalance.class.getName()));
//        OuManagerAccountBalance ouManagerAccountBalanceFrom = ouManagerAccountBalanceRepository.find("manager.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3 and ou.id = ?4", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getToAccountEntity().getId(), itransaction.getFromOu().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        ouAccountBalanceFrom.setAvailableBalance(ouAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//        ouAccountBalanceFrom.setTransactionsBalance(ouAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//        ouManagerAccountBalanceFrom.setAvailableBalance(ouManagerAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//        ouManagerAccountBalanceFrom.setTransactionsBalance(ouManagerAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//        if (ouAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (ouManagerAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T14)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getToUser().getId());
//      for (ITransaction itransaction : itransactions) {
//        OuAccountBalance ouAccountBalanceTo = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2", itransaction.getToOu().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        UserAccountBalance userAccountBalance = userAccountBalanceRepository.find("user.id = ?1 and itemCode.id = ?2", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserAccountBalance.class.getName()));
//        UserDepUnitAccountBalance userDepUnitAccountBalance = userDepUnitAccountBalanceRepository.find("user.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3 and ou.id = ?4", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId(), ouAccountBalanceTo.getDepUnit().getId(), ouAccountBalanceTo.getOu().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserDepUnitAccountBalance.class.getName()));
//        userAccountBalance.setTransactionsBalance(userAccountBalance.getTransactionsBalance().add(itransaction.getAmount()));
//        userAccountBalance.setAvailableBalance(userAccountBalance.getAvailableBalance().subtract(itransaction.getAmount()));
//        userDepUnitAccountBalance.setTransactionsBalance(userDepUnitAccountBalance.getTransactionsBalance().add(itransaction.getAmount()));
//        userDepUnitAccountBalance.setAvailableBalance(userDepUnitAccountBalance.getAvailableBalance().subtract(itransaction.getAmount()));
//        if (userAccountBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (userDepUnitAccountBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T15)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      for (ITransaction itransaction : itransactions) {
//        GenAccountBalance genAccountBalanceFrom = genAccountBalanceRepository.find("genDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        genAccountBalanceFrom.setTransactionsBalance(genAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//        genAccountBalanceFrom.setAvailableBalance(genAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//        if (genAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T16)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      for (ITransaction itransaction : itransactions) {
//        UserAccountBalance userAccountBalance = userAccountBalanceRepository.find("user.id = ?1 and itemCode.id = ?2", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserAccountBalance.class.getName()));
//        UserDepUnitAccountBalance userDepUnitAccountBalance = userDepUnitAccountBalanceRepository.find("user.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3 and ou.id is null", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getToAccountEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserDepUnitAccountBalance.class.getName()));
//        userAccountBalance.setAvailableBalance(userAccountBalance.getAvailableBalance().subtract(itransaction.getAmount()));
//        userAccountBalance.setTransactionsBalance(userAccountBalance.getTransactionsBalance().add(itransaction.getAmount()));
//        userDepUnitAccountBalance.setAvailableBalance(userDepUnitAccountBalance.getAvailableBalance().subtract(itransaction.getAmount()));
//        userDepUnitAccountBalance.setTransactionsBalance(userDepUnitAccountBalance.getTransactionsBalance().add(itransaction.getAmount()));
//        if (userAccountBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (userDepUnitAccountBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T17)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      for (ITransaction itransaction : itransactions) {
//        GenAccountBalance genAccountBalanceFrom = genAccountBalanceRepository.find("genDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        genAccountBalanceFrom.setTransactionsBalance(genAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//        genAccountBalanceFrom.setAvailableBalance(genAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//        if (genAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T18)) {
//      notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      for (ITransaction itransaction : itransactions) {
//        OuAccountBalance ouAccountBalanceFrom = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3", itransaction.getFromOu().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getToAccountEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuManagerAccountBalance.class.getName()));
//        OuManagerAccountBalance ouManagerAccountBalanceFrom = ouManagerAccountBalanceRepository.find("manager.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3 and ou.id = ?4", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getToAccountEntity().getId(), itransaction.getFromOu().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        ouAccountBalanceFrom.setAvailableBalance(ouAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//        ouAccountBalanceFrom.setTransactionsBalance(ouAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//        ouManagerAccountBalanceFrom.setAvailableBalance(ouManagerAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//        ouManagerAccountBalanceFrom.setTransactionsBalance(ouManagerAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//        if (ouAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (ouManagerAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T19)) {
//      GenDepUnit genDepUnit = parDepUnitRepository.findByIdOptional(compTransaction.getFromAccountEntity().getId()).orElseThrow(() -> new NotFoundAlertException(ParDepUnit.class.getName())).getGenDepUnit();
//      notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      for (ITransaction itransaction : itransactions) {
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().add(itransaction.getAmount()));
//        parAccountBalanceFrom.setAvailableBalance(parAccountBalanceFrom.getAvailableBalance().subtract(itransaction.getAmount()));
//        if (parAccountBalanceFrom.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itransaction.getItemCodeEntity().getCode());
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.setItemsInTransactionWhereGroupTransaction(itransaction.getId(), inTransactionId);
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(true);
//          item.setInTransactionId(inTransactionId);
//          itemRepository.persist(item);
//        }
//      }
//    }
//  }
//
//  @Transactional
//  @Traced
//  public void signCompoundTransaction(Long id, String verifyCode, Long userId) {
//    CompoundTransaction compTransaction = compoundTransactionRepository.find("id", id).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    if (modeNotVerifyCode || compTransaction.getSignatureVerifyCode().equals(verifyCode)) {
//      User user = userRepository.findByIdOptional(Long.valueOf(userId)).orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
//      String userFullName = user.getFirstName() + " " + user.getLastName();
//      String userVathmos = vathmosService.getVathmosDescription(user.getIdVathmou());
//      int index = compTransaction.getSignaturesCount() + 1;
//      signPdf(compTransaction, userFullName, userVathmos, index);
//      compTransaction.setSignaturesCount(compTransaction.getSignaturesCount() + 1);
//      List<CompoundTransaction> childrenTransaction = compoundTransactionRepository.list("parentTransaction.id", id);
//      if (compTransaction.getType().equals(TransactionType.T04) && compTransaction.getSignaturesCount() > 3) {
//        // Από γενική σε γενική όταν υπογράφουν οι παραλήπτες δεν υπογραφουν και τις μερικές
//      } else {
//        childrenTransaction.forEach(tr -> tr.setSignaturesCount(compTransaction.getSignaturesCount()));
//      }
//      compTransaction.setSignatureVerifyCode(generateRandomString());
//      compoundTransactionRepository.persist(compTransaction);
//      logService.persistLogTransaction(id, userId, LogTransaction.Action.SIGN);
//      // TOO
//      if (compTransaction.getType().equals(TransactionType.T00)) {
//        childrenTransaction.forEach((tr -> {
//          signPdf(tr, userFullName, userVathmos, index);
//          logService.persistLogTransaction(tr.getId(), userId, LogTransaction.Action.SIGN);
//        }));
//        if (compTransaction.getSignaturesCount() == 1) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.TMIMATARCHIS);
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.DIEFTHYNTIS);
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.ANAPLIROTIS_DIEFTHYNTIS);
//        }
//        if (compTransaction.getSignaturesCount() == 3) {
//          if (childrenTransaction.size() == 0) {
//            completeCompoundTransaction(compTransaction.getId());
//          } else {
//            childrenTransaction.forEach(tr -> {
//              tr.setStatus(CompoundTransactionStatus.FOR_SIGNATURE);
//              tr.setSignaturesCount(compTransaction.getSignaturesCount());
//              tr.setSignatureVerifyCode(generateRandomString());
//              notificationService.persistNotificationSignature(tr, tr.getToAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//            });
//          }
//        }
//      }
//      // TO1
//      if (compTransaction.getType().equals(TransactionType.T01)) {
//        if (compTransaction.getSignaturesCount() == 1) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.TMIMATARCHIS);
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.DIEFTHYNTIS);
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.ANAPLIROTIS_DIEFTHYNTIS);
//        }
//        if (compTransaction.getSignaturesCount() == 3) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//        }
//        if (compTransaction.getSignaturesCount() == 4) {
//          if (compTransaction.getParentTransaction() != null && compTransaction.getParentTransaction().getType().equals(TransactionType.T00)) {
//            log.info(compTransaction.getParentTransaction().getId() + "");
//            if (compoundTransactionRepository.count("parentTransaction.id = ?1 and signaturescount < 4", compTransaction.getParentTransaction().getId()) == 0) {
//              log.info("Ολοκλήρωση συναλλαγής");
//              completeCompoundTransaction(compTransaction.getParentTransaction().getId());
//              List<CompoundTransaction> childrenTransactionsT01 = compoundTransactionRepository.list("parentTransaction.id = ?1 and type = ?2", compTransaction.getParentTransaction().getId(), TransactionType.T01);
//              childrenTransactionsT01.forEach(tr -> completeCompoundTransaction(tr.getId()));
//            }
//          } else {
//            completeCompoundTransaction(compTransaction.getId());
//          }
//        }
//      }
//      // T03
//      if (compTransaction.getType().equals(TransactionType.T03)) {
//        GenDepUnit genDepUnit = parDepUnitRepository.findByIdOptional(compTransaction.getFromAccountEntity().getId()).orElseThrow(() -> new NotFoundAlertException(ParDepUnit.class.getName())).getGenDepUnit();
//        if (compTransaction.getSignaturesCount() == 1) {
//          notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.TMIMATARCHIS);
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.DIEFTHYNTIS);
//          notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.ANAPLIROTIS_DIEFTHYNTIS);
//        }
//        if (compTransaction.getSignaturesCount() == 3) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//        }
//        if (compTransaction.getSignaturesCount() == 4) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//        }
//        if (compTransaction.getSignaturesCount() == 5) {
//          completeCompoundTransaction(compTransaction.getId());
//        }
//      }
//      // T04
//      if (compTransaction.getType().equals(TransactionType.T04)) {
//        if (compTransaction.getSignaturesCount() == 1) {
//          childrenTransaction.forEach((tr -> signPdf(tr, userFullName, userVathmos, index)));
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.TMIMATARCHIS);
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          childrenTransaction.forEach((tr -> signPdf(tr, userFullName, userVathmos, index)));
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.DIEFTHYNTIS);
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.ANAPLIROTIS_DIEFTHYNTIS);
//        }
//        if (compTransaction.getSignaturesCount() == 3) {
//          childrenTransaction.forEach((tr -> signPdf(tr, userFullName, userVathmos, index)));
//          if (childrenTransaction.size() == 0) {
//            compTransaction.setPartiallyCompleted(true);
//            notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//          } else {
//            childrenTransaction.forEach(tr -> {
//              tr.setStatus(CompoundTransactionStatus.FOR_SIGNATURE);
//              tr.setSignaturesCount(compTransaction.getSignaturesCount());
//              tr.setSignatureVerifyCode(generateRandomString());
//              notificationService.persistNotificationSignature(tr, tr.getFromAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//            });
//          }
//        }
//        if (compTransaction.getSignaturesCount() == 4) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.TMIMATARCHIS);
//        }
//        if (compTransaction.getSignaturesCount() == 5) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.DIEFTHYNTIS);
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.ANAPLIROTIS_DIEFTHYNTIS);
//        }
//        if (compTransaction.getSignaturesCount() == 6) {
//          List<CompoundTransaction> childrenTransactionsT19 = compoundTransactionRepository.list("parentTransaction.id = ?1 and type = ?2", compTransaction.getId(), TransactionType.T19);
//          childrenTransactionsT19.forEach(tr -> completeCompoundTransaction(tr.getId()));
//          completeCompoundTransaction(compTransaction.getId());
//        }
//      }
//      // TO5
//      if (compTransaction.getType().equals(TransactionType.T05)) {
//        childrenTransaction.forEach((tr -> {
//          signPdf(tr, userFullName, userVathmos, index);
//          logService.persistLogTransaction(tr.getId(), userId, LogTransaction.Action.SIGN);
//        }));
//        if (compTransaction.getSignaturesCount() == 1) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.TMIMATARCHIS);
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.DIEFTHYNTIS);
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.ANAPLIROTIS_DIEFTHYNTIS);
//        }
//        if (compTransaction.getSignaturesCount() == 3) {
//          if (childrenTransaction.size() == 0) {
//            completeCompoundTransaction(compTransaction.getId());
//          } else {
//            childrenTransaction.forEach(tr -> {
//              tr.setStatus(CompoundTransactionStatus.FOR_SIGNATURE);
//              tr.setSignaturesCount(compTransaction.getSignaturesCount());
//              tr.setSignatureVerifyCode(generateRandomString());
//              notificationService.persistNotificationSignature(tr, tr.getFromAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//            });
//          }
//        }
//      }
//      // TO6
//      if (compTransaction.getType().equals(TransactionType.T06)) {
//        if (compTransaction.getSignaturesCount() == 4) {
//          if (compTransaction.getParentTransaction().getType().equals(TransactionType.T05)) {
//            if (compoundTransactionRepository.count("parentTransaction.id = ?1  and signaturescount < 4", compTransaction.getParentTransaction().getId()) == 0) {
//              log.info("Ολοκλήρωση συναλλαγής");
//              completeCompoundTransaction(compTransaction.getParentTransaction().getId());
//              List<CompoundTransaction> childrenTransactionsT06 = compoundTransactionRepository.list("parentTransaction.id = ?1 and type = ?2", compTransaction.getParentTransaction().getId(), TransactionType.T06);
//              childrenTransactionsT06.forEach(tr -> completeCompoundTransaction(tr.getId()));
//            }
//          } else {
//            throw new InternalServerErrorException("Τ06 δεν έχει προεθει απο Τ05");
//          }
//        }
//      }
//      // TO7
//      if (compTransaction.getType().equals(TransactionType.T07)) {
//        if (compTransaction.getSignaturesCount() == 1) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToUser().getId(), true);
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          completeCompoundTransaction(compTransaction.getId());
//        }
//      }
//      // TO8
//      if (compTransaction.getType().equals(TransactionType.T08)) {
//        if (compTransaction.getSignaturesCount() == 1) {
//          completeCompoundTransaction(compTransaction.getId());
//          notificationService.persistNotificationTransactionCompleted(compTransaction, compTransaction.getFromUser().getId(), true);
//        }
//        /*if (compTransaction.getSignaturesCount() == 2) {
//          completeCompoundTransaction(compTransaction.getId());
//        }*/
//      }
//      // TO9
//      if (compTransaction.getType().equals(TransactionType.T09)) {
//        if (compTransaction.getSignaturesCount() == 1) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToUser().getId());
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          completeCompoundTransaction(compTransaction.getId());
//        }
//      }
//      // T12
//      if (compTransaction.getType().equals(TransactionType.T12)) {
//        if (compTransaction.getSignaturesCount() == 1) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToUser().getId(), true);
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          completeCompoundTransaction(compTransaction.getId());
//        }
//      }
//      // T13
//      if (compTransaction.getType().equals(TransactionType.T13)) {
//        if (compTransaction.getSignaturesCount() == 1) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromUser().getId());
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          completeCompoundTransaction(compTransaction.getId());
//        }
//      }
//      // T14
//      if (compTransaction.getType().equals(TransactionType.T14)) {
//        if (compTransaction.getSignaturesCount() == 1) {
//          completeCompoundTransaction(compTransaction.getId());
//          notificationService.persistNotificationTransactionCompleted(compTransaction, compTransaction.getFromUser().getId(), true);
//        }
//        /*if (compTransaction.getSignaturesCount() == 2) {
//          completeCompoundTransaction(compTransaction.getId());
//        }*/
//      }
//      // T15
//      if (compTransaction.getType().equals(TransactionType.T15)) {
//        if (compTransaction.getSignaturesCount() == 1) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToUser().getId(), true);
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          completeCompoundTransaction(compTransaction.getId());
//        }
//      }
//      // T16
//      if (compTransaction.getType().equals(TransactionType.T16)) {
//        if (compTransaction.getSignaturesCount() == 1) {
//          completeCompoundTransaction(compTransaction.getId());
//          notificationService.persistNotificationTransactionCompleted(compTransaction, compTransaction.getFromUser().getId(), true);
//        }
//        /*if (compTransaction.getSignaturesCount() == 2) {
//          completeCompoundTransaction(compTransaction.getId());
//        }*/
//      }
//      // T17
//      if (compTransaction.getType().equals(TransactionType.T17)) {
//        if (compTransaction.getSignaturesCount() == 1) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToUser().getId());
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          completeCompoundTransaction(compTransaction.getId());
//        }
//      }
//      // T18
//      if (compTransaction.getType().equals(TransactionType.T18)) {
//        if (compTransaction.getSignaturesCount() == 1) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromUser().getId());
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          completeCompoundTransaction(compTransaction.getId());
//        }
//      }
//      if (compTransaction.getType().equals(TransactionType.T19)) {
//        GenDepUnit genDepUnit = parDepUnitRepository.findByIdOptional(compTransaction.getFromAccountEntity().getId()).orElseThrow(() -> new NotFoundAlertException(ParDepUnit.class.getName())).getGenDepUnit();
//        if (compTransaction.getSignaturesCount() == 1) {
//          notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.TMIMATARCHIS);
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.DIEFTHYNTIS);
//          notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.ANAPLIROTIS_DIEFTHYNTIS);
//        }
//        if (compTransaction.getSignaturesCount() == 3) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//        }
//        if (compTransaction.getSignaturesCount() == 4) {
//          if (compTransaction.getParentTransaction() == null) {
//            completeCompoundTransaction(compTransaction.getId());
//          } else {
//            if (compTransaction.getParentTransaction().getType().equals(TransactionType.T04)) {
//              if (compoundTransactionRepository.count("parentTransaction.id = ?1 and signaturescount < 4", compTransaction.getParentTransaction().getId()) == 0) {
//                log.info("Μερικώς Ολοκλήρωση συναλλαγής");
//                CompoundTransaction parentCompoundTransaction = compTransaction.getParentTransaction();
//                parentCompoundTransaction.setPartiallyCompleted(true);
//                notificationService.persistNotificationSignature(parentCompoundTransaction, parentCompoundTransaction.getToAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//              }
//            } else {
//              throw new InternalServerErrorException("T19 δεν έχει προέλθει απο Τ04");
//            }
//          }
//        }
//      }
//    } else {
//      throw new BadRequestAlertException(ErrorMessage.VERIFICATION_CODE_ERROR.getDescription());
//    }
//  }
//
//  @Transactional
//  @Traced
//  public void resendNotificationAndEmail(Long compTransactionId) {
//    CompoundTransaction compTransaction = compoundTransactionRepository.find("id", compTransactionId).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    CompoundTransactionStatus status = compTransaction.getStatus();
//    if (!status.equals(CompoundTransactionStatus.FOR_SIGNATURE)) {
//      return;
//    }
//    TransactionType type = compTransaction.getType();
//    int signaturesCount = compTransaction.getSignaturesCount();
//    if (type.equals(TransactionType.T00)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.TMIMATARCHIS);
//      }
//      if (signaturesCount == 2) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.DIEFTHYNTIS);
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//    }
//    if (type.equals(TransactionType.T01)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.TMIMATARCHIS);
//      }
//      if (signaturesCount == 2) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.DIEFTHYNTIS);
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//      if (signaturesCount == 3) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//    }
//    if (type.equals(TransactionType.T03)) {
//      GenDepUnit genDepUnit = parDepUnitRepository.findByIdOptional(compTransaction.getFromAccountEntity().getId()).orElseThrow(() -> new NotFoundAlertException(ParDepUnit.class.getName())).getGenDepUnit();
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.TMIMATARCHIS);
//      }
//      if (signaturesCount == 2) {
//        notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.DIEFTHYNTIS);
//        notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//      if (signaturesCount == 3) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 4) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//    }
//    if (type.equals(TransactionType.T04)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.TMIMATARCHIS);
//      }
//      if (signaturesCount == 2) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.DIEFTHYNTIS);
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//      if (signaturesCount == 3) {
//        if (Boolean.TRUE.equals(compTransaction.getPartiallyCompleted())) {
//          notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//        }
//      }
//      if (signaturesCount == 4) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.TMIMATARCHIS);
//      }
//      if (signaturesCount == 5) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.DIEFTHYNTIS);
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//    }
//    if (type.equals(TransactionType.T05)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.TMIMATARCHIS);
//      }
//      if (signaturesCount == 2) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.DIEFTHYNTIS);
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//    }
//    if (type.equals(TransactionType.T06)) {
//      if (signaturesCount == 3) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//    }
//    if (type.equals(TransactionType.T07)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToUser().getId(), true);
//      }
//    }
//    if (type.equals(TransactionType.T08)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      /*if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromUser().getId(), true);
//      }*/
//    }
//    if (type.equals(TransactionType.T09)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToUser().getId());
//      }
//    }
//    if (type.equals(TransactionType.T12)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromUser().getId());
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToUser().getId(), true);
//      }
//    }
//    if (type.equals(TransactionType.T13)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromUser().getId());
//      }
//    }
//    if (type.equals(TransactionType.T14)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToUser().getId());
//      }
//      /*if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromUser().getId(), true);
//      }*/
//    }
//    if (type.equals(TransactionType.T15)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToUser().getId(), true);
//      }
//    }
//    if (type.equals(TransactionType.T16)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      /*if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromUser().getId(), true);
//      }*/
//    }
//    if (type.equals(TransactionType.T17)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToUser().getId());
//      }
//    }
//    if (type.equals(TransactionType.T18)) {
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getToAccountEntity(), Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromUser().getId());
//      }
//    }
//    if (type.equals(TransactionType.T19)) {
//      GenDepUnit genDepUnit = parDepUnitRepository.findByIdOptional(compTransaction.getFromAccountEntity().getId()).orElseThrow(() -> new NotFoundAlertException(ParDepUnit.class.getName())).getGenDepUnit();
//      if (signaturesCount == 0) {
//        notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (signaturesCount == 1) {
//        notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.TMIMATARCHIS);
//      }
//      if (signaturesCount == 2) {
//        notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.DIEFTHYNTIS);
//        notificationService.persistNotificationSignature(compTransaction, genDepUnit, Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//      if (signaturesCount == 3) {
//        notificationService.persistNotificationSignature(compTransaction, compTransaction.getFromAccountEntity(), Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//    }
//  }
//
//  @Transactional
//  @Traced
//  public boolean hasPermissionsToSignCompoundTransaction(Long id, Long userId) {
//    CompoundTransaction compTransaction = compoundTransactionRepository.find("id", id).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    if (!compTransaction.getStatus().equals(CompoundTransactionStatus.FOR_SIGNATURE)
//    ) {
//      return false;
//    }
//    List<Role> roles;
//    if (compTransaction.getType().equals(TransactionType.T00)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getToAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return roles.contains(Role.TMIMATARCHIS);
//      }
//      if (compTransaction.getSignaturesCount() == 2) {
//        return roles.contains(Role.DIEFTHYNTIS) || roles.contains(Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T01)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getFromAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return roles.contains(Role.TMIMATARCHIS);
//      }
//      if (compTransaction.getSignaturesCount() == 2) {
//        return roles.contains(Role.DIEFTHYNTIS) || roles.contains(Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//      if (compTransaction.getSignaturesCount() == 3) {
//        roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getToAccountEntity().getId());
//        return roles.contains(Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T03)) {
//      Long genDepUnitId = Long.valueOf(departmentService.getParDepUnit(Long.valueOf(compTransaction.getFromAccountEntity().getId())).getGenDepUnit().getId());
//      roles = permissionService.getUserRoleByDepUnit(userId, genDepUnitId);
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return roles.contains(Role.TMIMATARCHIS);
//      }
//      if (compTransaction.getSignaturesCount() == 2) {
//        return roles.contains(Role.DIEFTHYNTIS) || roles.contains(Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//      if (compTransaction.getSignaturesCount() == 3) {
//        roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getFromAccountEntity().getId());
//        return roles.contains(Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 4) {
//        roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getToAccountEntity().getId());
//        return roles.contains(Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T04)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getFromAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return roles.contains(Role.TMIMATARCHIS);
//      }
//      if (compTransaction.getSignaturesCount() == 2) {
//        return roles.contains(Role.DIEFTHYNTIS) || roles.contains(Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getToAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 3) {
//        return Boolean.TRUE.equals(compTransaction.getPartiallyCompleted()) && roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 4) {
//        return roles.contains(Role.TMIMATARCHIS);
//      }
//      if (compTransaction.getSignaturesCount() == 5) {
//        return roles.contains(Role.DIEFTHYNTIS) || roles.contains(Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T05)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getFromAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return roles.contains(Role.TMIMATARCHIS);
//      }
//      if (compTransaction.getSignaturesCount() == 2) {
//        return roles.contains(Role.DIEFTHYNTIS) || roles.contains(Role.ANAPLIROTIS_DIEFTHYNTIS);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T06)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getFromAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return false;
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return false;
//      }
//      if (compTransaction.getSignaturesCount() == 2) {
//        return false;
//      }
//      if (compTransaction.getSignaturesCount() == 3) {
//        return roles.contains(Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T07)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getFromAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return compTransaction.getToUser().getId().equals(userId);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T08)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getToAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      /*if (compTransaction.getSignaturesCount() == 1) {
//        return compTransaction.getFromUser().getId().equals(userId);
//      }*/
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T09)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getFromAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return compTransaction.getToUser().getId().equals(userId);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T12)) {
//      if (compTransaction.getSignaturesCount() == 0) {
//        return compTransaction.getFromUser().getId().equals(userId);
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return compTransaction.getToUser().getId().equals(userId);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T13)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getToAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return compTransaction.getFromUser().getId().equals(userId);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T14)) {
//      if (compTransaction.getSignaturesCount() == 0) {
//        return compTransaction.getToUser().getId().equals(userId);
//      }
//      /*if (compTransaction.getSignaturesCount() == 1) {
//        return compTransaction.getFromUser().getId().equals(userId);
//      }*/
//    }
//    if (compTransaction.getType().equals(TransactionType.T15)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getFromAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return compTransaction.getToUser().getId().equals(userId);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T16)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getToAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      /*if (compTransaction.getSignaturesCount() == 1) {
//        return compTransaction.getFromUser().getId().equals(userId);
//      }*/
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T17)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getFromAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return compTransaction.getToUser().getId().equals(userId);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T18)) {
//      roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getToAccountEntity().getId());
//      if (compTransaction.getSignaturesCount() == 0) {
//        return roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//      if (compTransaction.getSignaturesCount() == 1) {
//        return compTransaction.getFromUser().getId().equals(userId);
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T19)) {
//      if (compTransaction.getParentTransaction() == null) {
//        Long genDepUnitId = Long.valueOf(departmentService.getParDepUnit(Long.valueOf(compTransaction.getFromAccountEntity().getId())).getGenDepUnit().getId());
//        roles = permissionService.getUserRoleByDepUnit(userId, genDepUnitId);
//        if (compTransaction.getSignaturesCount() == 0) {
//          return roles.contains(Role.GENIKOS_DIACHEIRISTIS_YLIKOU);
//        }
//        if (compTransaction.getSignaturesCount() == 1) {
//          return roles.contains(Role.TMIMATARCHIS);
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          return roles.contains(Role.DIEFTHYNTIS) || roles.contains(Role.ANAPLIROTIS_DIEFTHYNTIS);
//        }
//      } else {
//        if (compTransaction.getSignaturesCount() == 0) {
//          return false;
//        }
//        if (compTransaction.getSignaturesCount() == 1) {
//          return false;
//        }
//        if (compTransaction.getSignaturesCount() == 2) {
//          return false;
//        }
//      }
//      if (compTransaction.getSignaturesCount() == 3) {
//        roles = permissionService.getUserRoleByDepUnit(userId, compTransaction.getFromAccountEntity().getId());
//        return roles.contains(Role.MERIKOS_DIACHEIRISTIS_YLIKOU);
//      }
//    }
//    return false;
//  }
//
//  @Transactional
//  @Traced
//  public boolean hasPermissionsToRejectCompoundTransaction(Long id, Long userId) {
//    if (!hasPermissionsToSignCompoundTransaction(id, userId)) {
//      return false;
//    }
//    return true;
//  }
//
//  @Transactional
//  @Traced
//  public PageList getCompoundTransactionFilesForSign(Long id, Long userDepUnitId) {
//    CompoundTransaction compTransaction = compoundTransactionRepository.find("id", id).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    List<CompoundTransactionFile> list = new ArrayList<>();
//    list.add(compTransaction.getFiles().stream()
//        .filter(fl -> fl.getType().name().equals(compTransaction.getType().name().replace("T", "D"))).findFirst().get());
//    // Συμπεριλαμβάνω όλα τα αρχεία των παραγόμενων συναλλαγών εκτός αν 'Ανακατανομή απο γενική σε γενική' στον παραλήπτη δεν τα βάζω
//    if (!(compTransaction.getType().equals(TransactionType.T04) && compTransaction.getToAccountEntity().getId().equals(userDepUnitId))) {
//      List<CompoundTransaction> childrenTransactions = compoundTransactionRepository.list("parentTransaction.id = ?1", id);
//      childrenTransactions.forEach(tr -> list.add(tr.getFiles().stream()
//          .filter(fl -> fl.getType().name().equals(tr.getType().name().replace("T", "D"))).findFirst().get()));
//    }
//    return new PageList<>(transactionMapper.toCompoundTransactionFileDto(list));
//  }
//
//  @Transactional
//  @Traced
//  public void signPdf(CompoundTransaction compTransaction, String userFullName, String userVathmos, int index) {
//    try {
//      signingservice.signPdfDocumentFromRepository(compTransaction.getId(),
//          compTransaction.getFiles().stream()
//              .filter(fl -> fl.getType().name().equals(compTransaction.getType().name().replace("T", "D"))).findFirst().get().getName(),
//          "Υπεγράφη από  " + userFullName + (userVathmos != null ? " " + userVathmos : "")
//              + " εκ μέρους της ΕΛ.ΑΣ. στις " + PDFUtils.getTimestamp(), index);
//    } catch (Exception e) {
//      e.printStackTrace();
//      log.error(e.getMessage());
//      throw new InternalServerErrorException();
//    }
//  }
//
//  @Transactional
//  @Traced
//  public void completeCompoundTransaction(Long id) {
//    CompoundTransaction compTransaction = compoundTransactionRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    if (!compTransaction.getStatus().equals(CompoundTransactionStatus.FOR_SIGNATURE)) {
//      throw new BadRequestAlertException("Η συναλλαγή δεν μπορεί να εκτελεστεί επειδή είναι σε κατάσταση: " + compTransaction.getStatus().getDescription());
//    }
//    // validate itransactions
//    for (ITransaction itransaction : compTransaction.getItransactions()) {
//      if (itransaction.getItem() != null && !Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//        validateDiscreetItems(itransaction, compTransaction.getType(), itransaction.getItem());
//      }
//    }
//    compTransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//    compTransaction.setPartiallyCompleted(null);
//    compTransaction.setCompletedTimestamp(LocalDateTime.now(ZoneOffset.UTC));
//
//    List<ITransaction> itransactions = iTransactionRepository.list("cTransaction.id = ?1 and groupParentTransaction.id IS NULL", id);
//    //00 Αρχική Εγγραφή σε Γενική
//    if (compTransaction.getType().equals(TransactionType.T00)) {
//      for (ITransaction itransaction : itransactions) {
//        String toAccount = itransaction.getToAccountEntity().getCode();
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T00 i-transaction to " + toAccount + " for item " + itemCode + " with amount " + amount);
//        balanceService.initGenAccountBalance(toAccount, itemCode);
//        GenAccountBalance genAccountBalance = genAccountBalanceRepository.find("genDepUnit.code = ?1 and itemCode.code = ?2", toAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        //success
//        genAccountBalance.setBalance(genAccountBalance.getBalance().add(amount));
//        genAccountBalance.setTotalBalance(genAccountBalance.getTotalBalance().add(amount));
//        genAccountBalance.setAvailableBalance(genAccountBalance.getAvailableBalance().add(amount));
//        if (itransaction.getProxyTransactionTo() != null) {
//          genAccountBalance.setTransactionsBalance(genAccountBalance.getTransactionsBalance().add(amount));
//          genAccountBalance.setAvailableBalance(genAccountBalance.getAvailableBalance().subtract(amount));
//        }
//        //SOS together
//        genAccountBalanceRepository.persist(genAccountBalance);
//        if (itransaction.getItem() != null) {
//          if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//            List<ITransaction> ch = iTransactionRepository.list("groupParentTransaction.id = ?1", itransaction.getId());
//            for (ITransaction it : ch) {
//              Item item = it.getItem();
//              item.setAssigneeDepUnit(genAccountBalance.getGenDepUnit());
//              item.setDraft(false);
//              item.setInTransaction(false);
//              item.setInTransactionId(null);
//              itemRepository.persist(item);
//            }
//            //int updatedCount = itemRepository.assignItemsToDepUnitWhereGroupTransaction(genAccountBalance.getGenDepUnit().getId(), itransaction.getId());
//            //log.info("Updated count " + updatedCount);
//          } else  {
//            Item item = itransaction.getItem();
//            item.setAssigneeDepUnit(genAccountBalance.getGenDepUnit());
//            item.setDraft(false);
//            item.setInTransaction(false);
//            item.setInTransactionId(null);
//            itemRepository.persist(item);
//          }
//        }
//      } //for each itransaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //00
//
//    //01 Διανομή απο Γενική σε Μερική
//    if (compTransaction.getType().equals(TransactionType.T01)) {
//      for (ITransaction itransaction : itransactions) {
//        String fromAccount = itransaction.getFromAccountEntity().getCode();
//        String toAccount = itransaction.getToAccountEntity().getCode();
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T01 i-transaction from " + fromAccount + " to " + toAccount + " for item " + itemCode + " with amount " + amount);
//        //balanceService.initGenAccountBalance(fromAccount, itemCode);
//        GenAccountBalance genAccountBalance = genAccountBalanceRepository.find("genDepUnit.code = ?1 and itemCode.code = ?2", fromAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        balanceService.initParAccountBalance(toAccount, itemCode);
//        if ((genAccountBalance.getBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((genAccountBalance.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        ParAccountBalance paraccountbalance = parAccountBalanceRepository.find("parDepUnit.code = ?1 and itemCode.code = ?2", toAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        //success
//        genAccountBalance.setBalance(genAccountBalance.getBalance().subtract(amount));
//        genAccountBalance.setTransactionsBalance(genAccountBalance.getTransactionsBalance().subtract(amount));
//        paraccountbalance.setBalance(paraccountbalance.getBalance().add(amount));
//        paraccountbalance.setTotalBalance(paraccountbalance.getTotalBalance().add(amount));
//        paraccountbalance.setAvailableBalance(paraccountbalance.getAvailableBalance().add(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.assignItemsToDepUnitWhereGroupTransaction(paraccountbalance.getParDepUnit().getId(), itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeDepUnit(paraccountbalance.getParDepUnit());
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        parAccountBalanceRepository.persist(paraccountbalance);
//        genAccountBalanceRepository.persist(genAccountBalance);
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //01
//
//    //03 Ανακατανομή απο Μερική σε Μερική
//    if (compTransaction.getType().equals(TransactionType.T03)) {
//      for (ITransaction itransaction : itransactions) {
//        String fromAccount = itransaction.getFromAccountEntity().getCode();
//        String toAccount = itransaction.getToAccountEntity().getCode();
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T01 i-transaction from " + fromAccount + " to " + toAccount + " for item " + itemCode + " with amount " + amount);
//        //Find Proper GenAccountBalance based on item
//        balanceService.initParAccountBalance(fromAccount, itemCode);
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.code = ?1 and itemCode.code = ?2", fromAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        balanceService.initParAccountBalance(toAccount, itemCode);
//        if ((parAccountBalanceFrom.getBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((parAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        parAccountBalanceFrom.setBalance(parAccountBalanceFrom.getBalance().subtract(amount));
//        parAccountBalanceFrom.setTotalBalance(parAccountBalanceFrom.getTotalBalance().subtract(amount));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        ParAccountBalance parAccountBalanceTo = parAccountBalanceRepository.find("parDepUnit.code = ?1 and itemCode.code = ?2", toAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        parAccountBalanceTo.setBalance(parAccountBalanceTo.getBalance().add(amount));
//        parAccountBalanceTo.setTotalBalance(parAccountBalanceTo.getTotalBalance().add(amount));
//        parAccountBalanceTo.setAvailableBalance(parAccountBalanceTo.getAvailableBalance().add(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.assignItemsToDepUnitWhereGroupTransaction(parAccountBalanceTo.getParDepUnit().getId(), itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeDepUnit(parAccountBalanceTo.getParDepUnit());
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        parAccountBalanceRepository.persist(parAccountBalanceFrom);
//        parAccountBalanceRepository.persist(parAccountBalanceTo);
//      }
//      //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //03
//
//    //04 Ανακατανομή απο Γενική σε Γενική
//    if (compTransaction.getType().equals(TransactionType.T04)) {
//      for (ITransaction itransaction : itransactions) {
//        String fromAccount = itransaction.getFromAccountEntity().getCode();
//        String toAccount = itransaction.getToAccountEntity().getCode();
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T04 i-transaction from " + fromAccount + " to " + toAccount + " for item " + itemCode + " with amount " + amount);
//        balanceService.initGenAccountBalance(fromAccount, itemCode);
//        GenAccountBalance genAccountBalanceFrom = genAccountBalanceRepository.find("genDepUnit.code = ?1 and itemCode.code = ?2", fromAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        balanceService.initGenAccountBalance(toAccount, itemCode);
//        if ((genAccountBalanceFrom.getBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((genAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        genAccountBalanceFrom.setBalance(genAccountBalanceFrom.getBalance().subtract(amount));
//        genAccountBalanceFrom.setTotalBalance(genAccountBalanceFrom.getTotalBalance().subtract(amount));
//        genAccountBalanceFrom.setTransactionsBalance(genAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        GenAccountBalance genAccountBalanceTo = genAccountBalanceRepository.find("genDepUnit.code = ?1 and itemCode.code = ?2", toAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        //success
//        genAccountBalanceTo.setBalance(genAccountBalanceTo.getBalance().add(amount));
//        genAccountBalanceTo.setTotalBalance(genAccountBalanceTo.getTotalBalance().add(amount));
//        genAccountBalanceTo.setAvailableBalance(genAccountBalanceTo.getAvailableBalance().add(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.assignItemsToDepUnitWhereGroupTransaction(genAccountBalanceTo.getGenDepUnit().getId(), itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeDepUnit(genAccountBalanceTo.getGenDepUnit());
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        genAccountBalanceRepository.persist(genAccountBalanceFrom);
//        genAccountBalanceRepository.persist(genAccountBalanceTo);
//
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //04
//
//    //05 Διαγραφή απο Γενική
//    if (compTransaction.getType().equals(TransactionType.T05)) {
//      for (ITransaction itransaction : itransactions) {
//        String fromAccount = itransaction.getFromAccountEntity().getCode();
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T05 i-transaction from " + fromAccount + " for item " + itemCode + " with amount " + amount);
//        //Find Proper GenAccountBalance based on item
//        GenAccountBalance genAccountBalanceFrom = genAccountBalanceRepository.find("genDepUnit.code = ?1 and itemCode.code = ?2", fromAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        if ((genAccountBalanceFrom.getTotalBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        //success
//        // Αφαιρώ από διαθέσιμο υπόλοιπο επειδή το υλικό είναι στην αποθήκη της γενικής
//        if (!Boolean.TRUE.equals(itransaction.getProxyTransactionFrom())) {
//          if ((genAccountBalanceFrom.getBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//            throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//          }
//          if ((genAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//            throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//          }
//          genAccountBalanceFrom.setBalance(genAccountBalanceFrom.getBalance().subtract(amount));
//          genAccountBalanceFrom.setTotalBalance(genAccountBalanceFrom.getTotalBalance().subtract(amount));
//          genAccountBalanceFrom.setTransactionsBalance(genAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//          if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//            List<ITransaction> ch = iTransactionRepository.list("groupParentTransaction.id = ?1", itransaction.getId());
//            for (ITransaction it : ch) {
//              Item item = it.getItem();
//              item.setAssigneeDepUnit(null);
//              item.setDeleted(true);
//              item.setInTransaction(false);
//              itemRepository.persist(item);
//            }
//            //int updatedCount = itemRepository.unassignItemsFromDepUnitWhereGroupTransactionAndDelete(itransaction.getId());
//            //log.info("Updated count " + updatedCount);
//          }
//          if (itransaction.getItem() != null) {
//            Item item = itransaction.getItem();
//            item.setAssigneeDepUnit(null);
//            item.setDeleted(true);
//            item.setInTransaction(false);
//            itemRepository.persist(item);
//          }
//        }
//        //SOS together
//        genAccountBalanceRepository.persist(genAccountBalanceFrom);
//
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //05
//
//    //06 Διαγραφή απο Μερική
//    if (compTransaction.getType().equals(TransactionType.T06)) {
//      // Αν πρόκειται για ανακατανομή τότε δεν εκτελώ καμία ενέργεια και αναμένω εκτέλεση όλης της συναλλαγής
//      for (ITransaction itransaction : itransactions) {
//        String fromAccount = itransaction.getFromAccountEntity().getCode();
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        // update also proxy transaction of parent
//        //iTransactionRepository.update("status = 'COMPLETED' where proxyDepUnitFrom.id = ?1", compTransaction.getId());
//        Log.info("Executing T06 i-transaction from " + fromAccount + " for item " + itemCode + " with amount " + amount);
//        //Find Proper GenAccountBalance based on item
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.code = ?1 and itemCode.code = ?2", fromAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        if ((parAccountBalanceFrom.getBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((parAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        String fromAccountGen = parAccountBalanceFrom.getParDepUnit().getGenDepUnit().getCode();
//        GenAccountBalance genAccountBalanceFrom = genAccountBalanceRepository.find("genDepUnit.code = ?1 and itemCode.code = ?2", fromAccountGen, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        if ((genAccountBalanceFrom.getTotalBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        //success
//        // Αφαιρώ από διαθέσιμο υπόλοιπο επειδή το υλικό είναι στην αποθήκη της γενικής
//        parAccountBalanceFrom.setBalance(parAccountBalanceFrom.getBalance().subtract(amount));
//        parAccountBalanceFrom.setTotalBalance(parAccountBalanceFrom.getTotalBalance().subtract(amount));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        genAccountBalanceFrom.setTotalBalance(genAccountBalanceFrom.getTotalBalance().subtract(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unassignItemsFromDepUnitWhereGroupTransactionAndDelete(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeDepUnit(null);
//          item.setDeleted(true);
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        parAccountBalanceRepository.persist(parAccountBalanceFrom);
//        genAccountBalanceRepository.persist(genAccountBalanceFrom);
//
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //05
//
//    //07 Χρέωση
//    if (compTransaction.getType().equals(TransactionType.T07)) {
//      for (ITransaction itransaction : itransactions) {
//        String fromAccount = itransaction.getFromAccountEntity().getCode();
//        String toUser = String.valueOf(itransaction.getToUser().getId());
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T07 i-transaction from " + fromAccount + " to user " + toUser + " for item " + itemCode + " with amount " + amount);
//        //Find Proper GenAccountBalance based on item
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.code = ?1 and itemCode.code = ?2", fromAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        balanceService.initUserAccountBalance(itransaction.getToUser().getId(), itemCode);
//        balanceService.initUserDepUnitAccountBalance(itransaction.getToUser().getId(), itemCode, parAccountBalanceFrom.getParDepUnit().getId());
//        UserAccountBalance userAccountBalance = userAccountBalanceRepository.find("user.id = ?1 and itemCode.code = ?2", itransaction.getToUser().getId(), itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserAccountBalance.class.getName()));
//        if ((parAccountBalanceFrom.getBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((parAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        //success
//        userAccountBalance.setTotalBalance(userAccountBalance.getTotalBalance().add(amount));
//        userAccountBalance.setAvailableBalance(userAccountBalance.getAvailableBalance().add(amount));
//        UserDepUnitAccountBalance userDepUnitAccountBalance = userDepUnitAccountBalanceRepository.find("user.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3 and ou.id is null", itransaction.getToUser().getId(), itemCode, parAccountBalanceFrom.getParDepUnit().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserDepUnitAccountBalance.class.getName()));
//        userDepUnitAccountBalance.setTotalBalance(userDepUnitAccountBalance.getTotalBalance().add(amount));
//        userDepUnitAccountBalance.setAvailableBalance(userDepUnitAccountBalance.getAvailableBalance().add(amount));
//        parAccountBalanceFrom.setBalance(parAccountBalanceFrom.getBalance().subtract(amount));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.assignItemsToUserWhereGroupTransaction(userAccountBalance.getUser().getId(), itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeUser(userAccountBalance.getUser());
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        parAccountBalanceRepository.persist(parAccountBalanceFrom);
//        userAccountBalanceRepository.persist(userAccountBalance);
//        userDepUnitAccountBalanceRepository.persist(userDepUnitAccountBalance);
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //07
//
//    //08 Αποχρέωση
//    if (compTransaction.getType().equals(TransactionType.T08)) {
//      for (ITransaction itransaction : itransactions) {
//        String toAccount = itransaction.getToAccountEntity().getCode();
//        String fromUser = String.valueOf(itransaction.getFromUser().getId());
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T08 i-transaction to " + toAccount + " from user " + fromUser + " for item " + itemCode + " with amount " + amount);
//        //Find Proper GenAccountBalance based on item
//        ParAccountBalance parAccountBalanceTo = parAccountBalanceRepository.find("parDepUnit.code = ?1 and itemCode.code = ?2", toAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        UserDepUnitAccountBalance userDepUnitAccountBalance = userDepUnitAccountBalanceRepository.find("user.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3 and ou.id is null", itransaction.getFromUser().getId(), itemCode, parAccountBalanceTo.getParDepUnit().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserDepUnitAccountBalance.class.getName()));
//        if ((userDepUnitAccountBalance.getTotalBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE_FROM_DEP_UNIT.label + ' ' + itemCode + ' ' + toAccount);
//        }
//        if ((userDepUnitAccountBalance.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE_FROM_DEP_UNIT.label + ' ' + itemCode + ' ' + toAccount);
//        }
//        if (parAccountBalanceTo.getTotalBalance().compareTo(parAccountBalanceTo.getBalance().add(amount)) == -1) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE.label + ' ' + itemCode);
//        }
//        UserAccountBalance userAccountBalance = userAccountBalanceRepository.find("user.id = ?1 and itemCode.code = ?2", itransaction.getFromUser().getId(), itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserAccountBalance.class.getName()));
//        if ((userAccountBalance.getTotalBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE_FROM_USER.label + ' ' + itemCode);
//        }
//        if ((userAccountBalance.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE_FROM_USER.label + ' ' + itemCode);
//        }
//        //success
//        parAccountBalanceTo.setBalance(parAccountBalanceTo.getBalance().add(amount));
//        parAccountBalanceTo.setAvailableBalance(parAccountBalanceTo.getAvailableBalance().add(amount));
//        userAccountBalance.setTotalBalance(userAccountBalance.getTotalBalance().subtract(amount));
//        userAccountBalance.setTransactionsBalance(userAccountBalance.getTransactionsBalance().subtract(amount));
//        userDepUnitAccountBalance.setTotalBalance(userDepUnitAccountBalance.getTotalBalance().subtract(amount));
//        userDepUnitAccountBalance.setTransactionsBalance(userDepUnitAccountBalance.getTransactionsBalance().subtract(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unassignItemsFromUserWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeUser(null);
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        parAccountBalanceRepository.persist(parAccountBalanceTo);
//        userAccountBalanceRepository.persist(userAccountBalance);
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //08
//
//    //09 Χρέωση σε τοπικό υπεύθυνο
//    if (compTransaction.getType().equals(TransactionType.T09)) {
//      for (ITransaction itransaction : itransactions) {
//        String fromAccount = itransaction.getFromAccountEntity().getCode();
//        String toUser = String.valueOf(itransaction.getToUser().getId());
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T09 i-transaction from " + fromAccount + " to user " + toUser + " for item " + itemCode + " with amount " + amount);
//        if (ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2 and depUnit.id != ?3", itransaction.getToOu().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getFromAccountEntity().getId()).firstResultOptional().isPresent()) {
//          throw new BadRequestAlertException("Η υπηρεσία λαμβάνει τον συγκεκριμένο κωδικό υλικού από άλλη διαχείριση. Κωδικός Υλικού: " + itransaction.getItemCodeEntity().getCode());
//        }
//        //Find Proper GenAccountBalance based on item
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.code = ?1 and itemCode.code = ?2", fromAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        balanceService.initOuAccountBalance(itransaction.getFromAccountEntity().getId(), itemCode, itransaction.getToOu().getId());
//        balanceService.initOuManagerAccountBalance(itransaction.getToUser().getId(), itemCode, itransaction.getToOu().getId(), itransaction.getFromAccountEntity().getId());
//        if ((parAccountBalanceFrom.getBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((parAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        //success
//        OuAccountBalance ouAccountBalance = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3", itransaction.getToOu().getId(), itemCode, itransaction.getFromAccountEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuManagerAccountBalance.class.getName()));
//        ouAccountBalance.setTotalBalance(ouAccountBalance.getTotalBalance().add(amount));
//        ouAccountBalance.setBalance(ouAccountBalance.getBalance().add(amount));
//        ouAccountBalance.setAvailableBalance(ouAccountBalance.getAvailableBalance().add(amount));
//        OuManagerAccountBalance ouManagerAccountBalance = ouManagerAccountBalanceRepository.find("manager.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3 and ou.id = ?4", itransaction.getToUser().getId(), itemCode, itransaction.getFromAccountEntity().getId(), itransaction.getToOu().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        ouManagerAccountBalance.setTotalBalance(ouManagerAccountBalance.getTotalBalance().add(amount));
//        ouManagerAccountBalance.setAvailableBalance(ouManagerAccountBalance.getAvailableBalance().add(amount));
//        parAccountBalanceFrom.setBalance(parAccountBalanceFrom.getBalance().subtract(amount));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.assignItemsToManagerAndOuWhereGroupTransaction(ouManagerAccountBalance.getManager().getId(), ouManagerAccountBalance.getOu().getId(), itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeUser(ouManagerAccountBalance.getManager());
//          item.setAssigneeOu(ouAccountBalance.getOu());
//          item.setAssignedToTopikosYpeythynos(true);
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        parAccountBalanceRepository.persist(parAccountBalanceFrom);
//        ouAccountBalanceRepository.persist(ouAccountBalance);
//        ouManagerAccountBalanceRepository.persist(ouManagerAccountBalance);
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //09
//
//    //12 Επαναχρέωση
//    if (compTransaction.getType().equals(TransactionType.T12)) {
//      for (ITransaction itransaction : itransactions) {
//        String fromAccount = itransaction.getFromOu().getName();
//        String toUser = String.valueOf(itransaction.getToUser().getId());
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T12 i-transaction from " + fromAccount + " to user " + toUser + " for item " + itemCode + " with amount " + amount);
//        //Find Proper GenAccountBalance based on item
//        OuAccountBalance ouAccountBalanceFrom = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.code = ?2", itransaction.getFromOu().getId(), itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        balanceService.initUserAccountBalance(itransaction.getToUser().getId(), itemCode);
//        balanceService.initUserDepUnitAccountBalance(itransaction.getToUser().getId(), itemCode, ouAccountBalanceFrom.getDepUnit().getId(), ouAccountBalanceFrom.getOu().getId());
//        if ((ouAccountBalanceFrom.getBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((ouAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        OuManagerAccountBalance ouManagerAccountBalanceFrom = ouManagerAccountBalanceRepository.find("ou.id = ?1 and itemCode.code = ?2 and manager.id = ?3", itransaction.getFromOu().getId(), itemCode, itransaction.getFromUser().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuManagerAccountBalance.class.getName()));
//        if ((ouManagerAccountBalanceFrom.getTotalBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((ouManagerAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        UserAccountBalance userAccountBalance = userAccountBalanceRepository.find("user.id = ?1 and itemCode.code = ?2", itransaction.getToUser().getId(), itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserAccountBalance.class.getName()));
//        UserDepUnitAccountBalance userDepUnitAccountBalance = userDepUnitAccountBalanceRepository.find("user.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3 and ou.id = ?4", itransaction.getToUser().getId(), itemCode, ouAccountBalanceFrom.getDepUnit().getId(), ouAccountBalanceFrom.getOu().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserDepUnitAccountBalance.class.getName()));
//        //success
//        userAccountBalance.setTotalBalance(userAccountBalance.getTotalBalance().add(amount));
//        userAccountBalance.setAvailableBalance(userAccountBalance.getAvailableBalance().add(amount));
//        userDepUnitAccountBalance.setTotalBalance(userDepUnitAccountBalance.getTotalBalance().add(amount));
//        userDepUnitAccountBalance.setAvailableBalance(userDepUnitAccountBalance.getAvailableBalance().add(amount));
//        ouAccountBalanceFrom.setBalance(ouAccountBalanceFrom.getBalance().subtract(amount));
//        ouAccountBalanceFrom.setTransactionsBalance(ouAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        ouManagerAccountBalanceFrom.setTotalBalance(ouManagerAccountBalanceFrom.getTotalBalance().subtract(amount));
//        ouManagerAccountBalanceFrom.setTransactionsBalance(ouManagerAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.assignItemsToUserAndOuWhereGroupTransaction(userAccountBalance.getUser().getId(), ouAccountBalanceFrom.getOu().getId(), itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeUser(userAccountBalance.getUser());
//          item.setAssignedToTopikosYpeythynos(false);
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        ouAccountBalanceRepository.persist(ouAccountBalanceFrom);
//        ouManagerAccountBalanceRepository.persist(ouManagerAccountBalanceFrom);
//        userAccountBalanceRepository.persist(userAccountBalance);
//        userDepUnitAccountBalanceRepository.persist(userDepUnitAccountBalance);
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //12
//
//    //13
//    if (compTransaction.getType().equals(TransactionType.T13)) {
//      for (ITransaction itransaction : itransactions) {
//        String toAccount = itransaction.getToAccountEntity().getCode();
//        String fromUser = String.valueOf(itransaction.getFromUser().getId());
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T13 i-transaction from user" + fromUser + " to " + toAccount + " for item " + itemCode + " with amount " + amount);
//        //Find Proper GenAccountBalance based on item
//        OuManagerAccountBalance ouManagerAccountBalanceFrom = ouManagerAccountBalanceRepository.find("manager.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3 and ou.id = ?4", itransaction.getFromUser().getId(), itemCode, itransaction.getToAccountEntity().getId(), itransaction.getFromOu().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        if ((ouManagerAccountBalanceFrom.getTotalBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((ouManagerAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        OuAccountBalance ouAccountBalanceFrom = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3", itransaction.getFromOu().getId(), itemCode, itransaction.getToAccountEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuManagerAccountBalance.class.getName()));
//        ParAccountBalance parAccountBalanceTo = parAccountBalanceRepository.find("parDepUnit.code = ?1 and itemCode.code = ?2", toAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        //success
//        parAccountBalanceTo.setBalance(parAccountBalanceTo.getBalance().add(amount));
//        parAccountBalanceTo.setAvailableBalance(parAccountBalanceTo.getAvailableBalance().add(amount));
//        ouAccountBalanceFrom.setTotalBalance(ouAccountBalanceFrom.getTotalBalance().subtract(amount));
//        ouAccountBalanceFrom.setTransactionsBalance(ouAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        ouAccountBalanceFrom.setBalance(ouAccountBalanceFrom.getBalance().subtract(amount));
//        ouManagerAccountBalanceFrom.setTotalBalance(ouManagerAccountBalanceFrom.getTotalBalance().subtract(amount));
//        ouManagerAccountBalanceFrom.setTransactionsBalance(ouManagerAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unassignItemsFromUserWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeUser(null);
//          item.setAssigneeOu(null);
//          item.setAssignedToTopikosYpeythynos(false);
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        ouAccountBalanceRepository.persist(ouAccountBalanceFrom);
//        ouManagerAccountBalanceRepository.persist(ouManagerAccountBalanceFrom);
//        parAccountBalanceRepository.persist(parAccountBalanceTo);
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //13
//
//    //14
//    if (compTransaction.getType().equals(TransactionType.T14)) {
//      for (ITransaction itransaction : itransactions) {
//        String toUser = String.valueOf(itransaction.getToUser().getId());
//        String fromUser = String.valueOf(itransaction.getFromUser().getId());
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T14 i-transaction from user" + fromUser + " to user " + toUser + " for item " + itemCode + " with amount " + amount);
//        //Find Proper GenAccountBalance based on item
//        OuAccountBalance ouAccountBalanceTo = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.code = ?2", itransaction.getToOu().getId(), itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        UserDepUnitAccountBalance userDepUnitAccountBalance = userDepUnitAccountBalanceRepository.find("user.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3 and ou.id = ?4", itransaction.getFromUser().getId(), itemCode, ouAccountBalanceTo.getDepUnit().getId(), ouAccountBalanceTo.getOu().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserDepUnitAccountBalance.class.getName()));
//        if ((userDepUnitAccountBalance.getTotalBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((userDepUnitAccountBalance.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        UserAccountBalance userAccountBalance = userAccountBalanceRepository.find("user.id = ?1 and itemCode.code = ?2", itransaction.getFromUser().getId(), itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserAccountBalance.class.getName()));
//        userAccountBalance.setTotalBalance(userAccountBalance.getTotalBalance().subtract(amount));
//        userAccountBalance.setTransactionsBalance(userAccountBalance.getTransactionsBalance().subtract(amount));
//        userDepUnitAccountBalance.setTotalBalance(userDepUnitAccountBalance.getTotalBalance().subtract(amount));
//        userDepUnitAccountBalance.setTransactionsBalance(userDepUnitAccountBalance.getTransactionsBalance().subtract(amount));
//        OuManagerAccountBalance ouManagerAccountBalanceTo = ouManagerAccountBalanceRepository.find("ou.id = ?1 and itemCode.code = ?2 and manager.id = ?3", itransaction.getToOu().getId(), itemCode, itransaction.getToUser().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuManagerAccountBalance.class.getName()));
//        ouManagerAccountBalanceTo.setTotalBalance(ouManagerAccountBalanceTo.getTotalBalance().add(amount));
//        ouManagerAccountBalanceTo.setAvailableBalance(ouManagerAccountBalanceTo.getAvailableBalance().add(amount));
//        ouAccountBalanceTo.setBalance(ouAccountBalanceTo.getBalance().add(amount));
//        ouAccountBalanceTo.setAvailableBalance(ouAccountBalanceTo.getAvailableBalance().add(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.assignItemsToManagerAndOuWhereGroupTransaction(ouManagerAccountBalanceTo.getManager().getId(), ouManagerAccountBalanceTo.getOu().getId(), itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeUser(ouManagerAccountBalanceTo.getManager());
//          item.setAssignedToTopikosYpeythynos(true);
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        ouAccountBalanceRepository.persist(ouAccountBalanceTo);
//        ouManagerAccountBalanceRepository.persist(ouManagerAccountBalanceTo);
//        userAccountBalanceRepository.persist(userAccountBalance);
//        userDepUnitAccountBalanceRepository.persist(userDepUnitAccountBalance);
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //14
//
//    //15 Χρέωση
//    if (compTransaction.getType().equals(TransactionType.T15)) {
//      for (ITransaction itransaction : itransactions) {
//        String fromAccount = itransaction.getFromAccountEntity().getCode();
//        String toUser = String.valueOf(itransaction.getToUser().getId());
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T15 i-transaction from " + fromAccount + " to user " + toUser + " for item " + itemCode + " with amount " + amount);
//        //Find Proper GenAccountBalance based on item
//        GenAccountBalance genAccountBalanceFrom = genAccountBalanceRepository.find("genDepUnit.code = ?1 and itemCode.code = ?2", fromAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        balanceService.initUserAccountBalance(itransaction.getToUser().getId(), itemCode);
//        balanceService.initUserDepUnitAccountBalance(itransaction.getToUser().getId(), itemCode, genAccountBalanceFrom.getGenDepUnit().getId());
//        UserAccountBalance userAccountBalance = userAccountBalanceRepository.find("user.id = ?1 and itemCode.code = ?2", itransaction.getToUser().getId(), itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserAccountBalance.class.getName()));
//        if ((genAccountBalanceFrom.getBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((genAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        //success
//        userAccountBalance.setTotalBalance(userAccountBalance.getTotalBalance().add(amount));
//        userAccountBalance.setAvailableBalance(userAccountBalance.getAvailableBalance().add(amount));
//        UserDepUnitAccountBalance userDepUnitAccountBalance = userDepUnitAccountBalanceRepository.find("user.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3 and ou.id is null", itransaction.getToUser().getId(), itemCode, genAccountBalanceFrom.getGenDepUnit().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserDepUnitAccountBalance.class.getName()));
//        userDepUnitAccountBalance.setTotalBalance(userDepUnitAccountBalance.getTotalBalance().add(amount));
//        userDepUnitAccountBalance.setAvailableBalance(userDepUnitAccountBalance.getAvailableBalance().add(amount));
//        genAccountBalanceFrom.setBalance(genAccountBalanceFrom.getBalance().subtract(amount));
//        genAccountBalanceFrom.setTransactionsBalance(genAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.assignItemsToUserWhereGroupTransaction(userAccountBalance.getUser().getId(), itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeUser(userAccountBalance.getUser());
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        genAccountBalanceRepository.persist(genAccountBalanceFrom);
//        userAccountBalanceRepository.persist(userAccountBalance);
//        userDepUnitAccountBalanceRepository.persist(userDepUnitAccountBalance);
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //15
//
//    //16 Αποχρέωση
//    if (compTransaction.getType().equals(TransactionType.T16)) {
//      for (ITransaction itransaction : itransactions) {
//        String toAccount = itransaction.getToAccountEntity().getCode();
//        String fromUser = String.valueOf(itransaction.getFromUser().getId());
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T16 i-transaction to " + toAccount + " from user " + fromUser + " for item " + itemCode + " with amount " + amount);
//        //Find Proper GenAccountBalance based on item
//        GenAccountBalance genAccountBalanceTo = genAccountBalanceRepository.find("genDepUnit.code = ?1 and itemCode.code = ?2", toAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        UserAccountBalance userAccountBalance = userAccountBalanceRepository.find("user.id = ?1 and itemCode.code = ?2", itransaction.getFromUser().getId(), itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserAccountBalance.class.getName()));
//        UserDepUnitAccountBalance userDepUnitAccountBalance = userDepUnitAccountBalanceRepository.find("user.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3 and ou.id is null", itransaction.getFromUser().getId(), itemCode, genAccountBalanceTo.getGenDepUnit().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserDepUnitAccountBalance.class.getName()));
//        if ((userDepUnitAccountBalance.getTotalBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE_FROM_DEP_UNIT.label + ' ' + itemCode + ' ' + toAccount);
//        }
//        if ((userDepUnitAccountBalance.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE_FROM_DEP_UNIT.label + ' ' + itemCode + ' ' + toAccount);
//        }
//        if ((userAccountBalance.getTotalBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE_FROM_USER.label + ' ' + itemCode);
//        }
//        if ((userAccountBalance.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE_FROM_USER.label + ' ' + itemCode);
//        }
//        if (genAccountBalanceTo.getTotalBalance().compareTo(genAccountBalanceTo.getBalance().add(amount)) == -1) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE.label + ' ' + itemCode);
//        }
//        //success
//        genAccountBalanceTo.setBalance(genAccountBalanceTo.getBalance().add(amount));
//        genAccountBalanceTo.setAvailableBalance(genAccountBalanceTo.getAvailableBalance().add(amount));
//        userAccountBalance.setTotalBalance(userAccountBalance.getTotalBalance().subtract(amount));
//        userAccountBalance.setTransactionsBalance(userAccountBalance.getTransactionsBalance().subtract(amount));
//        userDepUnitAccountBalance.setTotalBalance(userDepUnitAccountBalance.getTotalBalance().subtract(amount));
//        userDepUnitAccountBalance.setTransactionsBalance(userDepUnitAccountBalance.getTransactionsBalance().subtract(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unassignItemsFromUserWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeUser(null);
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        genAccountBalanceRepository.persist(genAccountBalanceTo);
//        userAccountBalanceRepository.persist(userAccountBalance);
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //16
//
//    //17 Χρέωση σε τοπικό υπεύθυνο από Γενική
//    if (compTransaction.getType().equals(TransactionType.T17)) {
//      for (ITransaction itransaction : itransactions) {
//        String fromAccount = itransaction.getFromAccountEntity().getCode();
//        String toUser = String.valueOf(itransaction.getToUser().getId());
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T17 i-transaction from " + fromAccount + " to user " + toUser + " for item " + itemCode + " with amount " + amount);
//        if (ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2 and depUnit.id != ?3", itransaction.getToOu().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getFromAccountEntity().getId()).firstResultOptional().isPresent()) {
//          throw new BadRequestAlertException("Η υπηρεσία λαμβάνει τον συγκεκριμένο κωδικό υλικού από άλλη διαχείριση. Κωδικός Υλικού: " + itransaction.getItemCodeEntity().getCode());
//        }
//        //Find Proper GenAccountBalance based on item
//        GenAccountBalance genAccountBalanceFrom = genAccountBalanceRepository.find("genDepUnit.code = ?1 and itemCode.code = ?2", fromAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        balanceService.initOuAccountBalance(itransaction.getFromAccountEntity().getId(), itemCode, itransaction.getToOu().getId());
//        balanceService.initOuManagerAccountBalance(itransaction.getToUser().getId(), itemCode, itransaction.getToOu().getId(), itransaction.getFromAccountEntity().getId());
//        if ((genAccountBalanceFrom.getBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((genAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        //success
//        OuAccountBalance ouAccountBalance = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3", itransaction.getToOu().getId(), itemCode, itransaction.getFromAccountEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuManagerAccountBalance.class.getName()));
//        ouAccountBalance.setTotalBalance(ouAccountBalance.getTotalBalance().add(amount));
//        ouAccountBalance.setAvailableBalance(ouAccountBalance.getAvailableBalance().add(amount));
//        ouAccountBalance.setBalance(ouAccountBalance.getBalance().add(amount));
//        OuManagerAccountBalance ouManagerAccountBalance = ouManagerAccountBalanceRepository.find("manager.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3 and ou.id = ?4", itransaction.getToUser().getId(), itemCode, itransaction.getFromAccountEntity().getId(), itransaction.getToOu().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        ouManagerAccountBalance.setTotalBalance(ouManagerAccountBalance.getTotalBalance().add(amount));
//        ouManagerAccountBalance.setAvailableBalance(ouManagerAccountBalance.getAvailableBalance().add(amount));
//        genAccountBalanceFrom.setBalance(genAccountBalanceFrom.getBalance().subtract(amount));
//        genAccountBalanceFrom.setTransactionsBalance(genAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.assignItemsToManagerAndOuWhereGroupTransaction(ouManagerAccountBalance.getManager().getId(), ouManagerAccountBalance.getOu().getId(), itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeUser(ouManagerAccountBalance.getManager());
//          item.setAssigneeOu(ouAccountBalance.getOu());
//          item.setAssignedToTopikosYpeythynos(true);
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        genAccountBalanceRepository.persist(genAccountBalanceFrom);
//        ouAccountBalanceRepository.persist(ouAccountBalance);
//        ouManagerAccountBalanceRepository.persist(ouManagerAccountBalance);
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //17
//
//    //18
//    if (compTransaction.getType().equals(TransactionType.T18)) {
//      for (ITransaction itransaction : itransactions) {
//        String toAccount = itransaction.getToAccountEntity().getCode();
//        String fromUser = String.valueOf(itransaction.getFromUser().getId());
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T18 i-transaction from user" + fromUser + " to " + toAccount + " for item " + itemCode + " with amount " + amount);
//        //Find Proper GenAccountBalance based on item
//        OuManagerAccountBalance ouManagerAccountBalanceFrom = ouManagerAccountBalanceRepository.find("manager.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3 and ou.id = ?4", itransaction.getFromUser().getId(), itemCode, itransaction.getToAccountEntity().getId(), itransaction.getFromOu().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        if ((ouManagerAccountBalanceFrom.getTotalBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((ouManagerAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        OuAccountBalance ouAccountBalanceFrom = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.code = ?2 and depUnit.id = ?3", itransaction.getFromOu().getId(), itemCode, itransaction.getToAccountEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuManagerAccountBalance.class.getName()));
//        GenAccountBalance genAccountBalanceTo = genAccountBalanceRepository.find("genDepUnit.code = ?1 and itemCode.code = ?2", toAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        //success
//        genAccountBalanceTo.setBalance(genAccountBalanceTo.getBalance().add(amount));
//        genAccountBalanceTo.setAvailableBalance(genAccountBalanceTo.getAvailableBalance().add(amount));
//        ouAccountBalanceFrom.setTotalBalance(ouAccountBalanceFrom.getTotalBalance().subtract(amount));
//        ouAccountBalanceFrom.setBalance(ouAccountBalanceFrom.getBalance().subtract(amount));
//        ouAccountBalanceFrom.setTransactionsBalance(ouAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        ouManagerAccountBalanceFrom.setTotalBalance(ouManagerAccountBalanceFrom.getTotalBalance().subtract(amount));
//        ouManagerAccountBalanceFrom.setTransactionsBalance(ouManagerAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unassignItemsFromUserWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeUser(null);
//          item.setAssigneeOu(null);
//          item.setAssignedToTopikosYpeythynos(false);
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//        //SOS together
//        ouAccountBalanceRepository.persist(ouAccountBalanceFrom);
//        ouManagerAccountBalanceRepository.persist(ouManagerAccountBalanceFrom);
//        genAccountBalanceRepository.persist(genAccountBalanceTo);
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //18
//
//    //19 Αποχρέωση απο Μερική σε γενική (μπορει και λόγω ανακατανομής απο γενική σε γενική)
//    if (compTransaction.getType().equals(TransactionType.T19)) {
//      // Αν πρόκειται για ανακατανομή τότε δεν εκτελώ καμία ενέργεια και αναμένω εκτέλεση όλης της συναλλαγής
//      for (ITransaction itransaction : itransactions) {
//        String fromAccount = itransaction.getFromAccountEntity().getCode();
//        String itemCode = itransaction.getItemCode();
//        BigDecimal amount = itransaction.getAmount();
//        itransaction.setStatus(CompoundTransactionStatus.COMPLETED);
//        Log.info("Executing T19 i-transaction from " + fromAccount + " for item " + itemCode + " with amount " + amount);
//        //Find Proper GenAccountBalance based on item
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.code = ?1 and itemCode.code = ?2", fromAccount, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        if ((parAccountBalanceFrom.getBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        if ((parAccountBalanceFrom.getTransactionsBalance().subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.AMOUNT_OF_ITEMS_NOT_AVAILABLE.label + ' ' + itemCode);
//        }
//        //success
//        parAccountBalanceFrom.setBalance(parAccountBalanceFrom.getBalance().subtract(amount));
//        parAccountBalanceFrom.setTotalBalance(parAccountBalanceFrom.getTotalBalance().subtract(amount));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().subtract(amount));
//        String toAccountGen = parAccountBalanceFrom.getParDepUnit().getGenDepUnit().getCode();
//        GenAccountBalance genAccountBalanceto = genAccountBalanceRepository.find("genDepUnit.code = ?1 and itemCode.code = ?2", toAccountGen, itemCode).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        genAccountBalanceto.setBalance(genAccountBalanceto.getBalance().add(amount));
//        if (compTransaction.getParentTransaction() == null) {
//          genAccountBalanceto.setAvailableBalance(genAccountBalanceto.getAvailableBalance().add(amount));
//        } else {
//          if (compTransaction.getParentTransaction().getType().equals(TransactionType.T04)) {
//            // Αν προεχρεται απο συνναλλαγη Τ04 τότε τα βάζω προς συνναλλαγή το amount
//            genAccountBalanceto.setTransactionsBalance(genAccountBalanceto.getTransactionsBalance().add(amount));
//          } else {
//            throw new InternalServerErrorException("Η συνναλαγή δεν προέρχεται απο T04");
//          }
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          List<ITransaction> ch = iTransactionRepository.list("groupParentTransaction.id = ?1", itransaction.getId());
//          for (ITransaction it : ch) {
//            Item item = it.getItem();
//            item.setAssigneeDepUnit(genAccountBalanceto.getGenDepUnit());
//            if (compTransaction.getParentTransaction() == null) {
//              item.setInTransaction(false);
//              item.setInTransactionId(null);
//            }
//            itemRepository.persist(item);
//          }
//          //int updatedCount = itemRepository.assignItemsToDepUnitWhereGroupTransactionAndOnlyChangeDepUnit(genAccountBalanceFrom.getGenDepUnit().getId(), itransaction.getId());
//          //log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setAssigneeDepUnit(genAccountBalanceto.getGenDepUnit());
//          if (compTransaction.getParentTransaction() == null) {
//            item.setInTransaction(false);
//            item.setInTransactionId(null);
//          }
//          itemRepository.persist(item);
//        }
//        //SOS together
//        parAccountBalanceRepository.persist(parAccountBalanceFrom);
//        genAccountBalanceRepository.persist(genAccountBalanceto);
//      } //for each transaction
//      //Conclude Transaction
//      compoundTransactionRepository.persist(compTransaction);
//    } //19
//  }
//
//  @Transactional
//  @Traced
//  public void deleteCompoundTransaction(Long id) {
//    CompoundTransaction compoundTransaction = compoundTransactionRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    if (!compoundTransaction.getStatus().equals(CompoundTransactionStatus.DRAFT)) {
//      throw new BadRequestAlertException("Μπορείτε να διαγράψετε μόνο πρόχειρες συναλλαγές");
//    }
//    ochimaTransactionRepository.delete("cTransaction.id", id);
//    logService.deleteByTransactionId(id);
//    compoundTransactionRepository.delete(compoundTransaction);
//    for (ITransaction itransaction : compoundTransaction.getItransactions().stream().filter(tr -> tr.getItem() != null && tr.getItem().isDraft()).collect(Collectors.toList())) {
//      itemRepository.delete(itransaction.getItem());
//    }
//    storageService.deleteBucket(COMPOUND_TRANSACTION_BUCKET + id);
//  }
//
//  @Transactional
//  @Traced
//  public void rejectCompoundTransaction(Long id, Long userId) {
//    CompoundTransaction compTransaction = compoundTransactionRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    if (compTransaction.getParentTransaction() != null) {
//      compTransaction = compoundTransactionRepository.findByIdOptional(compTransaction.getParentTransaction().getId()).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    }
//    if (compTransaction.getStatus().equals(CompoundTransactionStatus.COMPLETED)) {
//      throw new BadRequestAlertException("Δεν μπορείτε να απορρίψετε ολοκληρωμένη συναλλαγή");
//    }
//    if (compTransaction.getStatus().equals(CompoundTransactionStatus.DRAFT)) {
//      throw new BadRequestAlertException("Δεν μπορείτε να απορρίψετε πρόχειρη συναλλαγή");
//    }
//    if (compTransaction.getStatus().equals(CompoundTransactionStatus.REJECTED)) {
//      throw new BadRequestAlertException("Η συναλλαγή έχει ήδη απορριφθεί");
//    }
//    if (compTransaction.getStatus().equals(CompoundTransactionStatus.FAILED)) {
//      throw new BadRequestAlertException("Η συναλλαγή έχει ήδη αποτυχει");
//    }
//    compTransaction.setStatus(CompoundTransactionStatus.REJECTED);
//    User user = userRepository.findByIdOptional(Long.valueOf(userId)).orElseThrow(() -> new NotFoundAlertException(User.class.getName()));
//    compTransaction.setRejectedFromUser(user);
//    LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);
//    compTransaction.setRejectedTimestamp(timestamp);
//    compTransaction.setRollbackTimestamp(timestamp);
//    logService.persistLogTransaction(id, userId, LogTransaction.Action.REJECT);
//    List<CompoundTransaction> childrenTransactions = compoundTransactionRepository.list("parentTransaction.id = ?1", compTransaction.getId());
//    childrenTransactions.forEach((tr -> {
//      tr.setStatus(CompoundTransactionStatus.REJECTED);
//      tr.setRejectedFromUser(user);
//      tr.setRejectedTimestamp(timestamp);
//      tr.setRollbackTimestamp(timestamp);
//      logService.persistLogTransaction(tr.getId(), userId, LogTransaction.Action.REJECT);
//    }));
//    rollBackTransaction(compTransaction);
//  }
//
//  private void rollBackTransaction(CompoundTransaction compTransaction) {
//    // update transaction balances and available balances
//    List<ITransaction> itransactions = iTransactionRepository.list("cTransaction.id = ?1 and groupParentTransaction.id IS NULL", compTransaction.getId());
//    if (compTransaction.getType().equals(TransactionType.T00)) {
//      // do nothing
//    }
//    if (compTransaction.getType().equals(TransactionType.T01)) {
//      for (ITransaction itransaction : itransactions) {
//        GenAccountBalance genAccountBalance = genAccountBalanceRepository.find("genDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        genAccountBalance.setTransactionsBalance(genAccountBalance.getTransactionsBalance().subtract(itransaction.getAmount()));
//        genAccountBalance.setAvailableBalance(genAccountBalance.getAvailableBalance().add(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T03)) {
//      for (ITransaction itransaction : itransactions) {
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().subtract(itransaction.getAmount()));
//        parAccountBalanceFrom.setAvailableBalance(parAccountBalanceFrom.getAvailableBalance().add(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T04)) {
//      for (ITransaction itransaction : itransactions) {
//        if (!Boolean.TRUE.equals(itransaction.getProxyTransactionFrom())) {
//          GenAccountBalance genAccountBalance = genAccountBalanceRepository.find("genDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//          genAccountBalance.setTransactionsBalance(genAccountBalance.getTransactionsBalance().subtract(itransaction.getAmount()));
//          genAccountBalance.setAvailableBalance(genAccountBalance.getAvailableBalance().add(itransaction.getAmount()));
//        } else {
//          ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getProxyDepUnitFrom().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//          parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().subtract(itransaction.getAmount()));
//          parAccountBalanceFrom.setAvailableBalance(parAccountBalanceFrom.getAvailableBalance().add(itransaction.getAmount()));
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T05)) {
//      for (ITransaction itransaction : itransactions) {
//        if (!Boolean.TRUE.equals(itransaction.getProxyTransactionFrom())) {
//          GenAccountBalance genAccountBalance = genAccountBalanceRepository.find("genDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//          genAccountBalance.setTransactionsBalance(genAccountBalance.getTransactionsBalance().subtract(itransaction.getAmount()));
//          genAccountBalance.setAvailableBalance(genAccountBalance.getAvailableBalance().add(itransaction.getAmount()));
//        } else {
//          ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getProxyDepUnitFrom().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//          parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().subtract(itransaction.getAmount()));
//          parAccountBalanceFrom.setAvailableBalance(parAccountBalanceFrom.getAvailableBalance().add(itransaction.getAmount()));
//        }
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T07)) {
//      for (ITransaction itransaction : itransactions) {
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().subtract(itransaction.getAmount()));
//        parAccountBalanceFrom.setAvailableBalance(parAccountBalanceFrom.getAvailableBalance().add(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T08)) {
//      for (ITransaction itransaction : itransactions) {
//        UserAccountBalance userAccountBalance = userAccountBalanceRepository.find("user.id = ?1 and itemCode.id = ?2", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserAccountBalance.class.getName()));
//        UserDepUnitAccountBalance userDepUnitAccountBalance = userDepUnitAccountBalanceRepository.find("user.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3 and ou.id is null", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getToAccountEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserDepUnitAccountBalance.class.getName()));
//        userAccountBalance.setTransactionsBalance(userAccountBalance.getTransactionsBalance().subtract(itransaction.getAmount()));
//        userAccountBalance.setAvailableBalance(userAccountBalance.getAvailableBalance().add(itransaction.getAmount()));
//        userDepUnitAccountBalance.setTransactionsBalance(userDepUnitAccountBalance.getTransactionsBalance().subtract(itransaction.getAmount()));
//        userDepUnitAccountBalance.setAvailableBalance(userDepUnitAccountBalance.getAvailableBalance().add(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T09)) {
//      for (ITransaction itransaction : itransactions) {
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().subtract(itransaction.getAmount()));
//        parAccountBalanceFrom.setAvailableBalance(parAccountBalanceFrom.getAvailableBalance().add(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T12)) {
//      for (ITransaction itransaction : itransactions) {
//        OuAccountBalance ouAccountBalanceFrom = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2", itransaction.getFromOu().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        OuManagerAccountBalance ouManagerAccountBalanceFrom = ouManagerAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2 and manager.id = ?3", itransaction.getFromOu().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getFromUser().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuManagerAccountBalance.class.getName()));
//        ouAccountBalanceFrom.setAvailableBalance(ouAccountBalanceFrom.getAvailableBalance().add(itransaction.getAmount()));
//        ouAccountBalanceFrom.setTransactionsBalance(ouAccountBalanceFrom.getTransactionsBalance().subtract(itransaction.getAmount()));
//        ouManagerAccountBalanceFrom.setAvailableBalance(ouManagerAccountBalanceFrom.getAvailableBalance().add(itransaction.getAmount()));
//        ouManagerAccountBalanceFrom.setTransactionsBalance(ouManagerAccountBalanceFrom.getTransactionsBalance().subtract(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T13)) {
//      for (ITransaction itransaction : itransactions) {
//        OuAccountBalance ouAccountBalanceFrom = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3", itransaction.getFromOu().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getToAccountEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuManagerAccountBalance.class.getName()));
//        OuManagerAccountBalance ouManagerAccountBalanceFrom = ouManagerAccountBalanceRepository.find("manager.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3 and ou.id = ?4", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getToAccountEntity().getId(), itransaction.getFromOu().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        ouAccountBalanceFrom.setAvailableBalance(ouAccountBalanceFrom.getAvailableBalance().add(itransaction.getAmount()));
//        ouAccountBalanceFrom.setTransactionsBalance(ouAccountBalanceFrom.getTransactionsBalance().subtract(itransaction.getAmount()));
//        ouManagerAccountBalanceFrom.setAvailableBalance(ouManagerAccountBalanceFrom.getAvailableBalance().add(itransaction.getAmount()));
//        ouManagerAccountBalanceFrom.setTransactionsBalance(ouManagerAccountBalanceFrom.getTransactionsBalance().subtract(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T14)) {
//      for (ITransaction itransaction : itransactions) {
//        OuAccountBalance ouAccountBalanceTo = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2", itransaction.getToOu().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        UserAccountBalance userAccountBalance = userAccountBalanceRepository.find("user.id = ?1 and itemCode.id = ?2", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserAccountBalance.class.getName()));
//        UserDepUnitAccountBalance userDepUnitAccountBalance = userDepUnitAccountBalanceRepository.find("user.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3 and ou.id = ?4", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId(), ouAccountBalanceTo.getDepUnit().getId(), ouAccountBalanceTo.getOu().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserDepUnitAccountBalance.class.getName()));
//        userAccountBalance.setTransactionsBalance(userAccountBalance.getTransactionsBalance().subtract(itransaction.getAmount()));
//        userAccountBalance.setAvailableBalance(userAccountBalance.getAvailableBalance().add(itransaction.getAmount()));
//        userDepUnitAccountBalance.setTransactionsBalance(userDepUnitAccountBalance.getTransactionsBalance().subtract(itransaction.getAmount()));
//        userDepUnitAccountBalance.setAvailableBalance(userDepUnitAccountBalance.getAvailableBalance().add(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T15)) {
//      for (ITransaction itransaction : itransactions) {
//        GenAccountBalance genAccountBalance = genAccountBalanceRepository.find("genDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        genAccountBalance.setTransactionsBalance(genAccountBalance.getTransactionsBalance().subtract(itransaction.getAmount()));
//        genAccountBalance.setAvailableBalance(genAccountBalance.getAvailableBalance().add(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T16)) {
//      for (ITransaction itransaction : itransactions) {
//        UserAccountBalance userAccountBalance = userAccountBalanceRepository.find("user.id = ?1 and itemCode.id = ?2", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserAccountBalance.class.getName()));
//        UserDepUnitAccountBalance userDepUnitAccountBalance = userDepUnitAccountBalanceRepository.find("user.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3 and ou.id is null", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getToAccountEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(UserDepUnitAccountBalance.class.getName()));
//        userAccountBalance.setAvailableBalance(userAccountBalance.getAvailableBalance().add(itransaction.getAmount()));
//        userAccountBalance.setTransactionsBalance(userAccountBalance.getTransactionsBalance().subtract(itransaction.getAmount()));
//        userDepUnitAccountBalance.setAvailableBalance(userDepUnitAccountBalance.getAvailableBalance().add(itransaction.getAmount()));
//        userDepUnitAccountBalance.setTransactionsBalance(userDepUnitAccountBalance.getTransactionsBalance().subtract(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T17)) {
//      for (ITransaction itransaction : itransactions) {
//        GenAccountBalance genAccountBalance = genAccountBalanceRepository.find("genDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        genAccountBalance.setTransactionsBalance(genAccountBalance.getTransactionsBalance().subtract(itransaction.getAmount()));
//        genAccountBalance.setAvailableBalance(genAccountBalance.getAvailableBalance().add(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//    if (compTransaction.getType().equals(TransactionType.T18)) {
//      for (ITransaction itransaction : itransactions) {
//        OuAccountBalance ouAccountBalanceFrom = ouAccountBalanceRepository.find("ou.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3", itransaction.getFromOu().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getToAccountEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuManagerAccountBalance.class.getName()));
//        OuManagerAccountBalance ouManagerAccountBalanceFrom = ouManagerAccountBalanceRepository.find("manager.id = ?1 and itemCode.id = ?2 and depUnit.id = ?3 and ou.id = ?4", itransaction.getFromUser().getId(), itransaction.getItemCodeEntity().getId(), itransaction.getToAccountEntity().getId(), itransaction.getFromOu().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(OuAccountBalance.class.getName()));
//        ouAccountBalanceFrom.setAvailableBalance(ouAccountBalanceFrom.getAvailableBalance().add(itransaction.getAmount()));
//        ouAccountBalanceFrom.setTransactionsBalance(ouAccountBalanceFrom.getTransactionsBalance().subtract(itransaction.getAmount()));
//        ouManagerAccountBalanceFrom.setAvailableBalance(ouManagerAccountBalanceFrom.getAvailableBalance().add(itransaction.getAmount()));
//        ouManagerAccountBalanceFrom.setTransactionsBalance(ouManagerAccountBalanceFrom.getTransactionsBalance().subtract(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//
//    if (compTransaction.getType().equals(TransactionType.T19)) {
//      for (ITransaction itransaction : itransactions) {
//        ParAccountBalance parAccountBalanceFrom = parAccountBalanceRepository.find("parDepUnit.id = ?1 and itemCode.id = ?2", itransaction.getFromAccountEntity().getId(), itransaction.getItemCodeEntity().getId()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(ParAccountBalance.class.getName()));
//        parAccountBalanceFrom.setTransactionsBalance(parAccountBalanceFrom.getTransactionsBalance().subtract(itransaction.getAmount()));
//        parAccountBalanceFrom.setAvailableBalance(parAccountBalanceFrom.getAvailableBalance().add(itransaction.getAmount()));
//        if (Boolean.TRUE.equals(itransaction.getIsGroupParent())) {
//          int updatedCount = itemRepository.unsetItemsInTransactionWhereGroupTransaction(itransaction.getId());
//          log.info("Updated count " + updatedCount);
//        }
//        if (itransaction.getItem() != null) {
//          Item item = itransaction.getItem();
//          item.setInTransaction(false);
//          item.setInTransactionId(null);
//          itemRepository.persist(item);
//        }
//      }
//    }
//  }
//
//  private static final String COMPOUND_TRANSACTION_BUCKET = "compound-transaction-";
//
//  @Transactional
//  @Traced
//  public PageList<CompoundTransactionFileResDto> getCompoundTransactionFiles(Long id) {
//    return new PageList<>(transactionMapper.toCompoundTransactionFileDto(compoundTransactionFileRepository.list("cTransaction.id", id)));
//  }
//
//  @Transactional
//  @Traced
//  public void persistCompoundTransactionFile(Long bucketId, FormData formData, CompoundTransactionFileType type, boolean allowupdate) {
//    if (formData.getData() == null) {
//      throw new BadRequestAlertException("File is required");
//    }
//    if (formData.getName() == null || formData.getName().isEmpty()) {
//      throw new BadRequestAlertException("File name is required");
//    }
//    if (formData.getType() == null || formData.getType().isEmpty()) {
//      throw new BadRequestAlertException("File type is required");
//    }
//    if (formData.getSize() == null) {
//      throw new BadRequestAlertException("File Size is required");
//    }
//    log.info("Add file for compoundTransaction: " + bucketId + " , file name: " + formData.getName() + " , file type: " + formData.getType() + ", file size: " + formData.getSize());
//    CompoundTransaction compoundTransaction =  compoundTransactionRepository.findByIdOptional(bucketId).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    if (compoundTransaction.getFiles() != null  && compoundTransaction.getFiles().stream().map(fl -> fl.getName()).collect(Collectors.toList()).contains(formData.getName())) {
//      if (!allowupdate) {
//        throw new BadRequestAlertException("File Name already exists for the transaction");
//      } else {
//        //update file
//        CompoundTransactionFile existing = compoundTransactionFileRepository.find("cTransaction.id = ?1 and name = ?2", bucketId, formData.getName()).firstResultOptional().get();
//        deleteCompoundTransactionFile(bucketId, existing.getId());
//      }
//    }
//    CompoundTransactionFile file = CompoundTransactionFile.builder().cTransaction(compoundTransaction).name(formData.getName()).type(type).contentType(formData.getType()).build();
//    compoundTransactionFileRepository.persistAndFlush(file);
//    storageService.putObjectOnBucket(COMPOUND_TRANSACTION_BUCKET + compoundTransaction.getId(), formData.getName(), formData.getData(), formData.getSize(), formData.getType());
//  }
//
//  @Transactional
//  @Traced
//  public FileResDto getCompoundTransactionFile(Long fileId) {
//    CompoundTransactionFile compoundTransactionFile = compoundTransactionFileRepository.find("id = ?1", fileId).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(CompoundTransactionFile.class.getName()));
//    String fileName = compoundTransactionFile.getName();
//    Long bucketId = compoundTransactionFile.getCTransaction().getId();
//    return new FileResDto(fileName, storageService.getObjectFromBucket(COMPOUND_TRANSACTION_BUCKET + bucketId,  fileName), compoundTransactionFile.getContentType());
//  }
//
//  @Transactional
//  @Traced
//  public void deleteCompoundTransactionFile(Long bucketId, Long fileId) {
//    CompoundTransactionFile compoundTransactionFile = compoundTransactionFileRepository.find("cTransaction.id = ?1 and id = ?2", bucketId, fileId).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(CompoundTransactionFile.class.getName()));
//    String fileName = compoundTransactionFile.getName();
//    log.info("Delete file for compoundTransaction: " + bucketId + " , file name: " + fileName);
//    compoundTransactionFileRepository.delete("cTransaction.id = ?1 and id = ?2", bucketId, fileId);
//    storageService.removeObjectFromBucket(COMPOUND_TRANSACTION_BUCKET + bucketId, fileName);
//  }
//
//  @Transactional
//  @Traced
//  public boolean hasPermissionsToDeleteCompoundTransactionFile(Long bucketId, Long fileId) {
//    CompoundTransactionFile compoundTransactionFile = compoundTransactionFileRepository.find("cTransaction.id = ?1 and id = ?2", bucketId, fileId).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(CompoundTransactionFile.class.getName()));
//    if (compoundTransactionFile.getType().isGenerated()) {
//      return false;
//    }
//    return true;
//  }
//
//  @Transactional
//  @Traced
//  public Page<ITransactionResWithCompTransDto> getITransactions(ToOptionsCompoundTransaction options) {
//    CriteriaBuilder cb = em.getCriteriaBuilder();
//    // Results
//    CriteriaQuery<ITransaction> lcq = cb.createQuery(ITransaction.class);
//    Root<ITransaction> root = lcq.from(ITransaction.class);
//    root.fetch(ITransaction_.cTransaction, JoinType.LEFT);
//    // Filter
//    List<Predicate> predicates = new ArrayList<Predicate>();
//
//    if (options.udc != null) {
//      predicates.add(cb.like(cb.upper(root.get(ITransaction_.udc)), "%" + options.udc.toUpperCase() + "%"));
//    }
//
//    if (options.transactionType != null) {
//      CriteriaBuilder.In<TransactionType> inClause = cb.in(root.get(ITransaction_.type));
//      for (String item : options.transactionType.list) {
//        inClause.value(TransactionType.valueOf(item));
//      }
//      predicates.add(inClause);
//    }
//
//    if (options.itemCode != null) {
//      CriteriaBuilder.In<Long> inClause = cb.in(root.get(ITransaction_.itemCodeEntity).get(ItemCode_.id));
//      for (String item : options.itemCode.list) {
//        inClause.value(Long.valueOf(item));
//      }
//      predicates.add(inClause);
//    }
//
//    if (options.fromAccount != null) {
//      CriteriaBuilder.In<Long> inClause = cb.in(root.get(ITransaction_.fromAccountEntity).get(DepUnit_.id));
//      for (String item : options.fromAccount.list) {
//        inClause.value(Long.valueOf(item));
//      }
//      predicates.add(inClause);
//    }
//
//    if (options.toAccount != null) {
//      CriteriaBuilder.In<Long> inClause = cb.in(root.get(ITransaction_.toAccountEntity).get(DepUnit_.id));
//      for (String item : options.toAccount.list) {
//        inClause.value(Long.valueOf(item));
//      }
//      predicates.add(inClause);
//    }
//
//    if (options.itemCode != null) {
//      CriteriaBuilder.In<Long> inClause = cb.in(root.get(ITransaction_.itemCodeEntity).get(ItemCode_.id));
//      for (String item : options.itemCode.list) {
//        inClause.value(Long.valueOf(item));
//      }
//      predicates.add(inClause);
//    }
//
//    if (options.dateFrom != null) {
//      predicates.add(cb.greaterThanOrEqualTo(root.get(ITransaction_.actualDate), options.dateFrom));
//    }
//
//    if (options.dateTo != null) {
//      predicates.add(cb.lessThanOrEqualTo(root.get(ITransaction_.actualDate), options.dateTo));
//    }
//
//    if (options.compTransaction != null) {
//      predicates.add(cb.equal(root.get(ITransaction_.cTransaction).get(CompoundTransaction_.id), options.compTransaction));
//    }
//
//    if (options.serialNumber != null) {
//      predicates.add(cb.or(cb.like(cb.upper(root.get(ITransaction_.item).get(Item_.serialNumber)), "%" + options.serialNumber.toUpperCase() + "%")));
//      options.viewMode = ToOptionsCompoundTransaction.ViewMode.NO_GROUP;
//    }
//
//    if (options.itemId != null) {
//      predicates.add(cb.equal(root.get(ITransaction_.item).get(Item_.id), options.itemId));
//      options.viewMode = ToOptionsCompoundTransaction.ViewMode.NO_GROUP;
//    }
//
//    if (options.fromAccountOrToAccount != null) {
//      CriteriaBuilder.In<Long> toInClause = cb.in(root.get(ITransaction_.toAccountEntity).get(DepUnit_.id));
//      for (String item : options.fromAccountOrToAccount.list) {
//        toInClause.value(Long.valueOf(item));
//      }
//      CriteriaBuilder.In<Long> fromInClause = cb.in(root.get(ITransaction_.fromAccountEntity).get(DepUnit_.id));
//      for (String item : options.fromAccountOrToAccount.list) {
//        fromInClause.value(Long.valueOf(item));
//      }
//      predicates.add(cb.or(toInClause, fromInClause));
//    }
//
//    if (options.fromOuOrToOu != null) {
//      CriteriaBuilder.In<Long> toInClause = cb.in(root.get(ITransaction_.toOu).get(Ou_.id));
//      for (String item : options.fromOuOrToOu.list) {
//        toInClause.value(Long.valueOf(item));
//      }
//      CriteriaBuilder.In<Long> fromInClause = cb.in(root.get(ITransaction_.fromOu).get(Ou_.id));
//      for (String item : options.fromOuOrToOu.list) {
//        fromInClause.value(Long.valueOf(item));
//      }
//      predicates.add(cb.or(toInClause, fromInClause));
//    }
//
//    if (options.user != null) {
//      CriteriaBuilder.In<Long> toInClause = cb.in(root.get(ITransaction_.toUser).get(User_.id));
//      for (String item : options.user.list) {
//        toInClause.value(Long.valueOf(item));
//      }
//      CriteriaBuilder.In<Long> fromInClause = cb.in(root.get(ITransaction_.fromUser).get(User_.id));
//      for (String item : options.user.list) {
//        fromInClause.value(Long.valueOf(item));
//      }
//      predicates.add(cb.or(toInClause, fromInClause));
//    }
//
//    if (options.toOrFromOu != null) {
//      predicates.add(cb.or(cb.isNotNull(root.get(ITransaction_.toOu)), cb.isNotNull(root.get(ITransaction_.fromOu))));
//    }
//
//    if (options.managedAccounts != null) {
//      CriteriaBuilder.In<Long> toInClause = cb.in(root.get(ITransaction_.toAccountEntity).get(DepUnit_.id));
//      for (String item : options.managedAccounts.list) {
//        toInClause.value(Long.valueOf(item));
//      }
//      CriteriaBuilder.In<Long> fromInClause = cb.in(root.get(ITransaction_.fromAccountEntity).get(DepUnit_.id));
//      for (String item : options.managedAccounts.list) {
//        fromInClause.value(Long.valueOf(item));
//      }
//      predicates.add(cb.or(toInClause, fromInClause));
//    }
//
//    if (options.viewMode != null) {
//      if (options.viewMode.equals(ToOptionsCompoundTransaction.ViewMode.NO_GROUP)) {
//        predicates.add(cb.or(cb.isNull(root.get(ITransaction_.isGroupParent)), cb.isFalse(root.get(ITransaction_.isGroupParent))));
//      } else if (options.viewMode.equals(ToOptionsCompoundTransaction.ViewMode.GROUP)) {
//        predicates.add(cb.isNull(root.get(ITransaction_.groupParentTransaction)));
//      }
//    } else {
//      predicates.add(cb.isNull(root.get(ITransaction_.groupParentTransaction)));
//    }
//
//    if (options.myUserId != null) {
//      predicates.add(cb.or(cb.and(cb.or(cb.equal(root.get(ITransaction_.type), TransactionType.T07), cb.equal(root.get(ITransaction_.type), TransactionType.T12)), cb.equal(root.get(ITransaction_.toUser).get(User_.id), options.myUserId)),
//          cb.and(cb.or(cb.equal(root.get(ITransaction_.type), TransactionType.T08), cb.equal(root.get(ITransaction_.type), TransactionType.T14)), cb.equal(root.get(ITransaction_.fromUser).get(User_.id), options.myUserId))));
//    }
//
//    predicates.add(cb.or(cb.equal(root.get(ITransaction_.cTransaction).get(CompoundTransaction_.status), CompoundTransactionStatus.COMPLETED), cb.equal(root.get(ITransaction_.status), CompoundTransactionStatus.COMPLETED)));
//
//    ServiceUtils<ITransaction> serviceUtils = new ServiceUtils<>();
//    Predicate predicate = serviceUtils.getAndPredicate(cb, predicates);
//    // End Filter
//
//    // Order
//    List<Order> orders = serviceUtils.getSort(cb, root, options.sort);
//    // Default ordering
//    orders.add(cb.desc(root.get(ITransaction_.updatedTimestamp)));
//    orders.add(cb.desc(root.get(ITransaction_.actualDate)));
//    // End Order
//
//    lcq.select(root).where(predicate).orderBy(orders);
//    Query query = em.createQuery(lcq);
//
//    List<ITransaction> results;
//    if (options.size != -1) {
//      results = query.setFirstResult(options.size * options.index).setMaxResults(options.size).getResultList();
//      // End Results
//
//      // Total count results
//      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//      cq.select(cb.count(cq.from(ITransaction.class)));
//      cq.where(predicate);
//      Long total = em.createQuery(cq).getSingleResult();
//      // End  Total count results
//      List<ITransactionResWithCompTransDto> list = transactionMapper.toITransactionWithCompTransDto(results);
//      for (ITransactionResWithCompTransDto tr : list) {
//        populateItem(tr, false);
//      }
//      return new Page<>(list, total, options.index, options.size);
//    } else {
//      results = query.getResultList();
//      List<ITransactionResWithCompTransDto> list = transactionMapper.toITransactionWithCompTransDto(results);
//      for (ITransactionResWithCompTransDto tr : list) {
//        populateItem(tr, false);
//      }
//      return new Page<>(list, results == null ? 0L : results.size(), options.index, options.size);
//    }
//  }
//
//  private void persistCompoundTransactionFromT05(CompoundTransaction compTransaction) {
//    // create compound t06 transaction and modify original transaction with proxy
//    HashMap<Long, ITransaction> groupParents = new HashMap<>();
//    int newTransactionsCounter = 1;
//    List<CompoundTransaction> compoundTransactions = new ArrayList<>();
//    for (ITransaction transaction : compTransaction.getItransactions().stream().filter(tr -> !tr.getFromAccountEntity().getId().equals(compTransaction.getFromAccountEntity().getId())).collect(Collectors.toList())) {
//      CompoundTransaction newCompoundTransaction = compoundTransactions.stream()
//          .filter(ct -> transaction.getFromAccountEntity().getId().equals(ct.getFromAccountEntity().getId()))
//          .findAny()
//          .orElse(null);
//      if (newCompoundTransaction == null) {
//        departmentService.validateDepUnitIsChildOf(transaction.getFromAccountEntity().getId(), compTransaction.getFromAccountEntity().getId());
//        newCompoundTransaction = CompoundTransaction.builder()
//            .type(TransactionType.T06)
//            .fromAccountEntity(transaction.getFromAccountEntity())
//            .actualDate(compTransaction.getActualDate())
//            .udc(compTransaction.getUdc() + "_" + newTransactionsCounter)
//            .status(CompoundTransactionStatus.FOR_PRE_SIGNATURE)
//            .author(compTransaction.getAuthor())
//            .signaturesCount(0)
//            .parentTransaction(compTransaction)
//            .itransactions(new HashSet<>())
//            .sendForSignTimestamp(compTransaction.getSendForSignTimestamp())
//            .build();
//        compoundTransactions.add(newCompoundTransaction);
//        newTransactionsCounter++;
//      }
//      ITransaction newTransaction = ITransaction.builder()
//          .type(newCompoundTransaction.getType())
//          .fromAccountEntity(newCompoundTransaction.getFromAccountEntity())
//          .actualDate(newCompoundTransaction.getActualDate())
//          .udc(newCompoundTransaction.getUdc())
//          .item(transaction.getItem())
//          .itemCode(transaction.getItemCode())
//          .itemCodeEntity(transaction.getItemCodeEntity())
//          .amount(transaction.getAmount())
//          .isGroupParent(transaction.getIsGroupParent())
//          .groupParentTransaction(transaction.getGroupParentTransaction())
//          .build();
//      if (Boolean.TRUE.equals(newTransaction.getIsGroupParent())) {
//        groupParents.put(transaction.getId(), newTransaction);
//      }
//      newCompoundTransaction.getItransactions().add(newTransaction);
//      // update original transaction
//      if (!transaction.getFromAccountEntity().getId().equals(compTransaction.getFromAccountEntity().getId())) {
//        transaction.setProxyTransactionFrom(true);
//        // set proxy transaction the original dep unit
//        transaction.setProxyDepUnitFrom(transaction.getFromAccountEntity());
//      }
//      transaction.setFromAccountEntity(compTransaction.getFromAccountEntity());
//    }
//
//    for (CompoundTransaction childCompoundTransaction : compoundTransactions) {
//      compoundTransactionRepository.persistAndFlush(childCompoundTransaction);
//      logService.persistLogTransaction(childCompoundTransaction.getId(), childCompoundTransaction.getAuthor().getId(), LogTransaction.Action.FOR_SIGN);
//      Set<ITransaction> itransactions = childCompoundTransaction.getItransactions();
//      for (ITransaction itransaction : itransactions) {
//        if (itransaction.getGroupParentTransaction() != null) {
//          itransaction.setGroupParentTransaction(groupParents.get(itransaction.getGroupParentTransaction().getId()));
//        }
//        itransaction.setCTransaction(childCompoundTransaction);
//        iTransactionRepository.persist(itransaction);
//      }
//    }
//  }
//
//  private void persistCompoundTransactionFromT00(CompoundTransaction compTransaction) {
//    // create compound t01 transaction and modify original transaction with proxy
//    HashMap<Long, ITransaction> groupParents = new HashMap<>();
//    int newTransactionsCounter = 1;
//    List<CompoundTransaction> compoundTransactions = new ArrayList<>();
//    List<ITransaction> transactions = iTransactionRepository.list("cTransaction.id = ?1", compTransaction.getId());
//    for (ITransaction transaction : transactions.stream().filter(tr -> !tr.getToAccountEntity().getId().equals(compTransaction.getToAccountEntity().getId())).collect(Collectors.toList())) {
//      CompoundTransaction newCompoundTransaction = compoundTransactions.stream()
//          .filter(ct -> transaction.getToAccountEntity().getId().equals(ct.getToAccountEntity().getId()))
//          .findAny()
//          .orElse(null);
//      if (newCompoundTransaction == null) {
//        departmentService.validateDepUnitIsChildOf(transaction.getToAccountEntity().getId(), compTransaction.getToAccountEntity().getId());
//        newCompoundTransaction = CompoundTransaction.builder()
//            .type(TransactionType.T01)
//            .fromAccountEntity(compTransaction.getToAccountEntity())
//            .toAccountEntity(transaction.getToAccountEntity())
//            .actualDate(compTransaction.getActualDate())
//            .udc(compTransaction.getUdc() + "_" + newTransactionsCounter)
//            .status(CompoundTransactionStatus.FOR_PRE_SIGNATURE)
//            .author(compTransaction.getAuthor())
//            .signaturesCount(0)
//            .parentTransaction(compTransaction)
//            .itransactions(new HashSet<>())
//            .sendForSignTimestamp(compTransaction.getSendForSignTimestamp())
//            .build();
//        compoundTransactions.add(newCompoundTransaction);
//        newTransactionsCounter++;
//      }
//      ITransaction newTransaction = ITransaction.builder()
//          .type(newCompoundTransaction.getType())
//          .fromAccountEntity(newCompoundTransaction.getFromAccountEntity())
//          .toAccountEntity(newCompoundTransaction.getToAccountEntity())
//          .actualDate(newCompoundTransaction.getActualDate())
//          .udc(newCompoundTransaction.getUdc())
//          .item(transaction.getItem())
//          .itemCode(transaction.getItemCode())
//          .itemCodeEntity(transaction.getItemCodeEntity())
//          .amount(transaction.getAmount())
//          .isGroupParent(transaction.getIsGroupParent())
//          .groupParentTransaction(transaction.getGroupParentTransaction())
//          .build();
//      if (Boolean.TRUE.equals(newTransaction.getIsGroupParent())) {
//        groupParents.put(transaction.getId(), newTransaction);
//      }
//      newCompoundTransaction.getItransactions().add(newTransaction);
//      // update original transaction
//      if (!transaction.getToAccountEntity().getId().equals(compTransaction.getToAccountEntity().getId())) {
//        transaction.setProxyTransactionTo(true);
//        // set proxy transaction the original dep unit
//        transaction.setProxyDepUnitTo(transaction.getToAccountEntity());
//      }
//      transaction.setToAccountEntity(compTransaction.getToAccountEntity());
//    }
//
//    for (CompoundTransaction childCompoundTransaction : compoundTransactions) {
//      compoundTransactionRepository.persistAndFlush(childCompoundTransaction);
//      logService.persistLogTransaction(childCompoundTransaction.getId(), childCompoundTransaction.getAuthor().getId(), LogTransaction.Action.FOR_SIGN);
//      Set<ITransaction> itransactions = childCompoundTransaction.getItransactions();
//      for (ITransaction itransaction : itransactions) {
//        if (itransaction.getGroupParentTransaction() != null) {
//          itransaction.setGroupParentTransaction(groupParents.get(itransaction.getGroupParentTransaction().getId()));
//        }
//        itransaction.setCTransaction(childCompoundTransaction);
//        iTransactionRepository.persist(itransaction);
//      }
//    }
//  }
//
//  private void persistCompoundTransactionFromT04FromAccount(CompoundTransaction compTransaction) {
//    HashMap<Long, ITransaction> groupParents = new HashMap<>();
//    // create compound t19 transaction and modify original transaction with proxy
//    int newTransactionsCounter = 1;
//    List<CompoundTransaction> compoundTransactions = new ArrayList<>();
//    for (ITransaction transaction : compTransaction.getItransactions().stream().filter(tr -> !tr.getFromAccountEntity().getId().equals(compTransaction.getFromAccountEntity().getId())).collect(Collectors.toList())) {
//      CompoundTransaction newCompoundTransaction = compoundTransactions.stream()
//          .filter(ct -> transaction.getFromAccountEntity().getId().equals(ct.getFromAccountEntity().getId()))
//          .findAny()
//          .orElse(null);
//      if (newCompoundTransaction == null) {
//        departmentService.validateDepUnitIsChildOf(transaction.getFromAccountEntity().getId(), compTransaction.getFromAccountEntity().getId());
//        newCompoundTransaction = CompoundTransaction.builder()
//            .type(TransactionType.T19)
//            .fromAccountEntity(transaction.getFromAccountEntity())
//            .toAccountEntity(compTransaction.getFromAccountEntity())
//            .actualDate(compTransaction.getActualDate())
//            .udc(compTransaction.getUdc() + "_" + newTransactionsCounter)
//            .status(CompoundTransactionStatus.FOR_PRE_SIGNATURE)
//            .author(compTransaction.getAuthor())
//            .signaturesCount(0)
//            .parentTransaction(compTransaction)
//            .itransactions(new HashSet<>())
//            .sendForSignTimestamp(compTransaction.getSendForSignTimestamp())
//            .build();
//        compoundTransactions.add(newCompoundTransaction);
//        newTransactionsCounter++;
//      }
//      ITransaction newTransaction = ITransaction.builder()
//          .type(newCompoundTransaction.getType())
//          .fromAccountEntity(newCompoundTransaction.getFromAccountEntity())
//          .toAccountEntity(newCompoundTransaction.getToAccountEntity())
//          .actualDate(newCompoundTransaction.getActualDate())
//          .udc(newCompoundTransaction.getUdc())
//          .item(transaction.getItem())
//          .itemCode(transaction.getItemCode())
//          .itemCodeEntity(transaction.getItemCodeEntity())
//          .amount(transaction.getAmount())
//          .isGroupParent(transaction.getIsGroupParent())
//          .groupParentTransaction(transaction.getGroupParentTransaction())
//          .build();
//      if (Boolean.TRUE.equals(newTransaction.getIsGroupParent())) {
//        groupParents.put(transaction.getId(), newTransaction);
//      }
//      newCompoundTransaction.getItransactions().add(newTransaction);
//      // update original transaction
//      if (!transaction.getFromAccountEntity().getId().equals(compTransaction.getFromAccountEntity().getId())) {
//        transaction.setProxyTransactionFrom(true);
//        // set proxy transaction the original dep unit
//        transaction.setProxyDepUnitFrom(transaction.getFromAccountEntity());
//      }
//      transaction.setFromAccountEntity(compTransaction.getFromAccountEntity());
//    }
//
//    for (CompoundTransaction childCompoundTransaction : compoundTransactions) {
//      compoundTransactionRepository.persistAndFlush(childCompoundTransaction);
//      logService.persistLogTransaction(childCompoundTransaction.getId(), childCompoundTransaction.getAuthor().getId(), LogTransaction.Action.FOR_SIGN);
//      Set<ITransaction> itransactions = childCompoundTransaction.getItransactions();
//      for (ITransaction itransaction : itransactions) {
//        if (itransaction.getGroupParentTransaction() != null) {
//          itransaction.setGroupParentTransaction(groupParents.get(itransaction.getGroupParentTransaction().getId()));
//        }
//        itransaction.setCTransaction(childCompoundTransaction);
//        iTransactionRepository.persist(itransaction);
//      }
//    }
//  }
//
//  @Transactional
//  @Traced
//  public PageList<CompoundTransactionResDto> getChildrenCompoundTransaction(Long parentId) {
//    List<CompoundTransaction> list = compoundTransactionRepository.list("parentTransaction.id", parentId);
//    return new PageList<>(transactionMapper.toCompoundTransactionForListDto(list));
//  }
//
//  private String generateRandomString() {
//    Random random = new Random();
//    // Generate a random number between 10000 and 99999
//    return String.valueOf(random.nextInt(90000) + 10000);
//  }
//
//  @Transactional
//  @Traced
//  public EntityDto persistTransactionInitialDataGenBalance(FormData formData, Long genDepUnitId) throws IOException, ParseException, CsvException {
//    Long userId = userRoleRepository.find("depUnit.id = ?1 and role = ?2", genDepUnitId, Role.GENIKOS_DIACHEIRISTIS_YLIKOU).firstResult().getUser().getId();
//    return persistTransactionInitialDataGenBalance(formData, genDepUnitId, userId);
//  }
//
//  @Transactional
//  @Traced
//  public EntityDto persistTransactionInitialDataGenBalance(FormData formData, Long genDepUnitId, Long userId) throws IOException, ParseException, CsvException {
//    if (!transactionT10Pending(genDepUnitId)) {
//      throw new BadRequestAlertException("Έχετε ήδη ανεβάσει το αρχείο απογραφής");
//    }
//    GenDepUnit genDepUnit = genDepUnitRepository.findByIdOptional(genDepUnitId).orElseThrow(() -> new NotFoundAlertException(GenDepUnit.class.getName()));
//    User user = new User();
//    user.setId(Long.valueOf(userId));
//    CompoundTransaction newCompoundTransaction = CompoundTransaction.builder()
//        .type(TransactionType.T10)
//        .fromAccountEntity(null)
//        .toAccountEntity(genDepUnit)
//        .actualDate(LocalDate.now())
//        .udc("ΑΠΟΓΡΑΦΗ_" + genDepUnit.getId())
//        .status(CompoundTransactionStatus.COMPLETED)
//        .author(user)
//        .signaturesCount(0)
//        .parentTransaction(null)
//        .itransactions(new HashSet<>())
//        .build();
//    compoundTransactionRepository.persistAndFlush(newCompoundTransaction);
//    Long bucketId = newCompoundTransaction.getId();
//    persistCompoundTransactionFile(bucketId, formData, CompoundTransactionFileType.D10, false);
//    Locale greekLocale = new Locale("el", "GR");
//    NumberFormat format = NumberFormat.getInstance(greekLocale);
//    List<String[]> lines;
//    byte[] cloneInput = storageService.getObjectFromBucket(COMPOUND_TRANSACTION_BUCKET + bucketId,  formData.getName());
//    try (CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(cloneInput)))) {
//      lines = reader.readAll();
//    }
//    int  i = 0;
//    while (++i < lines.size()) {
//      if (i == 0) {
//        // Ignore header
//        continue;
//      }
//      String[] line = lines.get(i);
//      String itemCodeFromFile = line[0].trim();
//      ItemCode itemCode = itemCodeRepository.findByCode(transformCodeWithLeadingZeros(itemCodeFromFile)).orElseThrow(() -> new NotFoundAlertException(ItemCode.class.getName() + itemCodeFromFile));
//      BigDecimal amount = new BigDecimal(format.parse(line[1]).toString());
//      // Υπολογισμός και Επαλήθευση του Άθροισμα των Στοιχείων στον Πίνακα
//      BigDecimal sumUnderGenDep = BigDecimal.ZERO;
//      for (int depIndex = 2; depIndex < lines.get(0).length; depIndex++) {
//        BigDecimal parAmount = line[depIndex].isBlank() ? BigDecimal.ZERO : new BigDecimal(format.parse(line[depIndex]).toString());
//        if (parAmount.compareTo(BigDecimal.ZERO) < 0) {
//          throw new BadRequestAlertException(TransactionError.ACCOUNT_MUST_NOT_BE_LESS_THAN_ZERO.label + ' ' + itemCodeFromFile);
//        }
//        if (itemCode.isDiscreet()) {
//          // Check if the BigDecimal is an integer
//          if (parAmount.stripTrailingZeros().scale() > 0) {
//            throw new BadRequestAlertException(TransactionError.ACCOUNT_MUST_BE_DISCREET.label + ' ' + itemCodeFromFile);
//          }
//        }
//        sumUnderGenDep = sumUnderGenDep.add(parAmount);
//      }
//      //log.info(String.valueOf(sumUnderGenDep));
//      //log.info(String.valueOf(amount));
//      if (amount.compareTo(sumUnderGenDep) != 0) {
//        log.info(itemCode.getCode() + " " + amount + " " + sumUnderGenDep);
//        throw new BadRequestAlertException("Το σύνολο της γενικής πρέπει να ταυτίζεται με το άθροισμα της γενικής αποθήκης και των μερικών αποθηκών");
//      }
//      if (amount.compareTo(BigDecimal.ZERO) < 0) {
//        throw new BadRequestAlertException(TransactionError.ACCOUNT_MUST_NOT_BE_LESS_THAN_ZERO.label + ' ' + itemCodeFromFile);
//      }
//
//      if (itemCode.isDiscreet()) {
//        // Check if the BigDecimal is an integer
//        if (amount.stripTrailingZeros().scale() > 0) {
//          throw new BadRequestAlertException(TransactionError.ACCOUNT_MUST_BE_DISCREET.label + ' ' + itemCodeFromFile);
//        }
//      }
//      balanceService.initGenAccountBalance(genDepUnit.getCode(), itemCode.getCode(), amount);
//      if (amount.compareTo(BigDecimal.ZERO) > 0) {
//        ITransaction itransaction = ITransaction.builder()
//            .type(newCompoundTransaction.getType())
//            .fromAccountEntity(newCompoundTransaction.getFromAccountEntity())
//            .toAccountEntity(newCompoundTransaction.getToAccountEntity())
//            .actualDate(newCompoundTransaction.getActualDate())
//            .udc(newCompoundTransaction.getUdc())
//            .item(null)
//            .itemCode(itemCode.getCode())
//            .itemCodeEntity(itemCode)
//            .amount(amount)
//            .isGroupParent(false)
//            .groupParentTransaction(null)
//            .build();
//        itransaction.setCTransaction(newCompoundTransaction);
//        iTransactionRepository.persist(itransaction);
//      }
//      if (i % 100 == 0) {
//        em.flush();
//        em.clear();
//      }
//    }
//    // For loop for iterating over pardepunits
//    for (int parDepUnitIndex = 3; parDepUnitIndex < lines.get(0).length; parDepUnitIndex++) {
//      persistTransactionInitialDataParBalance(lines, parDepUnitIndex, userId, newCompoundTransaction);
//    }
//    return new EntityDto(newCompoundTransaction.getId());
//  }
//
//  @Transactional
//  @Traced
//  public EntityDto persistTransactionInitialDataParBalance(List<String[]> lines, int parDepUnitIndex, Long userId, CompoundTransaction parentTransaction) throws ParseException {
//    String parDepName = lines.get(0)[parDepUnitIndex];
//    ParDepUnit parDepUnit = parDepUnitRepository.find("name = ?1 and genDepUnit.id =?2", parDepName, parentTransaction.getToAccountEntity().getId()).firstResultOptional().orElseThrow(() -> new NotFoundAlertException("Η Μερική Διαχείριση με όνομα: " + parDepName + " δεν βρέθηκε"));
//    GenDepUnit genDepUnit = parDepUnit.getGenDepUnit();
//    String fromAccount = genDepUnit.getCode();
//    User user = new User();
//    user.setId(Long.valueOf(userId));
//    CompoundTransaction newCompoundTransaction = CompoundTransaction.builder()
//        .type(TransactionType.T11)
//        .fromAccountEntity(genDepUnit)
//        .toAccountEntity(parDepUnit)
//        .actualDate(LocalDate.now())
//        .udc("ΑΠΟΓΡΑΦΗ_" + parDepUnit.getId())
//        .status(CompoundTransactionStatus.COMPLETED)
//        .author(user)
//        .signaturesCount(0)
//        .parentTransaction(parentTransaction)
//        .itransactions(new HashSet<>())
//        .build();
//    compoundTransactionRepository.persistAndFlush(newCompoundTransaction);
//    Locale greekLocale = new Locale("el", "GR");
//    NumberFormat format = NumberFormat.getInstance(greekLocale);
//    int  i = 0;
//    while (++i < lines.size()) {
//      if (i == 0) {
//        // Ignore header
//        continue;
//      }
//      String[] line = lines.get(i);
//      String itemCodeFromFile = line[0].trim();
//      ItemCode itemCode = itemCodeRepository.findByCode(transformCodeWithLeadingZeros(itemCodeFromFile)).orElseThrow(() -> new NotFoundAlertException(ItemCode.class.getName() + itemCodeFromFile));
//      BigDecimal amount = line[parDepUnitIndex].isBlank() ? BigDecimal.ZERO : new BigDecimal(format.parse(line[parDepUnitIndex]).toString());
//      if (amount.compareTo(BigDecimal.ZERO) < 0) {
//        throw new BadRequestAlertException(TransactionError.ACCOUNT_MUST_BE_GREATER_THAN_ZERO.label + ' ' + itemCodeFromFile);
//      }
//      if (amount.compareTo(BigDecimal.ZERO) > 0) {
//        balanceService.initParAccountBalance(parDepUnit.getCode(), itemCode.getCode(), amount);
//        GenAccountBalance genAccountBalance = genAccountBalanceRepository.find("genDepUnit.code = ?1 and itemCode.code = ?2", fromAccount, itemCode.getCode()).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional().orElseThrow(() -> new NotFoundAlertException(GenAccountBalance.class.getName()));
//        genAccountBalance.setBalance(genAccountBalance.getBalance().subtract(amount));
//        genAccountBalance.setAvailableBalance(genAccountBalance.getAvailableBalance().subtract(amount));
//        genAccountBalanceRepository.persist(genAccountBalance);
//        ITransaction itransaction = ITransaction.builder()
//            .type(newCompoundTransaction.getType())
//            .fromAccountEntity(newCompoundTransaction.getFromAccountEntity())
//            .toAccountEntity(newCompoundTransaction.getToAccountEntity())
//            .actualDate(newCompoundTransaction.getActualDate())
//            .udc(newCompoundTransaction.getUdc())
//            .item(null)
//            .itemCode(itemCode.getCode())
//            .itemCodeEntity(itemCode)
//            .amount(amount)
//            .isGroupParent(false)
//            .groupParentTransaction(null)
//            .build();
//        itransaction.setCTransaction(newCompoundTransaction);
//        iTransactionRepository.persist(itransaction);
//      }
//      if (i % 100 == 0) {
//        em.flush();
//        em.clear();
//      }
//    }
//    em.flush();
//    em.clear();
//    return new EntityDto(newCompoundTransaction.getId());
//  }
//
//  @Transactional
//  @Traced
//  public String getInitialDataGenBalancePrototypeFile(Long genDepId) throws IOException {
//    List<String> rowData = new ArrayList<>();
//    rowData.add("ΚΩΔΙΚΟΣ ΥΛΙΚΟΥ");
//    rowData.add("ΣΥΝΟΛΟ ΓΕΝΙΚΗΣ");
//    rowData.add("ΑΠΟΘΗΚΗ ΓΕΝΙΚΗΣ");
//    List<ParDepUnit> parDepUnits = depUnitRepository.find("genDepUnit.id", genDepId).list();
//    for (ParDepUnit parDepUnit : parDepUnits) {
//      rowData.add(parDepUnit.getName());
//    }
//    String csv = "";
//    String[] rowDataArray = rowData.toArray(new String[0]);
//    try (StringWriter sw = new StringWriter(); CSVWriter csvWriter = new CSVWriter(sw)) {
//      csvWriter.writeNext(rowDataArray);
//      csv = sw.toString();
//      System.out.println("CSV result: \n" + csv);
//    }
//    return csv;
//  }
//
//  @Transactional
//  @Traced
//  public List<String> getInitialDataGenBalancePrototypeData(Long genDepId) throws IOException {
//    List<String> rowData = new ArrayList<>();
//    rowData.add("ΚΩΔΙΚΟΣ ΥΛΙΚΟΥ");
//    rowData.add("ΣΥΝΟΛΟ ΓΕΝΙΚΗΣ");
//    rowData.add("ΑΠΟΘΗΚΗ ΓΕΝΙΚΗΣ");
//    List<ParDepUnit> parDepUnits = depUnitRepository.find("genDepUnit.id", Sort.by("code"), genDepId).list();
//    for (ParDepUnit parDepUnit : parDepUnits) {
//      rowData.add(parDepUnit.getName());
//    }
//    return rowData;
//  }
//
//  @Transactional
//  @Traced
//  public boolean transactionT10Pending(Long depUnitId) {
//    return !compoundTransactionRepository.find("toAccountEntity.id = ?1 and type = ?2", depUnitId, TransactionType.T10).firstResultOptional().isPresent();
//  }
//
//  @Transactional
//  @Traced
//  public boolean transactionT11Pending(Long depUnitId) {
//    return !compoundTransactionRepository.find("toAccountEntity.id = ?1 and type = ?2", depUnitId, TransactionType.T11).firstResultOptional().isPresent();
//  }
//
//  @Transactional
//  @Traced
//  public void failCompoundTransaction(Long id, String failComments) {
//    CompoundTransaction compTransaction = compoundTransactionRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    if (compTransaction.getParentTransaction() != null) {
//      compTransaction = compoundTransactionRepository.findByIdOptional(compTransaction.getParentTransaction().getId()).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//    }
//    compTransaction.setStatus(CompoundTransactionStatus.FAILED);
//    compTransaction.setFailComments(failComments);
//    LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);
//    compTransaction.setFailedTimestamp(timestamp);
//    compTransaction.setRollbackTimestamp(timestamp);
//    List<CompoundTransaction> childrenTransactions = compoundTransactionRepository.list("parentTransaction.id = ?1", compTransaction.getId());
//    childrenTransactions.forEach((tr -> {
//      tr.setStatus(CompoundTransactionStatus.FAILED);
//      tr.setFailComments(failComments);
//      tr.setFailedTimestamp(timestamp);
//      tr.setRollbackTimestamp(timestamp);
//    }));
//    rollBackTransaction(compTransaction);
//  }
//
//  @Transactional
//  @Traced
//  public void syncWithExternalSystems(Long id, ToUserParams userParams) {
//    try {
//      CompoundTransaction compTransaction = compoundTransactionRepository.findByIdOptional(id).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//      if (compTransaction.getParentTransaction() != null) {
//        compTransaction = compoundTransactionRepository.findByIdOptional(compTransaction.getParentTransaction().getId()).orElseThrow(() -> new NotFoundAlertException(CompoundTransaction.class.getName()));
//      }
//      log.info(compTransaction.getStatus().getDescription());
//      // Sync with pliroforiki
//      if (compTransaction.getStatus().equals(CompoundTransactionStatus.COMPLETED)) {
//        DepUnit depUnitPliroforiki = depUnitRepository.find("code", "0000001 0000003").firstResultOptional().orElseThrow(() -> new NotFoundAlertException(DepUnit.class.getName()));
//        log.info("Sync ctransaction: " + id);
//        switch (compTransaction.getType()) {
//          case T03:
//            // check transactions if fromaccount is plir
//            if (compTransaction.getFromAccountEntity().getId().equals(depUnitPliroforiki.getId())) {
//              String queryText = "select Distinct i.id, i.serialnumber, a.invcode, i.katastash_id, i.assigneeuser_id from ITransaction t"
//                  + " join Item i on t.item_id = i.id"
//                  + " left outer join assetpliroforiki a on a.id = i.id"
//                  + " where t.item_id is not null"
//                  + " and t.ctransaction_id = " + compTransaction.getId()
//                  + " and i.itemclass = 'Assetpliroforiki'";
//              Query query = em.createNativeQuery(queryText);
//              List<Object[]> items = query.getResultList();
//              for (Object[] item : items) {
//                log.info("Συχρονισμός με πληροφορική id: " + item[0].toString() + " σειριακός " + item[1].toString());
//                String invCode = item[2] != null ? item[2].toString() : null;
//                DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//                DateTimeFormatter formatterDatetime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//                if (invCode != null) {
//                  pliroforikiTransactionService.putXreosi(
//                      formatterDate.format(compTransaction.getActualDate()),
//                      formatterDatetime.format(compTransaction.getCompletedTimestamp()),
//                      invCode,
//                      null,
//                      Long.toString(compTransaction.getAuthor().getId()),
//                      compTransaction.getType().name(),
//                      item[3] != null ? item[3].toString() : null
//                  );
//                } else {
//                  // TODO sunc invcode on the fly
//                  log.warn(item[0].toString() + " δεν εχει invcode");
//                }
//              }
//            }
//            break;
//          case T04:
//          case T05:
//            // check children transtion fromaccount is plir
//            List<CompoundTransaction> clidrenCTransactions = compoundTransactionRepository.list("parentTransaction.id", compTransaction.getId());
//            for (CompoundTransaction childtransct : clidrenCTransactions) {
//              if (childtransct.getFromAccountEntity().getId().equals(depUnitPliroforiki.getId())) {
//                String queryText = "select Distinct i.id, i.serialnumber, a.invcode, i.katastash_id, i.assigneeuser_id from ITransaction t"
//                    + " join Item i on t.item_id = i.id"
//                    + " left outer join assetpliroforiki a on a.id = i.id"
//                    + " where t.item_id is not null"
//                    + " and t.ctransaction_id = " + childtransct.getId()
//                    + " and i.itemclass = 'Assetpliroforiki'";
//                Query query = em.createNativeQuery(queryText);
//                List<Object[]> items = query.getResultList();
//                for (Object[] item : items) {
//                  log.info("Συχρονισμός με πληροφορική id: " + item[0].toString() + " σειριακός " + item[1].toString());
//                  String invCode = item[2] != null ? item[2].toString() : null;
//                  DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//                  DateTimeFormatter formatterDatetime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//                  if (invCode != null) {
//                    pliroforikiTransactionService.putXreosi(
//                        formatterDate.format(compTransaction.getActualDate()),
//                        formatterDatetime.format(compTransaction.getCompletedTimestamp()),
//                        invCode,
//                        null,
//                        Long.toString(compTransaction.getAuthor().getId()),
//                        compTransaction.getType().name(),
//                        item[3] != null ? item[3].toString() : null
//                    );
//                  } else {
//                    log.warn(item[0].toString() + " δεν εχει invcode");
//                  }
//                }
//              }
//            }
//            break;
//          case T07:
//          case T09:
//            // check if from is pliroforiki
//            if (compTransaction.getFromAccountEntity().getId().equals(depUnitPliroforiki.getId())) {
//              String queryText = "select Distinct i.id, i.serialnumber, a.invcode, i.katastash_id, i.assigneeuser_id from ITransaction t"
//                  + " join Item i on t.item_id = i.id"
//                  + " left outer join assetpliroforiki a on a.id = i.id"
//                  + " where t.item_id is not null"
//                  + " and t.ctransaction_id = " + compTransaction.getId()
//                  + " and i.itemclass = 'Assetpliroforiki'";
//              Query query = em.createNativeQuery(queryText);
//              List<Object[]> items = query.getResultList();
//              for (Object[] item : items) {
//                log.info("Συχρονισμός με πληροφορική id: " + item[0].toString() + " σειριακός " + item[1].toString());
//                String invCode = item[2] != null ? item[2].toString() : null;
//                DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//                DateTimeFormatter formatterDatetime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//                if (invCode != null) {
//                  pliroforikiTransactionService.putXreosi(
//                      formatterDate.format(compTransaction.getActualDate()),
//                      formatterDatetime.format(compTransaction.getCompletedTimestamp()),
//                      invCode,
//                      item[4] != null ? item[4].toString() : null,
//                      Long.toString(compTransaction.getAuthor().getId()),
//                      compTransaction.getType().name(),
//                      item[3] != null ? item[3].toString() : null
//                  );
//                } else {
//                  log.warn(item[0].toString() + " δεν εχει invcode");
//                }
//              }
//            }
//            break;
//          case T08:
//          case T13:
//            // check if to is pliroforiki
//            if (compTransaction.getToAccountEntity().getId().equals(depUnitPliroforiki.getId())) {
//              String queryText = "select Distinct i.id, i.serialnumber, a.invcode, i.katastash_id, i.assigneeuser_id from ITransaction t"
//                  + " join Item i on t.item_id = i.id"
//                  + " left outer join assetpliroforiki a on a.id = i.id"
//                  + " where t.item_id is not null"
//                  + " and t.ctransaction_id = " + compTransaction.getId()
//                  + " and i.itemclass = 'Assetpliroforiki'";
//              Query query = em.createNativeQuery(queryText);
//              List<Object[]> items = query.getResultList();
//              for (Object[] item : items) {
//                log.info("Συχρονισμός με πληροφορική id: " + item[0].toString() + " σειριακός " + item[1].toString());
//                String invCode = item[2] != null ? item[2].toString() : null;
//                DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//                DateTimeFormatter formatterDatetime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//                if (invCode != null) {
//                  pliroforikiTransactionService.putXreosi(
//                      formatterDate.format(compTransaction.getActualDate()),
//                      formatterDatetime.format(compTransaction.getCompletedTimestamp()),
//                      invCode,
//                      null,
//                      Long.toString(compTransaction.getAuthor().getId()),
//                      compTransaction.getType().name(),
//                      item[3] != null ? item[3].toString() : null
//                  );
//                } else {
//                  log.warn(item[0].toString() + " δεν εχει invcode");
//                }
//              }
//            }
//            break;
//          case T12:
//          case T14:
//            // check if items are assignedto pliroforiki
//            String queryText = "select Distinct i.id, i.serialnumber, a.invcode, i.katastash_id, i.assigneeuser_id from ITransaction t"
//                + " join Item i on t.item_id = i.id"
//                + " left outer join assetpliroforiki a on a.id = i.id"
//                + " where t.item_id is not null"
//                + " and t.ctransaction_id = " + compTransaction.getId()
//                + " and i.itemclass = 'Assetpliroforiki'"
//                + " and i.assigneedepunit_id = " + depUnitPliroforiki.getId();
//            Query query = em.createNativeQuery(queryText);
//            List<Object[]> items = query.getResultList();
//            for (Object[] item : items) {
//              log.info("Συχρονισμός με πληροφορική id: " + item[0].toString() + " σειριακός " + item[1].toString());
//              String invCode = item[2] != null ? item[2].toString() : null;
//              DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//              DateTimeFormatter formatterDatetime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//              if (invCode != null) {
//                pliroforikiTransactionService.putXreosi(
//                    formatterDate.format(compTransaction.getActualDate()),
//                    formatterDatetime.format(compTransaction.getCompletedTimestamp()),
//                    invCode,
//                    item[4] != null ? item[4].toString() : null,
//                    Long.toString(compTransaction.getAuthor().getId()),
//                    compTransaction.getType().name(),
//                    item[3] != null ? item[3].toString() : null
//                );
//              } else {
//                log.warn(item[0].toString() + " δεν εχει invcode");
//              }
//            }
//            break;
//          default:
//            log.info("No sync with asset needed");
//        }
//      }
//      // END Sync with pliroforiki
//      // Sync with ochima
//      ochimaTransactionService.insertChange(compTransaction, userParams);
//      // END Sync with ochima
//    } catch (Exception ex) {
//      //ex.printStackTrace();
//      log.error("Failed to sync ctransaction: " + id);
//    }
//  }
//
}
