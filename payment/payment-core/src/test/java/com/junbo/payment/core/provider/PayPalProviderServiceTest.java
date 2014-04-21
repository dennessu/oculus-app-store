package com.junbo.payment.core.provider;

import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.PaymentCallbackService;
import com.junbo.payment.core.provider.paypal.PayPalProviderServiceImpl;
import com.junbo.payment.spec.enums.PIType;
import com.junbo.payment.spec.enums.PropertyField;
import com.junbo.payment.spec.model.*;
import junit.framework.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
                setQuantity(2);
            }
        };
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("USD");
                setAmount(new BigDecimal(3.00));
                setItems(Arrays.asList(item));
            }
        });
        payment.setWebPaymentInfo(new WebPaymentInfo() {
            {
                setReturnURL("http://www.abc.com");
                setCancelURL("http://www.abc.com/cancel");
            }
        });

        PaymentTransaction result = paymentService.charge(payment).wrapped().get();
        Assert.assertNotNull(result.getWebPaymentInfo().getToken());
        Map<PropertyField, String> properties = new HashMap<>();
        properties.put(PropertyField.EXTERNAL_ACCESS_TOKEN, result.getWebPaymentInfo().getToken());
        properties.put(PropertyField.EXTERNAL_PAYER_ID, "zwh@123.com");

        paymentService.getProviderTransaction(result.getId()).wrapped().get();
        //manual step: goo to the redirectRUL and save the PAYER_ID and token
        //paymentCallbackService.addPaymentProperties(result.getId(), properties);
        result = paymentService.confirm(result.getId(), payment).wrapped().get();
        paymentService.getProviderTransaction(result.getId()).wrapped().get();
    }

    private PaymentInstrument buildPayPalRequest(){
        PaymentInstrument request = buildBasePIRequest();
        request.setType(PIType.PAYPAL.toString());
        request.setAccountNum("zwh@123.com");
        return request;
    }
}
