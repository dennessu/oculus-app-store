package com.junbo.payment.core.service;

import com.junbo.common.id.PIType;
import com.junbo.langur.core.transaction.AsyncTransactionTemplate;
import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.mock.MockPaymentProviderServiceImpl;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.provider.adyen.AdyenCCProivderServiceImpl;
import com.junbo.payment.core.provider.adyen.AdyenProviderServiceImpl;
import com.junbo.payment.core.provider.braintree.BrainTreePaymentProviderServiceImpl;
import com.junbo.payment.core.provider.ewallet.EWalletProviderServiceImpl;
import com.junbo.payment.core.provider.paypal.PayPalProviderServiceImpl;
import com.junbo.payment.db.dao.payment.PaymentProviderDao;
import com.junbo.payment.db.entity.payment.PaymentProviderEntity;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.internal.ProviderCriteria;
import com.junbo.payment.spec.model.ChargeInfo;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.model.TypeSpecificDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class PaymentServiceTest extends BaseTest {
    private final String BILLING_REF_ID = "123";

    @Autowired
    private PaymentProviderDao providerDao;

    @Test
    public void testAddPI() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        PaymentInstrument result = null;
        result = piService.add(request).get();
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getTypeSpecificDetails().getCreditCardType(), "VISA");
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        PaymentInstrument getResult = piService.getById(result.getId()).get();
        Assert.assertEquals(getResult.getAccountName(), result.getAccountName());
       }

    @Test
    public void testRemovePI() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        PaymentInstrument result = piService.add(request).get();
        piService.delete(result.getId());
    }

    @Test
    public void testPutPI() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        PaymentInstrument result = piService.add(request).get();
        result.setIsActive(false);
        result.setBillingAddressId(123L);
        piService.update(result);
        PaymentInstrument resultUpdate = piService.getById(result.getId()).get();
        Assert.assertEquals(resultUpdate.getIsActive(), Boolean.FALSE);
        Assert.assertEquals(resultUpdate.getBillingAddressId().longValue(), 123L);
    }

    @Test
    public void testAuthSettleAndReverse() throws ExecutionException, InterruptedException {
        PaymentInstrument request = addPI(buildPIRequest());
        PaymentTransaction payment = buildPaymentTransaction(request);
        PaymentTransaction result = paymentService.authorize(payment).get();
        payment.setTrackingUuid(generateUUID());
        PaymentTransaction captureResult = paymentService.capture(result.getId(), payment).get();
        Assert.assertEquals(captureResult.getStatus().toString(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        payment.setTrackingUuid(generateUUID());
        payment.setChargeInfo(null);
        paymentService.reverse(result.getId(), payment).get();
        PaymentTransaction getResult = paymentService.getUpdatedTransaction(result.getId()).get();
        Assert.assertEquals(getResult.getStatus().toString(), PaymentStatus.REVERSED.toString());
    }

    @Test
    public void testAuthAndReverse() throws ExecutionException, InterruptedException {
        PaymentInstrument request = addPI(buildPIRequest());
        PaymentTransaction payment = buildPaymentTransaction(request);
        PaymentTransaction result = paymentService.authorize(payment).get();
        payment.setChargeInfo(null);
        paymentService.reverse(result.getId(), payment).get();
        PaymentTransaction getResult = paymentService.getUpdatedTransaction(result.getId()).get();
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.authExternalToken);
        Assert.assertEquals(getResult.getStatus().toString(), PaymentStatus.REVERSED.toString());
    }

    @Test
    public void testChargeAndReverse() throws ExecutionException, InterruptedException {
        PaymentInstrument request = addPI(buildPIRequest());
        PaymentTransaction payment = buildPaymentTransaction(request);
        PaymentTransaction result = paymentService.charge(payment).get();
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.chargeExternalToken);
        Assert.assertEquals(result.getStatus().toString(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        payment.setChargeInfo(null);
        paymentService.reverse(result.getId(), payment).get();
        PaymentTransaction getResult = paymentService.getUpdatedTransaction(result.getId()).get();
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.chargeExternalToken);
        Assert.assertEquals(getResult.getStatus().toString(), PaymentStatus.REVERSED.toString());
    }

    //commit addPI since there is standalone commit in payment transaction, so that PI is available fir them
    private PaymentInstrument addWallet(){
        final PaymentInstrument request = buildWalletPIRequest();
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentInstrument>() {
            public PaymentInstrument doInTransaction(TransactionStatus txnStatus) {
                return piService.add(request).get();
            }
        });
    }

    @Test
    public void testWallet() throws ExecutionException, InterruptedException {
        PaymentInstrument pi = addWallet();
        PaymentTransaction payment = buildPaymentTransaction(pi);
        PaymentTransaction result = paymentService.charge(payment).get();
        Assert.assertEquals(result.getStatus(), PaymentStatus.SETTLED.toString());
        Assert.assertNotNull(result.getExternalToken());
    }

    private PaymentProviderEntity addOrDeleteProvider(final PaymentProviderEntity entity, final boolean isAdd){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentProviderEntity>() {
            public PaymentProviderEntity doInTransaction(TransactionStatus txnStatus) {
                if(isAdd){
                    providerDao.save(entity);
                }else{
                    providerDao.delete(entity);
                }
                return entity;
            }
        });
    }


    @Test(enabled = false)
    public void testRoutingService() throws ExecutionException, InterruptedException {
        PaymentProviderEntity entity = null;
        try{
            entity = new PaymentProviderEntity();
            entity.setId(100);
            entity.setCountryCode("CN");
            entity.setPiType(0L);
            entity.setProviderName("BrainTree");
            entity.setCreatedTime(new Date());
            entity.setCreatedBy("ut");
            addOrDeleteProvider(entity, true);

            ProviderCriteria ccCriteria = new ProviderCriteria(PIType.CREDITCARD);
            PaymentProviderService service = providerRoutingService.getPaymentProvider(ccCriteria);
            Assert.assertEquals(true, service instanceof AdyenCCProivderServiceImpl);

            ccCriteria.setCountry("CN");
            service = providerRoutingService.getPaymentProvider(ccCriteria);
            Assert.assertEquals(true, service instanceof BrainTreePaymentProviderServiceImpl);

            service = providerRoutingService.getPaymentProvider(new ProviderCriteria(PIType.PAYPAL));
            Assert.assertEquals(true, service instanceof PayPalProviderServiceImpl);
            service = providerRoutingService.getPaymentProvider(new ProviderCriteria(PIType.STOREDVALUE));
            Assert.assertEquals(true, service instanceof EWalletProviderServiceImpl);
            service = providerRoutingService.getPaymentProvider(new ProviderCriteria(PIType.OTHERS));
            Assert.assertEquals(true, service instanceof AdyenProviderServiceImpl);
        }finally {
            if(entity != null){
                addOrDeleteProvider(entity, false);
            }
        }

    }

    private PaymentInstrument buildWalletPIRequest() {
        PaymentInstrument request = buildBasePIRequest();
        request.setAccountNumber(null);
        request.setType(PIType.STOREDVALUE.getId());
        request.setTypeSpecificDetails(new TypeSpecificDetails() {
            {
                setWalletType("STORED_VALUE");
                setStoredValueCurrency("USD");
            }
        });
        return request;


    }

    private PaymentTransaction buildPaymentTransaction(final PaymentInstrument pi){
        PaymentTransaction payment = new PaymentTransaction();
        payment.setTrackingUuid(generateUUID());
        payment.setUserId(pi.getUserId());
        payment.setBillingRefId(BILLING_REF_ID);
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("USD");
                setAmount(new BigDecimal(100.00));
                setBusinessDescriptor("ut");
            }
        });
        payment.setPaymentInstrumentId(pi.getId());
        return payment;
    }
}
