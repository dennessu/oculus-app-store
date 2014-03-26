package com.junbo.payment.core.service;

import com.junbo.common.error.AppErrorException;
import com.junbo.langur.core.transaction.AsyncTransactionTemplate;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.core.mock.MockPaymentProviderServiceImpl;
import com.junbo.payment.spec.enums.CreditCardType;
import com.junbo.payment.spec.enums.PIStatus;
import com.junbo.payment.spec.enums.PIType;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

public class PaymentServiceTest extends BaseTest {
    private final Long userId = 123L;
    private final String BILLING_REF_ID = "123";
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    public void setPiService(@Qualifier("mockPaymentInstrumentService")PaymentInstrumentService piService) {
        this.piService = piService;
    }
    @Autowired
    public void setPaymentService(@Qualifier("mockPaymentService")PaymentTransactionService paymentService) {
        this.paymentService = paymentService;
    }

    private PaymentInstrumentService piService;
    private PaymentTransactionService paymentService;

    @Test
    public void testAddPI() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        PaymentInstrument result = null;
        result = piService.add(request).wrapped().get();
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCreditCardRequest().getType(), CreditCardType.VISA.toString());
        Assert.assertEquals(result.getCreditCardRequest().getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        PaymentInstrument getResult = piService.getById(result.getId().getUserId(), result.getId().getPaymentInstrumentId());
        Assert.assertEquals(getResult.getAccountName(), result.getAccountName());
       }

    @Test
    public void testRemovePI() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        PaymentInstrument result = piService.add(request).wrapped().get();
        piService.delete(userId, result.getId().getPaymentInstrumentId());
    }

    @Test
    public void testPutPI() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        PaymentInstrument result = piService.add(request).wrapped().get();
        result.setStatus(PIStatus.DISABLE.toString());
        result.getAddress().setPostalCode("123");
        piService.update(result);
        PaymentInstrument resultUpdate = piService.getById(userId, result.getId().getPaymentInstrumentId());
        Assert.assertEquals(resultUpdate.getStatus().toString(), PIStatus.DISABLE.toString());
        Assert.assertEquals(resultUpdate.getAddress().getPostalCode(), "123");
    }

    private PaymentInstrument addPI(){
        final PaymentInstrument request = buildPIRequest();
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentInstrument>() {
            public PaymentInstrument doInTransaction(TransactionStatus txnStatus) {
                try {
                    return piService.add(request).wrapped().get();
                } catch (InterruptedException e) {
                    return null;
                } catch (ExecutionException e) {
                    return null;
                }
            }
        });
    }
    //@Test
    public void testAuthSettleAndReverse() throws ExecutionException, InterruptedException {
        PaymentInstrument request = addPI();
        PaymentTransaction payment = buildPaymentTransaction(request);
        PaymentTransaction result = paymentService.authorize(payment).wrapped().get();
        payment.setTrackingUuid(generateUUID());
        PaymentTransaction captureResult = paymentService.capture(result.getId(), payment).wrapped().get();
        Assert.assertEquals(captureResult.getStatus().toString(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        payment.setTrackingUuid(generateUUID());
        payment.setChargeInfo(null);
        paymentService.reverse(result.getId(), payment);
        PaymentTransaction getResult = paymentService.getById(result.getId());
        Assert.assertEquals(getResult.getStatus().toString(), PaymentStatus.REVERSED.toString());
    }

    //@Test
    public void testAuthAndReverse() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        piService.add(request);
        PaymentTransaction payment = buildPaymentTransaction(request);
        PaymentTransaction result = paymentService.authorize(payment).wrapped().get();
        payment.setChargeInfo(null);
        paymentService.reverse(result.getId(), payment);
        PaymentTransaction getResult = paymentService.getById(result.getId());
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.authExternalToken);
        Assert.assertEquals(getResult.getStatus().toString(), PaymentStatus.REVERSED.toString());
    }

    //@Test
    public void testChargeAndReverse() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        piService.add(request);
        PaymentTransaction payment = buildPaymentTransaction(request);
        PaymentTransaction result = paymentService.charge(payment).wrapped().get();
        PaymentTransaction getResult = paymentService.getById(result.getId());
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.chargeExternalToken);
        Assert.assertEquals(getResult.getPaymentProvider(), result.getPaymentProvider());
        Assert.assertEquals(getResult.getStatus().toString(), result.getStatus());
        Assert.assertEquals(getResult.getStatus().toString(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        payment.setChargeInfo(null);
        paymentService.reverse(result.getId(), payment);
        getResult = paymentService.getById(result.getId());
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.authExternalToken);
        Assert.assertEquals(getResult.getStatus().toString(), PaymentStatus.REVERSED.toString());
    }

    private PaymentInstrument buildPIRequest() {
        PaymentInstrument request = new PaymentInstrument();
        request.setId(new PIId(userId, null));
        request.setTrackingUuid(generateUUID());
        request.setAccountName("ut");
        request.setIsDefault(Boolean.TRUE.toString());
        request.setIsValidated(true);
        request.setType(PIType.CREDITCARD.toString());
        request.setAccountNum("4111111111111111");
        request.setAddress(new Address() {
            {
                setAddressLine1("3rd street");
                setCity("LA");
                setCountry("US");
                setPostalCode("12345");
            }
        });
        request.setPhone(new Phone(){
            {
                setType("Home");
                setNumber("12345678");
            }
        });
        request.setCreditCardRequest(new CreditCardRequest(){
            {
                setEncryptedCvmCode("111");
                setExpireDate("2025-11");
            }
        });

        return request;
    }

    private PaymentTransaction buildPaymentTransaction(final PaymentInstrument pi){
        PaymentTransaction payment = new PaymentTransaction();
        payment.setTrackingUuid(generateUUID());
        payment.setUserId(pi.getId().getUserId());
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
