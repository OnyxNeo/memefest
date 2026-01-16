package com.memefest.DataAccess.JSON;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.memefest.DataAccess.JSON.Deserialize.CustomLocalDateTimeDeserializer;
import com.memefest.DataAccess.JSON.Serialize.CustomLocalDateTimeSerializer;

@JsonRootName("event")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "eventId")
@JsonFilter("EventView")
public class EventJSON implements Serializable{
    
    @JsonProperty("eventId")
    private Long eventID;

    @JsonProperty("title")
    private String eventTitle;

    @JsonProperty("description")
    private String eventDescription; 

    @JsonProperty("eventPin")
    private String eventPin;

    @JsonProperty("venue")
    private String eventVenue;

    @JsonProperty("price")
    private int eventPrice;

    @JsonProperty("timestamp")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime eventDate;

    @JsonProperty("imageUrls")
    private Set<ImageJSON> posters;

    @JsonProperty("clips")
    private Set<VideoJSON> clips;

    @JsonProperty("posts")
    private Set<EventPostJSON> posts;

    @JsonProperty("postedBy")
    private UserJSON user;

    @JsonProperty("createdAt")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime datePosted;

    @JsonProperty("categories")
    private Set<CategoryJSON> categories;

    @JsonProperty("canceledCategories")
    private Set<CategoryJSON> canceledCats;

    @JsonProperty("canceledImages")
    private Set<ImageJSON> canceledImages;

    @JsonProperty("canceledClips")
    private Set<VideoJSON> canceledClips;

    @JsonProperty("isCanceled")
    private boolean isCanceled;
    @JsonCreator
    public EventJSON(@JsonProperty("eventId") Long eventID,
                        @JsonProperty("title") String eventTitle, 
                            @JsonProperty("description") String eventDescription,
                                @JsonProperty("eventPin") String eventPin, 
                                    @JsonProperty("timestamp") LocalDateTime eventDate,
                                        @JsonProperty("createdAt") LocalDateTime datePosted,
                                            @JsonProperty("clips") Set<VideoJSON> clips,
                                                @JsonProperty("imageUrls") Set<ImageJSON> posters,
                                                    @JsonProperty("posts") Set<EventPostJSON> posts,
                                                        @JsonProperty("canceledImages") Set<ImageJSON> canceledImages,
                                                            @JsonProperty("canceledClips") Set<VideoJSON> canceledClips,                                                   
                                                            @JsonProperty("venue") String eventVenue,
                                                                @JsonProperty("postedBy") UserJSON user,
                                                                @JsonProperty("categories") Set<CategoryJSON> categories,
                                                                @JsonProperty("canceledCategories") Set<CategoryJSON> canceledCategories,
                                                                @JsonProperty("price") int price){
        this.eventID = eventID;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventPin = eventPin;
        this.eventDate = eventDate;
        this.datePosted = datePosted;
        this.clips = clips;
        this.posters = posters;
        this.posts = posts;
        this.user = user;
        this.eventVenue = eventVenue;
        this.canceledImages = canceledImages;
        this.canceledClips = canceledClips;
        this.isCanceled = false;
        this.categories = categories;
        this.canceledCats = canceledCategories;
    }

    public Long getEventID() {
        return this.eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }


    public String getEventTitle() {
        return this.eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return this.eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setPostedBy(UserJSON user){
        this.user = user;
    }

    public UserJSON getPostedBy(){
        return this.user;
    }

    @JsonProperty("Posts")
    public Set<EventPostJSON> getPosts(){
        return this.posts;
    }

    @JsonProperty("Posts")
    public void setPosts(Set<EventPostJSON> posts){
        this.posts = posts;
    }

    public Set<VideoJSON> getClips(){
        return this.clips;
    }

    public void setClips(Set<VideoJSON> clips){
        this.clips = clips;
    }

    public Set<ImageJSON> getImageUrls(){
        return this.posters;
    }

    public void setImageUrls(Set<ImageJSON> posters){
        this.posters = posters;
    }

    public String getEventPin() {
        return this.eventPin;
    }

    public void setEventPin(String eventPin) {
        this.eventPin = eventPin;
    }

    public LocalDateTime getEventDate() {
        return this.eventDate;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }
    
    public LocalDateTime getDatePosted() {
        return this.datePosted;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventVenue() {
        return this.eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public Set<ImageJSON> getCanceledImages() {
        return canceledImages;
    }

    public void setCanceledImages(Set<ImageJSON> canceledImages) {
        this.canceledImages = canceledImages;
    }

    public Set<VideoJSON> getCanceledClips() {
        return canceledClips;
    }
    
    public void setCanceledClips(Set<VideoJSON> canceledClips) {
        this.canceledClips = canceledClips;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        this.isCanceled = canceled;
    }

    public Set<CategoryJSON> getCanceledCategories(){
        return this.canceledCats;
    }

    public void setCanceledCategories(Set<CategoryJSON> canceledCategories){
        this.canceledCats = canceledCategories;
    }

    public void setCategories(Set<CategoryJSON> categories){
        this.categories = categories;
    }

    public Set<CategoryJSON> getCategories(){
        return this.categories;
    }
    
}
