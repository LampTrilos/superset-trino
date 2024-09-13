package gr.police.polseal.resource;

import com.google.gson.Gson;
import gr.police.polseal.dto.*;
import gr.police.polseal.dto.options.SignRequestParams;
import gr.police.polseal.dto.options.ToList;
import gr.police.polseal.dto.options.extensions.ToOptionsCompoundTransaction;
import gr.police.polseal.dto.options.extensions.ToOptionsLogTransaction;
import gr.police.polseal.exception.BadRequestAlertException;
import gr.police.polseal.exception.ErrorMessage;
import gr.police.polseal.exception.FailAlertException;
import gr.police.polseal.model.TransactionType;
import gr.police.polseal.model.permissions.PermissionAction;
import gr.police.polseal.model.permissions.PermissionEntity;
import gr.police.polseal.model.permissions.Role;
import gr.police.polseal.service.*;
import io.quarkus.security.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.util.stream.Collectors;

import static gr.police.polseal.service.utils.GeneralUtils.traverseAndFindPSQLException;

@Singleton
@SecuritySchemes(value = {
  @SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.HTTP, scheme = "Bearer")
})
@SecurityRequirement(name = "apiKey")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Slf4j
public class TransactionResource {

//  private final TransactionService transactionService;

  //private final DepartmentService departmentService;

  //private final PermissionService permissionService;
  
  private final JwtService jwtService;

//  private final PDFService pdfService;

  private final LogService logService;

  //This method receives a Request from 3d party apps and sends an email/SMS containing the verification code to the user that requested it
//  @POST
//  @Path("/send-for-sign")
//  public Response sendCompoundTransactionForSign(@BeanParam SignRequestParams signRequestParams) {
//    //TODO: Lampros Do we need to check permissions?
////    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.valueOf("TRANSACTIONS_" + compoundTransactionResDto.getType().getId()))
////            .action(PermissionAction.ADD).depUnitId(jwtService.getDepUnitId()).build())) {
////      throw new UnauthorizedException();
////    }
//    try {
//      transactionService.sendCompoundTransactionForSign(id, jwtService.getUserId());
//      log.info("Η συναλλαγή με id: " + id + " στάλθηκε προς από τον χρήστη με id: " + jwtService.getUserId());
//      transactionService.syncWithExternalSystems(id, userParams);
//    } catch (Exception e) {
//      e.printStackTrace();
//      // Call the traverseAndFindPSQLException method to find PSQLException
//      BadRequestAlertException badRequestAlertException = traverseAndFindPSQLException(e);
//      if (badRequestAlertException != null) {
//        throw badRequestAlertException;
//      } else {
//        throw e;
//      }
//    }
//    try {
//      pdfService.createPDFsforTransactionId(id);
//    } catch (Exception ex) {
//      transactionService.failCompoundTransaction(id, "Τα έγγραφα προς υπογραφή απέτυχαν να δημιουργηθούν");
//      log.info("Η συναλλαγή με id: " + id + " προσπάθησε να σταλεί και απέτυχε από τον χρήστη με id: " + jwtService.getUserId());
//      transactionService.syncWithExternalSystems(id, userParams);
//    }
//    return Response.noContent().build();
//  }




//  @GET
//  public Response getCompoundTransactions(@BeanParam ToOptionsCompoundTransaction options) {
//    options.roles = jwtService.getDepUnitId() == null ? permissionService.getUserRole(jwtService.getUserId()) : permissionService.getUserRoleByDepUnit(jwtService.getUserId(), Long.valueOf(jwtService.getDepUnitId()));
//    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS)
//        .action(PermissionAction.ALL).allDepUnits(true).build())) {
//      if (permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS)
//          .action(PermissionAction.VIEW).depUnitId(jwtService.getDepUnitId()).build())) {
//        if (options.roles.contains(Role.TOPIKOS_YPEYTHYNOS)) {
//          options.user = new ToList(String.valueOf(jwtService.getUserId()));
//          options.ouUserId = jwtService.getUserId();
//          options.toOrFromOu = true;
//        } else {
//          options.managedAccounts = new ToList(departmentService.getManagedDepUnits(Long.valueOf(jwtService.getDepUnitId())).stream()
//              .map(String::valueOf)
//              .collect(Collectors.joining(",")));
//          options.managedAccount = jwtService.getDepUnitId();
//        }
//      } else {
//        throw new UnauthorizedException();
//      }
//    }
//    return Response.ok(transactionService.getCompoundTransactions(options)).build();
//  }

//  @GET
//  @Path("/my")
//  public Response getMyCompoundTransactions(@BeanParam ToOptionsMyCompoundTransaction options) {
//    options.user = String.valueOf(jwtService.getUserId());
//    return Response.ok(transactionService.getUserCompoundTransactions(options)).build();
//  }
//
//  @GET
//  @Path("/{id}")
//  public Response getCompoundTransaction(@PathParam("id") Long id) {
//    // TODO check permissions
//    return Response.ok(transactionService.getCompoundTransaction(id)).build();
//  }

//  @GET
//  @Path("/{id}/logs")
//  public Response getLogTransactions(@PathParam("id") Long id, @BeanParam ToOptionsLogTransaction options) {
//    // TODO check permissions
//    if (options == null) {
//      options = new ToOptionsLogTransaction();
//    }
//    options.transactionId = id;
//    return Response.ok(logService.getLogTransactionsResponse(options)).build();
//  }

//  @POST
//  public Response persistCompoundTransaction(@Valid CompoundTransactionReqDto compoundTransactionReqDto) {
//    // check permissions
//    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.valueOf("TRANSACTIONS_" + compoundTransactionReqDto.getType().getId()))
//        .action(PermissionAction.ADD).depUnitId(jwtService.getDepUnitId()).build())) {
//      throw new UnauthorizedException();
//    }
//    if (compoundTransactionReqDto.getType().getId().equals(TransactionType.T12.name())) {
//      if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.valueOf("TRANSACTIONS_" + compoundTransactionReqDto.getType().getId()))
//          .action(PermissionAction.ADD).ouId(compoundTransactionReqDto.getFromOu().getId()).build())) {
//        throw new UnauthorizedException();
//      }
//    }
//    if (compoundTransactionReqDto.getType().getId().equals(TransactionType.T14.name())) {
//      if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.valueOf("TRANSACTIONS_" + compoundTransactionReqDto.getType().getId()))
//          .action(PermissionAction.ADD).ouId(compoundTransactionReqDto.getToOu().getId()).build())) {
//        throw new UnauthorizedException();
//      }
//    }
//    // end check permissions
//    compoundTransactionReqDto.setAuthor(new EntityDto(jwtService.getUserId()));
//    if (compoundTransactionReqDto.getType().getId().equals(TransactionType.T12.name())) {
//      compoundTransactionReqDto.setFromUser(new UserDto().builder().id(String.valueOf(jwtService.getUserId())).build());
//    }
//    if (compoundTransactionReqDto.getType().getId().equals(TransactionType.T14.name())) {
//      compoundTransactionReqDto.setToUser(new UserDto().builder().id(String.valueOf(jwtService.getUserId())).build());
//    }
//    CompoundTransactionResDto resDto = transactionService.persistCompoundTransaction(compoundTransactionReqDto);
//    log.info("Η συναλλαγή με id: " + resDto.getId() + " επεξεργάσθηκε από τον χρήστη με id: " + jwtService.getUserId());
//    return Response.ok(resDto).build();
//  }

//  @POST
//  @Path("/send-for-sign/{id}")
//  public Response sendCompoundTransactionForSign(@PathParam("id") Long id, @BeanParam ToUserParams userParams) {
//    CompoundTransactionResDto compoundTransactionResDto = transactionService.getCompoundTransaction(id);
//    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.valueOf("TRANSACTIONS_" + compoundTransactionResDto.getType().getId()))
//        .action(PermissionAction.ADD).depUnitId(jwtService.getDepUnitId()).build())) {
//      throw new UnauthorizedException();
//    }
//    try {
//      transactionService.sendCompoundTransactionForSign(id, jwtService.getUserId());
//      log.info("Η συναλλαγή με id: " + id + " στάλθηκε προς από τον χρήστη με id: " + jwtService.getUserId());
//      transactionService.syncWithExternalSystems(id, userParams);
//    } catch (Exception e) {
//      e.printStackTrace();
//      // Call the traverseAndFindPSQLException method to find PSQLException
//      BadRequestAlertException badRequestAlertException = traverseAndFindPSQLException(e);
//      if (badRequestAlertException != null) {
//        throw badRequestAlertException;
//      } else {
//        throw e;
//      }
//    }
//    try {
//      pdfService.createPDFsforTransactionId(id);
//    } catch (Exception ex) {
//      transactionService.failCompoundTransaction(id, "Τα έγγραφα προς υπογραφή απέτυχαν να δημιουργηθούν");
//      log.info("Η συναλλαγή με id: " + id + " προσπάθησε να σταλεί και απέτυχε από τον χρήστη με id: " + jwtService.getUserId());
//      transactionService.syncWithExternalSystems(id, userParams);
//    }
//    return Response.noContent().build();
//  }


//  @POST
//  @Path("/sign/{id}")
//  public Response signCompoundTransaction(@PathParam("id") Long id, @NotNull @QueryParam("verifyCode") String verifyCode, @BeanParam ToUserParams userParams) {
//    if (!transactionService.hasPermissionsToSignCompoundTransaction(id, jwtService.getUserId())) {
//      throw new UnauthorizedException();
//    }
//    try {
//      transactionService.signCompoundTransaction(id, verifyCode, jwtService.getUserId());
//      log.info("Η συναλλαγή με id: " + id + " υπογράφηκε από τον χρήστη με id: " + jwtService.getUserId());
//      transactionService.syncWithExternalSystems(id, userParams);
//    } catch (Exception e) {
//      e.printStackTrace();
//      log.info("Exception is an instance of " + e.getClass().getSimpleName());
//      String message;
//      if (e.getClass().getSimpleName().equals("BadRequestAlertException")) {
//        message = new Gson().toJson(((BadRequestAlertException) e).getResponse().getEntity());
//        log.info(message);
//        if (message.contains(ErrorMessage.VERIFICATION_CODE_ERROR.getDescription())) {
//          throw e;
//        }
//        message = "Η συναλλαγή απέτυχε. " + message;
//      } else {
//        message = "Η συναλλαγή απέτυχε";
//      }
//      transactionService.failCompoundTransaction(id, message);
//      log.info("Η συναλλαγή με id: " + id + " προσπάθησε να υπογραφεί και απέτυχε από τον χρήστη με id: " + jwtService.getUserId());
//      transactionService.syncWithExternalSystems(id, userParams);
//      throw new FailAlertException(message);
//    }
//    return Response.noContent().build();
//  }

//  @GET
//  @Path("/has-sign-permissions/{id}")
//  public Response hasPermissionsToSignCompoundTransaction(@PathParam("id") Long id) {
//    return Response.ok(transactionService.hasPermissionsToSignCompoundTransaction(id, jwtService.getUserId())).build();
//  }
//
//  @GET
//  @Path("/has-reject-permissions/{id}")
//  public Response hasPermissionsToRejectCompoundTransaction(@PathParam("id") Long id) {
//    return Response.ok(transactionService.hasPermissionsToRejectCompoundTransaction(id, jwtService.getUserId())).build();
//  }
//
//  @POST
//  @Path("/reject/{id}")
//  public Response rejectCompoundTransaction(@PathParam("id") Long id, @BeanParam ToUserParams userParams) {
//    if (!transactionService.hasPermissionsToRejectCompoundTransaction(id, jwtService.getUserId())) {
//      throw new UnauthorizedException();
//    }
//    transactionService.rejectCompoundTransaction(id, jwtService.getUserId());
//    log.info("Η συναλλαγή με id: " + id + " απορρίφθηκε από τον χρήστη με id: " + jwtService.getUserId());
//    transactionService.syncWithExternalSystems(id, userParams);
//    return Response.noContent().build();
//  }
//
//  @POST
//  @Path("/resend-email/{id}")
//  public Response resendNotificationAndEmail(@PathParam("id") Long id) {
//    transactionService.resendNotificationAndEmail(id);
//    return Response.noContent().build();
//  }
//
//  @POST
//  @Path("/{id}/delete")
//  public Response deleteCompoundTransaction(@PathParam("id") Long id) {
//    // TODO check permissions
//    transactionService.deleteCompoundTransaction(id);
//    log.info("Η συναλλαγή με id: " + id + " διαγράφηκε από τον χρήστη με id: " + jwtService.getUserId());
//    return Response.noContent().build();
//  }
//
//  @GET
//  @Path("/{id}/files")
//  public Response getCompoundTransactionFiles(@PathParam("id") Long id) {
//    // TODO check permissions
//    return Response.ok(transactionService.getCompoundTransactionFiles(id)).build();
//  }
//
//  @POST
//  @Path("/{id}/files")
//  @Consumes(MediaType.MULTIPART_FORM_DATA)
//  public Response persistCompoundTransactionFile(@PathParam("id") Long id, @QueryParam("type") @DefaultValue("OTHER_FILE") CompoundTransactionFileType type, @MultipartForm FormData formData) {
//    // TODO check permissions
//    transactionService.persistCompoundTransactionFile(id, formData, type, false);
//    return Response.noContent().build();
//  }
//
//  @GET
//  @Path("/files/{fileId}")
//  @Produces(MediaType.APPLICATION_OCTET_STREAM)
//  public Response getCompoundTransactionFile(@NotNull @PathParam("fileId") Long fileId) {
//    // TODO check permissions
//    FileResDto file = transactionService.getCompoundTransactionFile(fileId);
//    byte[] bytes = file.getBytes();
//    ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
//    baos.write(bytes, 0, bytes.length);
//    Response.ResponseBuilder response = Response.ok((StreamingOutput) output -> baos.writeTo(output));
//    response.header("Content-Disposition", "attachment;filename=" + file.getFileName());
//    response.header("Content-Type", file.getContentType());
//    return response.build();
//  }
//
//  @POST
//  @Path("/{id}/files/{fileId}/delete")
//  public Response deleteCompoundTransactionFile(@NotNull @PathParam("id") Long id, @NotNull @PathParam("fileId") Long fileId) {
//    if (!transactionService.hasPermissionsToDeleteCompoundTransactionFile(id, fileId)) {
//      throw new UnauthorizedException();
//    }
//    transactionService.deleteCompoundTransactionFile(id, fileId);
//    return Response.noContent().build();
//  }
//
//  @GET
//  @Path("/{id}/files/for-sign")
//  public Response getCompoundTransactionFilesForSign(@PathParam("id") Long id) {
//    // TODO check permissions
//    Long depUnitId = jwtService.getDepUnitId() == null ? null : Long.valueOf(jwtService.getDepUnitId());
//    return Response.ok(transactionService.getCompoundTransactionFilesForSign(id, depUnitId)).build();
//  }
//
//  @GET
//  @Path("/itransactions")
//  public Response getITransactions(@BeanParam ToOptionsCompoundTransaction options) {
//    options.roles = jwtService.getDepUnitId() == null ? permissionService.getUserRole(jwtService.getUserId()) : permissionService.getUserRoleByDepUnit(jwtService.getUserId(), Long.valueOf(jwtService.getDepUnitId()));
//    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS)
//        .action(PermissionAction.ALL).allDepUnits(true).build())) {
//      if (permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS)
//          .action(PermissionAction.VIEW).depUnitId(jwtService.getDepUnitId()).build())) {
//        if (options.roles.contains(Role.TOPIKOS_YPEYTHYNOS)) {
//          options.user = new ToList(String.valueOf(jwtService.getUserId()));
//          options.toOrFromOu = true;
//        } else {
//          options.managedAccounts = new ToList(departmentService.getManagedDepUnits(Long.valueOf(jwtService.getDepUnitId())).stream()
//              .map(String::valueOf)
//              .collect(Collectors.joining(",")));
//        }
//      } else {
//        throw new UnauthorizedException();
//      }
//    }
//    return Response.ok(transactionService.getITransactions(options)).build();
//  }

  /*@GET
  @Path("/itransactions/all")
  public Response getAllITransactions(@BeanParam ToOptionsCompoundTransaction options) {
    return Response.ok(transactionService.getITransactions(options)).build();
  }*/

//  @GET
//  @Path("/{id}/children")
//  public Response getChildrenCompoundTransaction(@PathParam("id") Long id) {
//    // TODO check permissions
//    return Response.ok(transactionService.getChildrenCompoundTransaction(id)).build();
//  }
//
//  @GET
//  @Path("/itransactions/my")
//  public Response getMyITransactions(@BeanParam ToOptionsCompoundTransaction options) {
//    options.myUserId = jwtService.getUserId();
//    return Response.ok(transactionService.getITransactions(options)).build();
//  }

  /*@POST
  @Path("/initial-gen-account-balance")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response persistTransactionInitialDataGenBalance(@MultipartForm FormData formData, @QueryParam("depUitId") String depUitId) throws IOException, ParseException, CsvException {
    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.TRANSACTIONS_T10)
        .action(PermissionAction.ADD).allDepUnits(true).build())) {
      throw new UnauthorizedException();
    }
    return Response.ok(transactionService.persistTransactionInitialDataGenBalance(formData, Long.valueOf(depUitId))).build();
  }*/

  /*@GET
  @Path("/prototype-file")
  //@Produces("application/vnd.ms-excel")
  public Response getInitialDataGenBalancePrototypeFile() throws IOException {

    final String filename = "Apografi2023.xlsx";
    InputStream is = new ByteArrayInputStream(toByteArray());

    Response.ResponseBuilder responseBuilder = Response.ok(is);
    responseBuilder.header("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
    responseBuilder.header("Content-Type", "application/vnd.ms-excel");
    return responseBuilder.build();
  }


  public byte[] toByteArray() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
      XSSFWorkbook workbook = new XSSFWorkbook();
      XSSFSheet sheet = workbook.createSheet("Sheet 1");
      List<String> data = transactionService.getInitialDataGenBalancePrototypeData(Long.valueOf(jwtService.getDepUnitId()));
      System.out.println("Generating Excel content: " + new Gson().toJson(data));
      Row row = sheet.createRow(0);
      int cellIndex = 0;
      for (String cellData : data) {
        Cell cell = row.createCell(cellIndex++);
        cell.setCellValue(cellData);
      }
      workbook.write(bos);
      bos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bos.toByteArray();
  }*/

}

