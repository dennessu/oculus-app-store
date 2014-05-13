package com.junbo.payment.clientproxy.service;

import com.junbo.common.id.PIType;
import com.junbo.common.id.PaymentId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.resource.WalletResource;
import com.junbo.payment.clientproxy.BaseTest;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.*;
import com.junbo.payment.spec.resource.proxy.PaymentInstrumentResourceClientProxy;
import com.junbo.payment.spec.resource.proxy.PaymentTransactionResourceClientProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class PaymentClientProxyTest extends BaseTest {
    private static String BILLING_REF_ID = "123";
    @Autowired
    private PaymentInstrumentResourceClientProxy piClient;
    @Autowired
    private PaymentTransactionResourceClientProxy paymentClient;
    @Autowired
    private WalletResource walletClient;

    @Test(enabled = false)
    public void addPIAndUpdatePI() throws ExecutionException, InterruptedException {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).wrapped().get();
        Assert.assertNotNull(result.getId());
        final PaymentInstrument getResult = piClient.getById(new PaymentInstrumentId(result.getId())).wrapped().get();
        Assert.assertEquals(result.getUserId(), getResult.getUserId());
        Assert.assertEquals(result.getId(), getResult.getId());
        Long newPIIId = 123456L;
        getResult.setBillingAddressId(newPIIId);
        getResult.setPhoneNumber(newPIIId);
        PaymentInstrumentId piid = new PaymentInstrumentId(getResult.getId());
        final PaymentInstrument updateResult =  piClient.update(piid,getResult).wrapped().get();
        Assert.assertEquals(updateResult.getBillingAddressId(), newPIIId);
        Assert.assertEquals(updateResult.getPhoneNumber(), newPIIId);
        final PaymentInstrument getUpdatedResult = piClient.getById(new PaymentInstrumentId(result.getId())).wrapped().get();
        Assert.assertEquals(updateResult.getBillingAddressId(), newPIIId);
        Assert.assertEquals(updateResult.getPhoneNumber(), newPIIId);
        final PaymentInstrument result2 = piClient.postPaymentInstrument(pi).wrapped().get();
        Assert.assertNotNull(result2.getId());
        PaymentInstrumentSearchParam searchParam = new PaymentInstrumentSearchParam();
        searchParam.setUserId(userId);
        Results<PaymentInstrument> results = piClient.searchPaymentInstrument(searchParam,new PageMetaData()).wrapped().get();
        Assert.assertTrue(results.getItems().size() > 1);
        piClient.delete(new PaymentInstrumentId(result.getId())).wrapped().get();
        searchParam.setType(PIType.CREDITCARD.toString());
        Results<PaymentInstrument> results2 = piClient.searchPaymentInstrument(searchParam,new PageMetaData()).wrapped().get();
        Assert.assertEquals(results.getItems().size(), results2.getItems().size() + 1);
    }

    @Test(enabled = false)
    public void addPIAndAuthSettle() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(getLongId());
        PaymentInstrument pi = getPaymentInstrument();

        final PaymentInstrument result = piClient.postPaymentInstrument(pi).wrapped().get();
        Assert.assertNotNull(result.getId());
        final PaymentInstrument getResult = piClient.getById(new PaymentInstrumentId(result.getId())).wrapped().get();
        Assert.assertEquals(result.getUserId(), getResult.getUserId());
        Assert.assertEquals(result.getId(), getResult.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(11.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).wrapped().get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction getAuth = paymentClient.getPayment(new PaymentId(paymentResult.getId())).wrapped().get();
        Assert.assertEquals(getAuth.getPaymentInstrumentId(), paymentResult.getPaymentInstrumentId());
        Assert.assertEquals(getAuth.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
            }
        };
        PaymentTransaction captureResult = paymentClient.postPaymentCapture(new PaymentId(paymentResult.getId()), captureTrx).wrapped().get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());

    }

    @Test(enabled = false)
    public void addSVPIAndCharge() throws ExecutionException, InterruptedException {
        PaymentInstrument pi = getPaymentInstrument();
        final UserId userId = new UserId(pi.getUserId());
        pi.setType(PIType.STOREDVALUE.getId());
        pi.getTypeSpecificDetails().setStoredValueCurrency("usd");
        final PaymentInstrument result = piClient.postPaymentInstrument(pi).wrapped().get();
        Assert.assertNotNull(result.getId());
        //credit balance
        CreditRequest cr = new CreditRequest();
        cr.setUserId(userId.getValue());
        BigDecimal amount = new BigDecimal("1000.00");
        cr.setAmount(amount);
        cr.setCurrency("usd");
        walletClient.credit(cr).wrapped().get();
        PaymentInstrument getResult =  piClient.getById(new PaymentInstrumentId(result.getId())).wrapped().get();
        Assert.assertTrue(getResult.getTypeSpecificDetails().getStoredValueBalance().equals(amount));
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal("15.00"));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentCharge(trx).wrapped().get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.SETTLED.toString());
        //get Balance again
        PaymentInstrument getResult2 =  piClient.getById(new PaymentInstrumentId(result.getId())).wrapped().get();
        Assert.assertTrue(getResult2.getTypeSpecificDetails().getStoredValueBalance().equals(new BigDecimal("985.00")));
        //Credit Balance and get again
        trx.getChargeInfo().setAmount(new BigDecimal("1000.00"));
        paymentResult = paymentClient.postPaymentCredit(trx).wrapped().get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.SETTLED.toString());
        PaymentInstrument getResult3 =  piClient.getById(new PaymentInstrumentId(result.getId())).wrapped().get();
        Assert.assertTrue(getResult3.getTypeSpecificDetails().getStoredValueBalance().equals(new BigDecimal("1985.00")));
    }

    private PaymentInstrument getPaymentInstrument() {
        return new PaymentInstrument(){
                {
                    setAccountName("ut");
                    setAccountNum("4111111111111111");
                    setIsValidated(false);
                    setType(0L);
                    setTrackingUuid(generateUUID());
                    setUserId(getLongId());
                    setLabel("my first card");
                    setBillingAddressId(123L);
                    setTypeSpecificDetails(new TypeSpecificDetails() {
                        {
                            setExpireDate("2025-12");
                            setEncryptedCvmCode("111");
                        }
                    });
                }
            };
    }

    @Test(enabled = false)
    public void addPIAndAuthPartialSettle() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(getLongId());
        PaymentInstrument pi = getPaymentInstrument();

        final PaymentInstrument result = piClient.postPaymentInstrument(pi).wrapped().get();
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(12.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).wrapped().get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(10.00));
                    }
                });
            }
        };
        PaymentTransaction captureResult = paymentClient.postPaymentCapture(new PaymentId(paymentResult.getId()), captureTrx).wrapped().get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
    }

    @Test(enabled = false)
    public void addPIAndAuthReverse() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(getLongId());
        PaymentInstrument pi = getPaymentInstrument();

        final PaymentInstrument result = piClient.postPaymentInstrument(pi).wrapped().get();
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
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
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction reverseTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setBillingRefId(BILLING_REF_ID);
                setUserId(userId.getValue());
            }
        };
        PaymentTransaction captureResult = paymentClient.reversePayment(new PaymentId(paymentResult.getId()), reverseTrx).wrapped().get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.REVERSED.toString());
    }

    @Test(enabled = false)
    public void addPIAndChargeReverse() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(getLongId());
        PaymentInstrument pi = getPaymentInstrument();

        final PaymentInstrument result = piClient.postPaymentInstrument(pi).wrapped().get();
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(15.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentCharge(trx).wrapped().get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        PaymentTransaction reverseTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setBillingRefId(BILLING_REF_ID);
                setUserId(userId.getValue());
            }
        };
        PaymentTransaction captureResult = paymentClient.reversePayment(new PaymentId(paymentResult.getId()), reverseTrx).wrapped().get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.REVERSED.toString());
    }

    @Test(enabled = false)
    public void addPIAndAuthFailed() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(getLongId());
        PaymentInstrument pi = getPaymentInstrument();

        final PaymentInstrument result = piClient.postPaymentInstrument(pi).wrapped().get();
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
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
    public void addPIAndAuthCaptureFailed() throws Exception {
        final UserId userId = new UserId(getLongId());
        PaymentInstrument pi = getPaymentInstrument();

        final PaymentInstrument result = piClient.postPaymentInstrument(pi).wrapped().get();
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setPaymentInstrumentId(result.getId());
                setBillingRefId(BILLING_REF_ID);
                setChargeInfo(new ChargeInfo(){
                    {
                        setCurrency("USD");
                        setAmount(new BigDecimal(17.00));
                    }
                });
            }
        };
        PaymentTransaction paymentResult = paymentClient.postPaymentAuthorization(trx).wrapped().get();
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
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
            captureResult = paymentClient.postPaymentCapture(new PaymentId(paymentResult.getId()), captureTrx).wrapped().get();
        }catch (Exception ex){
            Assert.assertNull(captureResult);
            PaymentTransaction revertResult = paymentClient.getPayment(new PaymentId(paymentResult.getId())).wrapped().get();
            Assert.assertEquals(revertResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMIT_DECLINED.toString());
            for(PaymentEvent event : revertResult.getPaymentEvents()){
                if(event.getType().equalsIgnoreCase(PaymentEventType.SUBMIT_SETTLE.toString())
                        && event.getStatus().equalsIgnoreCase(PaymentStatus.SETTLEMENT_SUBMIT_DECLINED.toString())){
                    return;
                }
            }
            throw ex;
        }
    }

    @Test(enabled = false)
    public void addPIAndAuthReverseFailed() throws Exception {
        final UserId userId = new UserId(getLongId());
        PaymentInstrument pi = getPaymentInstrument();

        final PaymentInstrument result = piClient.postPaymentInstrument(pi).wrapped().get();
        Assert.assertNotNull(result.getId());
        PaymentTransaction trx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(userId.getValue());
                setBillingRefId(BILLING_REF_ID);
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
        Assert.assertEquals(paymentResult.getStatus().toUpperCase(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction reverseTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setBillingRefId(BILLING_REF_ID);
                setUserId(userId.getValue());
            }
        };
        PaymentTransaction reverseResult = null;
        try{
            reverseResult = paymentClient.reversePayment(new PaymentId(paymentResult.getId()), reverseTrx).wrapped().get();
        }catch (Exception ex){
            Assert.assertNull(reverseResult);
            PaymentTransaction getResult = paymentClient.getPayment(new PaymentId(paymentResult.getId())).wrapped().get();
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
