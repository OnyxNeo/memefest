package com.memefest.Services.Impl;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services. s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

/* 
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
*/
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.memefest.DataAccess.JSON.VideoMetadataJSON;
import com.memefest.Services.S3AccessOperations;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.PostActivate;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;

import java.util.logging.Level;

/**
 * S3 Video Storage Service that provides secure video storage with encryption
 * and read-only access control for other users
 */
@Stateful(name = "S3AccessService")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class S3AccessService implements S3AccessOperations{
    
    private final Logger LOGGER = Logger.getLogger(S3AccessService.class.getName());
    private final String VIDEO_CONTENT_TYPE_PREFIX = "video/";
    private final String DEFAULT_STORAGE_CLASS = "STANDARD_IA"; // Infrequent Access for cost optimization
    private final Duration DEFAULT_PRESIGN_DURATION = Duration.ofHours(1);
    private final int MAX_CONCURRENT_UPLOADS = 5;
    
    // Supported video formats
    private final Set<String> SUPPORTED_VIDEO_EXTENSIONS = Set.of(
            ".mp4", ".avi", ".mov", ".wmv", ".flv", ".webm", ".mkv", ".m4v"
    );
    
    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private KmsClient kmsClient;
    private IamClient iamClient;
    private String bucketName;
    private String kmsKeyId;
    private String readOnlyRoleArn;
    private ExecutorService executorService;
    private Region region;
    
    /**

    
    /**
     * Video metadata class
     */
    
    @PostConstruct
    @PostActivate
    private void init(){
        try{
            URL propsFile = Thread.currentThread().getContextClassLoader().getResource("s3.properties");
            Properties props = new Properties();
            props.load(new FileInputStream(new File(propsFile.toURI()))); 
            region = Region.of(props.getProperty("region"));
            bucketName = props.getProperty("bucketName");
            kmsKeyId = props.getProperty("kmsKeyId");
            readOnlyRoleArn = props.getProperty("readOnlyRoleArn");
        
            // Initialize AWS clients
            s3Client = S3Client.builder()
                    .region(region)
                    .credentialsProvider(DefaultCredentialsProvider.builder().build())
                    .build();
        
            s3Presigner = S3Presigner.builder()
                    .region(region)
                    .credentialsProvider(DefaultCredentialsProvider.builder().build())
                    .build();
        
            kmsClient = KmsClient.builder()
                    .region(region)
                    .credentialsProvider(DefaultCredentialsProvider.builder().build())
                    .build();
        
            iamClient = IamClient.builder()
                    .region(Region.AWS_GLOBAL) // IAM is global
                    .credentialsProvider(DefaultCredentialsProvider.builder().build())
                    .build();
        
            executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_UPLOADS);
            try {
                if (bucketName!= null) {
                    createBucketIfNotExists();
                }
                if (kmsKeyId != null) {
                    createKmsKeyIfNotExists();
                }
                if (readOnlyRoleArn != null) {
                    createReadOnlyRoleIfNotExists();
                }
                configureBucketPolicies();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize S3VideoStorageService", e);
                throw new RuntimeException("Service initialization failed", e);
            }
        }
        catch(URISyntaxException | IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Upload a video file to S3 with encryption
     */

    /*      
    public CompletableFuture<VideoMetadata> uploadVideo(String videoId, Path videoFilePath, 
                                                       Map<String, String> customMetadata) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateVideoFile(videoFilePath);
                
                String fileName = videoFilePath.getFileName().toString();
                String contentType = determineContentType(fileName);
                long fileSize = Files.size(videoFilePath);
                
                VideoMetadata metadata = new VideoMetadata(videoId, fileName, contentType, fileSize);
                if (customMetadata != null) {
                    metadata.setCustomMetadata(customMetadata);
                }
                
                // Prepare metadata for S3
                Map<String, String> s3Metadata = new HashMap<>(metadata.getCustomMetadata());
                s3Metadata.put("video-id", videoId);
                s3Metadata.put("original-filename", fileName);
                s3Metadata.put("upload-time", metadata.getUploadTime().toString());
                
                // Create put request with encryption
                PutObjectRequest putRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(metadata.getS3Key())
                        .contentType(contentType)
                        .metadata(s3Metadata)
                        .serverSideEncryption(ServerSideEncryption.AWS_KMS)
                        .ssekmsKeyId(kmsKeyId)
                        .storageClass(StorageClass.fromValue(DEFAULT_STORAGE_CLASS))
                        .acl(ObjectCannedACL.PRIVATE)
                        .build();
                
                // Upload the file
                PutObjectResponse response = s3Client.putObject(putRequest, 
                        RequestBody.fromFile(videoFilePath));
                
                metadata.setEtag(response.eTag());
                
                LOGGER.info("Successfully uploaded video: " + videoId + " to key: " + metadata.getS3Key());
                return metadata;
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to upload video: " + videoId, e);
                throw new RuntimeException("Upload failed", e);
            }
        }, executorService);
    }
    */
    /**
     * Upload video from InputStream
     */

    public CompletableFuture<VideoMetadataJSON> uploadVideo(String videoId, InputStream videoStream, 
                                                       String originalFileName, long contentLength,
                                                       Map<String, String> customMetadata) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateVideoFileName(originalFileName);
                
                String contentType = determineContentType(originalFileName);
                VideoMetadataJSON metadata = new VideoMetadataJSON(videoId, originalFileName, contentType, contentLength);
                if (customMetadata != null) {
                    metadata.setCustomMetadata(customMetadata);
                }
                
                // Prepare metadata for S3
                Map<String, String> s3Metadata = new HashMap<>(metadata.getCustomMetadata());
                s3Metadata.put("video-id", videoId);
                s3Metadata.put("original-filename", originalFileName);
                s3Metadata.put("upload-time", metadata.getUploadTime().toString());
                
                // Create put request with encryption
                PutObjectRequest putRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(metadata.getS3Key())
                        .contentType(contentType)
                        .contentLength(contentLength)
                        .metadata(s3Metadata)
                        .serverSideEncryption(ServerSideEncryption.AWS_KMS)
                        .ssekmsKeyId(kmsKeyId)
                        .storageClass(StorageClass.fromValue(DEFAULT_STORAGE_CLASS))
                        .acl(ObjectCannedACL.BUCKET_OWNER_FULL_CONTROL)
                        .build();
                
                // Upload the stream
                PutObjectResponse response = s3Client.putObject(putRequest, 
                        RequestBody.fromInputStream(videoStream, contentLength));
                
                metadata.setEtag(response.eTag());
                
                LOGGER.info("Successfully uploaded video from stream: " + videoId + " to key: " + metadata.getS3Key());
                return metadata;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to upload video from stream: " + videoId, e);
                throw new RuntimeException("Upload failed", e);
            }
        }, executorService);
    }
    
    /**
     * Generate a presigned URL for read-only access
     */
    public URL generateReadOnlyUrl(String s3Key, Duration expiration) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(expiration != null ? expiration : DEFAULT_PRESIGN_DURATION)
                    .getObjectRequest(getRequest)
                    .build();
            
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            
            LOGGER.info("Generated presigned URL for key: " + s3Key + " valid for: " + 
                       (expiration != null ? expiration : DEFAULT_PRESIGN_DURATION));
            
            return presignedRequest.url();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to generate presigned URL for key: " + s3Key, e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
    
    /**
     * Get video metadata
     */
    public VideoMetadataJSON getVideoMetadata(String s3Key) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            
            HeadObjectResponse response = s3Client.headObject(headRequest);
            
            VideoMetadataJSON metadata = new VideoMetadataJSON();
            metadata.setS3Key(s3Key);
            metadata.setContentType(response.contentType());
            metadata.setFileSize(response.contentLength());
            metadata.setEtag(response.eTag());
            metadata.setUploadTime(LocalDateTime.ofInstant( response.lastModified(), ZoneId.systemDefault()));
            
            // Extract custom metadata
            Map<String, String> s3Metadata = response.metadata();
            if (s3Metadata != null) {
                metadata.setVideoId(s3Metadata.get("video-id"));
                metadata.setOriginalFileName(s3Metadata.get("original-filename"));
                Map<String, String> customMetadata = new HashMap<>(s3Metadata);
                customMetadata.remove("video-id");
                customMetadata.remove("original-filename");
                customMetadata.remove("upload-time");
                metadata.setCustomMetadata(customMetadata);
            }
            
            return metadata;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to get video metadata for key: " + s3Key, e);
            throw new RuntimeException("Failed to get metadata", e);
        }
    }
    
    /**
     * List all videos in the bucket
     */
    public List<VideoMetadataJSON> listVideos(String prefix, int maxKeys) {
        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefix != null ? prefix : "videos/")
                    .maxKeys(maxKeys > 0 ? maxKeys : 1000)
                    .build();
            
            ListObjectsV2Response response = s3Client.listObjectsV2(listRequest);
            
            List<VideoMetadataJSON> videos = new ArrayList<>();
            for (S3Object obj : response.contents()) {
                try {
                    VideoMetadataJSON metadata = getVideoMetadata(obj.key());
                    videos.add(metadata);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to get metadata for object: " + obj.key(), e);
                }
            }
            
            return videos;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to list videos", e);
            throw new RuntimeException("Failed to list videos", e);
        }
    }
    
    /**
     * Delete a video
     */
    public void deleteVideo(String s3Key) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            
            s3Client.deleteObject(deleteRequest);
            LOGGER.info("Successfully deleted video with key: " + s3Key);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to delete video with key: " + s3Key, e);
            throw new RuntimeException("Failed to delete video", e);
        }
    }
    
    public InputStream getContent(String s3Key){
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                                            .bucket(bucketName)
                                            .key(s3Key)
                                            .build();
            return s3Client.getObject(getRequest);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to delete video with key: " + s3Key, e);
            throw new RuntimeException("Failed to delete video", e);
        }
    }
     
    /**
     * Download video to local file
     */
    /* 
     public void downloadVideo(String s3Key, Path destinationPath) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            
            s3Client.getObject(getRequest, ResponseTransformer.toFile(destinationPath));
            LOGGER.info("Successfully downloaded video from key: " + s3Key + " to: " + destinationPath);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to download video from key: " + s3Key, e);
            throw new RuntimeException("Failed to download video", e);
        }
    }
    */
    /**
     * Create S3 bucket if it doesn't exist
     */
    private void createBucketIfNotExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            LOGGER.info("Bucket already exists: " + bucketName);
        } catch (NoSuchBucketException e) {
            LOGGER.info("Creating bucket: " + bucketName);
            
            CreateBucketRequest.Builder requestBuilder = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .acl(BucketCannedACL.AUTHENTICATED_READ);
            
            // Add location constraint for regions other than us-east-1
                requestBuilder.createBucketConfiguration(
                        CreateBucketConfiguration.builder()
                                .locationConstraint(BucketLocationConstraint.fromValue(region.id()))
                                .build()
                );
            
            s3Client.createBucket(requestBuilder.build());
            
            // Enable versioning
            s3Client.putBucketVersioning(PutBucketVersioningRequest.builder()
                    .bucket(bucketName)
                    .versioningConfiguration(VersioningConfiguration.builder()
                            .status(BucketVersioningStatus.ENABLED)
                            .build())
                    .build());
            
            LOGGER.info("Successfully created bucket: " + bucketName);
        }
    }
    
    /**
     * Create KMS key if it doesn't exist
     */
    private void createKmsKeyIfNotExists() {
        try {
            // Check if key exists by trying to describe it
            DescribeKeyRequest describeRequest = DescribeKeyRequest.builder()
                    .keyId(kmsKeyId)
                    .build();
            
            kmsClient.describeKey(describeRequest);
            LOGGER.info("KMS key already exists: " + kmsKeyId);
            
        } catch (NotFoundException e) {
            LOGGER.info("Creating KMS key: " + kmsKeyId);
            
            CreateKeyRequest createKeyRequest = CreateKeyRequest.builder()
                    .description("KMS key for S3 video encryption")
                    .keyUsage(KeyUsageType.ENCRYPT_DECRYPT)
                    .keySpec(KeySpec.SYMMETRIC_DEFAULT)
                    .build();
            
            CreateKeyResponse response = kmsClient.createKey(createKeyRequest);
            
            // Create alias
            CreateAliasRequest aliasRequest = CreateAliasRequest.builder()
                    .aliasName(kmsKeyId)
                    .targetKeyId(response.keyMetadata().keyId())
                    .build();
            
            kmsClient.createAlias(aliasRequest);
            
            LOGGER.info("Successfully created KMS key with alias: " + kmsKeyId);
        }
    }
    
    /**
     * Create read-only IAM role if it doesn't exist
     */
    private void createReadOnlyRoleIfNotExists() {
        try {
            String roleName = extractRoleNameFromArn(readOnlyRoleArn);
            
            GetRoleRequest getRoleRequest = GetRoleRequest.builder()
                    .roleName(roleName)
                    .build();
            
            iamClient.getRole(getRoleRequest);
            LOGGER.info("IAM role already exists: " + roleName);
            
        } catch (NoSuchEntityException e) {
            String roleName = extractRoleNameFromArn(readOnlyRoleArn);
            LOGGER.info("Creating IAM role: " + roleName);
            
            // Trust policy for the role
            String trustPolicy = "{\n" +
                    "  \"Version\": \"2012-10-17\",\n" +
                    "  \"Statement\": [\n" +
                    "    {\n" +
                    "      \"Effect\": \"Allow\",\n" +
                    "      \"Principal\": {\n" +
                    "        \"AWS\": \"*\"\n" +
                    "      },\n" +
                    "      \"Action\": \"sts:AssumeRole\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            
            CreateRoleRequest createRoleRequest = CreateRoleRequest.builder()
                    .roleName(roleName)
                    .assumeRolePolicyDocument(trustPolicy)
                    .description("Read-only access role for S3 video bucket")
                    .build();
            
            iamClient.createRole(createRoleRequest);
            
            // Create and attach policy for read-only access
            String policyDocument = createReadOnlyPolicyDocument();
            String policyName = roleName + "Policy";
            
            CreatePolicyRequest createPolicyRequest = CreatePolicyRequest.builder()
                    .policyName(policyName)
                    .policyDocument(policyDocument)
                    .description("Read-only policy for S3 video bucket")
                    .build();
            
            CreatePolicyResponse policyResponse = iamClient.createPolicy(createPolicyRequest);
            
            // Attach policy to role
            AttachRolePolicyRequest attachPolicyRequest = AttachRolePolicyRequest.builder()
                    .roleName(roleName)
                    .policyArn(policyResponse.policy().arn())
                    .build();
            
            iamClient.attachRolePolicy(attachPolicyRequest);
            
            LOGGER.info("Successfully created IAM role: " + roleName);
        }
    }
    
    /**
     * Configure bucket policies for security
     */
    private void configureBucketPolicies() {
        try {
            String policyDocument = createBucketPolicyDocument();
            
            PutBucketPolicyRequest policyRequest = PutBucketPolicyRequest.builder()
                    .bucket(bucketName)
                    .policy(policyDocument)
                    .build();
            
            s3Client.putBucketPolicy(policyRequest);
            
            // Configure bucket encryption
            ServerSideEncryptionByDefault encryptionByDefault = ServerSideEncryptionByDefault.builder()
                    .sseAlgorithm(ServerSideEncryption.AWS_KMS)
                    .kmsMasterKeyID(kmsKeyId)
                    .build();
            
            ServerSideEncryptionRule encryptionRule = ServerSideEncryptionRule.builder()
                    .applyServerSideEncryptionByDefault(encryptionByDefault)
                    .bucketKeyEnabled(true)
                    .build();
            
            ServerSideEncryptionConfiguration encryptionConfig = ServerSideEncryptionConfiguration.builder()
                    .rules(encryptionRule)
                    .build();
            
            PutBucketEncryptionRequest encryptionRequest = PutBucketEncryptionRequest.builder()
                    .bucket(bucketName)
                    .serverSideEncryptionConfiguration(encryptionConfig)
                    .build();
            
            s3Client.putBucketEncryption(encryptionRequest);
            
            LOGGER.info("Successfully configured bucket policies and encryption");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to configure bucket policies", e);
            throw new RuntimeException("Failed to configure bucket policies", e);
        }
    }
    /* 
    private void validateVideoFile(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("Video file does not exist: " + filePath);
        }
        
        if (!Files.isRegularFile(filePath)) {
            throw new IllegalArgumentException("Path is not a regular file: " + filePath);
        }
        
        String fileName = filePath.getFileName().toString().toLowerCase();
        validateVideoFileName(fileName);
    }
    */
    private void validateVideoFileName(String fileName) {
        boolean isVideoFile = SUPPORTED_VIDEO_EXTENSIONS.stream()
                .anyMatch(ext -> fileName.toLowerCase().endsWith(ext));
        
        if (!isVideoFile) {
            throw new IllegalArgumentException("Unsupported video format. Supported formats: " + 
                                             SUPPORTED_VIDEO_EXTENSIONS);
        }
    }
    
    private String determineContentType(String fileName) {
        String extension = fileName.toLowerCase();
        
        if (extension.endsWith(".mp4")) return "video/mp4";
        if (extension.endsWith(".avi")) return "video/x-msvideo";
        if (extension.endsWith(".mov")) return "video/quicktime";
        if (extension.endsWith(".wmv")) return "video/x-ms-wmv";
        if (extension.endsWith(".flv")) return "video/x-flv";
        if (extension.endsWith(".webm")) return "video/webm";
        if (extension.endsWith(".mkv")) return "video/x-matroska";
        if (extension.endsWith(".m4v")) return "video/x-m4v";
        
        return "application/octet-stream"; // Default fallback
    }
    
    private static String extractRoleNameFromArn(String roleArn) {
        if (roleArn == null || !roleArn.contains("/")) {
            return roleArn; // Assume it's just the role name
        }
        return roleArn.substring(roleArn.lastIndexOf("/") + 1);
    }
    
    private String createReadOnlyPolicyDocument() {
        return "{\n" +
                "  \"Version\": \"2012-10-17\",\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Effect\": \"Allow\",\n" +
                "      \"Action\": [\n" +
                "        \"s3:GetObject\",\n" +
                "        \"s3:GetObjectVersion\",\n" +
                "        \"s3:ListBucket\"\n" +
                "      ],\n" +
                "      \"Resource\": [\n" +
                "        \"arn:aws:s3:::" + bucketName + "\",\n" +
                "        \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"Effect\": \"Allow\",\n" +
                "      \"Action\": [\n" +
                "        \"kms:Decrypt\",\n" +
                "        \"kms:DescribeKey\"\n" +
                "      ],\n" +
                "      \"Resource\": \"arn:aws:kms:" + region.id() + ":*:key/*\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
    
    private String createBucketPolicyDocument() {
        return "{\n" +
                "  \"Version\": \"2012-10-17\",\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Sid\": \"DenyInsecureConnections\",\n" +
                "      \"Effect\": \"Deny\",\n" +
                "      \"Principal\": \"*\",\n" +
                "      \"Action\": \"s3:*\",\n" +
                "      \"Resource\": [\n" +
                "        \"arn:aws:s3:::" + bucketName + "\",\n" +
                "        \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                "      ],\n" +
                "      \"Condition\": {\n" +
                "        \"Bool\": {\n" +
                "          \"aws:SecureTransport\": \"false\"\n" +
                "        }\n" +
                "      }\n";
    }
}