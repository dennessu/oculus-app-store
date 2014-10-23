/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.entitlement.common.def.EntitlementConsts;
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
        Assert.assertEquals(Boolean.FALSE, insertedEntitlement.getIsActive());
    }

    @Test
    public void testUpdate() {
        Entitlement entitlement = buildAnEntitlement();
        Entitlement updatedEntitlement = entitlementRepository.insert(entitlement);
        updatedEntitlement.setIsBanned(true);
        updatedEntitlement = entitlementRepository.update(updatedEntitlement, updatedEntitlement);
        Assert.assertEquals(updatedEntitlement.getIsBanned(), Boolean.TRUE);
        Assert.assertEquals(updatedEntitlement.getIsActive(), Boolean.FALSE);
    }

    // test search for SQL
    @Test(enabled = false)
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

        List<Entitlement> list1 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        Assert.assertEquals(list1.size(), 25);

        pageMetadata.setStart(25);
        List<Entitlement> list2 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        Assert.assertEquals(list2.size(), 23);

        pageMetadata.setStart(0);
        pageMetadata.setCount(100);
        searchParam.setStartGrantTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 22)));
        searchParam.setEndGrantTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 22)));
        List<Entitlement> list3 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        Assert.assertEquals(list3.size(), 48);

        searchParam.setStartGrantTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 23)));
        searchParam.setEndGrantTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 24)));
        List<Entitlement> list5 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        Assert.assertEquals(list5.size(), 0);
    }

    //test for cloudant search
    @Test(enabled = true)
    public void testCloudantSearch(){
        Date now = new Date();
        Long userId = idGenerator.nextId();
        for (int i = 0; i < 4; i++) {
            Entitlement entitlementEntity = buildAnEntitlement();
            entitlementEntity.setUserId(userId);
            entitlementRepository.insert(entitlementEntity);
        }
        Entitlement entitlementEntity = buildAnEntitlement();
        entitlementEntity.setUserId(userId);
        entitlementEntity.setExpirationTime(null);
        entitlementEntity.setUseCount(1);
        entitlementRepository.insert(entitlementEntity);
        entitlementEntity.setUseCount(0);
        entitlementRepository.insert(entitlementEntity);

        EntitlementSearchParam searchParam = new EntitlementSearchParam();
        searchParam.setUserId(new UserId(userId));
        searchParam.setIsActive(false);

        PageMetadata pageMetadata = new PageMetadata();
        pageMetadata.setCount(3);

        //test page
        Results<Entitlement> list1 = entitlementRepository.getBySearchParam(searchParam, pageMetadata);
//        Assert.assertEquals(list1.getItems().size(), 3);
        for(Entitlement entitlement : list1.getItems()){
            Assert.assertEquals(Boolean.FALSE, entitlement.getIsActive());
        }

        pageMetadata.setBookmark(list1.getNext().getHref());
        List<Entitlement> list2 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        //disable this temporally
//        Assert.assertEquals(list2.size(), 2);

        //test isBanned
        pageMetadata.setBookmark(null);
        searchParam.setIsBanned(true);
        List<Entitlement> list7 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        Assert.assertEquals(list7.size(), 0);

        //test isActive true
        searchParam.setIsActive(true);
        searchParam.setIsBanned(null);
        pageMetadata.setCount(null);
        List<Entitlement> list8 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        Assert.assertEquals(list8.size(), 1);
        for(Entitlement entitlement : list8){
            Assert.assertEquals(Boolean.TRUE, entitlement.getIsActive());
        }

        //test type
        searchParam.setType("download");
        List<Entitlement> list9 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        Assert.assertEquals(list9.size(), 0);
        searchParam.setType("null");
        List<Entitlement> list10 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        Assert.assertEquals(list10.size(), 1);

        //test grantTime range
        pageMetadata.setCount(100);
        searchParam.setIsActive(false);
        searchParam.setStartGrantTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 20)));
        searchParam.setEndGrantTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 25)));
        List<Entitlement> list3 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        Assert.assertEquals(list3.size(), 5);

        //test expirationTime range
        searchParam.setStartExpirationTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 27)));
        searchParam.setEndExpirationTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 29)));
        List<Entitlement> list4 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        Assert.assertEquals(list4.size(), 4);

        //test lastModifiedTime
        searchParam.setLastModifiedTime(EntitlementConsts.DATE_FORMAT.format(now));
        List<Entitlement> list5 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        Assert.assertEquals(list5.size(), 4);

        //test grantTime range
        searchParam.setStartGrantTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 23)));
        searchParam.setEndGrantTime(EntitlementConsts.DATE_FORMAT.format(new Date(114, 0, 24)));
        List<Entitlement> list6 = entitlementRepository.getBySearchParam(searchParam, pageMetadata).getItems();
        Assert.assertEquals(list6.size(), 0);
    }

    private Entitlement buildAnEntitlement() {
        Entitlement entitlement = new Entitlement();
        entitlement.setUserId(idGenerator.nextId());
        entitlement.setGrantTime(new Date(114, 0, 22));
        entitlement.setExpirationTime(new Date(114, 0, 28));
        entitlement.setIsBanned(false);
        entitlement.setFutureExpansion(new HashMap<String, JsonNode>());
        entitlement.setItemId(String.valueOf(idGenerator.nextId()));
        return entitlement;
    }
}
