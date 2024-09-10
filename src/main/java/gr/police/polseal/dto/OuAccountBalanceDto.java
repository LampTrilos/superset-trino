package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class OuAccountBalanceDto {

  private String id;

//  private ItemCodeBalanceDto itemCode;

  private OuDto ou;

//  private DepUnitDto depUnit;

  private BigDecimal balance;

  private BigDecimal totalBalance;

  private BigDecimal transactionsBalance;

  private BigDecimal temporalReservedBalance;

  private BigDecimal availableBalance;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdTimestamp;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedTimestamp;

}
