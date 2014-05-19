/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.rest;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.resource.proxy.EntitlementDefinitionResourceClientProxy;
import com.junbo.common.error.AppErrorException;
import com.junbo.common.id.EntitlementId;
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

import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * test for entitlementClientProxy.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public class EntitlementClientProxyTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private EntitlementResourceClientProxy entitlementResourceClientProxy;
    @Autowired
    private EntitlementDefinitionResourceClientProxy entitlementDefinitionResourceClientProxy;
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    @Test(enabled = false)
    public void testCreate() throws ExecutionException, InterruptedException {
        Entitlement entitlement = buildAnEntitlement();
        entitlement = entitlementResourceClientProxy.postEntitlement(entitlement).get();
        Assert.assertNotNull(entitlement.getEntitlementId());
    }

    @Test(enabled = false)
    public void testUpdate() throws ExecutionException, InterruptedException {
        Entitlement entitlement = entitlementResourceClientProxy.postEntitlement(buildAnEntitlement()).get();
        Date now = new Date();
        entitlement.setExpirationTime(now);
        entitlement = entitlementResourceClientProxy.updateEntitlement(new EntitlementId(entitlement.getEntitlementId()), entitlement).get();
        Assert.assertTrue(Math.abs(entitlement.getExpirationTime().getTime() - now.getTime()) <= 1000);
    }

    @Test(enabled = false)
    public void testDelete() throws ExecutionException, InterruptedException {
        Entitlement entitlement = entitlementResourceClientProxy.postEntitlement(buildAnEntitlement()).get();
        entitlementResourceClientProxy.deleteEntitlement(new EntitlementId(entitlement.getEntitlementId()));
        try {
            entitlementResourceClientProxy.getEntitlement(new EntitlementId(entitlement.getEntitlementId()));
        } catch (AppErrorException e) {
            Assert.assertEquals(e.getResponse().getStatus(), 404);
        }
    }

    @Test(enabled = false)
    public void testTransfer() throws ExecutionException, InterruptedException {
        Long targetId = idGenerator.nextId();
        Entitlement entitlement = entitlementResourceClientProxy.postEntitlement(buildAnEntitlement()).get();
        EntitlementTransfer transfer = new EntitlementTransfer();
        transfer.setEntitlementId(entitlement.getEntitlementId());
        transfer.setTargetUserId(targetId);
        Entitlement newEntitlement = entitlementResourceClientProxy.transferEntitlement(transfer).get();
        Assert.assertEquals(newEntitlement.getUserId(), targetId);
        Assert.assertNotEquals(newEntitlement.getEntitlementId(), entitlement.getEntitlementId());
        Assert.assertEquals(newEntitlement.getEntitlementDefinitionId(), entitlement.getEntitlementDefinitionId());
        Assert.assertEquals(newEntitlement.getGrantTime(), entitlement.getGrantTime());
        try {
            entitlementResourceClientProxy.getEntitlement(new EntitlementId(entitlement.getEntitlementId()));
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
        result = entitlementResourceClientProxy.searchEntitlements(searchParam, new PageMetadata()).get();
        Assert.assertEquals(result.getItems().size(), 0);
    }

    private Entitlement buildAnEntitlement() {
        Entitlement entitlement = new Entitlement();
        entitlement.setUserId(idGenerator.nextId());
        entitlement.setGrantTime(new Date());
        EntitlementDefinition definition = new EntitlementDefinition();
        definition.setEntitlementDefId(entitlement.getEntitlementDefinitionId());
        definition.setConsumable(false);
        definition.setDeveloperId(idGenerator.nextId());
        try {
            definition = entitlementDefinitionResourceClientProxy.postEntitlementDefinition(definition).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        entitlement.setEntitlementDefinitionId(definition.getEntitlementDefId());
        return entitlement;
    }
}
