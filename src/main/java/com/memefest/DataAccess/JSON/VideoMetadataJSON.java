package com.memefest.DataAccess.JSON;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.memefest.DataAccess.JSON.Deserialize.CustomLocalDateTimeDeserializer;
import com.memefest.DataAccess.JSON.Serialize.CustomLocalDateTimeSerializer;

@JsonRootName("VideoMetadata")
public class VideoMetadataJSON {

    @JsonProperty("VideoId")
    private String videoId;
    
    @JsonProperty("Name")
    private String originalFileName;
    
    @JsonProperty("ContentType")
    private String contentType;

    @JsonProperty("Size")
    private long fileSize;

    @JsonProperty("UploadTime")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime uploadTime;
    
    private String s3Key;
    
    @JsonProperty("Etag")
    private String etag;

    @JsonProperty("CustomMetadata")
    private Map<String, String> customMetadata = new HashMap<>();
        
    // Constructors, getters, and setters
    public VideoMetadataJSON() {}
        
    @JsonCreator
    public VideoMetadataJSON(@JsonProperty("VideoId")String videoId, 
                                @JsonProperty("Name") String originalFileName, 
                                @JsonProperty("ContentType") String contentType, 
                                    @JsonProperty("Size") long fileSize) {
        this.videoId = videoId;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uploadTime = LocalDateTime.now();
        this.s3Key = generateS3Key(videoId, originalFileName);
    }
        
    private String generateS3Key(String videoId, String fileName) {
        String extension = getFileExtension(fileName);
        return String.format("videos/%s/%s%s", 
            Instant.now().toString().substring(0, 10), // YYYY-MM-DD
            videoId, 
            extension);
    }
        
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot) : "";
    }
        
        // Getters and setters
    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }
        
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
        
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
        
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        
    public LocalDateTime getUploadTime() { return uploadTime; }
    public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }
        
    public String getS3Key() { return s3Key; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }
        
    public String getEtag() { return etag; }
    public void setEtag(String etag) { this.etag = etag; }
        
    public Map<String, String> getCustomMetadata() { return customMetadata; }
    public void setCustomMetadata(Map<String, String> customMetadata) { this.customMetadata = customMetadata; }        

}
