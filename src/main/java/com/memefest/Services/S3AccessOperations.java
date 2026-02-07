package com.memefest.Services;

import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.memefest.DataAccess.JSON.ContentMetadataJSON;
import com.memefest.DataAccess.JSON.MediaType;
import com.memefest.DataAccess.JSON.UserJSON;

public interface S3AccessOperations {
    
    public CompletableFuture<ContentMetadataJSON> uploadContent(Long contentId, ByteBuffer stream, 
                                                                String originalFileName, MediaType mediaType,
                                                                    Map<String, String> customMetadata, UserJSON user);                                                                
    public URL generateReadOnlyUrl(String s3Key, Duration expiration);

    public ContentMetadataJSON getVideoMetadata(String s3Key);

    public List<ContentMetadataJSON> listVideos(String prefix, int maxKeys);
    
    public void deleteVideo(String s3Key);
    
    public InputStream getContent(String s3Key);
}
