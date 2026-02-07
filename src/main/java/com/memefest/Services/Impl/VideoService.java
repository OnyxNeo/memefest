package com.memefest.Services.Impl;

import com.memefest.DataAccess.Image;
import com.memefest.DataAccess.PostImage;
import com.memefest.DataAccess.PostVideo;
import com.memefest.DataAccess.Video;
import com.memefest.DataAccess.JSON.ImageJSON;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.DataAccess.JSON.VideoJSON;
import com.memefest.Services.DataSourceOps;
import com.memefest.Services.PostOperations;
import com.memefest.Services.VideoOperations;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.PostActivate;
import jakarta.ejb.PrePassivate;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

@Stateful(name = "VideoService")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class VideoService implements VideoOperations{

    
    private EntityManager entityManager; 
    
    @EJB
    private DataSourceOps datasourceOps;

    @EJB
    private PostOperations postOps;

    @PostActivate
    @PostConstruct
    public void init(){
        this.entityManager = datasourceOps.getEntityManagerFactory().createEntityManager();
    }

    @PreDestroy
    @PrePassivate
    public void destroy(){
        //factory.close();
        entityManager.close();
    }  

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public VideoJSON createVideo(VideoJSON video){
        Video videoEntity = new Video();
        videoEntity.setVid_Path(video.getVidPath());
        entityManager.persist(videoEntity);
        entityManager.flush();
        video.setVidId(videoEntity.getVid_Id());
        return video;    
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public VideoJSON editVideo(VideoJSON video){
        try{
            Video videoEntity = getVideoEntity(video);
            if(video.getVidPath() != null && !video.getVidPath().equalsIgnoreCase(videoEntity.getVid_Path()))
                videoEntity.setVid_Path(video.getVidPath());
            if(video.getVidId() != null ){
                videoEntity.setVid_Id(video.getVidId());
            }
            entityManager.merge(videoEntity);
            removeVideo(video);
            return video;
        }
        catch(NoResultException ex){
            return createVideo(video);
        }
    }

    public void removeVideo(VideoJSON video){
        if(!video.isCanceled())
            return;
        try{
            Video videoEntity = getVideoEntity(video);
            entityManager.remove(videoEntity);
        }
        catch(NoResultException ex){
            return;
        }
    }

    public Video getVideoEntity(VideoJSON video) throws NoResultException{
        if (video.getTitle() == null && video.getVidId() == null)
            throw new NoResultException("Video not found"); 
        Video videoEntity = null;
        try{
            if(video.getVidId() != null)
             videoEntity = entityManager.find(Video.class, video.getVidId());
        }
        catch(NoResultException ex){
            Query query = entityManager.createNamedQuery("Video.getVideoByTitle", Video.class);
            query.setParameter("title", video.getTitle());
            videoEntity = (Video) query.getSingleResult();
            return videoEntity;
        }
        return videoEntity;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public VideoJSON getVideoInfo(VideoJSON video){
        try{
            Video videoEntity = getVideoEntity(video);
            VideoJSON videoJSON = new VideoJSON(videoEntity.getVid_Id(), videoEntity.getVid_Path(), null);
            return videoJSON;
        }
        catch(NoResultException ex){
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public PostJSON createPostImage(PostJSON post, VideoJSON video){
        try{
            post = postOps.getPostInfo(post);
        }
        catch(EJBException ex){
           post = postOps.editPost(post);
        }
        Video vidEntity = null;
        try{
            vidEntity = getVideoEntity(video);
        }
        catch(NoResultException ex){
            vidEntity = getVideoEntity(createVideo(video));
            video.setVidPath(vidEntity.getVid_Path());
        }
        PostVideo postVid = new PostVideo();
        postVid.setVid_Id(vidEntity.getVid_Id());
        postVid.setPost_Id(post.getPostId());
        this.entityManager.persist(postVid);
        return postOps.getPostInfo(post);
    }    
}
