package gr.police.polseal.service;

import gr.police.polseal.dto.SignRequestDto;
import gr.police.polseal.dto.mapper.SignRequestMapper;
import gr.police.polseal.model.SealingTemplate;
import gr.police.polseal.model.SignRequest;
import gr.police.polseal.repository.SealingTemplateRepository;
import gr.police.polseal.repository.SignRequestRepository;
import gr.police.polseal.resource.PDFResource;
import gr.police.polseal.service.mailer.Templates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class SignRequestService {

    private final SignRequestMapper signRequestMapper;
    private final SignRequestRepository signRequestRepository;
    private final SealingTemplateRepository sealingTemplateRepository;
    private final PDFResource pdfResource;

    @Transactional
    @Traced
    public int persistOrEditSignRequest(SignRequestDto signRequestDto) throws Exception {
        //Our current request
        SignRequest currentRequest = null;
        //If the user submitted a verification code
        if (signRequestDto.getVerificationCode() != null && signRequestDto.getVerificationCode().length() > 0) {
            //First we ll see the latest non signed request for this row (of the 3d party app) for this specific template Id
            List<SignRequest> foundRequests = signRequestRepository.find("rowId", signRequestDto.getRowId()).list();
            //If the were already requests for this row id
            if (!foundRequests.isEmpty()) {
                foundRequests = foundRequests.stream()
                        .sorted(Comparator.comparing(SignRequest::getTimestampOfRequest))
                        .collect(Collectors.toList());
                //This is our current request
                currentRequest = foundRequests.get(0);
                //TODO: If the Challenge Channel is via Authenticator, create a live code to validate with the user input
                ///If the request is not already fulfilled AND the user also submitted a verification code, check if it matches the saved one, also check if the userId is the same and sign
                if (currentRequest.getTimestampOfSignature() == null && currentRequest.getVerificationCode().equalsIgnoreCase(signRequestDto.getVerificationCode()) && currentRequest.getUserId().equalsIgnoreCase(signRequestDto.getUserId())) {
                    Response responseForSign = pdfResource.generatePDFs(false, signRequestDto.getFileData(), signRequestDto.getRowId(), signRequestDto.getSignatureText(), signRequestDto.getTemplateCode(),
                            signRequestDto.getUserId(), currentRequest.getCreationTimestamp(), signRequestDto);
                    //Persist the request if the operation was successfull
                    if (responseForSign.getStatus() == 200) {
                        currentRequest.setTimestampOfSignature(new Date().getTime());
                        signRequestRepository.persistAndFlush(currentRequest);
                    } else {
                        return responseForSign.getStatus();
                    }
                    return 202; //Accepted
                }
                //If the verification code doesnt match, send unauthorized
                else {
                    return 401;
                }
            }
            //If the user sent a verification code but there werent any requests prior to that
            else {
                return 401;
            }
        }
        //Since we have no existing request with this id OR the user didnt submit a verification code, we need a new one, and also save the pdf to Minio
        else {
            currentRequest = signRequestMapper.toSignRequest(signRequestDto);
            //Set the time it was received
            currentRequest.setTimestampOfRequest(new Date().getTime());
            //And a random verification code that the user will receive by email, and we will verify in the subsequent http call
            //currentRequest.setVerificationCode(generateRandomString());
            currentRequest.setVerificationCode("111111");
            //At first persist, timestamp of Signature must become null
            currentRequest.setTimestampOfSignature(null);

            //If the user has submitted fileData, save the pdf to Minio
            if (signRequestDto.getFileData()!=null && signRequestDto.getFileData().length() > 0) {
                Response responseForSign = pdfResource.generatePDFs(true, signRequestDto.getFileData(), signRequestDto.getRowId(), null, signRequestDto.getTemplateCode(),
                        signRequestDto.getUserId(), signRequestDto.getCreationTimestamp(), signRequestDto);
            }
            //We will send the email/sms to the user
            //For this we will need to fetch the template for the email Message
            SealingTemplate currentTemplate = null;
            List<SealingTemplate> foundTemplates = sealingTemplateRepository.find("code", currentRequest.getTemplateCode()).list();
            //If there is a template with this code
            if (!foundTemplates.isEmpty()) {
                currentTemplate = foundTemplates.get(0);
                Templates.sign_code(currentRequest.getVerificationCode(), currentRequest.getRowId())
                        .to(currentRequest.getEmail()).subject(currentTemplate.getEmailMessage() + " " + currentRequest.getVerificationCode()).send()
                        .subscribeAsCompletionStage()
                        .thenApply(x -> Response.accepted().build());
                signRequestRepository.persistAndFlush(currentRequest);
                return 201; //Created
            }
            //Else there is no template for the code sent to this backend, so return error not found
            else if (foundTemplates.isEmpty()) {
                return 404;
            }
        }
        return 200;
    }


    private String generateRandomString() {
        Random random = new Random();
        // Generate a random number between 100000 and 999999
        return String.valueOf(random.nextInt(900000) + 100000);
    }

}
