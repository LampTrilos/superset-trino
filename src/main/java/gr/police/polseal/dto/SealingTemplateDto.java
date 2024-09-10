package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class SealingTemplateDto {
    private String id;

//    private byte[] fileData;

    private String code;

    private String description;


    private String sealingApplicationId;


    private String maxSignatures;

    //    Is it allowed to put seal on every page ?
    private boolean sealOnEveryPage;

    private List<SignatureMetadataDto> signaturesMetadata = new ArrayList<>();

    //The message included in the confirmation email/SMS
    private String emailMessage;
}
