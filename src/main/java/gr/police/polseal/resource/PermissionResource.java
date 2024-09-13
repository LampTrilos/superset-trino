//package gr.police.polseal.resource;
//
//import gr.police.polseal.service.PermissionService;
//import lombok.RequiredArgsConstructor;
//import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
//import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
//import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
//import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
//
//import javax.inject.Singleton;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//
//@Singleton
//@SecuritySchemes(value = {
//  @SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.HTTP, scheme = "Bearer")
//})
//@SecurityRequirement(name = "apiKey")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
//@RequiredArgsConstructor
//public class PermissionResource {
//
//  private final PermissionService permissionService;
//
//  @GET
//  @Path("/my")
//  public Response getMyPermissions() {
//    return Response.ok(permissionService.getMyPermissions()).build();
//  }
//
////  @GET
////  @Path("/my-departments")
////  public Response getMyDepartments() {
////    return Response.ok(permissionService.getMyDepartments()).build();
////  }
//
//}
//
