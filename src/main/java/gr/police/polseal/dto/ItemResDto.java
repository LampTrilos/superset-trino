package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class ItemResDto {

  private String id;

  private EntityWithCodeWithTemplateDto itemCode;

//  private DepUnitDto assigneeDepUnit;

  private OuDto assigneeOu;

  private UserDto assigneeUser;

  private String serialNumber;

  private boolean assignedToTopikosYpeythynos;

  private boolean deleted;

  private boolean draft;

  private boolean inventory;

//  private Katastash katastash;

  private boolean inTransaction;

  private Long inTransactionId;

  private boolean temporalReserved;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdTimestamp;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedTimestamp;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate imerominiaSumvantos;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate imerominiaParalavis;

  private String idMetaptosis;

  private String paratiriseis;

}
