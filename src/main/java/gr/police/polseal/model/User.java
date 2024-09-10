package gr.police.polseal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "userp")
public class User {

  @Id
  private Long id;
  @Column(
      unique = true,
      nullable = false
  )
  private String username;
  @Column
  private String firstName;
  @Column
  private String lastName;
  @Column(
      unique = true,
      nullable = false
  )
  private String email;
  @Transient
  private List<String> roles = new ArrayList();
  @Transient
  private String clientId;
  @Transient
  private String realm;

  private String phone;

  private Long apolitiIerarchia;
  // IV-USER
  //@Column(nullable = false, unique = true)
  private String arithmosMitroou;
  private Long idApospasis;
  private Long idOrganikis;
  private Long idParousias;
  private Long idVathmou;
  private LocalDate imerominiaGennisis;
  private String katigoriaProsopikoy;
  //@Column(nullable = false, unique = true)
  // auto einai to mitroo mono o aritmos, den to xrhsimopoioume
  private Long mitroo;
  private String patronymo;
}
