package gr.police.polseal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@MappedSuperclass
public class AuditableEntity {

  @CreationTimestamp
  private LocalDateTime createdTimestamp;

  @UpdateTimestamp
  private LocalDateTime updatedTimestamp;
}
