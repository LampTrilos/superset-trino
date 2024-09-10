package gr.police.polseal.resource;

import gr.police.polseal.dto.PermissionResDto;
import gr.police.polseal.dto.SealingApplicationDto;
import gr.police.polseal.dto.mapper.SealingApplicationMapper;
import gr.police.polseal.dto.page.Page;
import gr.police.polseal.exception.NotFoundAlertException;
import gr.police.polseal.model.SealingApplication;
import gr.police.polseal.model.User;
import gr.police.polseal.model.permissions.PermissionAction;
import gr.police.polseal.model.permissions.PermissionEntity;
import gr.police.polseal.repository.SealingApplicationRepository;
import gr.police.polseal.service.JwtService;
import gr.police.polseal.service.PermissionService;
import gr.police.polseal.service.SealingApplicationService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.UnauthorizedException;
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
public class SealingApplicationResource {

    private final SealingApplicationService sealingApplicationService;
    private final SealingApplicationRepository sealingApplicationRepository;
    private final SealingApplicationMapper sealingApplicationMapper;

    private final JwtService jwtService;
    private final PermissionService permissionService;


    //Fetches the active or inactive Sealing Applications
    @GET
    @Path("/getSealingApps")
    @Authenticated
    public Response getSealingApps() {
        List<SealingApplicationDto> results = sealingApplicationService.getSealingApps();
        if (results.isEmpty()) {
            return Response.ok(new Page<>()).build();
        }
        return Response.ok(results).build();
    }

    //Fetches specific Sealing Applications By Id
    @GET
    @Path("/getSealingAppById/{id}")
    @Authenticated
    public Response getSealingApp(@PathParam("id") String id) {
        SealingApplicationDto result =  sealingApplicationService.getSealingAppById(id);
        if (result != null) {
            return Response.ok(result).build();
        }
        return Response.noContent().build();
    }

    //Used by 3d party apps to know what challenge channels are available
    @GET
    @Path("/getSealingApp/{appCode}")
    public Response getSealingAppByCode(@PathParam("appCode") String appCode) {
        List<SealingApplication> results = sealingApplicationRepository.find("appCode", appCode).list();
        SealingApplicationDto result = results.size() > 0 ? sealingApplicationMapper.toSealingApplicationDto(results.get(0)) : null;
        if (result != null) {
            return Response.ok(result).build();
        }
        return Response.noContent().build();
    }

    //Creates or edits a Sealing App
    @POST
    @Path("/insertOrUpdateSealingApp")
    @Authenticated
    public Response insertOrUpdateSealingApp(SealingApplicationDto applicationDto) {
        //TODO: Tasos Reinstate this code
//        if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.OUS)
//                .action(PermissionAction.ADD).build())) {
//            throw new UnauthorizedException();
//        }
        //for (SealingApplicationDto dto : apps) {
        sealingApplicationService.persistOrUpdateSealingAppsFromRest(applicationDto);
        //}
        return Response.noContent().build();
    }

    //Deletes a list of Sealing Applications
    @POST
    @Path("deleteApps")
    @Authenticated
    public Response delete(List<Long> appIds) {
        boolean successfullyDeletedAll = sealingApplicationService.delete(appIds);
        if(!successfullyDeletedAll){
            return Response.status(Response.Status.FORBIDDEN).entity("Βρέθηκαν συσχετιζόμενα Templates. Αφαιρέστε τα πρώτα και έπειτα προβείτε σε διαγραφή της εφαρμογής").build();
        }
        return Response.ok().build();
    }

}

