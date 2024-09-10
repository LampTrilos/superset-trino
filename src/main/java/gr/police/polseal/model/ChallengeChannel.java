package gr.police.polseal.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ChallengeChannel")
public class ChallengeChannel extends PanacheEntity {

    @Column (name = "Description")
    private String description;

    @ManyToMany (mappedBy = "challengeChannels")
    private List<SealingApplication> sealingApplications;
}
