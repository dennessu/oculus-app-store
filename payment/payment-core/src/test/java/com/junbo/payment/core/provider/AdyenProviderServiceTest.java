package com.junbo.payment.core.provider;

import com.junbo.common.id.PIType;
import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.PaymentCallbackService;
import com.junbo.payment.core.provider.adyen.AdyenProviderServiceImpl;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.ChargeInfo;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentCallbackParams;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 14-5-8.
 */
public class AdyenProviderServiceTest extends BaseTest {
    @Autowired
    private AdyenProviderServiceImpl adyenProviderService;
    @Autowired
    private PaymentCallbackService paymentCallbackService;
    @Test(enabled = false)
    public void testCharge() throws ExecutionException, InterruptedException {
        PaymentInstrument piRequest = buildBasePIRequest();
        piRequest.setType(PIType.OTHERS.getId());
        PaymentInstrument request = addPI(piRequest);
        PaymentTransaction payment = new PaymentTransaction();
        payment.setBillingRefId("123");
        payment.setUserId(piRequest.getUserId());
        payment.setPaymentInstrumentId(request.getId());
        payment.setTrackingUuid(generateUUID());
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("CNY");
                setAmount(new BigDecimal(1500.00));
            }
        });
        PaymentTransaction result = paymentService.charge(payment).get();
        Assert.assertNotNull(result.getWebPaymentInfo().getRedirectURL());
        Assert.assertEquals(result.getStatus(), PaymentStatus.UNCONFIRMED.toString());
        //manual pay through redirectURL
        PaymentCallbackParams properties = new PaymentCallbackParams();
        properties.setPspReference("ut1234");
        properties.setAuthResult("AUTHORISED");
        paymentCallbackService.addPaymentProperties(result.getId(), properties);
        result = paymentService.getUpdatedTransaction(result.getId()).get();
        Assert.assertNotNull(result.getExternalToken());
        Assert.assertNotNull(result.getStatus(), PaymentStatus.SETTLED.toString());
        boolean exception = false;
        try{
            result = paymentService.confirm(result.getId(), payment).get();
        }catch (Exception ex){
            exception = true;
        }
        //since the transaction is settled, so the exception should be true.
        Assert.assertTrue(exception);
        //Charge the user again, this time. there should be no ReturnURL and the transaction becomes settled immediately as we use recurring
        payment.setTrackingUuid(generateUUID());
        payment.setId(null);
        result = paymentService.charge(payment).get();
        Assert.assertNull(result.getWebPaymentInfo());
        Assert.assertEquals(result.getStatus(), PaymentStatus.SETTLED.toString());
    }

    @Test(enabled = false)
    public void testChargeAndCancel() throws ExecutionException, InterruptedException {
        PaymentInstrument piRequest = buildBasePIRequest();
        piRequest.setType(PIType.OTHERS.getId());
        PaymentInstrument request = addPI(piRequest);
        PaymentTransaction payment = new PaymentTransaction();
        payment.setBillingRefId("123");
        payment.setUserId(piRequest.getUserId());
        payment.setPaymentInstrumentId(request.getId());
        payment.setTrackingUuid(generateUUID());
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("EUR");
                setAmount(new BigDecimal(1500.00));
            }
        });
        PaymentTransaction result = paymentService.charge(payment).get();
        Assert.assertNotNull(result.getWebPaymentInfo().getRedirectURL());
        Assert.assertEquals(result.getStatus(), PaymentStatus.UNCONFIRMED.toString());
        //manual pay through redirectURL
        PaymentCallbackParams properties = new PaymentCallbackParams();
        properties.setPspReference("ut1234");
        properties.setAuthResult("AUTHORISED");
        paymentCallbackService.addPaymentProperties(result.getId(), properties).get();
        result = paymentService.getUpdatedTransaction(result.getId()).get();
        Assert.assertNotNull(result.getExternalToken());
        Assert.assertNotNull(result.getStatus(), PaymentStatus.SETTLED.toString());
        boolean exception = false;
        try{
            result = paymentService.confirm(result.getId(), payment).get();
        }catch (Exception ex){
            exception = true;
        }
        //since the transaction is settled, so the exception should be true.
        Assert.assertTrue(exception);
        //Charge the user again, this time. there should be no ReturnURL and the transaction becomes settled immediately as we use recurring
        payment.setTrackingUuid(generateUUID());
        payment.setId(null);
        result = paymentService.charge(payment).get();
        Assert.assertNull(result.getWebPaymentInfo());
        Assert.assertEquals(result.getStatus(), PaymentStatus.SETTLED.toString());
        payment.setChargeInfo(null);
        result = paymentService.reverse(result.getId(), payment).get();
        Assert.assertEquals(result.getStatus(), PaymentStatus.REVERSED.toString());
    }

    @Test(enabled = false)
    public void testChargeAndRefund() throws ExecutionException, InterruptedException {
        PaymentInstrument piRequest = buildBasePIRequest();
        piRequest.setType(PIType.OTHERS.getId());
        PaymentInstrument request = addPI(piRequest);
        PaymentTransaction payment = new PaymentTransaction();
        payment.setBillingRefId("123");
        payment.setUserId(piRequest.getUserId());
        payment.setPaymentInstrumentId(request.getId());
        payment.setTrackingUuid(generateUUID());
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCountry("DE");
                setCurrency("EUR");
                setAmount(new BigDecimal(1500.00));
            }
        });
        PaymentTransaction result = paymentService.charge(payment).get();
        Assert.assertNotNull(result.getWebPaymentInfo().getRedirectURL());
        Assert.assertEquals(result.getStatus(), PaymentStatus.UNCONFIRMED.toString());
        //manual pay through redirectURL
        PaymentCallbackParams properties = new PaymentCallbackParams();
        properties.setPspReference("ut1234");
        properties.setAuthResult("AUTHORISED");
        paymentCallbackService.addPaymentProperties(result.getId(), properties).get();
        result = paymentService.getUpdatedTransaction(result.getId()).get();
        Assert.assertNotNull(result.getExternalToken());
        Assert.assertNotNull(result.getStatus(), PaymentStatus.SETTLED.toString());
        boolean exception = false;
        try{
            result = paymentService.confirm(result.getId(), payment).get();
        }catch (Exception ex){
            exception = true;
        }
        //since the transaction is settled, so the exception should be true.
        Assert.assertTrue(exception);
        //Charge the user again, this time. there should be no ReturnURL and the transaction becomes settled immediately as we use recurring
        payment.setTrackingUuid(generateUUID());
        payment.setId(null);
        result = paymentService.charge(payment).get();
        Assert.assertNull(result.getWebPaymentInfo());
        Assert.assertEquals(result.getStatus(), PaymentStatus.SETTLED.toString());
        result = paymentService.refund(result.getId(), payment).get();
        Assert.assertEquals(result.getStatus(), PaymentStatus.REFUNDED.toString());
    }
}
