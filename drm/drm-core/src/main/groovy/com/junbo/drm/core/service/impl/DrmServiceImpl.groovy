/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.core.service.impl

import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.common.id.ItemId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.crypto.spec.model.ItemCryptoMessage
import com.junbo.crypto.spec.resource.ItemCryptoResource
import com.junbo.drm.core.service.DrmService
import com.junbo.drm.spec.error.AppErrors
import com.junbo.drm.spec.model.LicenseData
import com.junbo.drm.spec.model.SignedLicense
import com.junbo.entitlement.spec.model.Entitlement
import com.junbo.entitlement.spec.model.EntitlementSearchParam
import com.junbo.entitlement.spec.model.PageMetadata
import com.junbo.entitlement.spec.resource.EntitlementResource
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Base64
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

import java.security.MessageDigest

/**
 * drm.
 */
@CompileStatic
class DrmServiceImpl implements DrmService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DrmServiceImpl)

    private UserResource userResource;

    private ItemResource itemResource;

    private EntitlementResource entitlementResource;

    private ItemCryptoResource itemCryptoResource;

    private long defaultLicenseExpirationInSeconds;

    private long defaultRetryExpirationInSeconds;

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
    void setItemCryptoResource(ItemCryptoResource itemCryptoResource) {
        this.itemCryptoResource = itemCryptoResource
    }

    @Required
    void setDefaultLicenseExpirationInSeconds(long defaultExpirationInSeconds) {
        this.defaultLicenseExpirationInSeconds = defaultExpirationInSeconds
    }

    @Required
    void setDefaultRetryExpirationInSeconds(long defaultRetryExpirationInSeconds) {
        this.defaultRetryExpirationInSeconds = defaultRetryExpirationInSeconds
    }

    @Override
    Promise<SignedLicense> createLicense(
            UserId userId,
            String packageName,
            String versionCode,
            String nonce,
            String deviceId) {

        userResource.get(userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound().exception();
            }

            getItemByPackageName(packageName).then { Item item ->
                hasEntitlements(user.getId(), new ItemId(item.getId())).then { Boolean hasEntitlements ->

                    if (hasEntitlements) {
                        return sign(item.getId(), new LicenseData(
                                reasonCode: LicenseData.LICENSED,
                                nonce: nonce,
                                packageName: packageName,
                                versionCode: versionCode,
                                deviceId: deviceId,
                                userId: obfuscateUserId(user.getId(), packageName),
                                validUntil: System.currentTimeMillis() + defaultLicenseExpirationInSeconds * 1000,
                                retryUntil: System.currentTimeMillis() + defaultRetryExpirationInSeconds * 1000,
                        ));
                    }

                    return sign(item.getId(), new LicenseData(
                            reasonCode: LicenseData.NOT_LICENSED,
                            nonce: nonce,
                            packageName: packageName,
                            versionCode: versionCode,
                            deviceId: deviceId,
                            userId: obfuscateUserId(user.getId(), packageName),
                            validUntil: 0,
                            retryUntil: 0,
                    ))
                }
            }
        }
    }

    private Promise<Boolean> hasEntitlements(UserId userId, ItemId itemId) {

        def param = new EntitlementSearchParam(
                userId: userId,
                itemIds: [itemId] as Set,
                isActive: true
        )

        def pageMetadata = new PageMetadata()

        entitlementResource.searchEntitlements(param, pageMetadata).then { Results<Entitlement> results ->
            return Promise.pure(results != null && !results.items.empty)
        }
    }

    private Promise<Item> getItemByPackageName(String packageName) {

        ItemsGetOptions option = new ItemsGetOptions(
                packageName: packageName,
                type: ItemType.APP.toString()
        )

        return itemResource.getItems(option).then { Results<Item> itemResults ->
            if (itemResults.items.isEmpty()) {
                throw AppErrors.INSTANCE.itemNotFoundForPackageName(packageName).exception()
            }

            if (itemResults.items.size() > 1) {
                LOGGER.warn('name=IAP_Multiple_ItemsFound_With_PackgeName, packageName={}', packageName)
            }

            return Promise.pure(itemResults.items[0])
        }
    }

    private static String obfuscateUserId(UserId userId, String packageName) {

        String result = userId.value + "-" + packageName

        return Base64.encodeBase64String(MessageDigest.getInstance("SHA-256").digest(result.getBytes()))
    }

    private Promise<SignedLicense> sign(String itemId, LicenseData licenseData) {
        String jsonText = ObjectMapperProvider.instanceNoIdent().writeValueAsString(licenseData)

        itemCryptoResource.sign(itemId, new ItemCryptoMessage(message: jsonText)).then { ItemCryptoMessage itemCryptoMessage ->

            return Promise.pure(new SignedLicense(
                    reasonCode: licenseData.reasonCode,
                    payload: jsonText,
                    signature: itemCryptoMessage.message
            ))
        }
    }
}
