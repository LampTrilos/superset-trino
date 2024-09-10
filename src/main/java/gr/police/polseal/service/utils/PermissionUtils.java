package gr.police.polseal.service.utils;

import gr.police.polseal.dto.PermissionResDto;
import gr.police.polseal.model.permissions.PermissionAction;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class PermissionUtils {

  public static boolean containsPermission(List<PermissionResDto> userPermissionList, PermissionResDto resourcePermission) {
    for (PermissionResDto userPermission : userPermissionList) {
      if (comparePermissions(userPermission, resourcePermission)) {
        return true;
      }
    }
    return false;
  }

  private static boolean comparePermissions(PermissionResDto userPermission, PermissionResDto resourcePermission) {
    // Compare entity
    if (!userPermission.getEntity().equals(resourcePermission.getEntity())) {
      return false;
    }

    // Compare action
    if (!userPermission.getAction().equals(resourcePermission.getAction()) && !checkAllPermission(userPermission.getAction(), resourcePermission.getAction())) {
      return false;
    }

    // Check if resource is not bound to department e.g. CODES
    if (resourcePermission.getDepUnitId() == null && userPermission.getDepUnitId() == null && resourcePermission.getOuId() == null && userPermission.getOuId() == null) {
      return true;
    }

    // Check if user has action for all department units
    if (userPermission.getAllDepUnits() != null && userPermission.getAllDepUnits()) {
      return true;
    }

    // Compare department unit ID
    if (resourcePermission.getDepUnitId() != null) {
      if (userPermission.getDepUnitId() == null) {
        return false;
      } else if (!userPermission.getDepUnitId().equals(resourcePermission.getDepUnitId())) {
        return false;
      }
    }

    if (resourcePermission.getOuId() != null) {
      if (userPermission.getOuId() == null) {
        return false;
      } else if (!userPermission.getOuId().equals(resourcePermission.getOuId())) {
        return false;
      }
    }

    if (Boolean.TRUE.equals(resourcePermission.getAllDepUnits())) {
      return false;
    }

    // All checks passed, permissions match
    return true;
  }

  private static boolean checkAllPermission(PermissionAction userAction, PermissionAction resourceAction) {
    // Check if user has "ALL" permission
    if (!userAction.equals(PermissionAction.ALL)) {
      return false;
    }

    // Check if resource action is one of the specific actions ("VIEW", "ADD", "EDIT", "DELETE")
    if (!resourceAction.equals(PermissionAction.VIEW) && !resourceAction.equals(PermissionAction.ADD)
        && !resourceAction.equals(PermissionAction.EDIT) && !resourceAction.equals(PermissionAction.DELETE)) {
      return false;
    }

    // User has "ALL" permission and resource action is allowed
    return true;
  }

}
