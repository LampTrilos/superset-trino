package gr.police.polseal.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification")
public class Notification extends AuditableEntity {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @NotNull
  private boolean read = false;

  @Column(length = 1000)
  private String message;

  private String type;

  @Column(length = 1000)
  @Convert(converter = HashMapConverter.class)
  private Map<String, Object> attributes;

}
