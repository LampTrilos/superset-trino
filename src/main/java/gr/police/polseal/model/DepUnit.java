package gr.police.polseal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
/**
 *
 * @author pgouvas
 */
// Γενική υπηρεσία
@Getter
@Setter
//@Entity
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "depunittype",
        discriminatorType = DiscriminatorType.STRING)
@Table(name = "depunit")
public class DepUnit extends AuditableEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(
            nullable = false,
            unique = true,
            length = 23
    )
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "amu")
    private String amu;

    @Override
    public String toString() {
        return "\nGenDepUnit{" + "id=" + id + ", code=" + code + ", name=" + name + '}';
    }

}
