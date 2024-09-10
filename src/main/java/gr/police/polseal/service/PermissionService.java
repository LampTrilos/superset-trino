package gr.police.polseal.service;

import gr.police.polseal.dto.PermissionResDto;
import gr.police.polseal.model.permissions.PermissionAction;
import gr.police.polseal.model.permissions.PermissionEntity;
import gr.police.polseal.model.permissions.Role;
import gr.police.polseal.model.permissions.UserRole;
import gr.police.polseal.repository.UserRoleRepository;
import gr.police.polseal.service.utils.PermissionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

  private final UserRoleRepository userRoleRepository;

  private final JwtService jwtService;

//  private final DepartmentMapper departmentMapper;
//
//  private final TransactionService transactionService;

  //private final OuMapper ouMapper;

  @Transactional
  @Traced
  public boolean checkIfUserHasPermission(PermissionResDto resourcePermission) {
    return PermissionUtils.containsPermission(getUserPermissions(Long.valueOf(jwtService.getUserId()), jwtService.getDepUnitId() == null ? null : Long.valueOf(jwtService.getDepUnitId())), resourcePermission);
  }

  @Transactional
  @Traced
  public List<PermissionResDto> getMyPermissions() {
    String depUnitId = jwtService.getDepUnitId();
    return getUserPermissions(jwtService.getUserId(), depUnitId == null ? null : Long.valueOf(jwtService.getDepUnitId()));
  }

  @Transactional
  @Traced
  public List<PermissionResDto> getUserPermissions(Long userId, Long depUnit) {
    List<UserRole> roles;
    if (depUnit == null) {
      roles = userRoleRepository.list("user.id = ?1 and allDepUnits = true", userId);
    } else {
      roles = userRoleRepository.list("user.id = ?1 and (depUnit.id = ?2 or allDepUnits = true)", userId, depUnit);
    }
    List<PermissionResDto> permissions = new ArrayList<>();
    for (UserRole role : roles) {
//      permissions.addAll(getRolePermissions(role.getRole(), role.getDepUnit() == null ? null : String.valueOf(role.getDepUnit().getId()), role.getAllDepUnits(), role.getOu() == null ? null : String.valueOf(role.getOu().getId())));
    }
    permissions.addAll(getPermissionsAllUsersHave());
    return permissions;
  }

//  @Transactional
//  @Traced
//  public List<DepUnitDto> getMyDepartments() {
//    return getUserDepartments(Long.valueOf(jwtService.getUserId()));
//  }

//  @Transactional
//  @Traced
//  public List<OuDto> getMyOus(Long userId) {
//    List<UserRole> roles = userRoleRepository.list("user.id = ?1 and ou is not null", userId);
//    return ouMapper.toOuDto(roles.stream().map(rl -> rl.getOu()).collect(Collectors.toList()));
//  }

//  @Transactional
//  @Traced
//  public List<DepUnitDto> getUserDepartments(Long userId) {
//    List<UserRole> roles = userRoleRepository.list("user.id = ?1", userId);
//    //Lampros while clean up
//    //return departmentMapper.toDepUnitDto(roles.stream().filter(rl -> rl.getDepUnit() != null).map(userRole -> userRole.getDepUnit()).distinct().sorted(Comparator.comparing(DepUnit::getCode)).collect(Collectors.toList()));
//    return new ArrayList<DepUnitDto>();
//  }

  @Transactional
  @Traced
  public List<Role> getUserRoleByDepUnit(Long userId, Long depUnit) {
    List<UserRole> roles = userRoleRepository.list("user.id = ?1 and depUnit.id = ?2", userId, depUnit);
    return roles.stream().map(userRole -> userRole.getRole()).collect(Collectors.toList());
  }

  @Transactional
  @Traced
  public List<Role> getUserRole(Long userId) {
    List<UserRole> roles = userRoleRepository.list("user.id = ?1", userId);
    return roles.stream().map(userRole -> userRole.getRole()).collect(Collectors.toList());
  }

  private List<PermissionResDto> getRolePermissions(Role role, String depUnitId, Boolean allDepUnits, String ouId) {
    List<PermissionResDto> permissions = new ArrayList<>();
    if (role.equals(Role.SUPER_ADMIN)) {
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.OUS).action(PermissionAction.ALL).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.CODES).action(PermissionAction.ALL).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ANNOUNCEMENTS).action(PermissionAction.ALL).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMENUMS).action(PermissionAction.ALL).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DUTIES).action(PermissionAction.ALL).allDepUnits(true).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DUTIES_TOPIKOS_YPEYTHYNOS).action(PermissionAction.ALL).allDepUnits(true).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DEPARTMENTS).action(PermissionAction.ALL).allDepUnits(true).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_DEPARTMENTS).action(PermissionAction.ALL).allDepUnits(true).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.PAR_DEPARTMENTS).action(PermissionAction.ALL).allDepUnits(true).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.BALANCES).action(PermissionAction.VIEW).allDepUnits(true).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_BALANCES).action(PermissionAction.VIEW).allDepUnits(true).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.PAR_BALANCES).action(PermissionAction.VIEW).allDepUnits(true).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.OU_BALANCES).action(PermissionAction.VIEW).allDepUnits(true).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.ALL).allDepUnits(true).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS).action(PermissionAction.ALL).allDepUnits(true).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS_SYNDESI_KINITOU).action(PermissionAction.ALL).allDepUnits(true).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T10).action(PermissionAction.ADD).allDepUnits(allDepUnits).build());
    } else if (role.equals(Role.DIACHEIRISTIS_KODIKOLOGIOU)) {
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.CODES).action(PermissionAction.ALL).build());
    } else if (role.equals(Role.DIACHEIRISTIS_KATHIKONTOLIGIOU)) {
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DUTIES).action(PermissionAction.ALL).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DUTIES_TOPIKOS_YPEYTHYNOS).action(PermissionAction.ALL).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.PAR_DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
    } else if (role.equals(Role.GENIKOS_DIACHEIRISTIS_YLIKOU)) {
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.CODES).action(PermissionAction.VIEWONLYMANAGERS).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DUTIES).action(PermissionAction.ALL).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DUTIES_TOPIKOS_YPEYTHYNOS).action(PermissionAction.ALL).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_DEPARTMENTS).action(PermissionAction.EDIT).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_BALANCES).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T00).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T01).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T03).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T04).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T05).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T06).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T15).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T16).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T17).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T18).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T19).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS).action(PermissionAction.ALL).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS_SYNDESI_KINITOU).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS_INVENTORY).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_TEMPLATES).action(PermissionAction.ALL).build());
    } else if (role.equals(Role.TMIMATARCHIS)) {
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.CODES).action(PermissionAction.VIEWONLYMANAGERS).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.PAR_DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_BALANCES).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.SIGN_TRANSACTION).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS).action(PermissionAction.ALL).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
    }  else if (role.equals(Role.DIEFTHYNTIS)) {
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.CODES).action(PermissionAction.VIEWONLYMANAGERS).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.PAR_DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_BALANCES).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.SIGN_TRANSACTION).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS).action(PermissionAction.ALL).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
    } else if (role.equals(Role.ANAPLIROTIS_DIEFTHYNTIS)) {
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.CODES).action(PermissionAction.VIEWONLYMANAGERS).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.PAR_DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_BALANCES).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.SIGN_TRANSACTION).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS).action(PermissionAction.ALL).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
    } else if (role.equals(Role.SYNTAKTIS_DAPANON_PROMITHEIAS)) {
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.CODES).action(PermissionAction.VIEWONLYMANAGERS).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.PAR_DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.GEN_BALANCES).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T00).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T01).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T03).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T04).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T05).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T06).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T15).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T16).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T17).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T18).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T19).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS).action(PermissionAction.ALL).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_TEMPLATES).action(PermissionAction.ALL).build());
    } else if (role.equals(Role.MERIKOS_DIACHEIRISTIS_YLIKOU)) {
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.CODES).action(PermissionAction.VIEWONLYMANAGERS).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DEPARTMENTS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.PAR_DEPARTMENTS).action(PermissionAction.EDIT).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.PAR_BALANCES).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.VIEW).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.SIGN_TRANSACTION).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T07).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T08).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T09).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T11).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T13).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS).action(PermissionAction.ALL).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.DUTIES_TOPIKOS_YPEYTHYNOS).action(PermissionAction.ALL).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS_INVENTORY).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS_SYNDESI_KINITOU).action(PermissionAction.ADD).depUnitId(depUnitId).allDepUnits(allDepUnits).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_TEMPLATES).action(PermissionAction.ALL).build());
    } else if (role.equals(Role.TOPIKOS_YPEYTHYNOS)) {
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.CODES).action(PermissionAction.VIEWONLYMANAGERS).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.VIEW).ouId(ouId).depUnitId(depUnitId).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS).action(PermissionAction.SIGN_TRANSACTION).ouId(ouId).depUnitId(depUnitId).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T12).action(PermissionAction.ADD).ouId(ouId).depUnitId(depUnitId).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T14).action(PermissionAction.ADD).ouId(ouId).depUnitId(depUnitId).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.OU_BALANCES).action(PermissionAction.VIEW).ouId(ouId).depUnitId(depUnitId).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMS).action(PermissionAction.VIEW).ouId(ouId).depUnitId(depUnitId).build());
      permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_TEMPLATES).action(PermissionAction.ALL).build());
    }
    return permissions;
  }

  private List<PermissionResDto> getPermissionsAllUsersHave() {
    List<PermissionResDto> permissions = new ArrayList<>();
    permissions.add(PermissionResDto.builder().entity(PermissionEntity.CODES).action(PermissionAction.VIEW).build());
    permissions.add(PermissionResDto.builder().entity(PermissionEntity.ITEMENUMS).action(PermissionAction.VIEW).build());
    permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T07).action(PermissionAction.SIGN_TRANSACTION).allDepUnits(true).build());
    permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T08).action(PermissionAction.SIGN_TRANSACTION).allDepUnits(true).build());
    permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T12).action(PermissionAction.SIGN_TRANSACTION).allDepUnits(true).build());
    permissions.add(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T14).action(PermissionAction.SIGN_TRANSACTION).allDepUnits(true).build());
    return permissions;
  }

}