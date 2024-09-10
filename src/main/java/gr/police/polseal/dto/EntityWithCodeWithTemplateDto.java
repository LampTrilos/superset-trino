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
public class EntityWithCodeWithTemplateDto implements Comparable<EntityWithCodeWithTemplateDto> {

  private String id;

  private String name;

  private String code;

//  private ItemCodeTemplate template;

  public EntityWithCodeWithTemplateDto(String id) {
    this.id = id;
  }

  public EntityWithCodeWithTemplateDto(Long id) {
    this.id = String.valueOf(id);
  }

  public EntityWithCodeWithTemplateDto(Long id, String name) {
    this.id = String.valueOf(id);
    this.name = name;
  }

  public EntityWithCodeWithTemplateDto(Long id, String name, String code) {
    this.id = String.valueOf(id);
    this.name = name;
    this.code = code;
  }

  public EntityWithCodeWithTemplateDto(String id, String name, String code) {
    this.id = id;
    this.name = name;
    this.code = code;
  }

//  public EntityWithCodeWithTemplateDto(Long id, String name, String code, ItemCodeTemplate template) {
//    this.id = String.valueOf(id);
//    this.name = name;
//    this.code = code;
//    this.template = template;
//  }
//
//  public EntityWithCodeWithTemplateDto(String id, String name, String code, ItemCodeTemplate template) {
//    this.id = id;
//    this.name = name;
//    this.code = code;
//    this.template = template;
//  }

  @Override
  public int compareTo(EntityWithCodeWithTemplateDto entityDto) {
    return this.id.compareTo(entityDto.id);
  }
}
