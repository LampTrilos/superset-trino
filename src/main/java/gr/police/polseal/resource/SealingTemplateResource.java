package gr.police.polseal.resource;

import eu.ubitech.bitt.core.domain.storage.service.StorageService;
import gr.police.polseal.dto.SealingTemplateDto;
import gr.police.polseal.repository.SealingApplicationRepository;
import gr.police.polseal.service.JwtService;
//import gr.police.polseal.service.PermissionService;
import gr.police.polseal.service.SealingTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Slf4j
@Singleton
@SecuritySchemes(value = {
        @SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.HTTP, scheme = "Bearer")
})
@SecurityRequirement(name = "apiKey")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class SealingTemplateResource {

    private final SealingTemplateService sealingTemplateService;

    private final JwtService jwtService;
    //private final PermissionService permissionService;

    private static final String TEMPLATE_BUCKET = "template-";

    @POST
    @Path("/persistOrEditTemplate/")
    public Response persistOrEditTemplate(SealingTemplateDto templateDto) {
        sealingTemplateService.persistOrEditTemplate(templateDto);
        return Response.ok().build();
    }

    //Deletes a list of Templates
    @POST
    @Path("deleteTemplates")
    public Response delete(List<Long> templateIds) {
        sealingTemplateService.delete(templateIds);
        return Response.ok().build();
    }

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


//    @GET
//    @Path("/sync-ous")
//    @Authenticated
//    public Response syncOus() {
//        if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.OUS)
//                .action(PermissionAction.ADD).build())) {
//            throw new UnauthorizedException();
//        }
//        List<OuDtoClient> list = humanResourceClient.getYpiresia();
//        for (OuDtoClient dto : list) {
//            ouService.persistOrUpdateOuHistFromRest(dto);
//        }
//        for (OuDtoClient dto : list) {
//            ouService.persistOrUpdateOuFromRest(dto);
//        }
//        return Response.noContent().build();
//    }

}

