/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.common.id.OrderItemId;
import com.junbo.common.id.SubledgerId;
import com.junbo.common.id.SubledgerItemId;
import com.junbo.order.db.common.TestHelper;
import com.junbo.order.db.dao.SubledgerItemDao;
import com.junbo.order.db.entity.SubledgerItemEntity;
import com.junbo.order.spec.model.Subledger;
import com.junbo.order.spec.model.SubledgerItem;
import com.junbo.order.spec.model.enums.SubledgerItemStatus;
import com.junbo.sharding.ShardAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Created by LinYi on 2/11/14.
 */
public class SubledgerItemDaoTest extends BaseTest {
    @Autowired
    private SubledgerItemDao subledgerItemDao;

    @Autowired
    @Qualifier("userShardAlgorithm")
    private ShardAlgorithm shardAlgorithm;

    @Test
    public void testCreateAndRead() {
        SubledgerItemEntity entity = TestHelper.generateSubledgerItemEntity();
        entity.setSubledgerId(idGenerator.nextId(SubledgerId.class));
        entity.setSubledgerItemId(idGenerator.nextId(SubledgerItemId.class, entity.getSubledgerId()));
        Long id = subledgerItemDao.create(entity);
        SubledgerItemEntity returnedEntity = subledgerItemDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getStatus(), entity.getStatus());
        Assert.assertEquals(returnedEntity.getSubledgerItemId(), entity.getSubledgerItemId(),
                "The subledger item Id should not be different.");
    }

    @Test
    public void testUpdate() {
        SubledgerItemEntity entity = TestHelper.generateSubledgerItemEntity();
        entity.setSubledgerId(idGenerator.nextId(SubledgerId.class));
        entity.setSubledgerItemId(idGenerator.nextId(SubledgerItemId.class, entity.getSubledgerId()));
        Long id = subledgerItemDao.create(entity);
        entity.setUpdatedBy(123L);
        subledgerItemDao.update(entity);
        SubledgerItemEntity returnedEntity = subledgerItemDao.read(id);

        Assert.assertNotNull(returnedEntity, "Fail to create or read entity.");
        Assert.assertEquals(returnedEntity.getUpdatedBy(), entity.getUpdatedBy(),
                "The UpdatedBy field should not be different.");
    }

    @Test
    public void testGetByStatus() {
       /* SubledgerItemEntity entity = TestHelper.generateSubledgerItemEntity();
        entity.setSubledgerId(idGenerator.nextId(SubledgerId.class));
        entity.setSubledgerItemId(idGenerator.nextId(SubledgerItemId.class, entity.getSubledgerId()));
        subledgerItemDao.create(entity);
        Assert.assertEquals(subledgerItemDao.getByStatus(shardAlgorithm.dataCenterId(entity.getId()), shardAlgorithm.shardId(entity.getId()),
                entity.getStatus(), 0, 1).size(), 1);
        for (SubledgerItemEntity itemEntity : subledgerItemDao.getByStatus(shardAlgorithm.dataCenterId(entity.getId()), shardAlgorithm.shardId(entity.getId()),
                entity.getStatus(), 0, 1)) {
            Assert.assertEquals(itemEntity.getStatus(), entity.getStatus());
        }*/
    }

    @Test
    public void testGetSubledgerItems() {
        long orderItemId = idGenerator.nextId(OrderItemId.class);
        int dcId = shardAlgorithm.dataCenterId(orderItemId);
        int shardId = shardAlgorithm.shardId(orderItemId);
        Long time = System.currentTimeMillis();
        Date createdTime = new Date(System.currentTimeMillis() - 1000);
        List<String> offerIds = new ArrayList<>();
        for (int i = 0;i < 10;++i) {
            for (int j = 0; j < 5;++j) {
                SubledgerItemEntity entity = TestHelper.generateSubledgerItemEntity();
                entity.setStatus(SubledgerItemStatus.PENDING_PROCESS);
                entity.setSubledgerItemId(idGenerator.nextId(SubledgerItemId.class, orderItemId));
                entity.setOfferId("testOffer_" + time.toString() + "_" + i);
                offerIds.add(entity.getOfferId());
                entity.setCreatedTime(createdTime);
                subledgerItemDao.create(entity);
            }
        }

        List<String> offerIdResults = subledgerItemDao.getDistrictOfferIds(dcId, shardId, SubledgerItemStatus.PENDING_PROCESS, 0, 4);
        Assert.assertEquals(offerIdResults.size(), 4);
        verifyOfferDistinct(offerIdResults);

        offerIdResults = subledgerItemDao.getDistrictOfferIds(dcId, shardId, SubledgerItemStatus.PENDING_PROCESS, 0, 10);
        Assert.assertEquals(offerIdResults.size(), 10);
        verifyOfferDistinct(offerIdResults);

        for (int i = 0;i < 4;++i) {
            String offerId = offerIds.get(i);
            List<SubledgerItemEntity> subledgerItemEntityList = subledgerItemDao.getByStatusOfferIdCreatedTime(
                    dcId, shardId, SubledgerItemStatus.PENDING_PROCESS, offerId, new Date(), 0, 2);
            Assert.assertEquals(subledgerItemEntityList.size(), 2);
            verifySubledgerItems(subledgerItemEntityList, offerId, SubledgerItemStatus.PENDING_PROCESS);

            subledgerItemEntityList = subledgerItemDao.getByStatusOfferIdCreatedTime(
                    dcId, shardId, SubledgerItemStatus.PENDING_PROCESS, offerId, new Date(), 0, 10);
            Assert.assertEquals(subledgerItemEntityList.size(), 5);
            verifySubledgerItems(subledgerItemEntityList, offerId, SubledgerItemStatus.PENDING_PROCESS);

            subledgerItemEntityList = subledgerItemDao.getByStatusOfferIdCreatedTime(
                    dcId, shardId, SubledgerItemStatus.PENDING_PROCESS, offerId, new Date(), 4, 2);
            Assert.assertEquals(subledgerItemEntityList.size(), 1);
            verifySubledgerItems(subledgerItemEntityList, offerId, SubledgerItemStatus.PENDING_PROCESS);

            subledgerItemEntityList = subledgerItemDao.getByStatusOfferIdCreatedTime(
                    dcId, shardId, SubledgerItemStatus.PENDING_PROCESS, offerId, new Date(createdTime.getTime() - 100000), 0, 2);
            Assert.assertEquals(subledgerItemEntityList.size(), 0);

            subledgerItemEntityList = subledgerItemDao.getByStatusOfferIdCreatedTime(
                    dcId, shardId, SubledgerItemStatus.PENDING_PROCESS, offerId + "abesd", new Date(), 0, 2);
            Assert.assertEquals(subledgerItemEntityList.size(), 0);

            subledgerItemEntityList = subledgerItemDao.getByStatusOfferIdCreatedTime(
                    dcId, shardId, SubledgerItemStatus.PROCESSED, offerId, new Date(), 0, 2);
            Assert.assertEquals(subledgerItemEntityList.size(), 0);
        }
    }

    void verifyOfferDistinct(List<String> list) {
        Set<String> offerIds = new HashSet<>();
        for (String offerId: list) {
            Assert.assertTrue(!offerIds.contains(offerId));
            offerIds.add(offerId);
        }
    }

    void verifySubledgerItems(List<SubledgerItemEntity> subledgerItems, String offerId, SubledgerItemStatus status) {
        for (SubledgerItemEntity subledgerItem : subledgerItems) {
            if (offerId != null) {
                Assert.assertEquals(subledgerItem.getOfferId(), offerId);
            }
            if (status != null) {
                Assert.assertEquals(subledgerItem.getStatus(), status);
            }
        }
    }
}
