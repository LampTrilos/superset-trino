//package gr.police.polseal.resource;
//
//import gr.police.polseal.dto.options.extensions.ToOptionsNotification;
//import gr.police.polseal.service.JwtService;
////import gr.police.polseal.service.NotificationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
//import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
//import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
//import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
//
//import javax.inject.Inject;
//import javax.inject.Singleton;
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//@Singleton
//@SecuritySchemes(value = {
//    @SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.HTTP, scheme = "Bearer")
//})
//@SecurityRequirement(name = "apiKey")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationResource {
//
//  private final JwtService jwtService;
//
//  @Inject
//  NotificationService notificationService;
//
//  @GET
//  public Response getNotifications(@BeanParam ToOptionsNotification options) {
//    options.userId = Long.valueOf(jwtService.getUserId());
//    return Response.ok(notificationService.getNotificationsResponse(options)).build();
//  }
//
//  @POST
//  @Path("/{id}/read")
//  public Response markNotificationAsRead(@PathParam("id") Long id) {
//    notificationService.markNotificationAsRead(id);
//    return Response.noContent().build();
//  }
//
//  @POST
//  @Path("/{id}/unread")
//  public Response markNotificationAsUnread(@PathParam("id") Long id) {
//    notificationService.markNotificationAsUnread(id);
//    return Response.noContent().build();
//  }
//
//}
