package com.junbo.payment.db;


import com.junbo.payment.db.dao.TrackingUuidDao;
import com.junbo.payment.db.dao.payment.MerchantAccountDao;
import com.junbo.payment.db.dao.payment.PaymentDao;
import com.junbo.payment.db.dao.payment.PaymentEventDao;
import com.junbo.payment.db.dao.paymentinstrument.AddressDao;
import com.junbo.payment.db.dao.paymentinstrument.CreditCardPaymentInstrumentDao;
import com.junbo.payment.db.dao.paymentinstrument.PaymentInstrumentDao;
import com.junbo.payment.db.entity.TrackingUuidEntity;
import com.junbo.payment.db.entity.payment.MerchantAccountEntity;
import com.junbo.payment.db.entity.payment.PaymentEntity;
import com.junbo.payment.db.entity.payment.PaymentEventEntity;
import com.junbo.payment.db.entity.paymentinstrument.AddressEntity;
import com.junbo.payment.db.entity.paymentinstrument.PaymentInstrumentEntity;
import com.junbo.payment.db.mapper.*;
import com.junbo.payment.db.repository.MerchantAccountRepository;
import com.junbo.payment.spec.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class PaymentDaoTest extends BaseTest {
    @Autowired
    private PaymentInstrumentDao piDao;
    @Autowired
    private AddressDao addressDao;   
    @Autowired
    private CreditCardPaymentInstrumentDao ccDao;
    @Autowired
    private PaymentDao paymentDao;
    @Autowired
    private PaymentEventDao paymentEventDao;
    @Autowired
    private MerchantAccountDao merchantAccountDao;
    @Autowired
    private MerchantAccountRepository merchantAccountRepository;
    @Autowired
    private TrackingUuidDao trackingUuidDao;

    private static final Long userId = 123l;

    @Test
    public void testCreate() {        
        AddressEntity address = buildAddressRequest();
        addressDao.save(address);
        PaymentInstrumentEntity entity = buildRequest(address, userId);
        piDao.save(entity);

        Assert.assertNotNull(entity.getId(), "Entity id should not be null.");
    }

    @Test
    public void testGet() {        
        AddressEntity address = buildAddressRequest();
        addressDao.save(address);
        PaymentInstrumentEntity entity = buildRequest(address, userId);
        Long piId = piDao.save(entity);
        PaymentInstrumentEntity getEntity = piDao.get(piId);
        Assert.assertNotNull(getEntity);
        Assert.assertEquals(getEntity.getId(), entity.getId());
    }

    @Test
    public void testGetByUser() {
        Long user = generateId();        
        AddressEntity address = buildAddressRequest();
        addressDao.save(address);
        PaymentInstrumentEntity entity1 = buildRequest(address, user);
        Long piId1 = piDao.save(entity1);
        PaymentInstrumentEntity entity2 = buildRequest(address, user);
        Long piId2 = piDao.save(entity2);
        List<PaymentInstrumentEntity> getEntities = piDao.getByUserId(user);
        Assert.assertNotNull(getEntities);
        Assert.assertEquals(getEntities.size(), 2);
    }

    @Test
    public void testCreatePayment() {
        AddressEntity address = buildAddressRequest();
        addressDao.save(address);
        PaymentInstrumentEntity entity = buildRequest(address, userId);
        piDao.save(entity);

        PaymentEntity payment = buildPaymentRequest(entity);
        paymentDao.save(payment);

        PaymentEventEntity eventEntity = getPaymentEventEntity(payment);
        paymentEventDao.save(eventEntity);

        Assert.assertNotNull(payment.getId(), "Entity id should not be null.");
    }

    @Test
    public void testGetPayment() {
        AddressEntity address = buildAddressRequest();
        addressDao.save(address);
        PaymentInstrumentEntity entity = buildRequest(address, userId);
        piDao.save(entity);

        PaymentEntity payment = buildPaymentRequest(entity);
        paymentDao.save(payment);

        PaymentEventEntity eventEntity1 = getPaymentEventEntity(payment);
        paymentEventDao.save(eventEntity1);
        PaymentEventEntity eventEntity2 = getPaymentEventEntity(payment);
        paymentEventDao.save(eventEntity2);

        PaymentEntity result = paymentDao.get(payment.getId());
        Assert.assertNotNull(result, "Entity should not be null.");
        List<PaymentEventEntity> results = paymentEventDao.getByPaymentId(result.getId());
        Assert.assertEquals(results.size(), 2);
    }

    //@Test
    public void testTrackingUuid(){
        TrackingUuidEntity entity = buildTrackingUuidRequest();
        trackingUuidDao.save(entity);
        List<TrackingUuidEntity> entities = trackingUuidDao.getByTrackingUuid(entity.getUserId(), entity.getTrackingUuid());
        Assert.assertEquals(entities.size(), 1);
        Assert.assertEquals(entity.getId(), entities.get(0).getId());
    }

    protected AddressEntity buildAddressRequest(){
        AddressEntity entity = new AddressEntity();
        entity.setId(generateId());
        entity.setCountry("US");
        entity.setCreatedTime(new Date());
        entity.setAddressLine1("3rd street");
        entity.setPostalCode("12345");
        return entity;
    }

    protected PaymentInstrumentEntity buildRequest(AddressEntity address, Long userId) {
        PaymentInstrumentEntity entity = new PaymentInstrumentEntity();
        entity.setId(generateId());
        entity.setAccountName("David");
        entity.setAddressId(address.getId());
        entity.setStatus(PIStatus.ACTIVE);
        entity.setType(PIType.CREDITCARD);
        entity.setUserId(userId);
        entity.setUpdatedBy("ut");
        entity.setCreatedTime(new Date());

        return entity;
    }

    protected PaymentEntity buildPaymentRequest(PaymentInstrumentEntity pi){
        PaymentEntity entity = new PaymentEntity();
        entity.setId(generateId());
        entity.setUserId(generateId());
        entity.setCreatedBy("ut");
        entity.setCurrency("USD");
        entity.setCountryCode("US");
        entity.setCreatedTime(new Date());
        entity.setNetAmount(new BigDecimal(23));
        entity.setPaymentInstrumentId(pi.getId());
        entity.setPaymentProviderId(0);
        entity.setBillingRefId("123");
        entity.setStatus(PaymentStatus.AUTH_CREATED);
        entity.setType(PaymentType.AUTHORIZE);
        return entity;
    }

    protected PaymentEventEntity getPaymentEventEntity(PaymentEntity payment) {
        PaymentEventEntity eventEntity = new PaymentEventEntity();
        eventEntity.setPaymentId(payment.getId());
        eventEntity.setId(generateId());
        eventEntity.setType(PaymentEventType.AUTH_CREATE);
        eventEntity.setStatus(PaymentStatus.AUTH_CREATED);
        eventEntity.setCurrency("USD");
        eventEntity.setNetAmount(new BigDecimal(23));
        eventEntity.setCreatedTime(new Date());
        eventEntity.setCreatedBy("ut");
        eventEntity.setRequest("{\"ut\": 123}");
        eventEntity.setResponse("{\"ut\": 123}");
        return eventEntity;
    }

    protected MerchantAccountEntity buildMerchantRequest(){
        MerchantAccountEntity merchant = new MerchantAccountEntity();
        merchant.setCurrency("USD");
        merchant.setPiType(PIType.CREDITCARD);
        merchant.setCountryCode("US");
        merchant.setMerchantAccountId(new Random().nextInt());
        merchant.setPaymentProviderId(0);
        merchant.setCreatedTime(new Date());
        merchant.setCreatedBy("ut");
        merchant.setMerchantAccountRef("ut");
        return merchant;
    }

    protected TrackingUuidEntity buildTrackingUuidRequest(){
        TrackingUuidEntity entity = new TrackingUuidEntity();
        entity.setApi(PaymentAPI.AddPI);
        entity.setId(generateId());
        entity.setPaymentId(generateId());
        entity.setPaymentInstrumentId(generateId());
        entity.setTrackingUuid(generateUUID());
        entity.setUserId(generateId());
        return entity;
    }
}