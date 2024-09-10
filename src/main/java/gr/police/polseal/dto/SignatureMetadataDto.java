package gr.police.polseal.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
//@JsonIgnoreProperties(ignoreUnknown = true)
public class SignatureMetadataDto {
    private String id;
    private int index;
    private String sealingTemplateId;
    private String position;
//    private String sealX;
//    private String sealY;

}
