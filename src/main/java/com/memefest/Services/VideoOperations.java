package com.memefest.Services;

import com.memefest.DataAccess.Video;
import com.memefest.DataAccess.JSON.VideoJSON;

public interface VideoOperations {
    
    public VideoJSON createVideo(VideoJSON video);

    public VideoJSON editVideo(VideoJSON video);

    public void removeVideo(VideoJSON video);

    public Video getVideoEntity(VideoJSON video);

    public VideoJSON getVideoInfo(VideoJSON video);
}
