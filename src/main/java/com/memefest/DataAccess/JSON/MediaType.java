package com.memefest.DataAccess.JSON;

public enum MediaType {
    VIDEO, IMAGE, THUMBNAIL;

    public static MediaType forValues(String type){
        for(MediaType mediaType : MediaType.values()){
            if(mediaType.name().equalsIgnoreCase(type))
                return mediaType;
        }
        throw new IllegalArgumentException("Illegal MediaType:" + type);
    } 
}
