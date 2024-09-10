package gr.police.polseal.resource;

import eu.europa.esig.dss.model.DSSDocument;
import eu.ubitech.bitt.core.domain.storage.service.StorageService;
import gr.police.polseal.dto.SignRequestDto;
import gr.police.polseal.model.SealingTemplate;
import gr.police.polseal.service.PDFService;
import gr.police.polseal.service.SealingTemplateService;
import gr.police.polseal.service.SigningService;
import gr.police.polseal.service.utils.PDFUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.file.Files;


/**
 * @author pgouvas
 */
@Slf4j
@Singleton
@SecuritySchemes(value = {
        @SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.HTTP, scheme = "Bearer")
})
@SecurityRequirement(name = "apiKey")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class PDFResource {

    @Inject
    PDFService pdfservice;

    @Inject
    SigningService signingservice;

    @Inject
    SealingTemplateService sealingTemplateService;

    @ConfigProperty(name = "quarkus.sealer.base-uri")
    private String sealerServer;

    private final StorageService storageService;
    private static final String TRANSACTION_BUCKET = "transaction-";

    //During the first request, we dont want to sign, only save to the Minio, so we use the onlySaveNoSign
    @POST
    @Path("pdfgen")
    public Response generatePDFs(boolean onlySaveNoSign, String pdfAsBase64, @QueryParam("rowId") String rowId, @QueryParam("signatureText") String signatureText, @QueryParam("templateCode") String templateCode,
                                 @QueryParam("userid") String userid, @QueryParam("creationTimestamp") Long creationTimestamp, SignRequestDto signRequestDto) throws Exception {

        //Save to a TemporaryFile after decoding from base64, but only if the user has not resubmitted a fresh base64 file,
        if (pdfAsBase64 != null && !pdfAsBase64.isEmpty()) {
            PDFUtils.decodeBase64ToPdf(pdfAsBase64.replace("\"", ""), "temporary-" + rowId + ".pdf");
        }

//      If document exists already on minio it is being fetched, else temporary file is created
        DSSDocument document = signingservice.checkIfExistsAlreadyAndFetch(templateCode, rowId);

        //If we only want to save and not sign, because it is the first request
        if (onlySaveNoSign) {
            storageService.putObjectOnBucket(TRANSACTION_BUCKET + templateCode, rowId + ".pdf", document.openStream(), document.openStream().available(), "application/pdf");
            Response.ResponseBuilder response = Response.status(Response.Status.OK).entity(document);
//            deletes the in memory created files that are no more needed
            Files.deleteIfExists(java.nio.file.Path.of("signed_" + rowId + ".pdf"));
            return response.build();
        }
        //If we need to both sign and save
        else {
//        find the template based on the code given
            SealingTemplate template = sealingTemplateService.findByCode(templateCode);

//        count all the pages of the document. It's being used when all pages must be sealed
            int documentPagesCount = signingservice.getDocumentPagesCount(document);

//      condition to check if the document is allowed to be sealed or the maxSignatures count has been reached.
            int signaturesSoFar = signingservice.getPDFSignaturesSofar(document);

//       we check if the latest signature on the document is valid
            boolean lastSignatureIsValid = signingservice.checkSignatureValidity(document);
//        if the signature is invalid then we return an http 409
            if (!lastSignatureIsValid) {
                //todo maybe sent email to the admin
                return Response.status(Response.Status.CONFLICT).entity("Tampered Signature detected").build();
            }

            DSSDocument createdfile = null;

            if (signaturesSoFar < template.getMaxSignatures()) {
                if (signaturesSoFar == 0) {
//            todo define what the qr will eventually contain

//                String qrText = sealerServer + "/api/v1/pdf/get-specific-pdf?rowId=" + rowId + "&templateCode=" + templateCode;
                    pdfservice.addQRCodeToPDFAndSave(rowId, templateCode, "qr-" + templateCode + "-" + rowId + ".png", document, userid, creationTimestamp, signRequestDto);
                }
//            if document exists already on minio it is being fetched, else temporary file is loaded
                document = signingservice.checkIfExistsAlreadyAndFetch(templateCode, rowId);
                createdfile = signingservice.signPdfDocument(template, document, signatureText, signaturesSoFar, documentPagesCount, rowId);
            }
//            if we have the maxed the available signatures, a qr code is put on the specific position on the document
            else {
                return Response.status(Response.Status.FORBIDDEN).entity("Max signature count reached").build();
            }
            Response.ResponseBuilder response = Response.status(Response.Status.OK).entity(createdfile);
//            deletes the in memory created files that are no more needed
            Files.deleteIfExists(java.nio.file.Path.of("signed_" + rowId + ".pdf"));

            return response.build();
    }
    }

    @POST
    @Path("get-specific-pdf")
    @Produces("application/pdf")
    public Response getSpecificPdf(@QueryParam("rowId") String rowId, @QueryParam("templateCode") String templateCode) {
        Response.ResponseBuilder response = Response.ok(pdfservice.getSpecificPdf(rowId, templateCode)).header("Content-Disposition", "attachment;filename=" + templateCode + "-" + rowId + ".pdf");
        return response.build();
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
