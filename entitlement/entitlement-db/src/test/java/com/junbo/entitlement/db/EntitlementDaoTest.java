/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db;

import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.entity.def.EntitlementStatus;
import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.db.repository.EntitlementDefinitionRepository;
import com.junbo.entitlement.db.repository.EntitlementRepository;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementDefinition;
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
    private IdGenerator idGenerator;
    @Autowired
    private EntitlementRepository entitlementRepository;
    @Autowired
    private EntitlementDefinitionRepository entitlementDefinitionRepository;

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
        Assert.assertEquals(insertedEntitlement.getOfferId(), entitlement.getOfferId());
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
        Long developerId = idGenerator.nextId();
        for (int i = 0; i < 48; i++) {
            EntitlementDefinition entitlementDefinition = buildAnEntitlementDefinitionWithoutSave();
            entitlementDefinition.setDeveloperId(developerId);
            EntitlementDefinition insertedEntitlementDefinition =
                    entitlementDefinitionRepository.insert(entitlementDefinition);
            Entitlement entitlementEntity = buildAnEntitlement();
            entitlementEntity.setUserId(userId);
            entitlementEntity.setEntitlementDefinitionId(insertedEntitlementDefinition.getEntitlementDefinitionId());
            entitlementRepository.insert(entitlementEntity);
        }

        EntitlementSearchParam searchParam = new EntitlementSearchParam();
        searchParam.setUserId(userId);
        searchParam.setDeveloperId(developerId);

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

        searchParam.setGroups(Collections.singleton("testGroup"));
        searchParam.setType(EntitlementType.DEFAULT.toString());
        searchParam.setTags(Collections.singleton("TEST_ACCESS"));
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
        Long developerId = idGenerator.nextId();
        for (int i = 0; i < 48; i++) {
            EntitlementDefinition entitlementDefinition = buildAnEntitlementDefinitionWithoutSave();
            entitlementDefinition.setDeveloperId(developerId);
            EntitlementDefinition insertedEntitlementDefinition =
                    entitlementDefinitionRepository.insert(entitlementDefinition);
            Entitlement entitlement = buildAnEntitlement();
            entitlement.setUserId(userId);
            entitlement.setEntitlementDefinitionId(insertedEntitlementDefinition.getEntitlementDefinitionId());
            entitlement.setManagedLifecycle(true);
            entitlementRepository.insert(entitlement);
        }

        EntitlementSearchParam searchParam = new EntitlementSearchParam.Builder(userId, developerId)
                .status(EntitlementStatus.ACTIVE.toString()).build();

        PageMetadata pageMetadata = new PageMetadata();
        pageMetadata.setStart(0);
        pageMetadata.setCount(50);

        List<Entitlement> list1 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list1.size(), 0);

        searchParam.setStatus(EntitlementStatus.DISABLED.toString());
        List<Entitlement> list2 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list2.size(), 48);

        EntitlementContext.setNow(new Date(114, 0, 25));
        searchParam.setStatus(EntitlementStatus.ACTIVE.toString());
        List<Entitlement> list3 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list3.size(), 48);

        EntitlementContext.setNow(new Date(114, 0, 1));
        searchParam.setStatus(EntitlementStatus.PENDING.toString());
        List<Entitlement> list4 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
        Assert.assertEquals(list4.size(), 48);
    }

    @Test
    public void testSearchEntitlementDefinition() {
        EntitlementDefinition ed = buildAnEntitlementDefinition();
        List<EntitlementDefinition> l1 = entitlementDefinitionRepository.getByParams(
                ed.getDeveloperId(), null, null, ed.getType(), new PageMetadata());
        Assert.assertTrue(l1.size() > 0);
    }

    private Entitlement buildAnEntitlement() {
        Entitlement entitlement = new Entitlement();

        entitlement.setEntitlementId(new Random().nextLong());
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
        return entitlementDefinitionRepository.insert(entitlementDefinition);
    }

    private EntitlementDefinition buildAnEntitlementDefinitionWithoutSave() {
        EntitlementDefinition entitlementDefinition = new EntitlementDefinition();
        entitlementDefinition.setTag("TEST_ACCESS");
        entitlementDefinition.setGroup("testGroup");
        entitlementDefinition.setType(EntitlementType.DEFAULT.toString());
        entitlementDefinition.setDeveloperId(idGenerator.nextId());
        entitlementDefinition.setCreatedBy("test");
        entitlementDefinition.setModifiedBy("test");
        entitlementDefinition.setCreatedTime(new Date());
        entitlementDefinition.setModifiedTime(new Date());
        return entitlementDefinition;
    }
}
