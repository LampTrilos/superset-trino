package gr.police.polseal.resource;
//
//import gr.police.polseal.dto.PermissionResDto;
import gr.police.polseal.dto.KeycloakUser;
import gr.police.polseal.dto.UserDto;
//import gr.police.polseal.dto.options.extensions.ToOptionsUser;
//import gr.police.polseal.model.permissions.PermissionAction;
//import gr.police.polseal.model.permissions.PermissionEntity;
//import gr.police.polseal.model.permissions.Role;
//import gr.police.polseal.restclients.EmailDtoClient;
//import gr.police.polseal.restclients.HumanResourceClient;
//import gr.police.polseal.restclients.ProsopikoDtoClient;
import gr.police.polseal.service.*;
import io.quarkus.security.Authenticated;
import io.quarkus.security.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.eclipse.microprofile.rest.client.inject.RestClient;
//
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;
//
//
@Singleton
//@SecuritySchemes(value = {
//  @SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.HTTP, scheme = "Bearer")
//})
//@SecurityRequirement(name = "apiKey")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Slf4j
public class UserResource {
//
  //private final UserService userService;
  private final AuthorizationService authService;
//
//  //private final PermissionService permissionService;
//
////  private final DepartmentService departmentService;
//
//  private final JwtService jwtService;
//
////  private final VathmosService vathmosService;
//
//  @Inject
//  @RestClient
//  HumanResourceClient humanResourceClient;
//


  @POST
  @Path("/enrollUser")
  public Response enrollUser(@NotNull UserDto userDto) {
      Response response = null;
      try {
          //First we make a new Keycloak user object
          Map<String, String> attributeMap = new HashMap<>();
          //Let's make the list of realm roles, in this case it will be just one and based on this role, the Minio bucket and the Trino schema will be created
          List<String> roles = new ArrayList<>();
          roles.add(userDto.getRole());
          KeycloakUser kuser = KeycloakUser.builder()
                  .username(userDto.getId())
                  .email(userDto.getEmail())
                  .enabled("true")
                  .emailVerified(true)
                  .firstName(userDto.getFirstName())
                  .lastName(userDto.getLastName())
                  .attributes(attributeMap)
                  //Realm wide roles
                  .realmRoles(roles)
                  .build();
          kuser = authService.enrollUserFromWS(kuser);
          //If all went well and the Keycloak user was created, proceed to make the Minio bucker and Trino schema based on the user role
          //if (kuser!=null) {
              response =  Response.ok(kuser).build();
              return response;
          //}
      }
      catch(Exception ex) {
          response =  Response.status(500).entity(ex.getMessage()).build();
          return response;
      }
  }



//  @GET
//  @Path("/sync-users")
//  //@Authenticated
//  public Response syncUsers() {
//    //TODO: Do we need Aut here?
////    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.ITEMENUMS)
////        .action(PermissionAction.ADD).build())) {
////      throw new UnauthorizedException();
////    }
//    List<ProsopikoDtoClient> prosopiko = humanResourceClient.getProsopikoEnergo();
//    for (ProsopikoDtoClient pr : prosopiko) {
//      userService.persistOrUpdateUser(pr);
//      //break;
//    }
//    return Response.noContent().build();
//  }
//
//  @GET
//  @Path("/sync-emails")
//  //@Authenticated
//  public Response syncEmails() {
//    //TODO: Do we need Aut here?
////    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.ITEMENUMS)
////        .action(PermissionAction.ADD).build())) {
////      throw new UnauthorizedException();
////    }
//    List<EmailDtoClient> list = humanResourceClient.getProsopikoAllMails();
//    for (EmailDtoClient dto : list) {
//      userService.persistOrUpdateUserEmailFromRest(dto);
//    }
//    return Response.noContent().build();
//  }
//
//  @GET
//  public Response getUsers(@BeanParam ToOptionsUser options) {
//    if (options.role != null) {
//      if (options.ouId != null) {
//        options.depUnitId = jwtService.getDepUnitId();
//      }
//      return Response.ok(userService.getUsersByRole(options)).build();
//    }
////    return Response.ok(userService.getUsers(options)).build();
//    //todo na allaxei h grammi parakatw
//    return Response.ok().build();
//  }
//
//  @POST
//  public Response updateUser(@NotNull UserDto userDto) {
//    return Response.ok(userService.updateUser(userDto)).build();
//  }
//
//  @GET
//  @Path("/my")
//  public Response getMyUser() {
//    return Response.ok(userService.getMyUser(jwtService.getUserId())).build();
//  }
//
//  @GET
//  @Path("/my-roles")
//  public Response getMyUserRole(@QueryParam("depUnit") Long depUnit) {
//    List<Role> roles = jwtService.getDepUnitId() == null ? permissionService.getUserRole(jwtService.getUserId()) : permissionService.getUserRoleByDepUnit(jwtService.getUserId(), Long.valueOf(jwtService.getDepUnitId()));
//    return Response.ok(String.join(", ", roles.stream().map(role -> role.getDescription()).collect(Collectors.toList()))).build();
//  }
//
//  @GET
//  @Path("/{id}")
//  public Response getUser(@PathParam("id") Long id) {
//    return Response.ok(userService.getUser(id)).build();
//  }
//
//  @GET
//  @Path("/duties")
//  public Response getUsersByDepUnitAndRole(@QueryParam("depUnit") Long depUnit, @NotNull @QueryParam("role") Role role, @QueryParam("ouId") Long ouId) {
//    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES)
//        .action(PermissionAction.VIEW).allDepUnits(true).build())
//        && !permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES)
//            .action(PermissionAction.VIEW).depUnitId(String.valueOf(depUnit)).build())
//        && (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES_TOPIKOS_YPEYTHYNOS)
//        .action(PermissionAction.VIEW).allDepUnits(true).build()) && role.equals(Role.TOPIKOS_YPEYTHYNOS))
//        && (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES_TOPIKOS_YPEYTHYNOS)
//        .action(PermissionAction.VIEW).depUnitId(String.valueOf(depUnit)).build()) && role.equals(Role.TOPIKOS_YPEYTHYNOS))
//    ) {
//      throw new UnauthorizedException();
//    }
//    if (role.equals(Role.MERIKOS_DIACHEIRISTIS_YLIKOU)) {
////      return Response.ok(userService.getUsersByManageDepUnitAndRole(depUnit, role)).build();
//      return Response.ok().build();
//    } else if (role.equals(Role.TOPIKOS_YPEYTHYNOS)) {
//      return Response.ok(userService.getUsersByOuAndRole(ouId, role, depUnit)).build();
//    } else {
//      return Response.ok(userService.getUsersByDepUnitAndRole(depUnit, role)).build();
//    }
//  }
//
////  @POST
////  @Path("/duties")
////  public Response persistUserToDepUnitAndRole(@QueryParam("depUnit") Long depUnit, @NotNull @QueryParam("role") Role role, @NotNull @QueryParam("userId") Long userId, @QueryParam("ouId") Long ouId) {
////    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES)
////        .action(PermissionAction.EDIT).allDepUnits(true).build())
////        && (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES_TOPIKOS_YPEYTHYNOS)
////        .action(PermissionAction.EDIT).allDepUnits(true).build()) && role.equals(Role.TOPIKOS_YPEYTHYNOS))
////        && (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES_TOPIKOS_YPEYTHYNOS)
////        .action(PermissionAction.VIEW).depUnitId(String.valueOf(depUnit)).build()) && role.equals(Role.TOPIKOS_YPEYTHYNOS))
////    ) {
////      DepartmentClass departmentClass =  departmentService.getDepUnitType(depUnit);
////      if (departmentClass.equals(DepartmentClass.GEN)) {
////        if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES)
////            .action(PermissionAction.EDIT).depUnitId(String.valueOf(depUnit)).build())) {
////          throw new UnauthorizedException();
////        }
////      } else if (departmentClass.equals(DepartmentClass.PAR)) {
////        String genDepUnitId = departmentService.getParDepUnit(depUnit).getGenDepUnit().getId();
////        if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES)
////            .action(PermissionAction.EDIT).depUnitId(genDepUnitId).build())
////        ) {
////          throw new UnauthorizedException();
////        }
////      } else {
////        throw new UnauthorizedException();
////      }
////    }
////    userService.persistUserToDepUnitAndRole(userId, depUnit, role, ouId);
////    return Response.noContent().build();
////  }
//
////  @POST
////  @Path("/duties/delete")
////  public Response removeUserRoleFromDepUnit(@QueryParam("depUnit") Long depUnit, @NotNull @QueryParam("role") Role role, @NotNull @QueryParam("userId") Long userId, @QueryParam("ouId") Long ouId) {
////    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES)
////        .action(PermissionAction.EDIT).allDepUnits(true).build())
////        && (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES_TOPIKOS_YPEYTHYNOS)
////        .action(PermissionAction.EDIT).allDepUnits(true).build()) && role.equals(Role.TOPIKOS_YPEYTHYNOS))
////        && (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES_TOPIKOS_YPEYTHYNOS)
////        .action(PermissionAction.VIEW).depUnitId(String.valueOf(depUnit)).build()) && role.equals(Role.TOPIKOS_YPEYTHYNOS))
////    ) {
////      DepartmentClass departmentClass =  departmentService.getDepUnitType(depUnit);
////      if (departmentClass.equals(DepartmentClass.GEN)) {
////        if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES)
////            .action(PermissionAction.EDIT).depUnitId(String.valueOf(depUnit)).build())) {
////          throw new UnauthorizedException();
////        }
////      } else if (departmentClass.equals(DepartmentClass.PAR)) {
////        String genDepUnitId = departmentService.getParDepUnit(depUnit).getGenDepUnit().getId();
////        if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.DUTIES)
////            .action(PermissionAction.EDIT).depUnitId(genDepUnitId).build())) {
////          throw new UnauthorizedException();
////        }
////      } else {
////        throw new UnauthorizedException();
////      }
////    }
////    userService.removeUserRoleFromDepUnit(userId, depUnit, role, ouId);
////    return Response.noContent().build();
////  }
////
////  @GET
////  @Path("/sync-vathmos")
////  @Authenticated
////  public Response syncVathmos() {
////    List<VathmosDtoClient> list = humanResourceClient.getVathmos();
////    for (VathmosDtoClient vathmosDto : list) {
////      vathmosService.persistOrUpdateVathmosFromRest(vathmosDto);
////    }
////    return Response.noContent().build();
////  }
}
//
