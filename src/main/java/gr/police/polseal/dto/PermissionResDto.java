package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gr.police.polseal.model.permissions.PermissionAction;
import gr.police.polseal.model.permissions.PermissionEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@Getter
@Setter
@Builder
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResDto {

  private PermissionEntity entity;

  private PermissionAction action;

  private String depUnitId;

  private Boolean allDepUnits;

  private String ouId;

}
