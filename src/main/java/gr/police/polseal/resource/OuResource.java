package gr.police.polseal.resource;

import gr.police.polseal.dto.PermissionResDto;
import gr.police.polseal.dto.options.ToList;
import gr.police.polseal.dto.options.extensions.ToOptionsOu;
import gr.police.polseal.dto.page.Page;
import gr.police.polseal.model.permissions.PermissionAction;
import gr.police.polseal.model.permissions.PermissionEntity;
import gr.police.polseal.restclients.HumanResourceClient;
import gr.police.polseal.restclients.OuDtoClient;
import gr.police.polseal.service.JwtService;
import gr.police.polseal.service.OuService;
import gr.police.polseal.service.PermissionService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Singleton
@SecuritySchemes(value = {
  @SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.HTTP, scheme = "Bearer")
})
@SecurityRequirement(name = "apiKey")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class OuResource {

  private final OuService ouService;

  private final PermissionService permissionService;

  private final JwtService jwtService;

  @Inject
  @RestClient
  HumanResourceClient humanResourceClient;

//  @GET
//  public Response getOus(@BeanParam ToOptionsOu options) {
//    if (Boolean.TRUE.equals(options.manage)) {
//      options.id = new ToList(permissionService.getMyOus(jwtService.getUserId()).stream()
//          .map(ouDto -> ouDto.getId())
//          .collect(Collectors.joining(",")));
//      if (options.id == null || options.id.list.isEmpty()) {
//        return Response.ok(new Page<>()).build();
//      }
//    }
//    return Response.ok(ouService.getOus(options)).build();
//  }

  @GET
  @Path("{id}")
  public Response getOu(@PathParam("id") Long id) {
    return Response.ok(ouService.getOu(id)).build();
  }

  @GET
  @Path("/sync-ous")
  @Authenticated
  public Response syncOus() {
    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.OUS)
        .action(PermissionAction.ADD).build())) {
      throw new UnauthorizedException();
    }
    List<OuDtoClient> list = humanResourceClient.getYpiresia();
    for (OuDtoClient dto : list) {
      ouService.persistOrUpdateOuHistFromRest(dto);
    }
    for (OuDtoClient dto : list) {
      ouService.persistOrUpdateOuFromRest(dto);
    }
    return Response.noContent().build();
  }

}

