package gr.police.polseal.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignRequestDto {

    //Base64 representation of the file to be signed
    private String fileData;
    //private UserDto userDto;
    private String userId;
    private String email;
    private String phoneNumber;

    //This is the timestamp when the document has been created by the 3rd party app
    private Long creationTimestamp;

    //This will be filled only after the signing
    private Long timestampOfSignature;
    //thecode of the template to be signed (eg. VAS_122)
    private String templateCode;
    //The method by which the challenge was successfull (1 for SMS, 2 for EMail and so on)
    private String challengeChannelId;
    //The id of the row of the 3d party app, for logging purposes
    private String rowId;
    //The verirication code received by email/SMS
    private String verificationCode;
    //    The text that will be shown on the seal
    //    todo delete the signatureText if it's fixed for all
    private String signatureText;
}

//Sample ChallengeRequest for test
//{
//    "fileData": "Test.pdf",
//    "userId": "265442",
//    "email": "test@astynomia.gr",
//    "phoneNumber": "6969696969",
//    "timestampOfSignature": 1719034800000,
//    "templateCode": "VAS_122",
//    "challengeChannelId": "1",
//    "rowId": "11111",
//    "verificationCode": "123456"
//    "signatureText":"Υπεγράφη από τον τάδε"
//}
