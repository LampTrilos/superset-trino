package gr.police.polseal.resource;

import gr.police.polseal.service.mailer.Templates;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class ApiResource {

    private final AuthenticationResource authenticationResource;

    private final UserResource userResource;

//  private final ItemCodeResource itemCodeResource;
//
//  private final DepartmentResource departmentResource;
//
//  private final BalanceResource balanceResource;

    private final TransactionResource transactionResource;

//  private final ItemResource itemResource;

    //private final PermissionResource permissionResource;

//  private final EnumService enumService;

    private final NotificationResource notificationResource;


    private final OuResource ouResource;

//  private final ItemEnumResource itemEnumResource;

    private final TransactionTemplateResource transactionTemplateResource;


    private final TransformLoadDataResource transformLoadDataResource;


//  private final SoapResource soapResource;
//
//  private final ItemMetaptosiResource itemMetaptosiResource;
//
//  private final SoapVehicleResource soapVehicleResource;
//
//  private final ExcelResource excelResource;

//  private final BalanceService balanceService;
//
//  private final ItemService itemService;
//
//  private final PliroforikiService pliroforikiService;

//  private final AnnouncementResource announcementResource;

    @Path("/auth")
    public AuthenticationResource authenticationResource() {
        return authenticationResource;
    }

    //@Authenticated
    @Path("/users")
    public UserResource userResource() {
        return userResource;
    }

//  @Authenticated
//  @Path("/codes")
//  public ItemCodeResource codeResource() {
//    return itemCodeResource;
//  }
//
//  @Authenticated
//  @Path("/departments")
//  public DepartmentResource departmentResource() {
//    return departmentResource;
//  }
//
//  @Authenticated
//  @Path("/balances")
//  public BalanceResource balanceResource() {
//    return balanceResource;
//  }

    @Authenticated
    @Path("/transactions")
    public TransactionResource transactionResource() {
        return transactionResource;
    }

//  @Authenticated
//  @Path("/items")
//  public ItemResource itemResource() {
//    return itemResource;
//  }
//
//  @Authenticated
//  @Path("/items-metaptosi")
//  public ItemMetaptosiResource itemMetaptosiResource() {
//    return itemMetaptosiResource;
//  }

//    @Authenticated
//    @Path("/permissions")
//    public PermissionResource permissionResource() {
//        return permissionResource;
//    }

    @Authenticated
    @Path("/notifications")
    public NotificationResource notificationResource() {
        return notificationResource;
    }

    @Authenticated
    @Path("transaction-templates")
    public TransactionTemplateResource transactionTemplateResource() {
        return transactionTemplateResource;
    }


   //todo na bgei to sxolio sto authenticated
//    @Authenticated
    @Path("/tl")
    public TransformLoadDataResource transformLoadDataResource() {
        return transformLoadDataResource;
    }





//  @Path("soap")
//  public SoapResource soapResource() {
//    return soapResource;
//  }
//
//
//  @Path("soapvehicle")
//  public SoapVehicleResource soapVehicleResource() {
//    return soapVehicleResource;
//  }

    // Enumerations
//  @Authenticated
//  @GET
//  @Path("/genders")
//  public Response genders() {
//    return Response.ok(enumService.getGenders()).build();
//  }
//
//  @Authenticated
//  @GET
//  @Path("/transactions-types")
//  public Response getTransactionTypes() {
//    return Response.ok(enumService.getTransactionTypes()).build();
//  }
//
//  @Authenticated
//  @GET
//  @Path("/item-code-templates")
//  public Response getItemCodeTemplates() {
//    return Response.ok(enumService.getItemCodeTemplates()).build();
//  }



    @Path("/ous")
    public OuResource ouResource() {
        return ouResource;
    }

//  @Path("/excel")
//  public ExcelResource excelResource() {
//    return excelResource;
//  }

    @GET
    @Path("/test-email")
    public Response testEmail() {
        Templates.sign_code("32482342", "SVeoeqeu43").to("nkontoe@ubitech.eu").subject("Συναλλαγή προς υπογραφή").send()
                .subscribeAsCompletionStage()
                .thenApply(x -> Response.accepted().build());
        return Response.ok().build();
    }

//  @Authenticated
//  @Path("/item-enums")
//  public ItemEnumResource itemEnumResource() {
//    return itemEnumResource;
//  }
//
//  /*
//   * End point που καλείται από την εφαρμογή Διαχείριση Προσωπικού
//   * */
//  @GET
//  @Path("/diaxirisi-prosopikou-balances")
//  public Response getDiaxisrisiProsopikoiAccountBalances(@BeanParam ToOptionsAccountBalance options, @QueryParam("secretkey") String secretKey) {
//    if (!secretKey.equals(diaxirisProsopikouApiKey)) {
//      throw new UnauthorizedException();
//    }
//    if (options.user == null || options.user.list.isEmpty()) {
//      throw new BadRequestAlertException("User is null");
//    }
//    return Response.ok(balanceService.getAccountBalances(options)).build();
//  }
//
//  @GET
//  @Path("/diaxirisi-prosopikou-items")
//  public Response getMyItems(@BeanParam ToOptionsItem options, @QueryParam("secretkey") String secretKey) {
//    if (!secretKey.equals(diaxirisProsopikouApiKey)) {
//      throw new UnauthorizedException();
//    }
//    options.assignedToUser = true;
//    if (options.user == null || options.user.list.isEmpty()) {
//      throw new BadRequestAlertException("User is null");
//    }
//    return Response.ok(itemService.getItems(options)).build();
//  }
//
//  private final BalanceVerifyService balanceVerifyService;

//  @GET
//  @Path("/delete-initial")
//  public Response deleteInitial(@QueryParam("gendepunit") Long gendepunit, @QueryParam("secretkey") String secretKey) {
//    if (!secretKey.equals("secretsecret")) {
//      throw new UnauthorizedException();
//    }
//    balanceVerifyService.deleteInitial(gendepunit);
//    return Response.noContent().build();
//  }
//
//  @GET
//  @Path("/piliroforiki-findasset")
//  public Response findAsset(@NotNull @QueryParam("kae") String kae, @QueryParam("serialNumber") String serialNumber, @QueryParam("invCode") String invCode) {
//    return Response.ok(pliroforikiService.getAsset(kae, serialNumber, invCode)).build();
//  }

//  @Authenticated
//  @Path("/announcements")
//  public AnnouncementResource announcementResource() {
//    return announcementResource;
//  }

}
