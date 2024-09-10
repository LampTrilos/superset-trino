package gr.police.polseal.model.permissions;

import gr.police.polseal.model.AuditableEntity;
import gr.police.polseal.model.DepUnit;
import gr.police.polseal.model.Ou;
import gr.police.polseal.model.User;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_role", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role", "depunit_id", "ou_id"}))
public class UserRole extends AuditableEntity {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "depunit_id")
  private DepUnit depUnit;

  // Αν ο ρόλος αφορά όλες της υπηρεσίες τότε true
  @Column(name = "alldepunits")
  private Boolean allDepUnits;

  // Αφορά μόνο τον τοπικό υπεύθυνο
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "ou_id")
  private Ou ou;

}
