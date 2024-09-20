package gr.police.polseal.model.logs;

import gr.police.polseal.model.AuditableEntity;
import gr.police.polseal.model.User;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
//@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "logitemsyndesikinitou")
public class LogSyndesiKinitou extends AuditableEntity {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "item_id", nullable = false)
  private Long itemId;

  @Enumerated(EnumType.STRING)
  private Action action;

  @Lob
  @Type(type = "org.hibernate.type.TextType")
  private String comments;

  public enum Action {
    ADD("Πρώτη Εισαγωγή"),
    EDIT("Επεξεργασία"),
    DELETE("Διαγραφή"),
    EDITASSIGNEEOU("ΑΝΑΚΑΝΟΜΗ"), // 2
    EDITIMEROMINIAENERGOP("ΕΝΕΡΓΟΠΟΙΗΣΗ"), //1
    EDITASSIGNEEUSER("ΑΛΛΑΓΗ ΚΑΤΟΧΟΥ"), //9
    EDITEIDOSSYNDESISKINITOU("ΑΛΛΑΓΗ ΕΙΔΟΥΣ ΣΥΝΔΕΣΗΣ"), //8
    EDITPERIAGOGI("ΕΝΕΡΓΟΠΟΙΗΣΗ ΠΕΡΙΑΓΩΓΗΣ"), //10
    EDITEFFEDRIKI("ΕΦΕΔΡΙΚΗ"), //7
    EDITEIDOSDIKTYOU("ΑΛΛΑΓΗ ΔΙΚΤΥΟΥ"), //6
    EDITYPIRESIADIATHESIS("ΠΡΟΣΩΡΙΝΗ ΔΙΑΘΕΣΗ"), //5
    EDITIMEI("ΑΛΛΑΓΗ IMEI"), //11
    EDITIMEROMINAKATARGISIS("ΚΑΤΑΡΓΗΣΗ"), //4
    EDITARITHMOS("ΑΝΤΙΚΑΤΑΣΤΑΣΗ ΚΑΡΤΑΣ SIM"); //3

    private final String description;

    Action(String description) {
      this.description = description;
    }

    public String getDescription() {
      return this.description;
    }
  }

}
