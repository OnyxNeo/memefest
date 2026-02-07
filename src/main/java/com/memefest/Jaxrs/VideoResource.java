package com.memefest.Jaxrs;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.glassfish.jersey.internal.util.collection.ByteBufferInputStream;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.memefest.DataAccess.JSON.ContentMetadataJSON;
import com.memefest.DataAccess.JSON.ImageJSON;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.PostWithReplyJSON;
import com.memefest.DataAccess.JSON.UserJSON;
import com.memefest.DataAccess.JSON.VideoJSON;
import com.memefest.Services.ImageOperations;
import com.memefest.Services.PostOperations;
import com.memefest.Services.S3AccessOperations;
import com.memefest.Services.UserOperations;
import com.memefest.Services.VideoOperations;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@PermitAll
@Path("Video")
@RequestScoped
public class VideoResource extends Resource{
    
    ExecutorService pool;

    @Inject
    private S3AccessOperations s3Ops;

    @Inject
    private VideoOperations vidOps;

    @Inject 
    private UserOperations userOps;

    @Inject
    private  PostOperations postOps;

    private final Set<String> SUPPORTED_VIDEO_EXTENSIONS = Set.of(
            ".mp4", ".avi", ".mov", ".wmv", ".flv", ".webm", ".mkv", ".m4v", ".gif");

    private final Set<String> SUPPORTED_IMAGE_EXTENSION = Set.of(
            ".jpeg", ".png", ".svg"
    );

    @Inject
    private ImageOperations imageOps;

    @OPTIONS
    public Response videoOptions(){
        return Response.ok().build();
    }

    /* 

    @GET
    @Path("{VidId}")
    public InputStream getVideoContent(@PathParam("VidId") Long vidId, @Context SecurityContext context){
        UserJSON user = new UserJSON(context.getUserPrincipal().getName());
        user = userOps.getUserInfo(user);
        String vidPath = vidId.toString() + user.getUserId(); 
        VideoJSON video = new VideoJSON(vidId, vidPath , null);

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
    @Path("{Category}/{VidId}")
    public Response uploadVideo(InputStream content,@PathParam("Category") String vidCategory, 
                                    @PathParam("VidId") Long vidId, @Context SecurityContext context){
        UserJSON user = new UserJSON(context.getUserPrincipal().getName());
        StringBuilder sb = new StringBuilder();
        user = userOps.getUserInfo(user);
        Long userId = user.getUserId();
        String vidPath = userId.toString() + vidCategory;
        sb.append(user.getUserId());
        sb.append(vidCategory);
        sb.append(vidCategory);
        VideoJSON video = new VideoJSON(vidId, vidPath, null);
        s3Ops.uploadVideo(vidId.toString(), content, vidPath, 0, null);
        //vidOps.createVideo();
        
        return Response.ok().build(); 
    }

    */

    @OPTIONS
    @Path("{PostId}")
    public Response videoReplyOptions(){
        return Response.ok().build();
    }


    @POST
    @Path("{PostId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void uploadReplyContent(FormDataMultiPart multipartData, @Context SecurityContext context
                                    ,@PathParam("PostId") int postId, @Suspended AsyncResponse response) 
                                        throws IOException{
        FormDataBodyPart postPart = multipartData.getField("post");
        InputStream postContent = postPart.getContent();

        UserJSON user = userOps.getUserInfo(new UserJSON(context.getUserPrincipal().getName()));
        PostWithReplyJSON oPost = new PostWithReplyJSON(Long.valueOf(postId), null, null, 0, 0, user, null, null, null, null);
        PostJSON postJSON = handlePostContent(postContent,  oPost, user);
        oPost.setPosts(Collections.singleton(postJSON));
        oPost = postOps.editPostWithReply(oPost);
        postJSON = oPost.getPosts().iterator().next();

        List<FormDataBodyPart> fileParts = multipartData.getFields("files");
        for(FormDataBodyPart filePart : fileParts) {
            String filename = filePart.getFileName().get();
            String extension = (filename.substring(filename.lastIndexOf("."))).toLowerCase();
            if(SUPPORTED_VIDEO_EXTENSIONS.contains(extension) || SUPPORTED_IMAGE_EXTENSION.contains(extension)){
                ByteBuffer buffer = ByteBuffer.wrap(filePart.getContent().readAllBytes());
                if(SUPPORTED_IMAGE_EXTENSION.contains(extension)){
                    
                    pool = Executors.newCachedThreadPool();
                    pool.submit(() -> {
                        ImageJSON image = new ImageJSON(null, null, null);
                        image = imageOps.editImage(image);
                        CompletableFuture<ContentMetadataJSON> result =  s3Ops.uploadContent(image.getImgId(), buffer, filename, com.memefest.DataAccess.JSON.MediaType.IMAGE, null, user);
                        try{
                            while(!result.isDone()){
                                Thread.sleep(10);
                                ContentMetadataJSON metadata = result.get();
                                image.setImgPath(metadata.getS3Key());
                                imageOps.editImage(image);
                        }
                    }
                    catch(InterruptedException  | ExecutionException ex){
                            ex.printStackTrace();
                    }
                    });
                }
                else if(SUPPORTED_VIDEO_EXTENSIONS.contains(extension)){
                    pool = Executors.newCachedThreadPool();
                    pool.submit(() -> {
                        VideoJSON video = new VideoJSON(null, null, null);
                        video = vidOps.editVideo(video);
                        CompletableFuture<ContentMetadataJSON> result = s3Ops.uploadContent(video.getVidId(), buffer, filename, com.memefest.DataAccess.JSON.MediaType.VIDEO, null, user);
                        try{
                            while(!result.isDone()){
                                Thread.sleep(10);
                            }
                                ContentMetadataJSON metadata = result.get();
                                video.setVidPath(metadata.getS3Key());
                                vidOps.editVideo(video);
                        }
                        catch(InterruptedException | ExecutionException ex){
                        ex.printStackTrace();
                        }
                    });
                }   
            }   
        }
        
        postJSON = postOps.getPostInfo(postJSON);
        oPost.setPosts(Collections.singleton(postJSON));
        response.resume(mapper.writeValueAsString(postOps.getPostInfo(postJSON)));
    }

    private PostJSON handlePostContent(InputStream postContent, PostJSON replyTo, UserJSON byUser) throws IOException{
        BufferedInputStream stream = new BufferedInputStream(postContent);
        ByteBuffer buff = ByteBuffer.wrap(stream.readAllBytes());
        String post = buff.toString();
        PostJSON postEntity = new PostJSON(null, post, null, 0, 0, byUser, null, null, null);
        return postEntity;
    }
}
