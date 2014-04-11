/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core;

import com.junbo.common.error.AppErrorException;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.common.def.EntitlementStatusReason;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.entity.def.EntitlementStatus;
import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.EntitlementTransfer;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Entitlement service test.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
public class EntitlementServiceTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;
    @Autowired
    private EntitlementService entitlementService;

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
        Assert.assertEquals(addedEntitlement.getTag(), entitlement.getTag());
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
        Assert.assertEquals(addedEntitlement.getStatus(), EntitlementStatus.DISABLED.toString());
    }

    @Test
    public void testUpdateEntitlement() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        addedEntitlement.setUseCount(1);
        Entitlement updatedEntitlement = entitlementService.updateEntitlement(
                addedEntitlement.getEntitlementId(), addedEntitlement);
        Assert.assertEquals(updatedEntitlement.getUseCount(), (Integer)1);
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
        EntitlementContext.current().setNow(new Date(114, 1, 10));
        Long userId = idGenerator.nextId();
        for (int i = 0; i < 48; i++) {
            Entitlement entitlementEntity = buildAnEntitlement();
            entitlementEntity.setUserId(userId);
            entitlementEntity.setEntitlementDefinitionId(idGenerator.nextId());
            entitlementEntity.setExpirationTime(new Date(114, 2, 20));
            entitlementService.addEntitlement(entitlementEntity);
        }

        EntitlementContext.current().setNow(new Date(114, 2, 30));

        EntitlementSearchParam searchParam = new EntitlementSearchParam();
        searchParam.setUserId(new UserId(userId));
        searchParam.setClientId(userId.toString());
        searchParam.setStatus(EntitlementStatus.ACTIVE.toString());
        List<Entitlement> entitlements = entitlementService.searchEntitlement(searchParam, new PageMetadata());

        Assert.assertEquals(entitlements.size(), 0);
    }

    @Test
    public void testTransferEntitlement() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement addedEntitlement = entitlementService.addEntitlement(entitlement);
        EntitlementTransfer transfer = new EntitlementTransfer();
        transfer.setTargetUserId(998L);
        transfer.setEntitlementId(addedEntitlement.getEntitlementId());
        Entitlement newEntitlement = entitlementService.transferEntitlement(transfer);
        Entitlement oldEntitlement = entitlementService.getEntitlement(addedEntitlement.getEntitlementId());
        Assert.assertEquals(oldEntitlement.getStatus(), EntitlementStatus.DELETED.toString());
        Assert.assertEquals(oldEntitlement.getStatusReason(), EntitlementStatusReason.TRANSFERRED);
        Assert.assertEquals(newEntitlement.getUserId(), transfer.getTargetUserId());
    }

    private Entitlement buildAnEntitlement() {
        Entitlement entitlement = new Entitlement();

        entitlement.setUserId(idGenerator.nextId());
        entitlement.setGrantTime(new Date(114, 0, 22));
        entitlement.setExpirationTime(new Date(114, 0, 28));

        entitlement.setEntitlementDefinitionId(idGenerator.nextId());
        entitlement.setGroup("TEST");
        entitlement.setTag("TEST");
        entitlement.setType(EntitlementType.DEFAULT.toString());
        entitlement.setInAppContext(Collections.singletonList(String.valueOf(idGenerator.nextId())));
        return entitlement;
    }
}
