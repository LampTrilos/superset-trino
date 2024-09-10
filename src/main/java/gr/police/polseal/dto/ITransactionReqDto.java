package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pgouvas
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class ITransactionReqDto {


//  @Valid
//  @NotNull
//  private ItemCodeReqDto itemCodeEntity;

  @DecimalMin(value = "0.0", inclusive = false, message = "Η ποσότητα του υλικού πρέπει να είναι μεγαλύτερη από μηδέν")
  @Digits(integer = 8, fraction = 6)
  private BigDecimal amount;

  private Map<String, Object> itemJson;

//  private DepUnitDto fromAccountEntity;
//
//  private DepUnitDto toAccountEntity;

  private List<ITransactionReqDto> childrenTransactions;

}
