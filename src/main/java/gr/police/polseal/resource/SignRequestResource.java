package gr.police.polseal.resource;

import gr.police.polseal.dto.SignRequestDto;
import gr.police.polseal.service.KafkaProducerService;
import gr.police.polseal.service.SignRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@Singleton
@SecuritySchemes(value = {
        @SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.HTTP, scheme = "Bearer")
})
@SecurityRequirement(name = "apiKey")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class SignRequestResource {


    @Inject
    KafkaProducerService kafkaProducerService;

    @Inject
    SignRequestService signRequestService;

    //Receives a rest call from 3d party apps, and does the following:
    //The first call saves the pdf to Trino, as well as the SignRequest to our own table
    // Checks if there is a not null verification code, if the code is null, it persists an entry with a Date, UserId, templaTeCode, rowId (of the 3d part app) and verificationCode to be verified later
    //If the verification code is not null, it verifies that it is correct and puts the request to the Kafka queue, in order for the document to be signed
    //If the document is already in the minio (meaning it already has a signature), deny the request if the document has the max number of signatures
    //The user will have to upload the document only once, aferwards this back will retrieve it through minio
    @Path("/newSignRequest")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response receiveSignRequest(@Valid SignRequestDto signRequest) {
        //Our return Objects
        Integer status = 500;
        String message = "Something went wrong, Pol-Sealer team";
        //If the signRequest does not contain a verification Code (it is null or empty), persist a record in the Sign Requests Table with the to-be generated verification code
    //TODO: Lampros Do we need to check permissions or authentication?
//    if (!permissionService.checkIfUserHasPermission(PermissionResDto.builder().entity(PermissionEntity.valueOf("TRANSACTIONS_" + compoundTransactionResDto.getType().getId()))
//            .action(PermissionAction.ADD).depUnitId(jwtService.getDepUnitId()).build())) {
//      throw new UnauthorizedException();
//    }
        //TODO: Lampros-Tasos Block if max signatures reached, code the implementation using the PDF libraries
        //this can only happen if the user has submitted the verification is is trully attempting to sign a maxed document
//        if (signRequest.getChallengeUserInput() != null) {
//            // Document  = fetchDocumentFromMinio
//            if (DocumentAlreadyIsAtMaxSignatures) {
//                //forbidden
//                return Response.status(403) .build();
//            }
//        }
    try {
        status = signRequestService.persistOrEditSignRequest(signRequest);
        if (status == 201) {
            message = "Η συναλλαγή για το πρότυπο: " + signRequest.getTemplateCode() + "  και την εγγραφή " + signRequest.getRowId() + " στάλθηκε επιτυχώς προς τον χρήστη με μητρώο: " + signRequest.getUserId();
        }
        if (status == 202) {
            message = "Ο χρήστης: " + signRequest.getUserId() +  " υπέγραψε επιτυχώς την εγγραφή: "+ signRequest.getRowId();
        }
        else if (status == 401) {
            message = "Η συναλλαγή για το πρότυπο: " + signRequest.getTemplateCode() + "  και την εγγραφή " + signRequest.getRowId() +  " προς τον χρήστη με μητρώο: " + signRequest.getUserId() + " απέτυχε. Παρακαλώ σιγουρευτείτε ότι το έγγραφο δε φέρει τις μέγιστες δυνατές υπογραφές, και ότι έχετε υποβάλει το σωστό κωδικό για την υπογραφή του.";
        }
        else if (status == 404) {
            message = "Η συναλλαγή για το πρότυπο: " + signRequest.getTemplateCode() + "  και την εγγραφή " + signRequest.getRowId() +  " προς τον χρήστη με μητρώο: " + signRequest.getUserId() + " απέτυχε. Παρακαλώ σιγουρευτείτε ότι υπάρχει πρότυπο (Στην εφαρμογή της Σφραγίδας) συσχετισμένο με την εγγραφή που επιθυμείτε να υπογράψετε.";
        }
        //logg.info("Η συναλλαγή με id: " + id + " στάλθηκε προς από τον χρήστη με id: " + jwtService.getUserId());
        log.info(message);

    } catch (Exception e) {
        status = 500;
        message = "Η συναλλαγή για το πρότυπο: " + signRequest.getTemplateCode() + "  και την εγγραφή " + signRequest.getRowId() +  " προς τον χρήστη με μητρώο: " + signRequest.getUserId() + " απέτυχε ";
        log.info(message);
        e.printStackTrace();
    }
    finally {
        return Response.status(status).entity(message).build();
    }
    }
}

//Sample ChallengeRequest for test
//{
//    "fileName": "Test.pdf",
//    "fileData": "Test.pdf",
//    "userId": "265442",
//    "email": "test@astynomia.gr",
//    "phoneNumber": "6969696969",
//    "timestampOfSignature": 1719034800000,
//    "templateCode": "VAS_122",
//    "challengeChannelId": "1",
//    "rowId": "11111",
//    "verificationCode": "123456"
//}
