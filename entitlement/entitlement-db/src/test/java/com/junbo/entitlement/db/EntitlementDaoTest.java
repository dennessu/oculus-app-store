/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db;

import com.junbo.common.id.UserId;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.entity.def.EntitlementStatus;
import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.db.repository.EntitlementRepository;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
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
import java.util.Random;

/**
 * Entitlement dao test.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
public class EntitlementDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;
    @Autowired
    private EntitlementRepository entitlementRepository;

    @Override
    @Autowired
    @Qualifier("entitlementDataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Test
    public void testInsert() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement insertedEntitlement = entitlementRepository.insert(entitlement);
        Assert.assertEquals(insertedEntitlement.getGroup(), entitlement.getGroup());
    }

    @Test
    public void testUpdate() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement updatedEntitlement = entitlementRepository.insert(entitlement);
        updatedEntitlement.setStatus("BANNED");
        updatedEntitlement.setStatusReason("CHEAT");
        updatedEntitlement = entitlementRepository.update(updatedEntitlement);
        Assert.assertEquals(updatedEntitlement.getStatus(), EntitlementStatus.BANNED.toString());
        Assert.assertEquals(updatedEntitlement.getStatusReason(), "CHEAT");
    }

    @Test
    public void testSearch() {
        Long userId = idGenerator.nextId();
        String ownerId = String.valueOf(idGenerator.nextId());
        for (int i = 0; i < 48; i++) {
            Entitlement entitlementEntity = buildAnEntitlement();
            entitlementEntity.setUserId(userId);
            entitlementEntity.setOwnerId(ownerId);
            entitlementRepository.insert(entitlementEntity);
        }

        EntitlementSearchParam searchParam = new EntitlementSearchParam();
        searchParam.setUserId(new UserId(userId));
        searchParam.setOwnerId(ownerId);

        PageMetadata pageMetadata = new PageMetadata();
        pageMetadata.setStart(0);
        pageMetadata.setCount(25);

        List<Entitlement> list1 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list1.size(), 25);

        pageMetadata.setStart(25);
        List<Entitlement> list2 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list2.size(), 23);

        pageMetadata.setStart(0);
        pageMetadata.setCount(100);
        searchParam.setStartGrantTime(new Date(114, 0, 22));
        searchParam.setEndGrantTime(new Date(114, 0, 22));
        List<Entitlement> list3 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list3.size(), 48);

        searchParam.setGroups(Collections.singleton("TEST"));
        searchParam.setType(EntitlementType.DEFAULT.toString());
        searchParam.setTags(Collections.singleton("TEST"));
        List<Entitlement> list4 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list4.size(), 48);

        searchParam.setStartGrantTime(new Date(114, 0, 23));
        searchParam.setEndGrantTime(new Date(114, 0, 24));
        List<Entitlement> list5 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list5.size(), 0);
    }

    @Test
    public void testSearchManagedEntitlements() {
        Long userId = idGenerator.nextId();
        String ownerId = String.valueOf(idGenerator.nextId());
        for (int i = 0; i < 48; i++) {
            Entitlement entitlement = buildAnEntitlement();
            entitlement.setUserId(userId);
            entitlement.setOwnerId(ownerId);
            entitlementRepository.insert(entitlement);
        }

        EntitlementSearchParam searchParam = new EntitlementSearchParam.Builder(new UserId(userId), ownerId)
                .status(EntitlementStatus.ACTIVE.toString()).build();

        PageMetadata pageMetadata = new PageMetadata();
        pageMetadata.setStart(0);
        pageMetadata.setCount(50);

        List<Entitlement> list1 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list1.size(), 0);

        searchParam.setStatus(EntitlementStatus.DISABLED.toString());
        List<Entitlement> list2 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list2.size(), 48);

        EntitlementContext.current().setNow(new Date(114, 0, 25));
        searchParam.setStatus(EntitlementStatus.ACTIVE.toString());
        List<Entitlement> list3 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list3.size(), 48);

        EntitlementContext.current().setNow(new Date(114, 0, 1));
        searchParam.setStatus(EntitlementStatus.PENDING.toString());
        List<Entitlement> list4 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list4.size(), 48);
    }

    private Entitlement buildAnEntitlement() {
        Entitlement entitlement = new Entitlement();

        entitlement.setEntitlementId(new Random().nextLong());
        entitlement.setUserId(idGenerator.nextId());
        entitlement.setConsumable(false);
        entitlement.setGrantTime(new Date(114, 0, 22));
        entitlement.setExpirationTime(new Date(114, 0, 28));

        entitlement.setEntitlementDefinitionId(idGenerator.nextId());
        entitlement.setGroup("TEST");
        entitlement.setTag("TEST");
        entitlement.setType(EntitlementType.DEFAULT.toString());
        entitlement.setOwnerId(String.valueOf(idGenerator.nextId()));
        entitlement.setStatus(EntitlementStatus.ACTIVE.toString());
        return entitlement;
    }
}
