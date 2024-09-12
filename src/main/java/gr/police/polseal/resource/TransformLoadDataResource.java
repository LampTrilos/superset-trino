package gr.police.polseal.resource;

import gr.police.polseal.service.JwtService;
import gr.police.polseal.service.TransformLoadDataService;
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
import java.io.IOException;


@Slf4j
@Singleton
@SecuritySchemes(value = {
        @SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.HTTP, scheme = "Bearer")
})
@SecurityRequirement(name = "apiKey")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class TransformLoadDataResource {


    private final TransformLoadDataService transformLoadDataService;

    private final JwtService jwtService;
    private static final String TRANSACTION_BUCKET = "transaction-";


    @POST
    @Path("load-file-to-bucket")
    public Response loadFile(String csvAsBase64, @QueryParam("fileId") String fileId) throws Exception {

//        todo na mpei to tenantId apo to token
//        String tenantId = String.valueOf(jwtService.getUserId());
        String tenantId = "sam";

        transformLoadDataService.loadFileTOBucket(csvAsBase64, fileId, tenantId);

        return Response.status(Response.Status.OK).entity("File loaded successfully").build();

    }


//    @POST
//    @Path("add-qr-to-pdf")
//    public Response addQrToPdf(@QueryParam("rowId") String rowId, @QueryParam("templateCode") String templateCode, @QueryParam("qrText") String qrText) throws IOException, WriterException {
//
//        pdfservice.addQRCodeToPDFAndSave(rowId, templateCode, "qr-" + templateCode + "-" + rowId + ".png", qrText);
//
//        return Response.status(Response.Status.OK).entity("document sealed, with qr code and uploaded to bucket").build();
//    }


}
