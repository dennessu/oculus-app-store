/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.repository.EntitlementRepository;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
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
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Entitlement dao test.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public class EntitlementRepoTest extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;
    @Autowired
    private EntitlementRepository entitlementRepository;

    @Test
    public void testInsert() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement insertedEntitlement = entitlementRepository.insert(entitlement);
        Assert.assertEquals(insertedEntitlement.getUseCount(), entitlement.getUseCount());
    }

    @Test
    public void testUpdate() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement updatedEntitlement = entitlementRepository.insert(entitlement);
        updatedEntitlement.setIsBanned(true);
        updatedEntitlement = entitlementRepository.update(updatedEntitlement);
        Assert.assertEquals(updatedEntitlement.getIsBanned(), Boolean.TRUE);
        Assert.assertEquals(updatedEntitlement.getIsActive(), Boolean.FALSE);
    }

    @Test
    public void testSearch() {
        Long userId = idGenerator.nextId();
        for (int i = 0; i < 48; i++) {
            Entitlement entitlementEntity = buildAnEntitlement();
            entitlementEntity.setUserId(userId);
            entitlementRepository.insert(entitlementEntity);
        }

        EntitlementSearchParam searchParam = new EntitlementSearchParam();
        searchParam.setUserId(new UserId(userId));
        searchParam.setIsActive(false);

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
        searchParam.setStartGrantTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 22)));
        searchParam.setEndGrantTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 22)));
        List<Entitlement> list3 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list3.size(), 48);

        List<Entitlement> list4 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list4.size(), 48);

        searchParam.setStartGrantTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 23)));
        searchParam.setEndGrantTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 24)));
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
            entitlementRepository.insert(entitlement);
        }

        EntitlementSearchParam searchParam = new EntitlementSearchParam.Builder(new UserId(userId))
                .isActive(true).build();

        PageMetadata pageMetadata = new PageMetadata();
        pageMetadata.setStart(0);
        pageMetadata.setCount(50);

        List<Entitlement> list1 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list1.size(), 0);

        searchParam.setIsActive(false);
        List<Entitlement> list2 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list2.size(), 48);

        EntitlementContext.current().setNow(new Date(114, 0, 25));
        searchParam.setIsActive(true);
        List<Entitlement> list3 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list3.size(), 48);

        searchParam.setIsBanned(true);
        List<Entitlement> list4 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list4.size(), 0);
    }

    private Entitlement buildAnEntitlement() {
        Entitlement entitlement = new Entitlement();

        entitlement.setEntitlementId(new Random().nextLong());
        entitlement.setUserId(idGenerator.nextId());
        entitlement.setGrantTime(new Date(114, 0, 22));
        entitlement.setExpirationTime(new Date(114, 0, 28));
        entitlement.setIsBanned(false);
        entitlement.setFutureExpansion(new HashMap<String, JsonNode>());
        entitlement.setItemId(idGenerator.nextId());
        return entitlement;
    }
}
