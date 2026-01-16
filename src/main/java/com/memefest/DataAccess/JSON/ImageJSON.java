package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonRootName("image")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "ImageId")
public class ImageJSON {
    
    @JsonProperty("imageId")
    private Long imgId;

    @JsonProperty("imageUrl")
    private String imgPath;

    @JsonProperty("imageTitle")
    private String imgTitle;

    @JsonProperty("isCanceled")
    private boolean isCanceled;

    @JsonCreator
    public ImageJSON(@JsonProperty("imageId") Long imgId, 
                        @JsonProperty("imagePath") String imgPath,
                            @JsonProperty("imageTitle") String imgTitle) {
        this.imgId = imgId;
        this.imgPath = imgPath;
        this.imgTitle = imgTitle;
        this.isCanceled = false;
    }

    public Long getImgId() {
        return imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgTitle() {
        return imgTitle;
    }

    public void setImgTitle(String imgTitle) {
        this.imgTitle = imgTitle;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        this.isCanceled = canceled;
    }
    
}
