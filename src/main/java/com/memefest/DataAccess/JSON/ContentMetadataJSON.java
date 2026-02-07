package com.memefest.DataAccess.JSON;

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

@JsonRootName("ContentMetadata")
public class ContentMetadataJSON {

    private MediaType mediaType;

    @JsonProperty("contentId")
    private String contentId;

    @JsonProperty("uploadTime")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime uploadTime;

    @JsonProperty("fileName")
    private String fileName;

    private String s3Key;

    @JsonProperty("etag")
    private String etag;

    @JsonProperty("user")
    private UserJSON user;

    @JsonProperty("customMetadata")
    private Map<String, String> customMetadata = new HashMap<>();
        
    // Constructors, getters, and setters
    public ContentMetadataJSON() {}
        
    @JsonCreator
    public ContentMetadataJSON(@JsonProperty("contentId")String contentId, 
                                @JsonProperty("fileName") String path, 
                                    MediaType mediaType, 
                                        @JsonProperty("uploadTime") LocalDateTime time,
                                            @JsonProperty("user") UserJSON user) {
        this.contentId = contentId;
        this.mediaType = mediaType;
        if(time != null)
            this.uploadTime = time;
        else
            this.uploadTime = LocalDateTime.now();
        this.s3Key = generateS3Key(mediaType, contentId, path, user);
        this.mediaType = mediaType;
        this.user = user;
    }
        
    private String generateS3Key(MediaType mediaType, String contentId, String fileName, UserJSON user ) {
        String extension = getFileExtension(fileName);
        if(mediaType == MediaType.IMAGE){
        return String.format("%s/Image/%s/%s.%s", user.getUserId(), contentId, fileName,extension);    
        }
        else if(mediaType == MediaType.VIDEO){
            return  String.format("%s/Video/%s/%s.%s", user.getUserId(), contentId, fileName,extension);    
        }
        else 
            throw new IllegalArgumentException();
    }
        
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot) : "";
    }
        
        // Getters and setters
    public String getContentId() { return contentId; }
    public void setContentId(String contentId) { this.contentId = contentId; }
        
    public String getFileName() { return this.fileName; }
    public void setFileName(String originalFileName) { this.fileName = originalFileName; }
        
    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
        
    public LocalDateTime getUploadTime() { return uploadTime; }
    public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }
        
    public String getS3Key() { return s3Key; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }
        
    public String getEtag() { return etag; }
    public void setEtag(String etag) { this.etag = etag; }
        
    public Map<String, String> getCustomMetadata() { return customMetadata; }
    public void setCustomMetadata(Map<String, String> customMetadata) { this.customMetadata = customMetadata; }        

}
