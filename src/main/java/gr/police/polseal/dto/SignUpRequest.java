package gr.police.polseal.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class SignUpRequest {

  @NotNull
  @NotBlank
  private String username;

  @NotNull
  @NotBlank
  private String email;

  private List<String> realmRoles = new ArrayList<>();

  private String firstName;

  private String lastName;
}
