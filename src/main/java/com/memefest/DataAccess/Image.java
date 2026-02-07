package com.memefest.DataAccess;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FetchType;
import jakarta.persistence.FieldResult;

@NamedNativeQueries({
    @NamedNativeQuery(name = "Image.getImageByTitle",
    query = "SELECT TOP(1) FROM IMAGES I " 
                + "WHERE I.Img_Title LIKE CONCAT('%', :title, '%')", resultSetMapping = "ImageEntityMapping"
    )
})
@SqlResultSetMappings(
    @SqlResultSetMapping(
        name = "ImageEntityMapping",
        entities = {
            @EntityResult(
                entityClass = Image.class,
                fields = {
                    @FieldResult(name = "imageId", column = "Img_Id"),
                    @FieldResult(name = "imageTitle", column = "Img_Title"),
                    @FieldResult(name = "imagePath", column = "Img_Path")
                }
            )
        }
    )   
) 
@Entity(name = "ImageEntity")
@Table(name = "IMAGES")
public class Image {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Img_Id")
    //@UuidGenerator
    private Long imageId;
    
    @Column(name = "Img_Path")
    private String imagePath;

    @Column(name = "Img_Title")
    private  String imageTitle;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    //@JoinColumn(name = "UserId")
    private User user;

    @OneToMany(fetch =FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "image")
    @JoinColumn(referencedColumnName = "Img_Id")
    private Set<TopicImage> topic;

    @OneToMany(fetch =FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "image")
    private Set<PostImage> post;
    
    @OneToMany(fetch =FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "image")
    //@JoinColumn(name = "Img_Id")
    private Set<EventImage> event;


    public Long getImg_Id() {
        return this.imageId;
    }

    public void setImg_Id(Long imgId){
        this.imageId = imgId;
    }

    public String getImg_Path() {
        return this.imagePath;
    }

    public void setImg_Path(String imgPath){
        this.imagePath = imgPath;
    }

    public void setImg_Title(String title){
        this.imageTitle = title;
    }

    public String getImg_Title() {
        return this.imageTitle;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }   
}
