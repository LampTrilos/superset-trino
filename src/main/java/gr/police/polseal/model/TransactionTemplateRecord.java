package gr.police.polseal.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 *
 * @author pgouvas
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@Entity
@Table(name = "transactiontemplaterecord")
public class TransactionTemplateRecord extends AuditableEntity {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name = "transactiontemplate_id", nullable = false)
  private TransactionTemplate transactionTemplate;

//  @NotNull
//  @ManyToOne(fetch = FetchType.EAGER)
//  @JoinColumn(name = "itemcode_id", nullable = false)
//  private ItemCode itemCode;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = false)
  @Column(name = "amount", precision = 14, scale = 6, nullable = false)
  private BigDecimal amount;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "depunit_id")
  private DepUnit depUnit;

}
