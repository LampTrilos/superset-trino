package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gr.police.polseal.model.permissions.Role;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRoleDto {

  private Long id;

  private UserDto user;

  private Role role;

  private DepUnitDto depUnit;

  private Boolean allDepUnits;

  private OuDto ou;

}
