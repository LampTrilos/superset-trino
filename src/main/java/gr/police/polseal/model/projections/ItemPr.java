package gr.police.polseal.model.projections;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RegisterForReflection
public class ItemPr {

  private final Long id;

  private final String serialNumber;

}
