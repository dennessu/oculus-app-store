/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core;

import com.junbo.common.error.AppErrorException;
import com.junbo.entitlement.common.def.EntitlementStatusReason;
import com.junbo.entitlement.common.lib.CloneUtils;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.entity.def.EntitlementStatus;
import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.spec.model.*;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

/**
 * Entitlement service test.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
public class EntitlementServiceTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private EntitlementService entitlementService;
    @Autowired
    private EntitlementDefinitionService entitlementDefinitionService;

    @Override
    @Autowired
    @Qualifier("entitlementDataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Test
    public void testAddEntitlement() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        Assert.assertEquals(addedEntitlement.getOfferId(), entitlement.getOfferId());
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
        entitlement.setManagedLifecycle(true);
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        Assert.assertEquals(addedEntitlement.getStatus(), EntitlementStatus.DISABLED.toString());
    }

    @Test
    public void testUpdateEntitlement() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        addedEntitlement.setStatus(EntitlementStatus.BANNED.toString());
        addedEntitlement.setStatusReason("CHEAT");
        Entitlement updatedEntitlement = entitlementService.updateEntitlement(
                addedEntitlement.getEntitlementId(), addedEntitlement);
        Assert.assertEquals(updatedEntitlement.getStatus(), EntitlementStatus.BANNED.toString());
    }

    @Test
    public void testDeleteEntitlement() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        entitlementService.deleteEntitlement(addedEntitlement.getEntitlementId(), "TEST");
        Entitlement deletedEntitlement = entitlementService.getEntitlement(addedEntitlement.getEntitlementId());
        Assert.assertEquals(deletedEntitlement.getStatus(), EntitlementStatus.DELETED.toString());
        Assert.assertEquals(deletedEntitlement.getStatusReason(), "TEST");
    }

    @Test
    public void testSearchEntitlements() {
        EntitlementContext.setNow(new Date(114, 1, 10));
        Long userId = idGenerator.nextId();
        for (int i = 0; i < 48; i++) {
            Entitlement entitlementEntity = buildAnEntitlement();
            entitlementEntity.setUserId(userId);
            entitlementEntity.setManagedLifecycle(true);
            entitlementEntity.setEntitlementDefinitionId(buildAnEntitlementDefinition().getEntitlementDefinitionId());
            entitlementEntity.setExpirationTime(new Date(114, 2, 20));
            entitlementService.addEntitlement(entitlementEntity);
        }

        EntitlementContext.setNow(new Date(114, 2, 30));

        EntitlementSearchParam searchParam = new EntitlementSearchParam();
        searchParam.setUserId(userId);
        searchParam.setDeveloperId(userId);
        searchParam.setStatus(EntitlementStatus.ACTIVE.toString());
        List<Entitlement> entitlements = entitlementService.searchEntitlement(searchParam, new PageMetadata());

        Assert.assertEquals(entitlements.size(), 0);
    }

    @Test
    public void testTransferEntitlement() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        EntitlementTransfer transfer = new EntitlementTransfer();
        transfer.setUserId(entitlement.getUserId());
        transfer.setTargetUserId(998L);
        transfer.setEntitlementId(addedEntitlement.getEntitlementId());
        Entitlement newEntitlement = entitlementService.transferEntitlement(transfer);
        Entitlement oldEntitlement = entitlementService.getEntitlement(addedEntitlement.getEntitlementId());
        Assert.assertEquals(oldEntitlement.getStatus(), EntitlementStatus.DELETED.toString());
        Assert.assertEquals(oldEntitlement.getStatusReason(), EntitlementStatusReason.TRANSFERRED);
        Assert.assertEquals(newEntitlement.getUserId(), transfer.getTargetUserId());
        Assert.assertEquals(newEntitlement.getOfferId(), entitlement.getOfferId());
    }

    @Test
    public void testStackableEntitlement() {
        Entitlement entitlement = buildAnEntitlement();
        entitlement.setManagedLifecycle(true);
        entitlement.setConsumable(true);
        entitlement.setUseCount(0);
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        Assert.assertEquals(addedEntitlement.getStatus(), EntitlementStatus.DISABLED.toString());

        Entitlement e1 = CloneUtils.clone(addedEntitlement);
        e1.setUseCount(20);
        Entitlement entitlement1 = entitlementService.addEntitlement(e1);
        Assert.assertEquals(addedEntitlement.getEntitlementId(), entitlement1.getEntitlementId());
        Assert.assertEquals(entitlement1.getStatus(), EntitlementStatus.DISABLED.toString());
        Assert.assertEquals(entitlement1.getUseCount().intValue(), 20);

        Entitlement e2 = CloneUtils.clone(e1);
        e2.setUseCount(20);
        e2.setExpirationTime(new Date(814, 0, 22));
        Entitlement entitlement2 = entitlementService.addEntitlement(e2);
        Assert.assertEquals(addedEntitlement.getEntitlementId(), entitlement2.getEntitlementId());
        Assert.assertEquals(entitlement2.getStatus(), EntitlementStatus.ACTIVE.toString());
        Assert.assertEquals(entitlement2.getUseCount().intValue(), 40);
    }

    @Test
    public void testUpdateUsedEntitlementDefinition() {
        Entitlement entitlement = buildAnEntitlement();
        entitlement = entitlementService.addEntitlement(entitlement);
        EntitlementDefinition entitlementDefinition = entitlementDefinitionService.getEntitlementDefinition(entitlement.getEntitlementDefinitionId());
        entitlementDefinition.setGroup("ANOTHER_GROUP");
        try {
            entitlementDefinitionService.updateEntitlementDefinition(entitlementDefinition.getEntitlementDefinitionId(), entitlementDefinition);
        } catch (Exception e) {
            Assert.assertEquals(e.getClass(), AppErrorException.class);
        }

        entitlement.setStatus("BANNED");
        entitlementService.updateEntitlement(entitlement.getEntitlementId(), entitlement);
        entitlementDefinition = entitlementDefinitionService.updateEntitlementDefinition(entitlementDefinition.getEntitlementDefinitionId(), entitlementDefinition);
        Assert.assertEquals(entitlementDefinition.getGroup(), "ANOTHER_GROUP");
    }

    private Entitlement buildAnEntitlement() {
        Entitlement entitlement = new Entitlement();

        entitlement.setUserId(idGenerator.nextId());
        entitlement.setConsumable(false);
        entitlement.setGrantTime(new Date(114, 0, 22));
        entitlement.setExpirationTime(new Date(114, 0, 28));

        entitlement.setEntitlementDefinitionId(buildAnEntitlementDefinition().getEntitlementDefinitionId());
        entitlement.setOfferId(idGenerator.nextId());
        entitlement.setStatus(EntitlementStatus.ACTIVE.toString());
        entitlement.setUseCount(0);
        entitlement.setCreatedBy("test");
        entitlement.setModifiedBy("test");
        entitlement.setCreatedTime(new Date());
        entitlement.setModifiedTime(new Date());
        entitlement.setManagedLifecycle(false);
        return entitlement;
    }

    private EntitlementDefinition buildAnEntitlementDefinition() {
        EntitlementDefinition entitlementDefinition = new EntitlementDefinition();
        entitlementDefinition.setTag("TEST_ACCESS");
        entitlementDefinition.setGroup("testGroup");
        entitlementDefinition.setType(EntitlementType.DEFAULT.toString());
        entitlementDefinition.setDeveloperId(idGenerator.nextId());
        entitlementDefinition.setCreatedBy("test");
        entitlementDefinition.setModifiedBy("test");
        entitlementDefinition.setCreatedTime(new Date());
        entitlementDefinition.setModifiedTime(new Date());
        return entitlementDefinitionService.addEntitlementDefinition(entitlementDefinition);
    }
}
