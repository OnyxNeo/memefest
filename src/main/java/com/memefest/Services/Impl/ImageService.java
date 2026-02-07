package com.memefest.Services.Impl;

import com.memefest.DataAccess.Image;
import com.memefest.DataAccess.PostImage;
import com.memefest.DataAccess.PostImageId;
import com.memefest.DataAccess.JSON.ImageJSON;
import com.memefest.DataAccess.JSON.PostJSON;
import com.memefest.Services.DataSourceOps;
import com.memefest.Services.ImageOperations;
import com.memefest.Services.PostOperations;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.PrePassivate;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

@TransactionManagement(TransactionManagementType.CONTAINER)
@Stateful(name =  "ImageService")
public class ImageService implements ImageOperations{
    
    
    //@EJB
    //private S3AccessOperations s3Ops;

    @EJB
    private DataSourceOps datasourceOps;

    @EJB
    private PostOperations postOps;

    //@TransactionScoped
    private EntityManager entityManager;

    @PrePassivate
    @PostConstruct
    public void init(){
        this.entityManager = datasourceOps.getEntityManagerFactory().createEntityManager();
    }

    @PreDestroy
    @PrePassivate
    public void destroy(){
        entityManager.close();
    }  

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    //throw a custom exception to show object was not created
    public ImageJSON createImage(ImageJSON image){  
        Image imageEntity = new Image();
        imageEntity.setImg_Path(image.getImgPath());
        imageEntity.setImg_Title(image.getImgTitle());
        entityManager.persist(imageEntity);
        entityManager.flush();
        image.setImgId(imageEntity.getImg_Id());
        return image;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    //throw a custom exception to show object was not created
    public ImageJSON editImage(ImageJSON image){
        try{
            Image imageEntity = getImageEntity(image);
            if(image.getImgPath() != null && !image.getImgPath().equalsIgnoreCase(imageEntity.getImg_Path()))
                imageEntity.setImg_Path(image.getImgPath());
            if(image.getImgId() != null){
                imageEntity.setImg_Id(image.getImgId());
            }
            if(imageEntity.getImg_Title() != null && !image.getImgTitle().equalsIgnoreCase(imageEntity.getImg_Title()))
                imageEntity.setImg_Title(image.getImgTitle());
            entityManager.merge(imageEntity);
            removeImage(image);
            return image;
        }
        catch(NoResultException ex){
            return createImage(image);
        }
    }

    public void removeImage(ImageJSON image){
        if(!image.isCanceled())
            return;
        try{
            Image imageEntity = getImageEntity(image);
            entityManager.remove(imageEntity);
        }
        catch(NoResultException ex){
            return;
        }
    } 

    public Image getImageEntity(ImageJSON image) throws NoResultException{
        if (image.getImgTitle() == null && image.getImgId() == null)
            throw new NoResultException("Image not found"); 
        Image imageEntity = null;
        try{
            if(image.getImgId() != null )
            imageEntity = entityManager.find(Image.class, image.getImgId());
        }
        catch(NoResultException ex){
            Query query = entityManager.createNamedQuery("Image.getImgeByTitle", Image.class);
            query.setParameter("title", "%" + image.getImgTitle() + "%");
            imageEntity = (Image) query.getSingleResult();
            return imageEntity;
        }
        throw new NoResultException("Image not found");
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public ImageJSON getImageInfo(ImageJSON image){
        try{
            Image imageEntity = getImageEntity(image);
            ImageJSON imageJSON = new ImageJSON(imageEntity.getImg_Id(), 
                                    imageEntity.getImg_Path(), imageEntity.getImg_Title());
            return imageJSON;
        }
        catch(NoResultException ex){
            return null;
        }        
    } 

    public PostJSON createPostImage(PostJSON post, ImageJSON image){
        try{
            post = postOps.getPostInfo(post);
        }
        catch(EJBException ex){
           post = postOps.editPost(post);
        }
        Image imageEntity = null;
        try{
            imageEntity = getImageEntity(image);
        }
        catch(NoResultException ex){
            imageEntity = getImageEntity(createImage(image));
            image.setImgPath(imageEntity.getImg_Path());
        }
        PostImage postImage = new PostImage();
        postImage.setImg_Id(imageEntity.getImg_Id());
        postImage.setPost_Id(post.getPostId());
        this.entityManager.persist(postImage);
        return postOps.getPostInfo(post);
    }
}
