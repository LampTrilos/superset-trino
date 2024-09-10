package gr.police.polseal.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import javax.persistence.*;
import javax.validation.Constraint;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "SealingApplication", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"AppCode"})
})
public class SealingApplication extends PanacheEntity {


    @NotNull
    @Column(name = "AppCode" , unique = true)
    private String appCode;

    @NotNull
    @Column(name = "Description")
    private String description;

    @OneToMany(mappedBy = "sealingApplication",  cascade = CascadeType.ALL, orphanRemoval = true)
    public List<SealingTemplate> templates = new ArrayList<>();



    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "SealingApplicationChannel",
            joinColumns = {@JoinColumn(name = "SealingApplicationId", referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "ChallengeChanellId", referencedColumnName = "Id")},
            uniqueConstraints = {@UniqueConstraint(columnNames = {"SealingApplicationId", "ChallengeChanellId"})})
    public List<ChallengeChannel> challengeChannels = new ArrayList<>();

    @NotNull
    //If the App is active or inactive and cannot seal docs
    @Column(name = "active")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean active;

    @Column(name = "StartDate")
    private LocalDateTime startDate;

    @Column(name = "EndDate")
    private LocalDateTime endDate;
}
