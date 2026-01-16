package com.memefest.Jaxrs;

import java.io.InputStream;

import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.DataAccess.JSON.VideoJSON;
import com.memefest.DataAccess.JSON.VideoMetadataJSON;
import com.memefest.Services.ImageOperations;
import com.memefest.Services.S3AccessOperations;
import com.memefest.Services.UserOperations;
import com.memefest.Services.VideoOperations;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@PermitAll
@Path("Video")
@RequestScoped
public class VideoResource {
    
    @Inject
    private S3AccessOperations s3Ops;

    @Inject
    private VideoOperations vidOps;

    @Inject 
    private UserOperations userOps;

    @OPTIONS
    public Response videoOptions(){
        return Response.ok().build();
    }

    @GET
    @Path("{VidId}")
    public InputStream getVideoContent(@PathParam("VidId") Long vidId){
        VideoJSON video = new VideoJSON(vidId, null, null);
        VideoJSON vidInfo = vidOps.getVideoInfo(video);
        VideoMetadataJSON mataData = s3Ops.getVideoMetadata(vidInfo.getVidPath());
        return s3Ops.getContent(vidInfo.getVidPath());
    }

    @HEAD
    @Path("{VidId}")
    public Response getVideo(@PathParam("VidId") Long vidId){
        VideoJSON video = new VideoJSON(vidId, null, null);
        VideoJSON vidInfo = vidOps.getVideoInfo(video);
        VideoMetadataJSON mataData = s3Ops.getVideoMetadata(vidInfo.getVidPath());
        return Response.ok(vidInfo).header("Content-Type",mataData.getContentType()).build();
    }

    @POST
    @Path("{Category}")
    public Response uploadVideo(InputStream content,@PathParam("Category") String vidCategory, @Context SecurityContext context){
        UserJSON user = new UserJSON(context.getUserPrincipal().getName());
        StringBuilder sb = new StringBuilder();
        user = userOps.getUserInfo(user);
        sb.append(user.getUserId());
        sb.append(vidCategory);
        sb.append(vidCategory);
        //VideoJSON vid = new VideoJSON(null, null, 
        s3Ops.uploadVideo(null, content, null, 0, null);
        
        
        //vidOps.createVideo();
        
        return Response.ok().build(); 
    }
}
