package gr.police.polseal.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author pgouvas
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactiontemplate")
public class TransactionTemplate extends AuditableEntity {

  @Id
  @GeneratedValue
  private Long id;

  @NotNull
  @Column(name = "description", nullable = false)
  private String description;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "transactionTemplate", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private Set<TransactionTemplateRecord> records = new HashSet<>();
    
}
