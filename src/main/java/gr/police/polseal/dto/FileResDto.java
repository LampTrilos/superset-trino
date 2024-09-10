package gr.police.polseal.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@Getter
@Setter
@Builder
@RegisterForReflection
@AllArgsConstructor
@NoArgsConstructor
public class FileResDto {

  private String fileName;

  private byte[] bytes;

  private String contentType;
}
