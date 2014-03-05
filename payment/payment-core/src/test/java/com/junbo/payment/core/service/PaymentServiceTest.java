package com.junbo.payment.core.service;

import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.core.mock.MockPaymentProviderServiceImpl;
import com.junbo.payment.spec.enums.PIStatus;
import com.junbo.payment.spec.enums.PIType;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class PaymentServiceTest extends BaseTest {
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

    //@Test
    public void testAddPI() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        PaymentInstrument result = null;
        result = piService.add(request).wrapped().get();
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getCreditCardRequest().getType(), "Visa");
        Assert.assertEquals(result.getCreditCardRequest().getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
       }

    //@Test
    public void testRemovePI() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        PaymentInstrument result = piService.add(request).wrapped().get();
        piService.delete(result.getId());
        PaymentInstrument resultDelete = piService.getById(result.getId());
        Assert.assertEquals(resultDelete.getStatus().toString(), PIStatus.DELETED.toString());
    }

    //@Test
    public void testPutPI() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        PaymentInstrument result = piService.add(request).wrapped().get();
        result.setStatus(PIStatus.DELETED.toString());
        result.getAddress().setPostalCode("123");
        piService.update(result);
        PaymentInstrument resultUpdate = piService.getById(result.getId());
        Assert.assertEquals(resultUpdate.getStatus().toString(), PIStatus.DELETED.toString());
        Assert.assertEquals(resultUpdate.getAddress().getPostalCode(), "123");
    }

    //@Test
    public void testAuthSettleAndReverse() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        piService.add(request);
        PaymentTransaction payment = buildPaymentTransaction(request);
        PaymentTransaction result = paymentService.authorize(payment).wrapped().get();
        payment.setTrackingUuid(generateUUID());
        paymentService.capture(result.getPaymentId(), payment);
        PaymentTransaction getResult = paymentService.getById(result.getPaymentId());
        Assert.assertEquals(getResult.getStatus().toString(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        payment.setTrackingUuid(generateUUID());
        paymentService.reverse(result.getPaymentId());
        getResult = paymentService.getById(result.getPaymentId());
        Assert.assertEquals(getResult.getStatus().toString(), PaymentStatus.REVERSED.toString());
    }

    //@Test
    public void testAuthAndReverse() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        piService.add(request);
        PaymentTransaction payment = buildPaymentTransaction(request);
        PaymentTransaction result = paymentService.authorize(payment).wrapped().get();
        paymentService.reverse(result.getPaymentId());
        PaymentTransaction getResult = paymentService.getById(result.getPaymentId());
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.authExternalToken);
        Assert.assertEquals(getResult.getStatus().toString(), PaymentStatus.REVERSED.toString());
    }

    //@Test
    public void testChargeAndReverse() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        piService.add(request);
        PaymentTransaction payment = buildPaymentTransaction(request);
        PaymentTransaction result = paymentService.charge(payment).wrapped().get();
        PaymentTransaction getResult = paymentService.getById(result.getPaymentId());
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.chargeExternalToken);
        Assert.assertEquals(getResult.getPaymentProvider(), result.getPaymentProvider());
        Assert.assertEquals(getResult.getStatus().toString(), result.getStatus());
        Assert.assertEquals(getResult.getStatus().toString(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        paymentService.reverse(result.getPaymentId());
        getResult = paymentService.getById(result.getPaymentId());
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.authExternalToken);
        Assert.assertEquals(getResult.getStatus().toString(), PaymentStatus.REVERSED.toString());
    }

    private PaymentInstrument buildPIRequest() {
        PaymentInstrument request = new PaymentInstrument();
        request.setUserId(generateLong());
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
        payment.setUserId(pi.getUserId());
        payment.setChargeInfo(new ChargeInfo(){
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
