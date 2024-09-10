package gr.police.polseal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ouhist")
public class OuHist extends AuditableEntity  {

  @Id
  private Long id;
  private String titlosPliris;
  private LocalDateTime enarxi;
  private Long id_ad;
  private Long id_diakrisi_organotika;
  private Long id_energis;
  private Long id_gad;
  private Long id_sfragidas;
  private Long id_ypiresia;
  private LocalDateTime lixi;
  private String taxinomisi;
  private String titlos;
  private String mitroo;

}
