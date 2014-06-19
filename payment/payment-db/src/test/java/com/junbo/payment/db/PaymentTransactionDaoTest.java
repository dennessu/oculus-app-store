package com.junbo.payment.db;


import com.junbo.common.id.PIType;
import com.junbo.payment.db.dao.payment.PaymentEventDao;
import com.junbo.payment.db.dao.payment.PaymentTransactionDao;
import com.junbo.payment.db.dao.paymentinstrument.PaymentInstrumentDao;
import com.junbo.payment.db.entity.payment.PaymentEventEntity;
import com.junbo.payment.db.entity.payment.PaymentTransactionEntity;
import com.junbo.payment.db.entity.paymentinstrument.PaymentInstrumentEntity;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.enums.PaymentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class PaymentTransactionDaoTest extends BaseTest {
    @Autowired
    private PaymentInstrumentDao piDao;
    @Autowired
    private PaymentTransactionDao paymentTransactionDao;
    @Autowired
    private PaymentEventDao paymentEventDao;

    @Test
    @Transactional
    public void testCreate() {
        Long user = generateShardId();
        PaymentInstrumentEntity entity = buildRequest(user);
        piDao.save(entity);

        Assert.assertNotNull(entity.getId(), "Entity id should not be null.");
    }

    @Test
    public void testGet() {
        Long user = generateShardId();
        Long piid = generateShardId(user);
        PaymentInstrumentEntity entity = buildRequest(user);
        entity.setId(piid);
        Long piId = piDao.save(entity);
        PaymentInstrumentEntity getEntity = piDao.get(piId);
        Assert.assertNotNull(getEntity);
        Assert.assertEquals(getEntity.getId(), entity.getId());
    }

    @Test
    public void testGetByUser() {
        Long user = generateShardId();
        PaymentInstrumentEntity entity1 = buildRequest(user);
        piDao.save(entity1);
        PaymentInstrumentEntity entity2 = buildRequest(user);
        piDao.save(entity2);
        List<PaymentInstrumentEntity> getEntities = piDao.getByUserId(user);
        Assert.assertNotNull(getEntities);
        Assert.assertEquals(getEntities.size(), 2);
    }

    @Test
    public void testCreatePayment() {
        Long user = generateShardId();
        Long piid = generateShardId(user);
        PaymentInstrumentEntity entity = buildRequest(user);
        entity.setId(piid);
        piDao.save(entity);

        PaymentTransactionEntity payment = buildPaymentRequest(entity);
        payment.setId(piid);
        paymentTransactionDao.save(payment);

        PaymentEventEntity eventEntity = getPaymentEventEntity(payment);
        eventEntity.setPaymentId(piid);
        paymentEventDao.save(eventEntity);

        Assert.assertNotNull(payment.getId(), "Entity id should not be null.");
    }

    @Test
    public void testGetPayment() {
        Long user = generateShardId();
        Long piid = generateShardId(user);
        PaymentInstrumentEntity entity = buildRequest(user);
        entity.setId(piid);
        piDao.save(entity);

        PaymentTransactionEntity payment = buildPaymentRequest(entity);
        payment.setId(piid);
        paymentTransactionDao.save(payment);

        PaymentEventEntity eventEntity1 = getPaymentEventEntity(payment);
        eventEntity1.setPaymentId(piid);
        paymentEventDao.save(eventEntity1);
        PaymentEventEntity eventEntity2 = getPaymentEventEntity(payment);
        eventEntity2.setPaymentId(piid);
        paymentEventDao.save(eventEntity2);

        PaymentTransactionEntity result = paymentTransactionDao.get(payment.getId());
        Assert.assertNotNull(result, "Entity should not be null.");
        List<PaymentEventEntity> results = paymentEventDao.getByPaymentId(result.getId());
        Assert.assertEquals(results.size(), 2);
    }

    protected PaymentInstrumentEntity buildRequest(Long userId) {
        PaymentInstrumentEntity entity = new PaymentInstrumentEntity();
        entity.setAccountName("David");
        entity.setBillingAddressId(generateShardId(userId));
        entity.setIsActive(true);
        entity.setType(PIType.CREDITCARD.getId());
        entity.setUserId(userId);
        entity.setUpdatedBy("ut");
        entity.setCreatedTime(new Date());

        return entity;
    }

    protected PaymentTransactionEntity buildPaymentRequest(PaymentInstrumentEntity pi){
        PaymentTransactionEntity entity = new PaymentTransactionEntity();
        entity.setUserId(pi.getUserId());
        entity.setCreatedBy("ut");
        entity.setCurrency("USD");
        entity.setCountryCode("US");
        entity.setCreatedTime(new Date());
        entity.setNetAmount(new BigDecimal(23));
        entity.setPaymentInstrumentId(pi.getId());
        entity.setPaymentProviderId(0);
        entity.setBillingRefId("123");
        entity.setStatusId(PaymentStatus.AUTH_CREATED.getId());
        entity.setTypeId(PaymentType.AUTHORIZE.getId());
        return entity;
    }

    protected PaymentEventEntity getPaymentEventEntity(PaymentTransactionEntity payment) {
        PaymentEventEntity eventEntity = new PaymentEventEntity();
        eventEntity.setPaymentId(payment.getId());
        eventEntity.setEventTypeId(PaymentEventType.AUTH_CREATE.getId());
        eventEntity.setStatusId(PaymentStatus.AUTH_CREATED.getId());
        eventEntity.setCurrency("USD");
        eventEntity.setNetAmount(new BigDecimal(23));
        eventEntity.setCreatedTime(new Date());
        eventEntity.setCreatedBy("ut");
        eventEntity.setRequest("{\"ut\": 123}");
        eventEntity.setResponse("{\"ut\": 123}");
        return eventEntity;
    }
}