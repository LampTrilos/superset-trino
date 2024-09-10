package gr.police.polseal.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vathmos")
public class Vathmos extends PanacheEntityBase {

  @Id
  private Long id;

  private String energos;
  private String groupVathmos;
  private String ierarchia;
  private String sntm;
  private String vathmos;

}
