package gr.police.polseal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class SealingApplicationDto {

  private String id;
  private String appCode;
  private String description;
  private List<SealingTemplateDto> templates = new ArrayList<>();
  private List<ChallengeChannelDto> challengeChannels = new ArrayList<>();
  private boolean active;
  private String startDate;
  private String endDate;

}
