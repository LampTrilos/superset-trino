package gr.police.polseal.model.logs;

import gr.police.polseal.model.AuditableEntity;
import gr.police.polseal.model.User;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "logtransaction")
public class LogTransaction extends AuditableEntity {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

//  @ManyToOne
//  @JoinColumn(name = "ctransaction_id", nullable = false)
//  private CompoundTransaction cTransaction;

  @Enumerated(EnumType.STRING)
  private Action action;

  public enum Action {
    ADD("Πρώτη Εισαγωγή"),
    EDIT("Επεξεργασία"),
    FOR_SIGN("Προς Υπογραφή"),
    SIGN("Υπογραφή"),
    REJECT("Απόρριψη");

    private final String description;

    Action(String description) {
      this.description = description;
    }

    public String getDescription() {
      return this.description;
    }
  }

}
