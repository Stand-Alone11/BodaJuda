package com.aeye.thirdeye.dto;

import com.aeye.thirdeye.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {

    private Long id;
    private String image;
    private String title;
    private String imageValidate;
    private String typeA;
    private String typeB;
    private String typeC;
    private String Provider;
    private String faceYN;

    private int L_X;
    private int L_Y;
    private int R_X;
    private int R_Y;

    public ImageDto(Image image){
        this.id = image.getId();
        this.image = image.getImage();
        this.title = image.getTitle();
        this.imageValidate = image.getImageValidate();
        this.typeA = image.getTypeA();
        this.typeB = image.getTypeB();
        this.typeC = image.getTypeC();
        this.Provider = image.getProvider();
        this.faceYN = image.getFaceYN();

        this.L_X = image.getL_X();
        this.L_Y = image.getL_Y();
        this.R_X = image.getR_X();
        this.R_Y = image.getR_Y();
    }



}
