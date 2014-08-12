/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.drm;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.entitlement.impl.EntitlementServiceImpl;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.entitlement.EntitlementService;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.enums.EntitlementType;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.drm.spec.model.LicenseRequest;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.drm.spec.model.SignedLicense;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.drm.impl.DrmServiceImpl;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * @author Jason
 * Time: 7/28/2014
 * For testing drm APIs
 */
public class DrmTesting extends TestClass {

    private LogHelper logger = new LogHelper(DrmTesting.class);

    User user;
    Item item;
    OrganizationId organizationId;

    @Property(
            priority = Priority.BVT,
            features = "Post v1/licenses",
            component = Component.Drm,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test post a licence",
            steps = {
                    "Post a license and verify getting the signed response"
            }
    )
    @Test
    public void testPostLicense() throws Exception {
        this.prepareTestData();

        LicenseRequest request = new LicenseRequest();

        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();
        ItemRevision itemRevision = itemRevisionService.getItemRevision(item.getCurrentRevisionId());
        request.setPackageName(itemRevision.getPackageName());
        request.setVersionCode(RandomFactory.getRandomStringOfNumeric(3));

        request.setNonce(RandomFactory.getRandomStringOfAlphabet(10));
        request.setDeviceId(RandomFactory.getRandomStringOfAlphabetOrNumeric(10));

        DrmService drmService = DrmServiceImpl.instance();
        SignedLicense signedLicense = drmService.postLicense(request);
        Assert.assertNotNull(signedLicense.getSignature());
    }

    private void prepareTestData() throws Exception {
        UserService userService = UserServiceImpl.instance();
        ItemService itemService = ItemServiceImpl.instance();
        OAuthService oAuthService = OAuthServiceImpl.getInstance();
        EntitlementService entitlementService = EntitlementServiceImpl.instance();
        OrganizationService organizationService = OrganizationServiceImpl.instance();

        //prepare user and organization
        String userId = userService.PostUser();
        user = Master.getInstance().getUser(userId);
        organizationId = organizationService.postDefaultOrganization(userId).getId();

        //prepare item
        item = itemService.postDefaultItem(CatalogItemType.APP, organizationId);

        item = releaseItem(item);

        //prepare entitlement
        Entitlement entitlement = new Entitlement();
        entitlement.setUserId(user.getId().getValue());
        entitlement.setType(EntitlementType.DOWNLOAD.name());
        entitlement.setItemId(item.getItemId());

        oAuthService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.DRM);
        oAuthService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.ENTITLEMENT);
        entitlementService.grantEntitlement(entitlement);

    }

    protected Item releaseItem(Item item) throws Exception {
        ItemService itemService = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        OAuthService oAuthTokenService = OAuthServiceImpl.getInstance();
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOGADMIN);

        //Attach item revision to the item
        ItemRevision itemRevision = itemRevisionService.postDefaultItemRevision(item);

        //Approve the item revision
        itemRevision.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        return itemService.getItem(item.getItemId());
    }

}
