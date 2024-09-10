package gr.police.polseal.restclients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@Getter
@Setter
@Builder
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class OuDtoClient {

  private String enarxi;
  private long id;
  private long id_ad;
  private long id_diakrisi_organotika;
  private long id_energis;
  private long id_gad;
  private long id_sfragidas;
  private long id_ypiresia;
  private String lixi;
  private String taxinomisi;
  private String titlos;
  private String titlosPliris;
  private String mitroo;

}
