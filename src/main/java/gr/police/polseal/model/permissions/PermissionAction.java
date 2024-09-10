package gr.police.polseal.model.permissions;

public enum PermissionAction {

  VIEW,
  VIEWONLYMANAGERS,
  ADD,
  EDIT,
  DELETE,
  ALL, // ALL is VIEW, ADD, EDIT, DELETE -> check PermissionUtils
  SIGN_TRANSACTION;

}