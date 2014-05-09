package com.junbo.payment.core.provider;

import com.junbo.common.id.PIType;
import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.PaymentCallbackService;
import com.junbo.payment.core.provider.adyen.AdyenProviderServiceImpl;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.ChargeInfo;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentProperties;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 14-5-8.
 */
public class AdyenProviderServiceTest extends BaseTest {
    @Autowired
    private AdyenProviderServiceImpl adyenProviderService;
    @Autowired
    private PaymentCallbackService paymentCallbackService;
    @Test(enabled = true)
    public void testCharge() throws ExecutionException, InterruptedException {
        PaymentInstrument piRequest = buildBasePIRequest();
        piRequest.setType(PIType.OTHERS.getId());
        PaymentInstrument request = addPI(piRequest);
        PaymentTransaction payment = new PaymentTransaction();
        payment.setBillingRefId("123");
        payment.setUserId(userId);
        payment.setPaymentInstrumentId(request.getId());
        payment.setTrackingUuid(generateUUID());
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("USD");
                setAmount(new BigDecimal(1500.00));
            }
        });
        PaymentTransaction result = paymentService.charge(payment).wrapped().get();
        Assert.assertNotNull(result.getWebPaymentInfo().getRedirectURL());
        //manual pay through redirectURL
        PaymentProperties properties = new PaymentProperties();
        properties.setPspReference("ut1234");
        properties.setAuthResult("AUTHORISED");
        paymentCallbackService.addPaymentProperties(result.getId(), properties);
        result = paymentService.getUpdatedTransaction(result.getId()).wrapped().get();
        Assert.assertNotNull(result.getExternalToken());
        Assert.assertNotNull(result.getStatus(), PaymentStatus.SETTLED.toString());
        boolean exception = false;
        try{
            result = paymentService.confirm(result.getId(), payment).wrapped().get();
        }catch (Exception ex){
            exception = true;
        }
        Assert.assertTrue(exception);
    }
}
