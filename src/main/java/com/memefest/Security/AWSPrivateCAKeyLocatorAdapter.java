package com.memefest.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.ProtectedHeader;
import io.jsonwebtoken.security.EcPrivateJwk;
import io.jsonwebtoken.security.JwkThumbprint;
import io.jsonwebtoken.security.Jwks;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import com.memefest.DataAccess.JSON.UserJSON;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.awscore.endpoint.AwsClientEndpointProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.waiters.Waiter;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.acmpca.AcmPcaClient;
import software.amazon.awssdk.services.acmpca.AcmPcaClientBuilder;
import software.amazon.awssdk.services.acmpca.endpoints.AcmPcaEndpointParams;
import software.amazon.awssdk.services.acmpca.endpoints.AcmPcaEndpointProvider;
import software.amazon.awssdk.services.acmpca.endpoints.internal.Value.Endpoint;
import software.amazon.awssdk.services.acmpca.model.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.*;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * AWS Private CA Key Locator Adapter that implements JWT SigningKeyResolver interface
 * Manages Private CA creation, subordinate CA signing, and keystore operations
 */
public class AWSPrivateCAKeyLocatorAdapter extends LocatorAdapter<Key> {
    
    private static final String BOUNCY_CASTLE_PROVIDER = "BC";
    private static String endEntitySubjectDn;
    private static String defaultKeyId;
    private static String defaultJwtCertDn;
  
    private static  AcmPcaClient acmPcaClient;
    private static  String keystorePath;
    private static  String keystorePassword;
    private static  String truststorePath;
    private static  String truststorePassword;
    private static  Region region;
    private static  String crlBucketName;
    
    private static String rootCaArn;
    private static String subordinateCaArn;
    private static KeyStore keyStore;
    private static KeyStore trustStore;
    
    static {
        // Add BouncyCastle provider
        Security.addProvider(new BouncyCastleProvider());    
        try {
        URL propsFile = Thread.currentThread().getContextClassLoader()
                            .getResource("keystore.properties");
        Properties props = new Properties();
        props.load(new FileInputStream(new File(propsFile.toURI())));   
        keyStore = KeyStore.getInstance("JKS");
        trustStore = KeyStore.getInstance("JKS");    
        
        keystorePassword = props.getProperty("keyStorePass");
        keystorePath = props.getProperty("keyStorePath");
        truststorePath = props.getProperty("trustStorePath");
        truststorePassword = props.getProperty("trustStorePass");
        endEntitySubjectDn = props.getProperty("endEntitySubjectDn");
        defaultJwtCertDn = props.getProperty("defaultJwtSubjectDn");
        defaultKeyId = props.getProperty("defaultKeyId");
        crlBucketName = props.getProperty("crlBucketName");

        region = Region.of(props.getProperty("region"));

        URL keyStoreLocation = Thread.currentThread().getContextClassLoader()
                                    .getResource(keystorePath);
        FileInputStream fis = new FileInputStream(new File(keyStoreLocation.toURI()));
        keyStore.load(fis, keystorePassword.toCharArray());
        
        URL trustStoreLocation = Thread.currentThread().getContextClassLoader()
                                    .getResource(truststorePath);
        FileInputStream tsis = new FileInputStream(new File(trustStoreLocation.toURI()));
        trustStore.load(tsis, truststorePassword.toCharArray());
        setupCAInfrastructure();
    } catch (KeyStoreException ex) {
      ex.printStackTrace();
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    } catch (CertificateException ex) {
      ex.printStackTrace();
    } catch (URISyntaxException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } 
    }

    @Override
    public Key locate(ProtectedHeader header){
        String keyId = header.getKeyId();
        if (keyId == null) {
            keyId = defaultKeyId;
        }
        
        // Check cache first
        /* 
        Key cachedKey = keyCache.get(keyId);
        if (cachedKey != null) {
            return cachedKey;
        }
        */
        // Try to load from keystore
        try {
            if (keyStore.containsAlias(keyId)) {
                Certificate cert = keyStore.getCertificate(keyId);
                if (cert != null) {
                    Key publicKey = cert.getPublicKey();
                    // /keyCache.put(keyId, publicKey);
                    return publicKey;
                }
            }
            
            // If not found, try default key
            if (!keyId.equals(defaultKeyId) && keyStore.containsAlias(defaultKeyId)) {
                Certificate cert = keyStore.getCertificate(defaultKeyId);
                if (cert != null) {
                    Key publicKey = cert.getPublicKey();
                    //keyCache.put(keyId, publicKey);
                    return publicKey;
                }
            }
            else{
                try {
                    AWSPrivateCAKeyLocatorAdapter.getClient();
                    AWSPrivateCAKeyLocatorAdapter.createJWTCertificate(defaultKeyId,defaultJwtCertDn);
                    AWSPrivateCAKeyLocatorAdapter.cleanup();
                    return locate(header);    
                } catch ( NoSuchAlgorithmException | UnrecoverableEntryException |
                    OperatorCreationException | IOException | CertificateException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error creating end entity certificate", e);
                }
            }
            
        } catch (KeyStoreException e) {
            throw new RuntimeException("Error accessing keystore", e);
        }
        
        throw new RuntimeException("Could not resolve signing key for keyId: " + keyId);
    }
    
    public static AcmPcaClient getClient(){
        
        return AcmPcaClient.builder().region(region)
                //.credentialsProvider(ProfileCredentialsProvider.create("memefest-api"))
                //.endpointProvider(AcmPcaEndpointProvider.defaultProvider()
                //.resolveEndpoint(AcmPcaEndpointParams.builder().region(region).build()))
                .build();
    }
    
    /**
     * Get signing key for JWT creation (private key)
     */
    /* 
     public Key getSigningKey(String keyId) {
        if (keyId == null) {
            keyId = DEFAULT_KEY_ID;
        }
        
        try {
            // Try to get private key from keystore
            if (keyStore.containsAlias(keyId)) {
                return keyStore.getKey(keyId, keystorePassword.toCharArray());
            }
            
            // Try end-entity key
            if (keyStore.containsAlias("end-entity")) {
                return keyStore.getKey("end-entity", keystorePassword.toCharArray());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving signing key", e);
        }
        
        throw new RuntimeException("Could not find signing key for keyId: " + keyId);
    }
    */
    
    
    /**
     * Main method to orchestrate the entire CA and certificate creation process
     */
    public static void setupCAInfrastructure() {

        try{
            getClient();
            // Step 1: Create or retrieve root CA
            rootCaArn = createOrRetrieveRootCA();
        
            // Step 2: Create subordinate CA
            subordinateCaArn = createOrRetreiveSubordinateCA();
        
            // Step 3: Sign subordinate CA with root CA
            //signSubordinateCA();
            // Step 5: Import subordinate CA into truststore
            importSubordinateCAToTruststore();
            // Step 4: Create jwt certificate
            createJWTCertificate(defaultKeyId,defaultJwtCertDn);
        

            cleanup();
        }
            // Step 6: Cache keys for quick access
            //cacheKeysFromKeystore();
        catch(InvalidArgsException | InvalidPolicyException | LimitExceededException
                | NoSuchAlgorithmException | OperatorCreationException
                    | IOException | KeyStoreException | UnrecoverableEntryException | CertificateException ex){
            ex.printStackTrace();
        }
    }
    /* 
    private void initializeKeystores() {
        try {
            // Initialize main keystore
            keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream(keystorePath)) {
                keyStore.load(fis, keystorePassword.toCharArray());
            } catch (IOException e) {
                //Create new keystore if it doesn't exist
                keyStore.load(null, keystorePassword.toCharArray());
            }
            
            // Initialize truststore
            trustStore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream(truststorePath)) {
                trustStore.load(fis, truststorePassword.toCharArray());
            } catch (IOException e) {
                // Create new truststore if it doesn't exist
                trustStore.load(null, truststorePassword.toCharArray());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize keystores", e);
        }
    }
    
    private void cacheKeysFromKeystore() {
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = keyStore.getCertificate(alias); 
                if (cert != null) {
                    keyCache.put(alias, cert.getPublicKey());
                }
            }
        } catch (KeyStoreException e) {
            throw new RuntimeException("Error caching keys from keystore", e);
        }
    }
    */
    private static String createOrRetrieveRootCA() throws InvalidArgsException, InvalidPolicyException, LimitExceededException {
        // Check if root CA already exists
        ListCertificateAuthoritiesRequest listRequest = ListCertificateAuthoritiesRequest.builder()
                .build();
        
        ListCertificateAuthoritiesResponse listResponse = acmPcaClient.listCertificateAuthorities(listRequest);
        for (CertificateAuthority ca : listResponse.certificateAuthorities()) {
            if (ca.type() == CertificateAuthorityType.ROOT && 
                ca.status() == CertificateAuthorityStatus.ACTIVE) {
                System.out.println("Found existing root CA: " + ca.arn());
                return ca.arn();
            }
        }
        
        // Create new root CA
        System.out.println("Creating new root CA...");
        
        CertificateAuthorityConfiguration caConfig = CertificateAuthorityConfiguration.builder()
                .keyAlgorithm(KeyAlgorithm.RSA_2048)
                .signingAlgorithm(SigningAlgorithm.SHA256_WITHRSA)
                .subject(ASN1Subject.builder()
                        .commonName("https://g-nice.com")
                        .organization("Ginice Corp")
                        .country("Kenya")
                        .locality("Nairobi")
                        .build())
                .build();


        CrlConfiguration crlConfig = CrlConfiguration.builder()
                                        .enabled(true)
                                        .expirationInDays(365)
                                        .customCname(null)
                                        .s3BucketName(crlBucketName)
                                        .build();
        
        RevocationConfiguration recConfig = RevocationConfiguration.builder()
                                                .crlConfiguration(crlConfig)
                                                .build();

        CreateCertificateAuthorityRequest createRequest = CreateCertificateAuthorityRequest.builder()
                .certificateAuthorityConfiguration(caConfig)
                .revocationConfiguration(recConfig)
                .idempotencyToken("12590")
                .certificateAuthorityType(CertificateAuthorityType.ROOT)
                .build();
        
        CreateCertificateAuthorityResponse createResponse = acmPcaClient.createCertificateAuthority(createRequest);
        String caArn = createResponse.certificateAuthorityArn();
        
        // Self-sign the root CA
        //selfSignRootCA(caArn);
        
        System.out.println("Root CA created and activated: " + caArn);
        return caArn;
    }
    
    /* 
    private void selfSignRootCA(String caArn) throws InvalidArgsException, InvalidPolicyException, LimitExceededException{
        // Get CSR from the CA
        GetCertificateAuthorityCsrRequest csrRequest = GetCertificateAuthorityCsrRequest.builder()
                .certificateAuthorityArn(caArn)
                .build();
        
        GetCertificateAuthorityCsrResponse csrResponse = acmPcaClient.getCertificateAuthorityCsr(csrRequest);
        String csr = csrResponse.csr();
        
        // Issue certificate using the CA itself
        IssueCertificateRequest issueRequest = IssueCertificateRequest.builder()
                .certificateAuthorityArn(caArn)
                .csr(SdkBytes.fromUtf8String(csr))
                .signingAlgorithm(SigningAlgorithm.SHA256_WITHRSA)
                .templateArn("arn:aws:acm-pca:::template/RootCACertificate/V1")
                .validity(Validity.builder()
                        .type(ValidityPeriodType.YEARS)
                        .value(10L)
                        .build())
                .build();
        
        IssueCertificateResponse issueResponse = acmPcaClient.issueCertificate(issueRequest);
        
        // Wait for certificate to be issued
        waitForCertificateIssuance(caArn, issueResponse.certificateArn());
        
        // Get the issued certificate
        GetCertificateRequest getCertRequest = GetCertificateRequest.builder()
                .certificateAuthorityArn(caArn)
                .certificateArn(issueResponse.certificateArn())
                .build();
        
        GetCertificateResponse getCertResponse = acmPcaClient.getCertificate(getCertRequest);
        
        // Import the certificate back to the CA
        ImportCertificateAuthorityCertificateRequest importRequest = 
                ImportCertificateAuthorityCertificateRequest.builder()
                        .certificateAuthorityArn(caArn)
                        .certificate(SdkBytes.fromUtf8String(getCertResponse.certificate()))
                        .build();
        
        acmPcaClient.importCertificateAuthorityCertificate(importRequest);
    }
    */
    
    private static  String createOrRetreiveSubordinateCA() throws  InvalidArgsException, InvalidPolicyException, LimitExceededException {
        System.out.println("Creating subordinate CA...");
        
        CertificateAuthorityConfiguration caConfig = CertificateAuthorityConfiguration.builder()
                .keyAlgorithm(KeyAlgorithm.RSA_2048)
                .signingAlgorithm(SigningAlgorithm.SHA256_WITHRSA)
                .subject(ASN1Subject.builder()
                        .commonName("Subordinate CA")
                        .organization("Ginice Corp")
                        .country("Kenya")
                        .build())
                .build();
        CreateCertificateAuthorityRequest createRequest = CreateCertificateAuthorityRequest.builder()
                .certificateAuthorityConfiguration(caConfig)
                .certificateAuthorityType(CertificateAuthorityType.SUBORDINATE)
                .build();
        
        CreateCertificateAuthorityResponse createResponse = acmPcaClient.createCertificateAuthority(createRequest);
        String subordinateArn = createResponse.certificateAuthorityArn();
        
        System.out.println("Subordinate CA created: " + subordinateArn);
        return subordinateArn;
    }
    /* 
    private void signSubordinateCA() throws  InvalidArgsException, InvalidPolicyException, LimitExceededException  {
        System.out.println("Signing subordinate CA with root CA...");
        
        // Get CSR from subordinate CA
        GetCertificateAuthorityCsrRequest csrRequest = GetCertificateAuthorityCsrRequest.builder()
                .certificateAuthorityArn(subordinateCaArn)
                .build();
        
        GetCertificateAuthorityCsrResponse csrResponse = acmPcaClient.getCertificateAuthorityCsr(csrRequest);
        
        // Issue certificate from root CA
        IssueCertificateRequest issueRequest = IssueCertificateRequest.builder()
                .certificateAuthorityArn(rootCaArn)
                .csr(SdkBytes.fromUtf8String(csrResponse.csr()))
                .signingAlgorithm(SigningAlgorithm.SHA256_WITHRSA)
                .templateArn("arn:aws:acm-pca:::template/SubordinateCACertificate_PathLen0/V1")
                .validity(Validity.builder()
                        .type(ValidityPeriodType.YEARS)
                        .value(5L)
                        .build())
                .build();
        
        IssueCertificateResponse issueResponse = acmPcaClient.issueCertificate(issueRequest);
        
        // Wait for certificate issuance
        waitForCertificateIssuance(rootCaArn, issueResponse.certificateArn());
        
        // Get the certificate and certificate chain
        GetCertificateRequest getCertRequest = GetCertificateRequest.builder()
                .certificateAuthorityArn(rootCaArn)
                .certificateArn(issueResponse.certificateArn())
                .build();
        
        GetCertificateResponse getCertResponse = acmPcaClient.getCertificate(getCertRequest);
        
        // Import certificate to subordinate CA
        ImportCertificateAuthorityCertificateRequest importRequest = 
                ImportCertificateAuthorityCertificateRequest.builder()
                        .certificateAuthorityArn(subordinateCaArn)
                        .certificate(SdkBytes.fromUtf8String(getCertResponse.certificate()))
                        .certificateChain(SdkBytes.fromUtf8String(getCertResponse.certificateChain()))
                        .build();
        
        acmPcaClient.importCertificateAuthorityCertificate(importRequest);
        
        System.out.println("Subordinate CA signed and activated");
    }
    */
    private static void createJWTCertificate(String keyId, String subjectDn) throws  InvalidArgsException, InvalidPolicyException,
                                                         LimitExceededException, NoSuchAlgorithmException,
                                                         OperatorCreationException, IOException,
                                                         CertificateException, KeyStoreException, UnrecoverableEntryException  {
        if(keyStore.containsAlias(keyId))
            return;
        System.out.println("Creating jwt certificate...");
        
        /* 
        // Generate key pair for end entity
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("");
        keyGen.initialize(2048);
        KeyPair endEntityKeyPair = keyGen.generateKeyPair();
        */
        // Create CSR
        KeyPair keyPair = createNewKey();
        //ContentSigner csrSigner = new JcaContentSignerBuilder("ES512")
        //        .setProvider(BOUNCY_CASTLE_PROVIDER)
        //        .build(keyPair.getPrivate());
        createOrRetrieveRootCA();
        createOrRetreiveSubordinateCA(); 
        KeyStore.ProtectionParameter subCaParam = new KeyStore.PasswordProtection(keystorePassword.toCharArray());  
        PrivateKeyEntry subCaEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(defaultKeyId, subCaParam);
         if (subCaEntry == null)
                throw new KeyStoreException("Thats not a private key!"); 
        Certificate subCaCert = subCaEntry.getCertificate();
        PublicKey subCaPubKey = subCaCert.getPublicKey();
        KeyPair subCaPair =  new KeyPair(subCaPubKey, subCaEntry.getPrivateKey());
        //PKCS10CertificationRequest csr = csrBuilder.build(csrSigner);
        //long now = System.currentTimeMillis();
        //Date startDate = new Date(now);
        //X500Name dnName = new X500Name(subjectDn);
        //BigInteger certSerialNumber = new BigInteger(Long.toString(now));
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTime(startDate);
        //calendar.add(1, 1);
        //Date endDate = calendar.getTime();
        //String signatureAlgorithm = "ES512";
        //SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
        //X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(dnName, certSerialNumber, startDate,
        //                                                endDate, dnName, subjectPublicKeyInfo);
        //ContentSigner contentSigner = (new JcaContentSignerBuilder(signatureAlgorithm)).setProvider(BOUNCY_CASTLE_PROVIDER)
        //                                .build(subCaPair.getPrivate());
        //X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);
        //Certificate selfSignedCertificate = (new JcaX509CertificateConverter()).getCertificate(certificateHolder);
         CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        byte[] derBytes = subCaEntry.getCertificateChain()[0].getEncoded();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(derBytes)) {
        X509Certificate subCacert =(X509Certificate) certFactory.generateCertificate(bis);
        Certificate jwtCert =  signCertificate(keyPair.getPublic(), subjectDn, subCaPair.getPrivate(),subCacert, 31);
        new KeyPair(jwtCert.getPublicKey(),keyPair.getPrivate());
        keyStore.setKeyEntry(keyId,keyPair.getPrivate().getEncoded(), new Certificate[]{jwtCert});
        // Issue certificate from subordinate CA
        } catch (IOException e) {
            throw new CertificateException("Error reading DER bytes", e);
        }                                                    
    }

    public static Certificate signCertificate(
            PublicKey endEntityPublicKey,
            String endEntitySubject,
            PrivateKey caPrivateKey,
            X509Certificate caCertificate,
            int validityDays) throws OperatorCreationException, CertificateException, IOException {

        // Generate serial number (should be unique for each certificate)
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());

        // Set validity period
        Instant now = Instant.now();
        Date notBefore = Date.from(now);
        Date notAfter = Date.from(now.plus(validityDays, ChronoUnit.DAYS));

        // Create subject and issuer names
        X500Name subject = new X500Name(endEntitySubject);
        X500Name issuer = new X500Name(caCertificate.getSubjectX500Principal().getName());
        // Build the certificate
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serialNumber,
                notBefore,
                notAfter,
                subject,
                endEntityPublicKey
        );

        // Add extensions for end-entity certificate
        certBuilder = addEndEntityExtensions(certBuilder, endEntityPublicKey, caCertificate);

        // Create content signer using CA private key
        ContentSigner signer = new JcaContentSignerBuilder("ES512")
                .setProvider(BOUNCY_CASTLE_PROVIDER)
                .build(caPrivateKey);

        // Sign the certificate
        X509CertificateHolder certHolder = certBuilder.build(signer);

        // Convert to X509Certificate
        return new JcaX509CertificateConverter()
                .setProvider(BOUNCY_CASTLE_PROVIDER)
                .getCertificate(certHolder);
    }

     private static X509v3CertificateBuilder addEndEntityExtensions(
            X509v3CertificateBuilder certBuilder,
            PublicKey subjectPublicKey,
            X509Certificate issuerCert) throws IOException, CertificateException {

        // 1. Subject Key Identifier
        SubjectKeyIdentifier subjectKeyIdentifier = 
            new SubjectKeyIdentifier(subjectPublicKey.getEncoded());
        certBuilder.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                subjectKeyIdentifier
        );

        // 2. Authority Key Identifier (from CA certificate)
        byte[] issuerPublicKeyHash = issuerCert.getPublicKey().getEncoded();
        AuthorityKeyIdentifier authorityKeyIdentifier = 
            new AuthorityKeyIdentifier(issuerPublicKeyHash);
        certBuilder.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                authorityKeyIdentifier
        );

        // 3. Basic Constraints (critical) - NOT a CA
        BasicConstraints basicConstraints = new BasicConstraints(false);
        certBuilder.addExtension(
                Extension.basicConstraints,
                true,
                basicConstraints
        );

        // 4. Key Usage (critical) - Digital Signature and Key Encipherment
        org.bouncycastle.asn1.x509.KeyUsage encipherKU = new org.bouncycastle.asn1.x509.KeyUsage(
                                                        org.bouncycastle.asn1.x509.KeyUsage.dataEncipherment);
        org.bouncycastle.asn1.x509.KeyUsage digitalSignKU = new KeyUsage(KeyUsage.digitalSignature);
        org.bouncycastle.asn1.x509.KeyUsage keyEncipherKU = new KeyUsage(KeyUsage.keyEncipherment);
        
        certBuilder.addExtension(
                Extension.keyUsage,
                true,
                encipherKU
        );
        
        certBuilder.addExtension(
            Extension.keyUsage,
            true,
            digitalSignKU
        );
        
        certBuilder.addExtension(
            Extension.keyUsage,
            true,
            keyEncipherKU
        );

        KeyPurposeId clientAuthEKU = KeyPurposeId.id_kp_clientAuth;
        KeyPurposeId serverAuthEKU = KeyPurposeId.id_kp_serverAuth;
        //remove later
        KeyPurposeId anyEKU = KeyPurposeId.anyExtendedKeyUsage;
        // 5. Extended Key Usage - Server and Client Authentication
        ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(new KeyPurposeId[]{
            clientAuthEKU,serverAuthEKU,anyEKU
        });

        certBuilder.addExtension(
                Extension.extendedKeyUsage,
                false,
                extendedKeyUsage
        );
        return certBuilder;
    }

    public static String getThumbPrint(KeyPair pair) {
        EcPrivateJwk ecPrivateJwk = (EcPrivateJwk)Jwks.builder().ecKeyPair(pair).build();
        JwkThumbprint thumbPrint = ecPrivateJwk.thumbprint();
        return thumbPrint.toString();
    }

    private static void createEndCertificate(String alias) throws IOException, 
                                                OperatorCreationException, 
                                                    NoSuchAlgorithmException, KeyStoreException{
         if(keyStore.containsAlias(alias))
            return;
        System.out.println("Creating jwt certificate...");
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ES512");
        keyGen.initialize(2048);
        KeyPair endEntityKeyPair = keyGen.generateKeyPair();
        X500Name subject = new X500Name(endEntitySubjectDn);
        PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(
                subject, endEntityKeyPair.getPublic());
        ContentSigner csrSigner = new JcaContentSignerBuilder("ES512")
                .setProvider(BOUNCY_CASTLE_PROVIDER)
                .build(endEntityKeyPair.getPrivate());
        createOrRetrieveRootCA();
        createOrRetreiveSubordinateCA();
        PKCS10CertificationRequest csr = csrBuilder.build(csrSigner);
        // Issue certificate from subordinate CA
        IssueCertificateRequest issueRequest = IssueCertificateRequest.builder()
                .certificateAuthorityArn(subordinateCaArn)
                .csr(SdkBytes.fromByteArray(csr.getEncoded()))
                .signingAlgorithm(SigningAlgorithm.SHA512_WITHECDSA)
                .templateArn("arn:aws:acm-pca:::template/EndEntityCertificate/V1")
                .validity(Validity.builder()
                        .type(ValidityPeriodType.YEARS)
                        .value(1L)
                        .build())
                .build();

        IssueCertificateResponse issueResponse = acmPcaClient.issueCertificate(issueRequest);
        // Get the certificate
        
        GetCertificateRequest getCertRequest = GetCertificateRequest.builder()
                .certificateAuthorityArn(subordinateCaArn)
                .certificateArn(issueResponse.certificateArn())
                .build();
        
        
        
        WaiterResponse<GetCertificateResponse> waiter = acmPcaClient.waiter().waitUntilCertificateIssued(getCertRequest);
        
        waiter.matched().response().ifPresentOrElse((r)-> {
            try{
                // Parse the certificate
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                X509Certificate endEntityCert = (X509Certificate) certFactory.generateCertificate(
                    new ByteArrayInputStream(r.certificate().getBytes()));
        
                // Create certificate chain
                List<Certificate> certChain = new ArrayList<>();
                certChain.add(endEntityCert);
        
                // Add intermediate certificates if present
                if (r.certificateChain() != null) {
                    String[] chainCerts = r.certificateChain().split("-----END CERTIFICATE-----");
                    for (String chainCert : chainCerts) {
                        if (chainCert.trim().isEmpty()) continue;
                            String fullCert = chainCert + "-----END CERTIFICATE-----";
                            X509Certificate intermediateCert = (X509Certificate) certFactory.generateCertificate(
                            new ByteArrayInputStream(fullCert.getBytes()));
                            certChain.add(intermediateCert);
                    }
                    keyStore.setKeyEntry(defaultKeyId, endEntityKeyPair.getPrivate(), 
                        keystorePassword.toCharArray(), certChain.toArray(new Certificate[0]));
        
                    System.out.println("Jwt certificate created and stored in keystore");            
                }
            }
            catch(KeyStoreException | CertificateException ex){
                    ex.printStackTrace();
            }
        }
        ,new Runnable() {
                @Override 
                public void run(){
                  System.out.println("Certificate not issued by CA");
                } 
            }
        );        

    }
    
    private static void importSubordinateCAToTruststore() throws CertificateException, KeyStoreException,
                                                             IOException, NoSuchAlgorithmException{
        System.out.println("Importing subordinate CA to truststore...");
        
        // Get subordinate CA certificate
        DescribeCertificateAuthorityRequest describeRequest = DescribeCertificateAuthorityRequest.builder()
                .certificateAuthorityArn(subordinateCaArn)
                .build();
        
        DescribeCertificateAuthorityResponse describeResponse = acmPcaClient.describeCertificateAuthority(describeRequest);
        
        GetCertificateAuthorityCertificateRequest getCertRequest = GetCertificateAuthorityCertificateRequest.builder()
                .certificateAuthorityArn(subordinateCaArn)
                .build();
        
        GetCertificateAuthorityCertificateResponse getCertResponse = 
                acmPcaClient.getCertificateAuthorityCertificate(getCertRequest);
        
        // Parse certificate
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate subordinateCert = (X509Certificate) certFactory.generateCertificate(
                new ByteArrayInputStream(getCertResponse.certificate().getBytes()));
        
        // Add to truststore
        trustStore.setCertificateEntry("subordinate-ca", subordinateCert);
        
        // Also add root CA to truststore
        GetCertificateAuthorityCertificateRequest rootCertRequest = 
                GetCertificateAuthorityCertificateRequest.builder()
                        .certificateAuthorityArn(rootCaArn)
                        .build();
        
        GetCertificateAuthorityCertificateResponse rootCertResponse = 
                acmPcaClient.getCertificateAuthorityCertificate(rootCertRequest);
        
        X509Certificate rootCert = (X509Certificate) certFactory.generateCertificate(
                new ByteArrayInputStream(rootCertResponse.certificate().getBytes()));
        
        trustStore.setCertificateEntry("root-ca", rootCert);
        
        // Save truststore
        try (FileOutputStream fos = new FileOutputStream(truststorePath)) {
            trustStore.store(fos, truststorePassword.toCharArray());
        }
        
        System.out.println("CA certificates imported to truststore");
    }

      public static KeyPair loadFromJKS(String alias) throws KeyStoreException, NoSuchAlgorithmException, 
                    UnrecoverableEntryException, IOException, CertificateException, OperatorCreationException {
        KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection(keystorePassword.toCharArray());
        KeyStore.PrivateKeyEntry entry = null;
        if(keyStore.containsAlias(alias)){
            entry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, param);
        }
        else if(keyStore.containsAlias(defaultKeyId)){
            entry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(defaultKeyId, param);
        }
        else{
            getClient();
            createJWTCertificate(defaultKeyId, defaultJwtCertDn);
            cleanup();
            return loadFromJKS(alias);
        }
        if (entry == null)
                throw new KeyStoreException("Thats not a private key!"); 
            Certificate cert = entry.getCertificate();
            PublicKey publicKey = cert.getPublicKey();
            return new KeyPair(publicKey, entry.getPrivateKey());

    }

    private static KeyPair createNewKey() {
        KeyPair pair = (KeyPair)Jwts.SIG.ES512.keyPair().build();
        return pair;
    }
    
    /**
     * Get cached key by ID
     */
    /* 
    public Key getCachedKey(String keyId) {
        return keyCache.get(keyId);
    }
        
    /**
     * List all available key IDs
     
    public Set<String> getAvailableKeyIds() {
        return new HashSet<>(keyCache.keySet());
    }    
    */
    
    /**
     * Cleanup resources
     */
    public static void cleanup() {
        if (acmPcaClient != null) {
            acmPcaClient.close();
        }
        //keyCache.clear();
    }

}