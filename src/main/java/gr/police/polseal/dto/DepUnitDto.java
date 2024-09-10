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
public class DepUnitDto {

  private String id;

  private String code;

  private String name;

  @Override
  public String toString() {
    return "DepUnitDto{" + "id=" + id + ", code=" + code + ", name=" + name + '}';
  }  
  
}
