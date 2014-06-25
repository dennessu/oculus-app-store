/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core;

import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.AuthorizeService;
import com.junbo.authorization.AuthorizeServiceImpl;
import com.junbo.catalog.spec.model.item.EntitlementDef;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.common.error.AppErrorException;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.common.cache.CommonCache;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.EntitlementTransfer;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

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
        }

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

    @Test
    public void testTransferEntitlement() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        EntitlementTransfer transfer = new EntitlementTransfer();
        transfer.setTargetUserId(idGenerator.nextId());
        transfer.setEntitlementId(addedEntitlement.getId());
        Entitlement newEntitlement = entitlementService.transferEntitlement(transfer);
        try {
            entitlementService.getEntitlement(addedEntitlement.getId());
        } catch (WebApplicationException e) {
            Assert.assertEquals(e.getResponse().getStatus(), 404);
        }
        Assert.assertEquals(newEntitlement.getUserId(), transfer.getTargetUserId());
    }

    private Entitlement buildAnEntitlement() {
        Entitlement entitlement = new Entitlement();
        entitlement.setUserId(idGenerator.nextId());
        entitlement.setGrantTime(new Date(114, 0, 22));
        entitlement.setExpirationTime(new Date(114, 0, 28));
        entitlement.setItemId(String.valueOf(idGenerator.nextId()));
        final ItemRevision item = new ItemRevision();
        item.setItemId(entitlement.getItemId());
        List<EntitlementDef> defs = new ArrayList<>();
        EntitlementDef def = new EntitlementDef();
        def.setConsumable(false);
        defs.add(def);
        item.setEntitlementDefs(defs);
        CommonCache.ITEM_REVISION.get(item.getItemId(), new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return item;
            }
        });
        return entitlement;
    }

    @Override
    @BeforeClass(alwaysRun = true, dependsOnMethods = "springTestContextBeforeTestClass")
    protected void springTestContextPrepareTestInstance() throws Exception {
        super.springTestContextPrepareTestInstance();
        AuthorizeServiceImpl authorizeService = (AuthorizeServiceImpl)applicationContext.getBean(AuthorizeService.class);
        authorizeService.setDisabled(true);
        AuthorizeContext.setAuthorizeDisabled(true);
    }
}
