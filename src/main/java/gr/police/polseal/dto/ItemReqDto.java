package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class ItemReqDto {

  private String id;

  @NotNull
  private EntityWithCodeWithTemplateDto itemCode;

  private String serialNumber;

  private EntityDto assigneeDepUnit;

  private boolean isInventory = false;

//  private Katastash katastash;

  private String paratiriseis;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate imerominiaSumvantos;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate imerominiaParalavis;

  private String idMetaptosis;

  private Long userIdEditor;

}
