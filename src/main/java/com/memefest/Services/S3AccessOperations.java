package com.memefest.Services;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.memefest.DataAccess.JSON.VideoMetadataJSON;

public interface S3AccessOperations {
    
    public CompletableFuture<VideoMetadataJSON> uploadVideo(String videoId, InputStream videoStream, 
                                                                String originalFileName, long contentLength,
                                                                    Map<String, String> customMetadata);

    public URL generateReadOnlyUrl(String s3Key, Duration expiration);

    public VideoMetadataJSON getVideoMetadata(String s3Key);

    public List<VideoMetadataJSON> listVideos(String prefix, int maxKeys);
    
    public void deleteVideo(String s3Key);
    
    public InputStream getContent(String s3Key);
}
