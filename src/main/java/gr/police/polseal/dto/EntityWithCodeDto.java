package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class EntityWithCodeDto implements Comparable<EntityWithCodeDto> {

  private String id;

  private String name;

  private String code;

  public EntityWithCodeDto(String id) {
    this.id = id;
  }

  public EntityWithCodeDto(Long id) {
    this.id = String.valueOf(id);
  }

  public EntityWithCodeDto(Long id, String name) {
    this.id = String.valueOf(id);
    this.name = name;
  }

  public EntityWithCodeDto(Long id, String name, String code) {
    this.id = String.valueOf(id);
    this.name = name;
    this.code = code;
  }

  public EntityWithCodeDto(String id, String name, String code) {
    this.id = id;
    this.name = name;
    this.code = code;
  }

  @Override
  public int compareTo(EntityWithCodeDto entityDto) {
    return this.id.compareTo(entityDto.id);
  }
}
