package gr.police.polseal.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SealingTemplate", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"SealingApplicationId", "code"})
})
public class SealingTemplate extends PanacheEntity {

//    @Lob
//    @Column(name = "fileData")
//    //todo na bgei to not null
////    @NotNull
//    private byte[] fileData;

    //The templateCode is auto-generated and used by 3d party apps to send requests for signing
    @NotNull
    @Column (name = "code")
    private String code;

    @NotNull
    @Column (name = "Description")
    private String description;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "SealingApplicationId", nullable = false)
    @NotNull
    private SealingApplication sealingApplication;

//   Max number of seals on a template before it's being closed
    @Column (name = "maxSignatures")
    @NotNull
    private int maxSignatures;

    //    Is it allowed to put seal on every page ?
    @Column (name = "sealOnEveryPage")
    @NotNull
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean sealOnEveryPage;

    //The metadata for each signature, index 0 is the metadata for the QR code
    //(fetch = FetchType.EAGER) Causes too many bags error
    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name = "SealingTemplateId")
    @NotNull
    public List<SignatureMetadata> signaturesMetadata = new ArrayList<>();


    //The message included in the confirmation email/SMS
    @NotNull
    @Column (name = "EmailMessage")
    private String emailMessage;








}
