package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityDto implements Comparable<EntityDto> {

  @NotNull
  private String id;

  private String name;

  public EntityDto() {
  }

  public EntityDto(String id) {
    this.id = id;
  }

  public EntityDto(Long id) {
    this.id = String.valueOf(id);
  }

  public EntityDto(Long id, String name) {
    this.id = String.valueOf(id);
    this.name = name;
  }

  public EntityDto(String id, String name) {
    this.id = String.valueOf(id);
    this.name = name;
  }

  @Override
  public int compareTo(EntityDto entityDto) {
    return this.id.compareTo(entityDto.id);
  }
}
