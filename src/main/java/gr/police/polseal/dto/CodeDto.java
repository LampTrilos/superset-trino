package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@Getter
@Setter
@Builder
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class CodeDto {

  private String id;

  private String code;

}
