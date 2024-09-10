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
public class VathmosDtoClient {

  private String energos;
  private String groupVathmos;
  private Long id;
  private String ierarchia;
  private String sntm;
  private String vathmos;

}
