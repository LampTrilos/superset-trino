package gr.police.polseal.model.logs;

import gr.police.polseal.model.AuditableEntity;
import gr.police.polseal.model.User;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "logitem")
public class LogItem extends AuditableEntity {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
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
    EDITSERIALNUMBER("Αλλαγή σειριακού αριθμού");

    private final String description;

    Action(String description) {
      this.description = description;
    }

    public String getDescription() {
      return this.description;
    }
  }

}
