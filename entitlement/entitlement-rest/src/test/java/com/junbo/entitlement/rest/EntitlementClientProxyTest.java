/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.rest;

import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.model.item.*;
import com.junbo.catalog.spec.resource.proxy.ItemResourceClientProxy;
import com.junbo.catalog.spec.resource.proxy.ItemRevisionResourceClientProxy;
import com.junbo.common.error.AppErrorException;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.EntitlementTransfer;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.entitlement.spec.resource.proxy.EntitlementResourceClientProxy;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * test for entitlementClientProxy.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public class EntitlementClientProxyTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private EntitlementResourceClientProxy entitlementResourceClientProxy;
    @Autowired
    private ItemResourceClientProxy itemResourceClientProxy;
    @Autowired
    private ItemRevisionResourceClientProxy itemRevisionResourceClientProxy;
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    @Test(enabled = false)
    public void testCreate() throws ExecutionException, InterruptedException {
        Entitlement entitlement = buildAnEntitlement();
        entitlement = entitlementResourceClientProxy.postEntitlement(entitlement).get();
        Assert.assertNotNull(entitlement.getId());
    }

    @Test(enabled = false)
    public void testUpdate() throws ExecutionException, InterruptedException {
        Entitlement entitlement = entitlementResourceClientProxy.postEntitlement(buildAnEntitlement()).get();
        Date now = new Date();
        entitlement.setExpirationTime(now);
        entitlement = entitlementResourceClientProxy.updateEntitlement(new EntitlementId(entitlement.getId()), entitlement).get();
        Assert.assertTrue(Math.abs(entitlement.getExpirationTime().getTime() - now.getTime()) <= 1000);
    }

    @Test(enabled = false)
    public void testDelete() throws ExecutionException, InterruptedException {
        Entitlement entitlement = entitlementResourceClientProxy.postEntitlement(buildAnEntitlement()).get();
        entitlementResourceClientProxy.deleteEntitlement(new EntitlementId(entitlement.getId()));
        try {
            entitlementResourceClientProxy.getEntitlement(new EntitlementId(entitlement.getId()));
        } catch (AppErrorException e) {
            Assert.assertEquals(e.getResponse().getStatus(), 404);
        }
    }

    @Test(enabled = false)
    public void testTransfer() throws ExecutionException, InterruptedException {
        Long targetId = idGenerator.nextId();
        Entitlement entitlement = entitlementResourceClientProxy.postEntitlement(buildAnEntitlement()).get();
        EntitlementTransfer transfer = new EntitlementTransfer();
        transfer.setEntitlementId(entitlement.getId());
        transfer.setTargetUserId(targetId);
        Entitlement newEntitlement = entitlementResourceClientProxy.transferEntitlement(transfer).get();
        Assert.assertEquals(newEntitlement.getUserId(), targetId);
        Assert.assertNotEquals(newEntitlement.getId(), entitlement.getId());
        Assert.assertEquals(newEntitlement.getItemId(), entitlement.getItemId());
        Assert.assertEquals(newEntitlement.getGrantTime(), entitlement.getGrantTime());
        try {
            entitlementResourceClientProxy.getEntitlement(new EntitlementId(entitlement.getId()));
        } catch (AppErrorException e) {
            Assert.assertEquals(e.getResponse().getStatus(), 404);
        }
    }

    @Test(enabled = false)
    public void testSearch() throws ExecutionException, InterruptedException {
        Long userId = idGenerator.nextId();
        Entitlement entitlement = buildAnEntitlement();
        entitlement.setUserId(userId);
        entitlementResourceClientProxy.postEntitlement(entitlement).get();
        entitlementResourceClientProxy.postEntitlement(entitlement).get();
        EntitlementSearchParam searchParam = new EntitlementSearchParam.Builder(new UserId(userId)).isActive(true).lastModifiedTime("2014-01-01T00:00:00Z").build();
        Results<Entitlement> result = entitlementResourceClientProxy.searchEntitlements(searchParam, new PageMetadata()).get();
        Assert.assertEquals(result.getItems().size(), 2);
        searchParam.setIsBanned(true);
        searchParam.setIsActive(null);
        result = entitlementResourceClientProxy.searchEntitlements(searchParam, new PageMetadata()).get();
        Assert.assertEquals(result.getItems().size(), 0);
    }

    private Entitlement buildAnEntitlement() {
        Entitlement entitlement = new Entitlement();
        entitlement.setUserId(idGenerator.nextId());
        entitlement.setGrantTime(new Date());
        Item item = new Item();
        item.setType("DIGITAL");
        item.setOwnerId(new OrganizationId(idGenerator.nextId()));
        item.setGenres(new ArrayList<String>());
        try {
            item = itemResourceClientProxy.create(item).get();
            ItemRevision revision = itemRevisionResourceClientProxy.createItemRevision(buildAnItemRevision(item)).get();
            revision.setStatus(Status.APPROVED.name());
            itemRevisionResourceClientProxy.updateItemRevision(new ItemRevisionId(revision.getRevisionId()), revision).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        entitlement.setItemId(item.getItemId());
        return entitlement;
    }

    private ItemRevision buildAnItemRevision(Item item) {
        ItemRevision itemRevision = new ItemRevision();
        itemRevision.setOwnerId(item.getOwnerId());
        itemRevision.setItemId(item.getItemId());
        itemRevision.setStatus(Status.DRAFT.name());

        Map<String, Binary> binaries = new HashMap<>();
        Binary binary = new Binary();
        binary.setHref("xxx");
        binary.setSize(1L);
        binary.setVersion("0");
        binaries.put("default", binary);
        itemRevision.setBinaries(binaries);

        Map<String, ItemRevisionLocaleProperties> locales = new HashMap<>();
        ItemRevisionLocaleProperties locale = new ItemRevisionLocaleProperties();
        locale.setName("name");
        locales.put("default", locale);
        itemRevision.setLocales(locales);

        List<EntitlementDef> defs = new ArrayList<>();
        EntitlementDef def = new EntitlementDef();
        def.setConsumable(false);
        defs.add(def);
        itemRevision.setEntitlementDefs(defs);

        return itemRevision;
    }
}
