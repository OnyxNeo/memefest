package com.memefest.Services;

import com.memefest.DataAccess.Image;
import com.memefest.DataAccess.JSON.ImageJSON;
import com.memefest.DataAccess.JSON.PostJSON;

public interface ImageOperations {
    
    public ImageJSON createImage(ImageJSON image);

    public ImageJSON editImage(ImageJSON o);

    public void removeImage(ImageJSON video);

    public Image getImageEntity(ImageJSON video);

    public ImageJSON getImageInfo(ImageJSON video);

    public PostJSON createPostImage(PostJSON post, ImageJSON image);
}
