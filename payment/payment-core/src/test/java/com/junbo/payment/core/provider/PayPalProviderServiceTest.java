package com.junbo.payment.core.provider;

import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.provider.paypal.PayPalProviderServiceImpl;
import com.junbo.payment.spec.enums.PIType;
import com.junbo.payment.spec.model.*;
import junit.framework.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 14-4-14.
 */
public class PayPalProviderServiceTest extends BaseTest {
    @Autowired
    private PayPalProviderServiceImpl paypalProviderService;
    @Test(enabled = false)
    public void testCharge() throws ExecutionException, InterruptedException {
        PaymentInstrument request = addPI(buildPayPalRequest());
        PaymentTransaction payment = new PaymentTransaction();
        payment.setBillingRefId("123");
        payment.setPaymentInstrumentId(request.getId());
        payment.setTrackingUuid(generateUUID());
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("USD");
                setAmount(new BigDecimal(10.00));
            }
        });
        payment.setWebPaymentInfo(new WebPaymentInfo(){
            {
                setReturnURL("www.abc.com");
                setCancelURL("www.abc.com/cancel");
            }
        });
        PaymentTransaction result = paymentService.charge(payment).wrapped().get();
        Assert.assertNotNull(result.getExternalToken());
        Assert.assertNotNull(result.getWebPaymentInfo().getToken());
    }

    private PaymentInstrument buildPayPalRequest(){
        PaymentInstrument request = buildBasePIRequest();
        request.setType(PIType.PAYPAL.toString());
        request.setEmail("zwh@123.com");
        return request;
    }
}
