package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Builder
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnumDto implements Comparable<EnumDto> {

  @NotNull
  private String id;

  private String description;

  public EnumDto(Long id, String description) {
    this.id = id.toString();
    this.description = description;
  }

  public EnumDto(UUID id, String description) {
    this.id = id.toString();
    this.description = description;
  }

  public EnumDto(String id, String description) {
    this.id = id;
    this.description = description;
  }

  public EnumDto() {
  }

  @Override
  public int compareTo(EnumDto enumDto) {
    return this.id.compareTo(enumDto.getId());
  }

}
