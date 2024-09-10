package gr.police.polseal.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.*;
import org.hibernate.annotations.Cascade;

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
@Table(name = "SignatureMetadata")
public class SignatureMetadata extends PanacheEntity {
    //public SignatureMetadata(String index, String sealX, String sealY) {
    public SignatureMetadata(int index, String position) {
        this.index = Integer.valueOf(index);
        this.position = position;
//        this.sealX = Integer.valueOf(sealX);
//        this.sealY = Integer.valueOf(sealY);
    }

    //Signature's index. 0 index is reversed for QR
    @Column (name = "index")
    private Integer index;

    @JoinColumn (name = "SealingTemplateId")
    @ManyToOne(fetch=FetchType.LAZY)
    private SealingTemplate sealingTemplate;

    //The position on the document
    //It is comprised of 2 digits in { 0,1,2 }
    //X0 = left, X1 = mid, X2 = right
    //Y0 = bottom, Y1 = mid, Y2 = top
    @Column (name = "Position")
    private String position;
//    x , y of the seal
//    @Column (name = "sealX")
//    private Integer sealX;
//
//    @Column (name = "sealY")
//    private Integer sealY;

}
