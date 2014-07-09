/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.junbo.authorization.AuthorizeCallback;
import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.AuthorizeService;
import com.junbo.authorization.RightsScope;
import com.junbo.catalog.spec.enums.EntitlementType;
import com.junbo.catalog.spec.model.item.Binary;
import com.junbo.catalog.spec.model.item.EntitlementDef;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.ItemId;
import com.junbo.common.model.Results;
import com.junbo.entitlement.auth.EntitlementAuthorizeCallbackFactory;
import com.junbo.entitlement.core.EntitlementService;
import com.junbo.entitlement.db.repository.EntitlementRepository;
import com.junbo.entitlement.spec.error.AppErrors;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.langur.core.promise.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Service of Entitlement.
 */
public class EntitlementServiceImpl extends BaseService implements EntitlementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitlementService.class);
    @Autowired
    private EntitlementRepository entitlementRepository;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private EntitlementAuthorizeCallbackFactory authorizeCallbackFactory;

    @Autowired
    private AmazonS3Client awsClient;

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
                    entitlement.setBinaries(generateDownloadUrls(entitlement.getItemId()));
                }
                return entitlement;
            }
        });
    }

    @Override
    @Transactional
    public Entitlement addEntitlement(final Entitlement entitlement) {
        if (entitlement.getTrackingUuid() != null) {
            Entitlement existing = getByTrackingUuid(entitlement.getUserId(), entitlement.getTrackingUuid(), "create");
            if (existing != null) {
                return existing;
            }
        }

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
        return entitlementRepository.update(existing);
    }

    @Override
    @Transactional
    public Entitlement updateEntitlement(final String entitlementId, final Entitlement entitlement) {
        validateUpdateId(entitlementId, entitlement);
        if (entitlement.getTrackingUuid() != null) {
            Entitlement existing = getByTrackingUuid(entitlement.getUserId(), entitlement.getTrackingUuid(), "update");
            if (existing != null) {
                return existing;
            }
        }

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
                return entitlementRepository.update(existingEntitlement);
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
                return entitlementRepository.getBySearchParam(entitlementSearchParam, pageMetadata);
            }
        });
    }

    private void fillHostItemId(EntitlementSearchParam entitlementSearchParam) {
        if (entitlementSearchParam.getHostItemId() == null) {
            return;
        }
        Set<String> itemIds = itemFacade.getItemIdsByHostItemId(entitlementSearchParam.getHostItemId().getValue());
        if (CollectionUtils.isEmpty(itemIds)) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("hostItemId",
                    "there is no item with hostItemId [" + entitlementSearchParam.getHostItemId() + "]").exception();
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
    public Entitlement getByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        return entitlementRepository.getByTrackingUuid(shardMasterId, trackingUuid);
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

    private Map<String, String> generateDownloadUrls(String itemId) {
        ItemRevision itemRevision = getItem(itemId);
        validateNotNull(itemRevision, "item");

        Map<String, String> binaries = new HashMap<>();

        for (Map.Entry<String, Binary> entry : itemRevision.getBinaries().entrySet()) {
            try {
                binaries.put(entry.getKey(), generateDownloadUrl(entry.getValue().getHref()));
            } catch (MalformedURLException e) {
                String msg = "Error occurred during parsing url " + entry.getValue().getHref();
                LOGGER.error(msg, e);
                throw AppErrors.INSTANCE.errorParsingUrl(msg).exception();
            }
        }
        return binaries;
    }

    private String generateDownloadUrl(String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        String bucketName = url.getHost();
        String objectKey = url.getPath().substring(1);

        java.util.Date expiration = new java.util.Date();
        long milliSeconds = expiration.getTime();
        milliSeconds += 1000 * 30; // Add 30 seconds.
        expiration.setTime(milliSeconds);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, objectKey);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET);
        generatePresignedUrlRequest.setExpiration(expiration);

        URL downloadUrl = awsClient.generatePresignedUrl(generatePresignedUrlRequest);
        return downloadUrl.toString();
    }
}
