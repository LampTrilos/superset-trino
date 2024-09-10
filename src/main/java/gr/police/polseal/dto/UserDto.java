package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

  private String id;

  private String lastName;

  private String firstName;

  private String email;

  private String phone;

  private String arithmosMitroou;

  private String vathmosDescription;
}
