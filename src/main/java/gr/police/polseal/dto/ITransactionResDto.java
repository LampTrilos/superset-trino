package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pgouvas
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class ITransactionResDto {
   
  private String id;

  private EnumDto type;

  //private ItemCodeDto itemCodeEntity;

  private String itemCode;
  
  private DepUnitDto fromAccountEntity;

  private String toAccount;

  private DepUnitDto toAccountEntity;

  private UserDto toUser;

  private UserDto fromUser;

  private OuDto toOu;

  private OuDto fromOu;

  @DecimalMin(value = "0.0", inclusive = false)
  @Digits(integer = 8, fraction = 6)
  private BigDecimal amount;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate actualDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdTimestamp;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedTimestamp;

  private String udc;

  private Map<String, Object> itemJson;

  private Long itemId;

  private DepUnitDto proxyDepUnitFrom;

  private DepUnitDto proxyDepUnitTo;

  public String getKey() {
    return id;
  }

  @Override
  public String toString() {
    return "ITransactionResDto{" + "id=" + id + ", proxyDepUnitFrom=" + proxyDepUnitFrom + ", proxyDepUnitTo=" + proxyDepUnitTo + '}';
  }

  private List<ITransactionResDto> childrenTransactions;

  private Boolean isGroupParent;

}