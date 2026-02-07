package com.memefest.DataAccess.JSON;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "VideoID")
@JsonRootName("Clip")
public class VideoJSON extends MediaJSON{
    
    @JsonProperty("clipID")
    private Long vidId;

    @JsonProperty("clipPath")
    private String vidPath;

    @JsonProperty("clipTitle") 
    private String title;

    @JsonProperty("canceled")
    private boolean isCanceled;

    @JsonProperty("thumbNail")
    private ImageJSON thumbNail;

    @JsonCreator
    public VideoJSON(@JsonProperty("clipID") Long vidId,
                        @JsonProperty("clipPath") String vidPath, 
                            @JsonProperty("clipTitle") String title) {
        this.vidId = vidId;
        this.vidPath = vidPath;
        this.title = title;
        this.isCanceled = false;
    }

    public Long getVidId() {
        return vidId;
    }

    public void setVidId(Long vidId) {
        this.vidId = vidId;
    }

    public String getVidPath() {
        return vidPath;
    }
    
    public void setVidPath(String vidPath) {
        this.vidPath = vidPath;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        this.isCanceled = canceled;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setThumbnail(ImageJSON image){
        this.thumbNail = image;
    }

    public ImageJSON getThumbnail(){
        return this.thumbNail;
    }
}
