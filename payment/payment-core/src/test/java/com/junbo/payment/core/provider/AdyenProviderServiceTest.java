package com.junbo.payment.core.provider;

import com.junbo.common.id.PIType;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.PaymentCallbackService;
import com.junbo.payment.core.provider.adyen.AdyenProviderServiceImpl;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.enums.Platform;
import com.junbo.payment.spec.model.*;
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
                setAmount(new BigDecimal("12.34"));
            }
        });
        PaymentTransaction result = paymentService.charge(payment).get();
        Assert.assertNotNull(result.getWebPaymentInfo().getRedirectURL());
        Assert.assertEquals(result.getStatus(), PaymentStatus.UNCONFIRMED.toString());
        //manual pay through redirectURL
        String callbackRequest = getMockCallbackRequest(result);
        paymentCallbackService.addPaymentProperties(callbackRequest).get();
        result = paymentService.getUpdatedTransaction(result.getId()).get();
        Assert.assertNotNull(result.getExternalToken());
        Assert.assertNotNull(result.getStatus(), PaymentStatus.SETTLED.toString());
        //Charge the user again, this time. there should be no ReturnURL and the transaction becomes settled immediately as we use recurring
        payment.setTrackingUuid(generateUUID());
        payment.setId(null);
        payment.setPaymentEvents(null);
        result = paymentService.charge(payment).get();
        Assert.assertNull(result.getWebPaymentInfo());
        Assert.assertEquals(result.getStatus(), PaymentStatus.SETTLED.toString());
    }

    @Test(enabled = false)
    public void testNotify() throws ExecutionException, InterruptedException {
        String notifyMsg = "pspReference=8614093008843994&eventDate=2014-08-29T08%3A28%3A04.42Z&merchantAccountCode=OculusCOM&reason=34914%3A1111%3A6%2F2016&originalReference=&value=1083&eventCode=AUTHORISATION&merchantReference=$merchantRef&operations=CANCEL%2CCAPTURE%2CREFUND&success=true&paymentMethod=visa&currency=USD&live=false";
        String requestMsg = notifyMsg.replace("$merchantRef", "2BD5FFADBC9F");
        paymentService.processNotification(PaymentProvider.Adyen, requestMsg).get();
    }

    private String getMockCallbackRequest(PaymentTransaction result) {
        String strToSign="AUTHORISEDut1234" + CommonUtil.encode(result.getId()) + "0ceFRQOp3650-3399-5423";
        String sign = CommonUtil.calHMCASHA1(strToSign, "1234");
        String callbackRequest = "provider=Adyen&merchantReference=" + CommonUtil.encode(result.getId()) +
                "&skinCode=0ceFRQOp&shopperLocale=en_GB" +
                "&paymentMethod=unionpay&authResult=AUTHORISED&pspReference=ut1234&merchantReturnData=3650-3399-5423" +
                "&merchantSig=";
        callbackRequest += sign;
        return callbackRequest;
    }

    @Test(enabled = false)
    public void testMobileCharge() throws ExecutionException, InterruptedException {
        PaymentInstrument piRequest = buildBasePIRequest();
        piRequest.setType(PIType.OTHERS.getId());
        PaymentInstrument request = addPI(piRequest);
        PaymentTransaction payment = new PaymentTransaction();
        payment.setBillingRefId("123");
        payment.setUserId(piRequest.getUserId());
        payment.setPaymentInstrumentId(request.getId());
        payment.setTrackingUuid(generateUUID());
        WebPaymentInfo webPaymentInfo = new WebPaymentInfo(){
            {
                setPlatform(Platform.Mobile);
            }
        };
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("CNY");
                setAmount(new BigDecimal("12.34"));
            }
        });
        payment.setWebPaymentInfo(webPaymentInfo);
        PaymentTransaction result = paymentService.charge(payment).get();
        Assert.assertNotNull(result.getWebPaymentInfo().getRedirectURL());
        Assert.assertEquals(result.getStatus(), PaymentStatus.UNCONFIRMED.toString());
        //manual pay through redirectURL
        String callbackRequest = getMockCallbackRequest(result);
        paymentCallbackService.addPaymentProperties(callbackRequest).get();
        result = paymentService.getUpdatedTransaction(result.getId()).get();
        Assert.assertNotNull(result.getExternalToken());
        Assert.assertNotNull(result.getStatus(), PaymentStatus.SETTLED.toString());
        //Charge the user again, this time. there should be no ReturnURL and the transaction becomes settled immediately as we use recurring
        payment.setTrackingUuid(generateUUID());
        payment.setId(null);
        payment.setPaymentEvents(null);
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
        String callbackRequest = getMockCallbackRequest(result);
        paymentCallbackService.addPaymentProperties(callbackRequest).get();
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
        String callbackRequest = getMockCallbackRequest(result);
        paymentCallbackService.addPaymentProperties(callbackRequest).get();
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
