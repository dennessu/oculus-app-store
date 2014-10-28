package com.junbo.payment.core.provider;

import com.junbo.common.id.PaymentId;
import com.junbo.langur.core.transaction.AsyncTransactionTemplate;
import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.core.mock.MockPaymentProviderServiceImpl;
import com.junbo.payment.core.provider.facebook.FacebookCCProviderServiceImpl;
import com.junbo.payment.core.provider.facebook.FacebookPaymentUtils;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.ChargeInfo;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;
import scala.tools.nsc.Global;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

/**
 * Created by wenzhu on 9/16/14.
 */
public class FacebookPaymentServiceTest extends BaseTest {
    private final String BILLING_REF_ID = "123";

    private FacebookCCProviderServiceImpl fbProviderService;
    @Autowired
    private FacebookPaymentUtils facebookPaymentUtils;
    private PaymentInstrumentService mockFBPiService;
    private PaymentTransactionService mockFBPaymentService;

    @Autowired
    public void setMockFBPiService(@Qualifier("mockFBPaymentInstrumentService")PaymentInstrumentService mockFBPiService) {
        this.mockFBPiService = mockFBPiService;
    }

    @Autowired
    public void setMockFBPaymentService(@Qualifier("mockFBPaymentService")PaymentTransactionService mockFBPaymentService) {
        this.mockFBPaymentService = mockFBPaymentService;
    }


    public void setFbProviderService(FacebookCCProviderServiceImpl fbProviderService) {
        this.fbProviderService = fbProviderService;
    }

    @Test(enabled = false)
    public void testGetAccessToken() throws ExecutionException, InterruptedException {
        String accessToken = facebookPaymentUtils.getAccessToken().get();
        Assert.assertNotNull(accessToken);
        Assert.assertNotEquals(accessToken, "");
        Assert.assertFalse(accessToken.contains("access_token"));
    }

    @Test(enabled = false)
    public void testAuthCaptureFB() throws ExecutionException, InterruptedException {
        final PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("4111117711552927");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        PaymentTransaction transaction = buildPaymentTransaction(request);
        PaymentTransaction paymentResult = mockFBPaymentService.authorize(transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(request.getUserId());
                setBillingRefId(BILLING_REF_ID);
            }
        };
        PaymentTransaction captureResult = mockFBPaymentService.capture(paymentResult.getId(), captureTrx).get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
    }

    @Test(enabled = false)
    public void testAuthCapturePartialFB() throws ExecutionException, InterruptedException {
        final PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("eyJjYyI6NDExMTExNzcxMTU1MjkyNywiY3Z2IjoxMjMsInNhbHQiOjQyODg2MTU2NjExODgyNTA2MjN9");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        PaymentTransaction transaction = buildPaymentTransaction(request);
        PaymentTransaction paymentResult = mockFBPaymentService.authorize(transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(request.getUserId());
                setBillingRefId(BILLING_REF_ID);
            }
        };
        ChargeInfo chargeInfo = new ChargeInfo(){
            {
                setAmount(new BigDecimal("10"));
                setCurrency("USD");
            }
        };
        captureTrx.setChargeInfo(chargeInfo);
        PaymentTransaction captureResult = mockFBPaymentService.capture(paymentResult.getId(), captureTrx).get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
    }

    @Test(enabled = false)
    public void testAuthReverseFB() throws ExecutionException, InterruptedException {
        final PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("4111117711552927");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        PaymentTransaction transaction = buildPaymentTransaction(request);
        PaymentTransaction paymentResult = mockFBPaymentService.authorize(transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction reverseTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(request.getUserId());
                setBillingRefId(BILLING_REF_ID);
            }
        };
        PaymentTransaction captureResult = mockFBPaymentService.reverse(paymentResult.getId(), reverseTrx).get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.REVERSED.toString());
    }

    @Test(enabled = false)
    public void testAuthChargeRefundFB() throws ExecutionException, InterruptedException {
        final PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("4111117711552927");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        PaymentTransaction transaction = buildPaymentTransaction(request);
        PaymentTransaction paymentResult = mockFBPaymentService.authorize(transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.AUTHORIZED.toString());
        PaymentTransaction captureTrx = new PaymentTransaction(){
            {
                setTrackingUuid(generateUUID());
                setUserId(request.getUserId());
                setBillingRefId(BILLING_REF_ID);
            }
        };
        PaymentTransaction captureResult = mockFBPaymentService.capture(paymentResult.getId(), captureTrx).get();
        Assert.assertEquals(captureResult.getStatus().toUpperCase(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());

        ChargeInfo first = new ChargeInfo();
        first.setCurrency(transaction.getChargeInfo().getCurrency());
        first.setAmount(new BigDecimal("5.00"));
        transaction.setChargeInfo(first);
        paymentResult = mockFBPaymentService.refund(paymentResult.getId(), transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.REFUNDED.toString());
        ChargeInfo second = new ChargeInfo();
        second.setCurrency(transaction.getChargeInfo().getCurrency());
        second.setAmount(new BigDecimal("95.00"));
        transaction.setChargeInfo(second);
        paymentResult = mockFBPaymentService.refund(paymentResult.getId(), transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.REFUNDED.toString());
    }

    //negative, should fail as refund only apply to charge
    @Test(enabled = false)
    public void testAuthRefundFB() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("4111117711552927");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        //test payment
        PaymentTransaction transaction = buildPaymentTransaction(request);
        PaymentTransaction paymentResult = mockFBPaymentService.authorize(transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.AUTHORIZED.toString());

        try{
            paymentResult = mockFBPaymentService.refund(paymentResult.getId(), transaction).get();
        }catch (Exception ex){
            //TODO, validate exception
            return;
        }
        throw new RuntimeException("expected exception");
    }

    //negative: capture fail card
    @Test(enabled = false)
    public void testCaptureFailFB() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("4111114869229598");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.captureFailTOken);
        //test payment
        PaymentTransaction transaction = buildPaymentTransaction(request);
        PaymentTransaction paymentResult = mockFBPaymentService.authorize(transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.AUTHORIZED.toString());

        try{
            paymentResult = mockFBPaymentService.capture(paymentResult.getId(), transaction).get();
        }catch (Exception ex){
            //TODO, validate exception
            return;
        }
        throw new RuntimeException("expected exception");
    }


    @Test(enabled = false)
    public void testChargeFB() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("4111117711552927");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        PaymentTransaction transaction = buildPaymentTransaction(request);
        PaymentTransaction paymentResult = mockFBPaymentService.charge(transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());

    }

    @Test(enabled = false)
    public void testChargeRefundFB() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("4111117711552927");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        //test payment
        PaymentTransaction transaction = buildPaymentTransaction(request);
        PaymentTransaction paymentResult = mockFBPaymentService.charge(transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        paymentResult = mockFBPaymentService.refund(paymentResult.getId(), transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.REFUNDED.toString());
    }

    @Test(enabled = false)
    public void testChargePartialRefundFB() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("4111117711552927");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        //test payment
        PaymentTransaction transaction = buildPaymentTransaction(request);
        PaymentTransaction paymentResult = mockFBPaymentService.charge(transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        ChargeInfo first = new ChargeInfo();
        first.setCurrency(transaction.getChargeInfo().getCurrency());
        first.setAmount(new BigDecimal("5.00"));
        transaction.setChargeInfo(first);
        paymentResult = mockFBPaymentService.refund(paymentResult.getId(), transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.REFUNDED.toString());
        ChargeInfo second = new ChargeInfo();
        second.setCurrency(transaction.getChargeInfo().getCurrency());
        second.setAmount(new BigDecimal("95.00"));
        transaction.setChargeInfo(second);
        paymentResult = mockFBPaymentService.refund(paymentResult.getId(), transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.REFUNDED.toString());
    }

    //negative: over refund would failed
    @Test(enabled = false)
    public void testChargeOverRefundFB() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("4111117711552927");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        //test payment
        PaymentTransaction transaction = buildPaymentTransaction(request);
        PaymentTransaction paymentResult = mockFBPaymentService.charge(transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        ChargeInfo first = new ChargeInfo();
        first.setCurrency(transaction.getChargeInfo().getCurrency());
        first.setAmount(new BigDecimal("5.00"));
        transaction.setChargeInfo(first);
        paymentResult = mockFBPaymentService.refund(paymentResult.getId(), transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.REFUNDED.toString());
        ChargeInfo second = new ChargeInfo();
        second.setCurrency(transaction.getChargeInfo().getCurrency());
        second.setAmount(new BigDecimal("100.00"));
        transaction.setChargeInfo(second);
        try{
            paymentResult = mockFBPaymentService.refund(paymentResult.getId(), transaction).get();
        }catch (Exception ex){
            //TODO, validate exception
            return ;
        }
        throw new RuntimeException("expected exception");
    }


    //negative, should fail as reverse only apply to authorised
    @Test(enabled = false)
    public void testChargeReverseFB() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("4111117711552927");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        //test payment
        PaymentTransaction transaction = buildPaymentTransaction(request);
        PaymentTransaction paymentResult = mockFBPaymentService.charge(transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        transaction.setChargeInfo(null);

        try{
            paymentResult = mockFBPaymentService.reverse(paymentResult.getId(), transaction).get();
        }catch (Exception ex){
            //TODO, validate exception
            return;
        }
        throw new RuntimeException("expected exception");
    }

    //negative: charge fail card
    @Test(enabled = false)
    public void testChargeFailFB() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("4111119315405122");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.chargeFailToken);
        //test payment
        PaymentTransaction transaction = buildPaymentTransaction(request);

        try{
            PaymentTransaction paymentResult = mockFBPaymentService.charge(transaction).get();
        }catch (Exception ex){
            //TODO, validate exception
            return;
        }
        throw new RuntimeException("expected exception");
    }

    //negative: refund fail card
    @Test(enabled = false)
    public void testChargeRefundFailFB() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        request.setAccountNumber("4111110448424155");
        request.setBillingAddressId(null);
        request.setPhoneNumber(null);
        result = addPI(request);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.refundFailToken);
        //test payment
        PaymentTransaction transaction = buildPaymentTransaction(request);
        PaymentTransaction paymentResult = mockFBPaymentService.charge(transaction).get();
        Assert.assertEquals(paymentResult.getStatus().toString(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        ChargeInfo first = new ChargeInfo();
        first.setCurrency(transaction.getChargeInfo().getCurrency());
        first.setAmount(new BigDecimal("5.00"));
        transaction.setChargeInfo(first);

        try{
            paymentResult = mockFBPaymentService.refund(paymentResult.getId(), transaction).get();
        }catch (Exception ex){
            //TODO, validate exception
            return;
        }
        throw new RuntimeException("expected exception");
    }

    public PaymentInstrument addPI(final PaymentInstrument request){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentInstrument>() {
            public PaymentInstrument doInTransaction(TransactionStatus txnStatus) {
                return mockFBPiService.add(request).get();
            }
        });
    }

    private PaymentTransaction buildPaymentTransaction(final PaymentInstrument pi){
        PaymentTransaction payment = new PaymentTransaction();
        payment.setTrackingUuid(generateUUID());
        payment.setUserId(pi.getUserId());
        payment.setBillingRefId(BILLING_REF_ID);
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("USD");
                setAmount(new BigDecimal("100.00"));
                setBusinessDescriptor("ut");
            }
        });
        payment.setPaymentInstrumentId(pi.getId());
        return payment;
    }
}
