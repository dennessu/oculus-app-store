package com.junbo.payment.clientproxy.service;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.payment.clientproxy.BaseTest;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.*;
import com.junbo.payment.spec.resource.proxy.PaymentInstrumentResourceClientProxy;
import com.junbo.payment.spec.resource.proxy.PaymentTransactionResourceClientProxy;
import junit.framework.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

public class PaymentClientProxyTest extends BaseTest {
    @Autowired
    private PaymentInstrumentResourceClientProxy piClient;
    @Autowired
    private PaymentTransactionResourceClientProxy paymentClient;

    @Test(enabled = false)
    public void addPIAndAuthSettle() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(generateLong());
        PaymentInstrument pi = new PaymentInstrument(){
            {
                setAccountName("ut");
                setAccountNum("4111111111111111");
                setIsValidated(false);
                setIsDefault("true");
                setType("CREDITCARD");
                setTrackingUuid(generateUUID());
                setAddress(new Address() {
                    {
                        setAddressLine1("3rd street");
                        setPostalCode("12345");
                        setCountry("US");
                    }
                });
                setCreditCardRequest(new CreditCardRequest(){
                    {
                        setExpireDate("2025-12");
                        setEncryptedCvmCode("111");
                    }
                });
            }
        };

        final PaymentInstrument result = piClient.postPaymentInstrument(userId, pi).wrapped().get();
        Assert.assertNotNull(result.getCreditCardRequest().getExternalToken());
        Assert.assertNotNull(result.getId());
        final PaymentInstrument getResult = piClient.getById(userId, new PaymentInstrumentId(result.getId()
                .getPaymentInstrumentId())).wrapped().get();
        Assert.assertEquals(result.getCreditCardRequest().getExternalToken(), getResult.getCreditCardRequest().getExternalToken());
        Assert.assertEquals(result.getId().getUserId(), result.getId().getUserId());
        Assert.assertEquals(result.getId().getPaymentInstrumentId(), result.getId().getPaymentInstrumentId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(11.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).wrapped().get();
        Assert.assertNotNull(paymentResult.getExternalToken());
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction getAuth = paymentClient.getPayment(paymentResult.getId()).wrapped().get();
        Assert.assertEquals(getAuth.getPaymentInstrumentId().getPaymentInstrumentId(), paymentResult.getPaymentInstrumentId().getPaymentInstrumentId());
        Assert.assertEquals(getAuth.getPaymentInstrumentId().getUserId(), paymentResult.getPaymentInstrumentId().getUserId());
        Assert.assertEquals(getAuth.getExternalToken(), paymentResult.getExternalToken());
        Assert.assertEquals(getAuth.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
            }
        };
        PaymentTransaction captureResult = paymentClient.postPaymentCapture(paymentResult.getId(), captureTrx).wrapped().get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());

    }

    @Test(enabled = false)
    public void addPIAndAuthPartialSettle() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(generateLong());
        PaymentInstrument pi = new PaymentInstrument(){
            {
                setAccountName("ut");
                setAccountNum("4111111111111111");
                setIsValidated(false);
                setIsDefault("true");
                setType("CREDITCARD");
                setTrackingUuid(generateUUID());
                setAddress(new Address() {
                    {
                        setAddressLine1("3rd street");
                        setPostalCode("12345");
                        setCountry("US");
                    }
                });
                setCreditCardRequest(new CreditCardRequest(){
                    {
                        setExpireDate("2025-12");
                        setEncryptedCvmCode("111");
                    }
                });
            }
        };

        final PaymentInstrument result = piClient.postPaymentInstrument(userId, pi).wrapped().get();
        Assert.assertNotNull(result.getCreditCardRequest().getExternalToken());
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(12.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).wrapped().get();
        Assert.assertNotNull(paymentResult.getExternalToken());
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(10.00));
                    }
                });
            }
        };
        PaymentTransaction captureResult = paymentClient.postPaymentCapture(paymentResult.getId(), captureTrx).wrapped().get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
    }

    @Test(enabled = false)
    public void addPIAndAuthReverse() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(generateLong());
        PaymentInstrument pi = new PaymentInstrument(){
            {
                setAccountName("ut");
                setAccountNum("4111111111111111");
                setIsValidated(false);
                setIsDefault("true");
                setType("CREDITCARD");
                setTrackingUuid(generateUUID());
                setAddress(new Address() {
                    {
                        setAddressLine1("3rd street");
                        setPostalCode("12345");
                        setCountry("US");
                    }
                });
                setCreditCardRequest(new CreditCardRequest(){
                    {
                        setExpireDate("2025-12");
                        setEncryptedCvmCode("111");
                    }
                });
            }
        };

        final PaymentInstrument result = piClient.postPaymentInstrument(userId, pi).wrapped().get();
        Assert.assertNotNull(result.getCreditCardRequest().getExternalToken());
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(14.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).wrapped().get();
        Assert.assertNotNull(paymentResult.getExternalToken());
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction reverseTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
            }
        };
        PaymentTransaction captureResult = paymentClient.reversePayment(paymentResult.getId(), reverseTrx).wrapped().get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.REVERSED.toString());
    }

    @Test(enabled = false)
    public void addPIAndChargeReverse() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(generateLong());
        PaymentInstrument pi = new PaymentInstrument(){
            {
                setAccountName("ut");
                setAccountNum("4111111111111111");
                setIsValidated(false);
                setIsDefault("true");
                setType("CREDITCARD");
                setTrackingUuid(generateUUID());
                setAddress(new Address() {
                    {
                        setAddressLine1("3rd street");
                        setPostalCode("12345");
                        setCountry("US");
                    }
                });
                setCreditCardRequest(new CreditCardRequest(){
                    {
                        setExpireDate("2025-12");
                        setEncryptedCvmCode("111");
                    }
                });
            }
        };

        final PaymentInstrument result = piClient.postPaymentInstrument(userId, pi).wrapped().get();
        Assert.assertNotNull(result.getCreditCardRequest().getExternalToken());
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(15.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentCharge(trx).wrapped().get();
        Assert.assertNotNull(paymentResult.getExternalToken());
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        PaymentTransaction reverseTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
            }
        };
        PaymentTransaction captureResult = paymentClient.reversePayment(paymentResult.getId(), reverseTrx).wrapped().get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.REVERSED.toString());
    }

    @Test(enabled = false)
    public void addPIAndAuthFailed() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(generateLong());
        PaymentInstrument pi = new PaymentInstrument(){
            {
                setAccountName("ut");
                setAccountNum("4111111111111111");
                setIsValidated(false);
                setIsDefault("true");
                setType("CREDITCARD");
                setTrackingUuid(generateUUID());
                setAddress(new Address() {
                    {
                        setAddressLine1("3rd street");
                        setPostalCode("12345");
                        setCountry("US");
                    }
                });
                setCreditCardRequest(new CreditCardRequest(){
                    {
                        setExpireDate("2025-12");
                        setEncryptedCvmCode("111");
                    }
                });
            }
        };

        final PaymentInstrument result = piClient.postPaymentInstrument(userId, pi).wrapped().get();
        Assert.assertNotNull(result.getCreditCardRequest().getExternalToken());
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("abc");
                        setAmount(new BigDecimal(16.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = null;
        try{
            paymentResult = paymentClient.postPaymentAuthorization(trx).wrapped().get();
        }catch(Exception ex){

        }

    }

    @Test(enabled = false)
    public void addPIAndAuthCaptureFailed() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(generateLong());
        PaymentInstrument pi = new PaymentInstrument(){
            {
                setAccountName("ut");
                setAccountNum("4111111111111111");
                setIsValidated(false);
                setIsDefault("true");
                setType("CREDITCARD");
                setTrackingUuid(generateUUID());
                setAddress(new Address() {
                    {
                        setAddressLine1("3rd street");
                        setPostalCode("12345");
                        setCountry("US");
                    }
                });
                setCreditCardRequest(new CreditCardRequest(){
                    {
                        setExpireDate("2025-12");
                        setEncryptedCvmCode("111");
                    }
                });
            }
        };

        final PaymentInstrument result = piClient.postPaymentInstrument(userId, pi).wrapped().get();
        Assert.assertNotNull(result.getCreditCardRequest().getExternalToken());
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(17.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).wrapped().get();
        Assert.assertNotNull(paymentResult.getExternalToken());
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(10.00));
                    }
                });
            }
        };
        PaymentTransaction captureResult = null;
        try{
            captureResult = paymentClient.postPaymentCapture(paymentResult.getId(), captureTrx).wrapped().get();
        }catch (Exception ex){
            Assert.assertNull(captureResult);
            PaymentTransaction revertResult = paymentClient.getPayment(paymentResult.getId()).wrapped().get();
            Assert.assertEquals(revertResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_DECLINED.toString());
            for(PaymentEvent event : revertResult.getPaymentEvents()){
                if(event.getType().equalsIgnoreCase(PaymentEventType.SUBMIT_SETTLE.toString())
                        && event.getStatus().equalsIgnoreCase(PaymentStatus.SETTLEMENT_DECLINED.toString())){
                    return;
                }
            }
            throw ex;
        }
    }

    @Test(enabled = false)
    public void addPIAndAuthReverseFailed() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(generateLong());
        PaymentInstrument pi = new PaymentInstrument(){
            {
                setAccountName("ut");
                setAccountNum("4111111111111111");
                setIsValidated(false);
                setIsDefault("true");
                setType("CREDITCARD");
                setTrackingUuid(generateUUID());
                setAddress(new Address() {
                    {
                        setAddressLine1("3rd street");
                        setPostalCode("12345");
                        setCountry("US");
                    }
                });
                setCreditCardRequest(new CreditCardRequest(){
                    {
                        setExpireDate("2025-12");
                        setEncryptedCvmCode("111");
                    }
                });
            }
        };

        final PaymentInstrument result = piClient.postPaymentInstrument(userId, pi).wrapped().get();
        Assert.assertNotNull(result.getCreditCardRequest().getExternalToken());
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(18.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).wrapped().get();
        Assert.assertNotNull(paymentResult.getExternalToken());
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction reverseTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
            }
        };
        PaymentTransaction reverseResult = null;
        try{
            reverseResult = paymentClient.reversePayment(paymentResult.getId(), reverseTrx).wrapped().get();
        }catch (Exception ex){
            Assert.assertNull(reverseResult);
            PaymentTransaction getResult = paymentClient.getPayment(paymentResult.getId()).wrapped().get();
            Assert.assertEquals(getResult.getStatus().toUpperCase(), PaymentStatus.REVERSE_DECLINED.toString());
            for(PaymentEvent event : getResult.getPaymentEvents()){
                if(event.getType().equalsIgnoreCase(PaymentEventType.AUTH_REVERSE.toString())
                        && event.getStatus().equalsIgnoreCase(PaymentStatus.REVERSE_DECLINED.toString())){
                    return;
                }
            }
            throw ex;
        }
        throw new RuntimeException("Expect exception");
    }
}
