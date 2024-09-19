//package gr.police.polseal.resource;
//
//import gr.police.polseal.dto.EntityDto;
//import gr.police.polseal.dto.TransactionTemplateReqDto;
//import gr.police.polseal.dto.options.extensions.ToOptionsTransactionTemplate;
//import gr.police.polseal.service.JwtService;
//import gr.police.polseal.service.TransactionTemplateService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
//import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
//import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
//import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
//
//import javax.inject.Singleton;
//import javax.validation.Valid;
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//
//@Slf4j
//@Singleton
//@SecuritySchemes(value = {
//  @SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.HTTP, scheme = "Bearer")
//})
//@SecurityRequirement(name = "apiKey")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
//@RequiredArgsConstructor
//public class TransactionTemplateResource {
//
//  private final TransactionTemplateService transactionTemplateService;
//
//  private final JwtService jwtService;
//
////  @GET
////  public Response getTransactionTemplates(@BeanParam ToOptionsTransactionTemplate options) {
////    options.user = jwtService.getUserId();
////    return Response.ok(transactionTemplateService.getTransactionTemplates(options)).build();
////  }
////
////  @GET
////  @Path("{id}")
////  public Response getTransactionTemplate(@PathParam("id") Long id) {
////    transactionTemplateService.hasPermissions(id, jwtService.getUserId());
////    return Response.ok(transactionTemplateService.getTransactionTemplate(id)).build();
////  }
//
//  @POST
//  public Response persistOrUpdate(@Valid TransactionTemplateReqDto transactionTemplateDto) {
//    if (transactionTemplateDto.getId() == null) {
//      transactionTemplateDto.setUser(new EntityDto(jwtService.getUserId()));
//    } else {
//      transactionTemplateService.hasPermissions(Long.valueOf(transactionTemplateDto.getId()), jwtService.getUserId());
//    }
//    return Response.ok(transactionTemplateService.persistOrUpdate(transactionTemplateDto)).build();
//  }
//
//  @POST
//  @Path("/{id}/delete")
//  public Response delete(@PathParam("id") Long id) {
//    transactionTemplateService.hasPermissions(id, jwtService.getUserId());
//    transactionTemplateService.delete(id);
//    return Response.noContent().build();
//  }
//
//}
//
