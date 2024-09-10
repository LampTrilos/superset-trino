package gr.police.polseal.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sign_requests")
public class SignRequest extends AuditableEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String userId;
    private String email;
    private String phoneNumber;

    //This is the timestamp when the document has been created by the 3rd party app
    private Long creationTimestamp;

    //Timestamp of request
    private Long timestampOfRequest;

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
