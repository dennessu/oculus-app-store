package com.junbo.token.db;


import com.junbo.sharding.IdGenerator;
import com.junbo.token.db.dao.*;
import com.junbo.token.db.entity.*;
import com.junbo.token.spec.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;


public class TokenDaoTest extends BaseTest {
    @Autowired
    private TokenConsumptionDao tokenConsumptionDao;
    @Autowired
    private TokenSetDao tokenSetDao;
    @Autowired
    private TokenItemDao tokenItemDao;
    @Autowired
    private TokenOrderDao orderDao;
    @Autowired
    private TokenSetOfferDao tokenSetOfferDao;

    @Test(enabled = true)
    public void testCreate() {
        TokenSetEntity set = buildTokenSetRequest();
        tokenSetDao.save(set);
        TokenSetOfferEntity setOffer = buildTokenSetOfferRequest(set);
        tokenSetOfferDao.save(setOffer);
        TokenOrderEntity order = buildTokenOrderRequest(set);
        orderDao.save(order);
        TokenItemEntity item = buildTokenItemRequest(order);
        tokenItemDao.save(item);
        TokenConsumptionEntity consumption = buildTokenItemRequest(item);
        tokenConsumptionDao.save(consumption);
        Assert.assertNotNull(set.getId(), "set Entity id should not be null.");
        Assert.assertNotNull(setOffer.getId(), "set offer Entity id should not be null.");
        Assert.assertNotNull(order.getId(), "order Entity id should not be null.");
        Assert.assertNotNull(item.getId(), "item Entity id should not be null.");
        Assert.assertNotNull(consumption.getId(), "consumption Entity id should not be null.");
    }

    protected TokenSetOfferEntity buildTokenSetOfferRequest(TokenSetEntity set){
        TokenSetOfferEntity entity = new TokenSetOfferEntity();
        entity.setId(generateId());
        entity.setProductId(String.valueOf(generateId()));
        entity.setProductType(ProductType.OFFER);
        entity.setTokenSetId(set.getId());
        return entity;
    }

    protected TokenSetEntity buildTokenSetRequest(){
        TokenSetEntity entity = new TokenSetEntity();
        entity.setId(generateId());
        entity.setDescription("ut");
        entity.setStatus(SetStatus.ACTIVE);
        entity.setGenerationLength(TokenLength.LEN16);
        return entity;
    }

    protected TokenOrderEntity buildTokenOrderRequest(TokenSetEntity set){
        TokenOrderEntity entity = new TokenOrderEntity();
        entity.setId(generateId());
        entity.setDescription("ut");
        entity.setActivation("Yes");
        entity.setStatus(OrderStatus.COMPLETED);
        entity.setCreateMethod(CreateMethod.GENERATION);
        entity.setExpiredTime(new Date());
        entity.setQuantity(100L);
        entity.setTokenSetId(set.getId());
        entity.setUsageLimit("1");
        return entity;
    }

    protected TokenItemEntity buildTokenItemRequest(TokenOrderEntity order){
        TokenItemEntity entity = new TokenItemEntity();
        entity.setHashValue(generateLong());
        entity.setStatus(ItemStatus.ACTIVATED);
        entity.setOrderId(order.getId());
        entity.setId(generateId());
        return entity;
    }

    protected TokenConsumptionEntity buildTokenItemRequest(TokenItemEntity item){
        TokenConsumptionEntity entity = new TokenConsumptionEntity();
        entity.setItemId(idGenerator.nextId());
        entity.setProduct("123");
        entity.setUserId(generateId());
        return entity;
    }
}