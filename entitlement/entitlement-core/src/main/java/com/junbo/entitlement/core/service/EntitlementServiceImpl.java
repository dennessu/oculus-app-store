/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.PEM;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.junbo.authorization.AuthorizeCallback;
import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.AuthorizeService;
import com.junbo.authorization.RightsScope;
import com.junbo.catalog.spec.enums.EntitlementType;
import com.junbo.catalog.spec.model.item.EntitlementDef;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.model.Results;
import com.junbo.entitlement.auth.EntitlementAuthorizeCallbackFactory;
import com.junbo.entitlement.core.EntitlementService;
import com.junbo.entitlement.db.repository.EntitlementRepository;
import com.junbo.entitlement.spec.error.AppErrors;
import com.junbo.entitlement.spec.model.DownloadUrlGetOptions;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.langur.core.promise.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service of Entitlement.
 */
public class EntitlementServiceImpl extends BaseService implements EntitlementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitlementService.class);
    private static final String ENTITLEMENT_SERVICE_SCOPE = "entitlement.service";
    private static final String ITEM_BINARIES_MANAGE_SCOPE = "item-binaries.manage";
    //work around for cloudfront
    private static final Map<String, String> bucketMap = new HashMap<String, String>() {{
        put("d1aifagf6hhneo.cloudfront.net", "ovr_ink_uploader");
        put("d39nlaid7cu5vo.cloudfront.net", "static.oculusvr.com");
    }};

    @Autowired
    private EntitlementRepository entitlementRepository;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private EntitlementAuthorizeCallbackFactory authorizeCallbackFactory;

    @Autowired
    private AmazonS3Client awsClient;

    private static Set<String> bucketNames;
    private static Set<String> cloudFrontDomains;
    private static Integer expirationTime;
    private static PrivateKey privateKey;
    private static String privateKeyId;

    @Override
    @Transactional
    public Entitlement getEntitlement(final String entitlementId) {
        final Entitlement entitlement = entitlementRepository.get(entitlementId);

        if (entitlement == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("entitlement", entitlementId).exception();
        }

        AuthorizeCallback callback = authorizeCallbackFactory.create(entitlement);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Entitlement>() {

            @Override
            public Entitlement apply() {
                if (!AuthorizeContext.hasRights("read")) {
                    throw AppCommonErrors.INSTANCE.resourceNotFound("entitlement", entitlementId).exception();
                }
                if (EntitlementType.DOWNLOAD.toString().equalsIgnoreCase(entitlement.getType())) {
                    entitlement.setBinaries(generateDownloadUrls(entitlement));
                }
                return entitlement;
            }
        });
    }

    @Override
    @Transactional
    public Entitlement addEntitlement(final Entitlement entitlement) {
        /**  remove byTrackingUuid to save time
        if (entitlement.getTrackingUuid() != null) {
            Entitlement existing = getByTrackingUuid(entitlement.getUserId(), entitlement.getTrackingUuid(), "create");
            if (existing != null) {
                return existing;
            }
        }
         **/

        fillCreate(entitlement);
        validateCreate(entitlement);

        AuthorizeCallback callback = authorizeCallbackFactory.create(entitlement);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Entitlement>() {

            @Override
            public Entitlement apply() {
                if (!AuthorizeContext.hasRights("create")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                return merge(entitlement);
            }
        });
    }

    private Entitlement merge(Entitlement entitlement) {
        ItemRevision item = getItem(entitlement.getItemId());
        EntitlementDef def = filter(item.getEntitlementDefs(), entitlement.getType());
        if (!def.getConsumable()) {
            return entitlementRepository.insert(entitlement);
        }

        Entitlement existing = entitlementRepository.get(entitlement.getUserId(), entitlement.getItemId(), entitlement.getType());
        if (existing == null) {
            return entitlementRepository.insert(entitlement);
        }

        existing.setIsBanned(false);
        existing.setUseCount(existing.getUseCount() + entitlement.getUseCount());
        return entitlementRepository.update(existing, existing);
    }

    @Override
    @Transactional
    public Entitlement updateEntitlement(final String entitlementId, final Entitlement entitlement) {
        validateUpdateId(entitlementId, entitlement);
        /**  remove byTrackingUuid to save time
        if (entitlement.getTrackingUuid() != null) {
            Entitlement existing = getByTrackingUuid(entitlement.getUserId(), entitlement.getTrackingUuid(), "update");
            if (existing != null) {
                return existing;
            }
        }
         **/

        final Entitlement existingEntitlement = entitlementRepository.get(entitlementId);
        if (existingEntitlement == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("entitlement", entitlementId).exception();
        }

        AuthorizeCallback callback = authorizeCallbackFactory.create(entitlement);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Entitlement>() {

            @Override
            public Entitlement apply() {
                if (!AuthorizeContext.hasRights("update")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                fillUpdate(entitlement, existingEntitlement);
                validateUpdate(entitlement, existingEntitlement);
                return entitlementRepository.update(existingEntitlement, existingEntitlement);
            }
        });
    }

    @Override
    @Transactional
    public void deleteEntitlement(final String entitlementId) {
        Entitlement existingEntitlement = entitlementRepository.get(entitlementId);
        if (existingEntitlement == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("entitlement", entitlementId).exception();
        }
        checkUser(existingEntitlement.getUserId());

        AuthorizeCallback callback = authorizeCallbackFactory.create(existingEntitlement);
        RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Void>() {

            @Override
            public Void apply() {
                if (!AuthorizeContext.hasRights("delete")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                entitlementRepository.delete(entitlementId);
                return null;
            }
        });
    }

    @Override
    @Transactional
    public Results<Entitlement> searchEntitlement(final EntitlementSearchParam entitlementSearchParam,
                                                  final PageMetadata pageMetadata) {
        validateNotNull(entitlementSearchParam.getUserId(), "userId");
        checkUser(entitlementSearchParam.getUserId().getValue());

        AuthorizeCallback callback = authorizeCallbackFactory.create(entitlementSearchParam.getUserId().getValue());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Results<Entitlement>>() {

            @Override
            public Results<Entitlement> apply() {
                if (!AuthorizeContext.hasRights("search")) {
                    Results<Entitlement> results = new Results<>();
                    results.setItems(new ArrayList<Entitlement>());
                    return results;
                }

                fillClient(entitlementSearchParam);
                fillHostItemId(entitlementSearchParam);
                checkSearchDateFormat(entitlementSearchParam);
                checkIsActiveAndIsBanned(entitlementSearchParam);
                Results<Entitlement> results = entitlementRepository.getBySearchParam(entitlementSearchParam, pageMetadata);
                for (Entitlement entitlement : results.getItems()) {
                    if (EntitlementType.DOWNLOAD.toString().equalsIgnoreCase(entitlement.getType())) {
                        entitlement.setBinaries(generateDownloadUrls(entitlement));
                    }
                }
                return results;
            }
        });
    }

    private void fillHostItemId(EntitlementSearchParam entitlementSearchParam) {
        if (entitlementSearchParam.getHostItemId() == null) {
            return;
        }
        Set<String> itemIds = itemFacade.getItemIdsByHostItemId(entitlementSearchParam.getHostItemId().getValue());
        if (CollectionUtils.isEmpty(itemIds)) {
            return;
        }
        for (String itemId : itemIds) {
            entitlementSearchParam.getItemIds().add(new ItemId(itemId));
        }
    }

    private void checkIsActiveAndIsBanned(EntitlementSearchParam entitlementSearchParam) {
        if (Boolean.TRUE.equals(entitlementSearchParam.getIsBanned()) &&
                Boolean.TRUE.equals(entitlementSearchParam.getIsActive())) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("isActive", "isActive and isSuspended can not be set to true at same time").exception();
        }
    }

    private void fillClient(EntitlementSearchParam entitlementSearchParam) {
        //TODO: get item by clientId and then set the itemIds to entitlementSearchParam.
    }

    private void checkSearchDateFormat(EntitlementSearchParam entitlementSearchParam) {
        checkDateFormat("startGrantTime", entitlementSearchParam.getStartGrantTime());
        checkDateFormat("endGrantTime", entitlementSearchParam.getEndGrantTime());
        checkDateFormat("startExpirationTime", entitlementSearchParam.getStartExpirationTime());
        checkDateFormat("endExpirationTime", entitlementSearchParam.getEndExpirationTime());
        checkDateFormat("lastModifiedTime", entitlementSearchParam.getLastModifiedTime());
    }

    @Override
    @Transactional
    public String getDownloadUrl(String itemId, DownloadUrlGetOptions options) {
        String platform = options.getPlatform();
        if (StringUtils.isEmpty(platform)) {
            throw AppCommonErrors.INSTANCE.fieldRequired("platform").exception();
        }

        ItemRevision itemRevision = null;
        if (options.getEntitlementId() != null) {
            Entitlement entitlement = this.getEntitlement(options.getEntitlementId().getValue());
            if (!itemId.equalsIgnoreCase(entitlement.getItemId())) {
                throw AppCommonErrors.INSTANCE.fieldInvalid("itemId", "itemId does not match the itemId in entitlement").exception();
            }
            itemRevision = getItem(itemId);
        } else {
            if (options.getItemRevisionId() == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired("itemRevisionId").exception();
            }
            authorizeDownloadUrl();
            itemRevision = itemFacade.getItemRevision(options.getItemRevisionId());
        }
        if (itemRevision == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid(null, "there is no matched itemRevision").exception();
        }

        if (!itemRevision.getBinaries().keySet().contains(platform)) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("platform", "there is no platform " +
                    platform + " in item "
                    + itemId + "' binaries").exception();
        }
        String urlString = itemRevision.getBinaries().get(platform).getHref();
        String version = itemRevision.getBinaries().get(platform).getVersion();
        try {
            return generatePreSignedDownloadUrl(urlString, itemRevision.getDownloadName(), version, platform);
        } catch (MalformedURLException e) {
            String msg = "Error occurred during parsing url " + urlString;
            LOGGER.error(msg, e);
            throw AppErrors.INSTANCE.errorParsingDownloadUrl(msg).exception();
        } catch (URISyntaxException e) {
            //just ignore this
            e.printStackTrace();
        }
        return null;
    }

    private Entitlement getByTrackingUuid(Long shardMasterId, UUID trackingUuid, final String requiredRight) {
        final Entitlement existing = entitlementRepository.getByTrackingUuid(shardMasterId, trackingUuid);
        if (existing == null) {
            return null;
        }

        AuthorizeCallback callback = authorizeCallbackFactory.create(existing);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Entitlement>() {

            @Override
            public Entitlement apply() {
                if (!AuthorizeContext.hasRights(requiredRight)) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                return existing;
            }
        });
    }

    private Map<String, String> generateDownloadUrls(Entitlement entitlement) {
        ItemRevision itemRevision = getItem(entitlement.getItemId());
        validateNotNull(itemRevision, "item");

        Map<String, String> binaries = new HashMap<>();

        for (String platform : itemRevision.getBinaries().keySet()) {
            binaries.put(platform, generateDownloadUrl(entitlement, platform));
        }
        return binaries;
    }

    private String generateDownloadUrl(Entitlement entitlement, String platform) {
        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("item-binary").path(entitlement.getItemId());
        builder.queryParam("entitlementId", entitlement.getId());
        builder.queryParam("platform", platform);
        return builder.toTemplate();
    }

    private String generatePreSignedDownloadUrl(String urlString, String filename, String version, String platform) throws MalformedURLException, URISyntaxException {
        try {
            urlString = URLDecoder.decode(urlString, "utf-8");
        } catch (UnsupportedEncodingException ignore) {
        }

        String bucketName = parseS3BucketName(urlString);
        if (bucketName != null) {
            Pattern s3Pattern = Pattern.compile(".*" + bucketName + "/(.*)");
            Matcher s3Matcher = s3Pattern.matcher(urlString);
            if (s3Matcher.matches()) {
                String objectKey = s3Matcher.group(1);
                return generateS3Url(bucketName, objectKey,
                        generateFilename(objectKey, filename, version, platform), generateExpirationDate());
            }
        } else if (isCloudFront(urlString)){
            //TODO: work around for cloudfront
            URL url = new URL(urlString);
            bucketName = bucketMap.get(url.getHost());
            String objectKey = url.getPath().substring(1);
            return generateS3Url(bucketName, objectKey,
                    generateFilename(objectKey, filename, version, platform), generateExpirationDate());
        }

        return urlString;
    }

    private String parseS3BucketName(String url) {
        for (String allowedBucketName : bucketNames) {
            if (url.contains(allowedBucketName)) {
                return allowedBucketName;
            }
        }
        return null;
    }

    private Boolean isCloudFront(String url) {
        return url.contains("cloudfront.net");
    }

    private String generateFilename(String objectKey, String filename, String version, String platform) {
        String extension = getExtension(objectKey);

        if (!StringUtils.isEmpty(filename)) {
            if (!StringUtils.isEmpty(version)) {
                filename = filename + "_" + version;
            }
            filename = filename + "_" + platform;
            return extension == null ? filename : filename + "." + extension;
        }

        return null;
    }

    private String getExtension(String objectKey) {
        String[] parts = objectKey.split("\\.");
        if (parts.length == 1) {
            return null;
        }

        String lastPart = parts[parts.length - 1];
        if (lastPart.indexOf("/") != -1) {
            return null;
        }

        return lastPart;
    }

    private Date generateExpirationDate() {
        java.util.Date expiration = new java.util.Date();
        long milliSeconds = expiration.getTime();
        milliSeconds += 1000 * expirationTime;
        expiration.setTime(milliSeconds);
        return expiration;
    }

    private String generateCloudantFrontUrl(String urlString, String filename, Date expiration) {
        try {
            return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                    urlString + (filename == null ? "" : ("?" + "response-content-disposition=attachment;" + URLEncoder.encode("filename=\"" + filename + "\"", "UTF-8"))),
                    privateKeyId, privateKey, expiration);
        } catch (UnsupportedEncodingException ignore) {
            return null;
        }
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

    private static void authorizeDownloadUrl() {
        if (!AuthorizeContext.hasScopes(ENTITLEMENT_SERVICE_SCOPE) && !AuthorizeContext.hasScopes(ITEM_BINARIES_MANAGE_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception();
        }
    }

    public static Set<String> getBucketNames() {
        return bucketNames;
    }

    public void setBucketNames(String bucketNames) {
        if (CollectionUtils.isEmpty(EntitlementServiceImpl.bucketNames)) {
            EntitlementServiceImpl.bucketNames = new HashSet<>(Arrays.asList(bucketNames.split(",")));
        }
    }

    public static Set<String> getCloudFrontDomains() {
        return cloudFrontDomains;
    }

    public void setCloudFrontDomains(String cloudFrontDomains) {
        if (CollectionUtils.isEmpty(EntitlementServiceImpl.cloudFrontDomains)) {
            EntitlementServiceImpl.cloudFrontDomains = new HashSet<>(Arrays.asList(cloudFrontDomains.split(",")));
        }
    }

    public static Map<String, String> getBucketMap() {
        return bucketMap;
    }

    public static Integer getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Integer expirationTime) {
        if (EntitlementServiceImpl.expirationTime == null) {
            EntitlementServiceImpl.expirationTime = expirationTime;
        }
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKeyFilePath) {
        if (EntitlementServiceImpl.privateKey == null) {
            Resource resource = null;
            PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            try {
                resource = patternResolver.getResources(privateKeyFilePath)[0];
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrivateKey privateKey = null;
            try {
                InputStream is = resource.getInputStream();
                privateKey = PEM.readPrivateKey(is);
            } catch (InvalidKeySpecException | IOException e) {
                e.printStackTrace();
            }
            assert privateKey != null;
            EntitlementServiceImpl.privateKey = privateKey;
        }
    }

    public static String getPrivateKeyId() {
        return privateKeyId;
    }

    public void setPrivateKeyId(String privateKeyId) {
        if (StringUtils.isEmpty(EntitlementServiceImpl.privateKeyId)) {
            EntitlementServiceImpl.privateKeyId = privateKeyId;
        }
    }
}
