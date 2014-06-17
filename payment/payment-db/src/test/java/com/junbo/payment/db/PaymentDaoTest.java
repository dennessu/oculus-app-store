package com.junbo.payment.db;


import com.junbo.common.id.PIType;
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
import com.junbo.payment.db.repository.MerchantAccountRepository;
import com.junbo.payment.spec.enums.PaymentAPI;
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

    @Test
    @Transactional
    public void testCreate() {
        Long addressId = generateShardId(userId);
        AddressEntity address = buildAddressRequest();
        address.setId(addressId);
        addressDao.save(address);
        PaymentInstrumentEntity entity = buildRequest(address, userId);
        entity.setId(addressId);
        piDao.save(entity);

        Assert.assertNotNull(entity.getId(), "Entity id should not be null.");
    }

    @Test
    public void testGet() {
        Long addressId = generateShardId(userId);
        AddressEntity address = buildAddressRequest();
        address.setId(addressId);
        addressDao.save(address);
        PaymentInstrumentEntity entity = buildRequest(address, userId);
        entity.setId(addressId);
        Long piId = piDao.save(entity);
        PaymentInstrumentEntity getEntity = piDao.get(piId);
        Assert.assertNotNull(getEntity);
        Assert.assertEquals(getEntity.getId(), entity.getId());
    }

    @Test
    public void testGetByUser() {
        Long user = generateShardId(userId);
        Long addressId = generateShardId(user);
        AddressEntity address = buildAddressRequest();
        address.setId(addressId);
        addressDao.save(address);
        PaymentInstrumentEntity entity1 = buildRequest(address, user);
        entity1.setId(addressId);
        Long piId1 = piDao.save(entity1);
        PaymentInstrumentEntity entity2 = buildRequest(address, user);
        entity1.setId(addressId);
        Long piId2 = piDao.save(entity2);
        List<PaymentInstrumentEntity> getEntities = piDao.getByUserId(user);
        Assert.assertNotNull(getEntities);
        Assert.assertEquals(getEntities.size(), 2);
    }

    @Test
    public void testCreatePayment() {
        Long addressId = generateShardId(userId);
        AddressEntity address = buildAddressRequest();
        address.setId(addressId);
        addressDao.save(address);
        PaymentInstrumentEntity entity = buildRequest(address, userId);
        entity.setId(addressId);
        piDao.save(entity);

        PaymentEntity payment = buildPaymentRequest(entity);
        payment.setId(addressId);
        paymentDao.save(payment);

        PaymentEventEntity eventEntity = getPaymentEventEntity(payment);
        eventEntity.setPaymentId(addressId);
        paymentEventDao.save(eventEntity);

        Assert.assertNotNull(payment.getId(), "Entity id should not be null.");
    }

    @Test
    public void testGetPayment() {
        Long addressId = generateShardId(userId);
        AddressEntity address = buildAddressRequest();
        address.setId(addressId);
        addressDao.save(address);
        PaymentInstrumentEntity entity = buildRequest(address, userId);
        entity.setId(addressId);
        piDao.save(entity);

        PaymentEntity payment = buildPaymentRequest(entity);
        payment.setId(addressId);
        paymentDao.save(payment);

        PaymentEventEntity eventEntity1 = getPaymentEventEntity(payment);
        eventEntity1.setPaymentId(addressId);
        paymentEventDao.save(eventEntity1);
        PaymentEventEntity eventEntity2 = getPaymentEventEntity(payment);
        eventEntity2.setPaymentId(addressId);
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
        entity.setCountry("US");
        entity.setCreatedTime(new Date());
        entity.setAddressLine1("3rd street");
        entity.setPostalCode("12345");
        return entity;
    }

    protected PaymentInstrumentEntity buildRequest(AddressEntity address, Long userId) {
        PaymentInstrumentEntity entity = new PaymentInstrumentEntity();
        entity.setAccountName("David");
        entity.setBillingAddressId(123L);
        entity.setIsActive(true);
        entity.setType(PIType.CREDITCARD.getId());
        entity.setUserId(userId);
        entity.setUpdatedBy("ut");
        entity.setCreatedTime(new Date());

        return entity;
    }

    protected PaymentEntity buildPaymentRequest(PaymentInstrumentEntity pi){
        PaymentEntity entity = new PaymentEntity();
        entity.setUserId(userId);
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

    protected PaymentEventEntity getPaymentEventEntity(PaymentEntity payment) {
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

    protected MerchantAccountEntity buildMerchantRequest(){
        MerchantAccountEntity merchant = new MerchantAccountEntity();
        merchant.setCurrency("USD");
        merchant.setPiType(PIType.CREDITCARD.getId());
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
        entity.setApiId(PaymentAPI.AddPI.getId());
        entity.setTrackingUuid(generateUUID());
        return entity;
    }
}