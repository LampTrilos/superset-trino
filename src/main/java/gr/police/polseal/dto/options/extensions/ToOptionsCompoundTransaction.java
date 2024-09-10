package gr.police.polseal.dto.options.extensions;


import gr.police.polseal.dto.options.ToList;
import gr.police.polseal.dto.options.ToOptions;
import gr.police.polseal.model.permissions.Role;

import javax.ws.rs.QueryParam;
import java.time.LocalDate;
import java.util.List;

public class ToOptionsCompoundTransaction extends ToOptions {

  @QueryParam("transactionType")
  public ToList transactionType;

  @QueryParam("itemCode")
  public ToList itemCode;

  @QueryParam("fromAccount")
  public ToList fromAccount;

  @QueryParam("toAccount")
  public ToList toAccount;

  @QueryParam("fromAccountOrToAccount")
  public ToList fromAccountOrToAccount;

  @QueryParam("fromOuOrToOu")
  public ToList fromOuOrToOu;

  @QueryParam("udc")
  public String udc;

  @QueryParam("dateFrom")
  public LocalDate dateFrom;

  @QueryParam("dateTo")
  public LocalDate dateTo;

  @QueryParam("compTransaction")
  public String compTransaction;

  @QueryParam("serialNumber")
  public String serialNumber;

  @QueryParam("itemId")
  public String itemId;

  public ToList managedAccounts;

  public String managedAccount;

//  @QueryParam("status")
//  public CompoundTransactionStatus status;

  @QueryParam("pending")
  public Boolean pending;

  @QueryParam("ou")
  public Boolean toOrFromOu;

  public List<Role> roles;

  @QueryParam("user")
  public ToList user;

  @QueryParam("ouUserId")
  public Long ouUserId;

  @QueryParam("viewMode")
  // 'group', 'noGroup'
  public ViewMode viewMode;

  public Long myUserId;

  public enum ViewMode {
    GROUP,
    NO_GROUP
  }

}
