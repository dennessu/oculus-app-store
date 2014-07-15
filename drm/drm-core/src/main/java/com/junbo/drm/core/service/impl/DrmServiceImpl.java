/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.core.service.impl;

import com.junbo.catalog.spec.enums.EntitlementType;
import com.junbo.catalog.spec.enums.ItemType;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.drm.core.service.DrmService;
import com.junbo.drm.spec.error.AppErrors;
import com.junbo.drm.spec.model.Entitlement;
import com.junbo.drm.spec.model.License;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.option.model.UserGetOptions;
import com.junbo.identity.spec.v1.resource.UserResource;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.util.*;

/**
 * drm.
 */
public class DrmServiceImpl implements DrmService {
    private static final int DEFAULT_PAGE_SIZE = 50;

    private UserResource userResource;

    private ItemResource itemResource;

    private EntitlementResource entitlementResource;

    private long defaultLicenseExpiration;

    @Required
    public void setUserResource(UserResource userResource) {
        this.userResource = userResource;
    }

    @Required
    public void setItemResource(ItemResource itemResource) {
        this.itemResource = itemResource;
    }

    @Required
    public void setEntitlementResource(EntitlementResource entitlementResource) {
        this.entitlementResource = entitlementResource;
    }

    @Required
    public void setDefaultLicenseExpiration(long defaultLicenseExpiration) {
        this.defaultLicenseExpiration = defaultLicenseExpiration;
    }

    @Override
    public License createLicense(UserId userId, ItemId itemId, String machineHash) {
        Assert.notNull(userId, "userId is null");
        Assert.notNull(itemId, "itemId is null");

        User user = userResource.get(userId, new UserGetOptions()).syncGet();
        if (user == null) {
            throw AppErrors.INTSTANCE.userNotFound(userId).exception();
        }

        Item item = itemResource.getItem(itemId.getValue()).syncGet();
        if (!ItemType.APP.is(item.getType())) {
            throw AppErrors.INTSTANCE.invalidItemType(item.getType()).exception();
        }

        List<Entitlement> entitlements = searchEntitlement(userId, itemId);
        boolean hasDownload = false;
        for (Entitlement entitlement : entitlements) {
            if (entitlement.getItemId().equals(itemId) && EntitlementType.DOWNLOAD.is(entitlement.getType())) {
                hasDownload = true;
                break;
            }
        }

        if (!hasDownload) {
           throw AppErrors.INTSTANCE.noDownloadEntitlement(userId, itemId).exception();
        }

        License license = new License();
        license.setUserId(userId.getValue());
        license.setItemId(itemId.getValue());
        license.setExpirationTime(new Date(System.currentTimeMillis() + defaultLicenseExpiration * 1000));
        license.setEntitlements(entitlements);
        return license;
    }

    private List<Entitlement> searchEntitlement(UserId userId, ItemId itemId) {
        EntitlementSearchParam param = new EntitlementSearchParam();
        param.setUserId(userId);
        Set<ItemId> itemIds = new HashSet<>();
        itemIds.add(itemId);
        param.setItemIds(itemIds);
        param.setHostItemId(itemId);
        param.setIsActive(true);

        int start = 0;
        PageMetadata pageMetadata = new PageMetadata();
        pageMetadata.setStart(start);
        pageMetadata.setCount(DEFAULT_PAGE_SIZE);

        List<Entitlement> entitlements = new ArrayList<>();

        Results<com.junbo.entitlement.spec.model.Entitlement> results = entitlementResource.searchEntitlements(param, pageMetadata).syncGet();
        for (com.junbo.entitlement.spec.model.Entitlement entitlement : results.getItems()) {
            entitlements.add(wrapEntitlement(entitlement));
        }

        while (results.hasNext()) {
            start += DEFAULT_PAGE_SIZE;
            pageMetadata.setStart(start);
            results = entitlementResource.searchEntitlements(param, pageMetadata).syncGet();
            for (com.junbo.entitlement.spec.model.Entitlement entitlement : results.getItems()) {
                entitlements.add(wrapEntitlement(entitlement));
            }
        }

        return entitlements;
    }

    private static Entitlement wrapEntitlement(com.junbo.entitlement.spec.model.Entitlement entitlement) {
        Entitlement ent = new Entitlement();
        ent.setId(new EntitlementId(entitlement.getId()));
        ent.setItemId(new ItemId(entitlement.getItemId()));
        ent.setType(entitlement.getType());
        ent.setGrantTime(entitlement.getGrantTime());
        ent.setExpirationTime(entitlement.getExpirationTime());

        return ent;
    }
}
