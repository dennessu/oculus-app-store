/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.AuthorizeService;
import com.junbo.authorization.AuthorizeServiceImpl;
import com.junbo.authorization.ResourceScopeValidatorImpl;
import com.junbo.catalog.spec.model.item.Binary;
import com.junbo.catalog.spec.model.item.EntitlementDef;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.common.error.AppErrorException;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.common.cache.CommonCache;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.core.service.EntitlementServiceImpl;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.langur.core.rest.ResourceScopeValidator;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Entitlement service test.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public class EntitlementServiceTest extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;
    @Autowired
    private EntitlementService entitlementService;
    @Autowired
    private AmazonS3Client awsClient;
    @Autowired
    private BasicAWSCredentials awsCredentials;

    @Test
    public void testAddEntitlement() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        Assert.assertEquals(addedEntitlement.getUseCount(), entitlement.getUseCount());
    }

    @Test(expectedExceptions = AppErrorException.class)
    public void testAddWrongDateEntitlement() {
        Entitlement entitlement = buildAnEntitlement();
        Date currentDate = new Date();
        entitlement.setGrantTime(currentDate);
        entitlement.setExpirationTime(new Date(currentDate.getTime() - 6000));
        entitlementService.addEntitlement(entitlement);
    }

    @Test
    public void testGetEntitlementWithManagedLifecycle() {
        Entitlement entitlement = buildAnEntitlement();
        entitlement.setExpirationTime(new Date(System.currentTimeMillis() - 1 * 24 * 3600 * 1000));
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        Assert.assertEquals(addedEntitlement.getIsActive(), Boolean.FALSE);
    }

    @Test
    public void testUpdateEntitlement() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        Date now = new Date();
        addedEntitlement.setExpirationTime(now);
        Entitlement updatedEntitlement = entitlementService.updateEntitlement(
                addedEntitlement.getId(), addedEntitlement);
        Assert.assertTrue(Math.abs(updatedEntitlement.getExpirationTime().getTime() - now.getTime()) <= 1000);
    }

    @Test
    public void testDeleteEntitlement() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        entitlementService.deleteEntitlement(addedEntitlement.getId());
        try {
            entitlementService.getEntitlement(addedEntitlement.getId());
        } catch (WebApplicationException e) {
            Assert.assertEquals(e.getResponse().getStatus(), 404);
            return;
        }
        Assert.assertEquals(true, false);

    }

    @Test
    public void testSearchEntitlements() {
        EntitlementContext.current().setNow(new Date(114, 1, 10));
        Long userId = idGenerator.nextId();
        for (int i = 0; i < 5; i++) {
            Entitlement entitlementEntity = buildAnEntitlement();
            entitlementEntity.setUserId(userId);
            entitlementEntity.setExpirationTime(new Date(114, 2, 20));
            entitlementService.addEntitlement(entitlementEntity);
        }

        EntitlementContext.current().setNow(new Date(114, 2, 30));

        EntitlementSearchParam searchParam = new EntitlementSearchParam();
        searchParam.setUserId(new UserId(userId));
        searchParam.setIsActive(true);
        List<Entitlement> entitlements = entitlementService.searchEntitlement(searchParam, new PageMetadata()).getItems();
        Assert.assertEquals(entitlements.size(), 0);

        searchParam.setIsActive(false);
        entitlements = entitlementService.searchEntitlement(searchParam, new PageMetadata()).getItems();
        Assert.assertEquals(entitlements.size(), 5);
    }

    private Entitlement buildAnEntitlement() {
        Entitlement entitlement = new Entitlement();
        entitlement.setUserId(idGenerator.nextId());
        entitlement.setGrantTime(new Date(114, 0, 22));
        entitlement.setExpirationTime(new Date(114, 0, 28));
        entitlement.setItemId(String.valueOf(idGenerator.nextId()));
        ItemRevision item = new ItemRevision();
        item.setItemId(entitlement.getItemId());
        List<EntitlementDef> defs = new ArrayList<>();
        EntitlementDef def = new EntitlementDef();
        def.setConsumable(false);
        defs.add(def);
        item.setEntitlementDefs(defs);
        item.setBinaries(new HashMap<String, Binary>());
        CommonCache.ITEM_REVISION.put(item.getItemId(), item);
        return entitlement;
    }

    @Override
    @BeforeClass(alwaysRun = true, dependsOnMethods = "springTestContextBeforeTestClass")
    protected void springTestContextPrepareTestInstance() throws Exception {
        super.springTestContextPrepareTestInstance();
        AuthorizeServiceImpl authorizeService = (AuthorizeServiceImpl) applicationContext.getBean(AuthorizeService.class);
        authorizeService.setDisabled(true);
        AuthorizeContext.setAuthorizeDisabled(true);
        ResourceScopeValidatorImpl resourceScopeValidator = (ResourceScopeValidatorImpl) applicationContext.getBean(ResourceScopeValidator.class);
        resourceScopeValidator.setDisabled(true);
    }

    @Test(enabled = false)
    //make sure the url and key info are valid and just check whether the url generated is valid
    public void testGenerateUrl() throws IOException, URISyntaxException, InvalidKeySpecException {
        //   String url = "http://static.oculusvr.com/uploads/14013776640911fhvo9od2t9-pc.zip";
        String url = "https://d1aifagf6hhneo.cloudfront.net/binaries/sr51r1VTfeqZFaFF0ZXy_SpotifyInstaller.zip";
        String result = generatePreSignedDownloadUrl(url, "xx", "1.0", "PC");
        System.out.println(result);
    }

    private String generatePreSignedDownloadUrl(String urlString, String filename, String version, String platform) throws IOException, URISyntaxException, InvalidKeySpecException {
        URL url = new URL(urlString);
        String domainName = url.getHost();
        String objectKey = url.getPath().substring(1);
        String extension = getExtension(objectKey);

        java.util.Date expiration = new java.util.Date();
        long milliSeconds = expiration.getTime();
        milliSeconds += 1000 * 30; // Add 30 seconds.
        expiration.setTime(milliSeconds);

        String finalFilename = null;
        if (!StringUtils.isEmpty(filename)) {
            if (!StringUtils.isEmpty(version)) {
                filename = filename + "_" + version;
            }
            filename = filename + "_" + platform;
            finalFilename = extension == null ? filename : filename + "." + extension;
        }

        if (domainName.equalsIgnoreCase("static.oculusvr.com")) {
            return generateS3Url(domainName, objectKey, finalFilename, expiration);
        }

        if (domainName.equalsIgnoreCase("d1aifagf6hhneo.cloudfront.net")) {
//            String bucketName = "ovr_ink_uploader";
            return generateCloudantFrontUrl(urlString, finalFilename, expiration);
//            return generateS3Url(bucketName, objectKey, finalFilename, expiration);
        }

        return urlString;
    }

    private String generateCloudantFrontUrl(String urlString, String filename, Date expiration) throws InvalidKeySpecException, IOException {
        //return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(CloudFrontUrlSigner.Protocol.http, "d1aifagf6hhneo.cloudfront.net", new File("E:\\junbo\\pk-APKAJ3QVRQMSFBXFINKA.pem"), "binaries/sr51r1VTfeqZFaFF0ZXy_SpotifyInstaller.zip", "APKAJ3QVRQMSFBXFINKA", expiration);

        return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
//                urlString,
                urlString + (filename == null ? "" : ("?" + "response-content-disposition=attachment;filename=\"" + filename + "\"")),
                EntitlementServiceImpl.getPrivateKeyId(), EntitlementServiceImpl.getPrivateKey(), expiration);   
    }

    private String generateS3Url(String bucketName, String objectKey, String filename, Date expiration) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, objectKey);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET);
        generatePresignedUrlRequest.setExpiration(expiration);
        if (!StringUtils.isEmpty(filename)) {
            generatePresignedUrlRequest.addRequestParameter("response-content-disposition",
                    "attachment;filename=\"" + filename + "\"");
        }
        URL downloadUrl = awsClient.generatePresignedUrl(generatePresignedUrlRequest);
        return downloadUrl.toString();
    }

    private String getExtension(String objectKey) {
        String[] parts = objectKey.split("\\.");
        if (parts.length == 0) {
            return null;
        }

        String lastPart = parts[parts.length - 1];
        if (lastPart.indexOf("/") != -1) {
            return null;
        }

        return lastPart;
    }
}
