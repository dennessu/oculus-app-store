package com.junbo.payment.core.provider;

import com.junbo.common.id.PIType;
import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.PaymentCallbackService;
import com.junbo.payment.core.provider.paypal.PayPalProviderServiceImpl;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 14-4-14.
 */
public class PayPalProviderServiceTest extends BaseTest {
    @Autowired
    private PayPalProviderServiceImpl paypalProviderService;
    @Autowired
    private PaymentCallbackService paymentCallbackService;
    @Test(enabled = false)
    public void testCharge() throws ExecutionException, InterruptedException {
        PaymentInstrument request = addPI(buildPayPalRequest());
        PaymentTransaction payment = new PaymentTransaction();
        payment.setBillingRefId("123");
        payment.setUserId(userId);
        payment.setPaymentInstrumentId(request.getId());
        payment.setTrackingUuid(generateUUID());
        final Item item = new Item(){
            {
                setAmount("1.5");
                setName("ut item");
                setQuantity(1000);
            }
        };
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("USD");
                setAmount(new BigDecimal(1500.00));
                setItems(Arrays.asList(item));
            }
        });
        payment.setWebPaymentInfo(new WebPaymentInfo() {
            {
                setReturnURL("http://www.abc.com/");
                setCancelURL("http://www.abc.com/cancel");
            }
        });

        PaymentTransaction result = paymentService.charge(payment).wrapped().get();
        Assert.assertNotNull(result.getWebPaymentInfo().getToken());
        PaymentProperties properties = new PaymentProperties();
        properties.setExternalAccessToken(result.getWebPaymentInfo().getToken());
        properties.setExternalPayerId("CCZA9BJT9NKTS");
        //manual step: should go to the redirectRUL and save the PAYER_ID and token
        paymentCallbackService.addPaymentProperties(result.getId(), properties);
        PaymentTransaction newStatus = paymentService.getUpdatedTransaction(result.getId()).wrapped().get();
        Assert.assertEquals(newStatus.getStatus(), PaymentStatus.UNCONFIRMED.toString());
        //manual step: should go to the redirectRUL and save the PAYER_ID and token
        result = paymentService.confirm(result.getId(), payment).wrapped().get();
        Assert.assertNotNull(result.getExternalToken());
        Assert.assertNotNull(result.getStatus(), PaymentStatus.SETTLED.toString());
        newStatus = paymentService.getUpdatedTransaction(result.getId()).wrapped().get();
        Assert.assertEquals(newStatus.getStatus(), PaymentStatus.SETTLED.toString());
    }

    private PaymentInstrument buildPayPalRequest(){
        PaymentInstrument request = buildBasePIRequest();
        request.setType(PIType.PAYPAL.getId());
        request.setAccountNum("zwh@123.com");
        return request;
    }
}
