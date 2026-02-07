package com.memefest.DataAccess.JSON;

import java.io.InputStream;

public abstract class MediaJSON {
     
    private InputStream data;
    
    public void setImageData(InputStream data){
        this.data = data;
    }

    public InputStream getImageData(){
        return this.data;
    }

}
