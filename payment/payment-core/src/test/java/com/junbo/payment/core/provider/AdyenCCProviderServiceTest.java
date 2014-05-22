package com.junbo.payment.core.provider;

import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.PaymentCallbackService;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.core.provider.adyen.AdyenCCProivderServiceImpl;
import com.junbo.payment.core.provider.adyen.AdyenProviderServiceImpl;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.ChargeInfo;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * Created by Administrator on 14-5-20.
 */
public class AdyenCCProviderServiceTest  extends BaseTest {

    private AdyenCCProivderServiceImpl adyenProviderService;

    private PaymentInstrumentService mockAdyenPIService;

    protected PaymentTransactionService mockAdyenPaymentService;

    public void setAdyenProviderService(AdyenCCProivderServiceImpl adyenProviderService) {
        this.adyenProviderService = adyenProviderService;
    }
    @Autowired
    public void setMockAdyenPIService(@Qualifier("mockAdyenPaymentInstrumentService")PaymentInstrumentService mockAdyenPIService) {
        this.mockAdyenPIService = mockAdyenPIService;
    }
    @Autowired
    public void setMockAdyenPaymentService(@Qualifier("mockAdyenPaymentService")PaymentTransactionService mockAdyenPaymentService) {
        this.mockAdyenPaymentService = mockAdyenPaymentService;
    }
    @Test(enabled = false)
    public void testAddPI(){
        PaymentInstrument request = buildPIRequest();
        //AccountNumber is the js encrypted value for card : 4111111111111111, 737, 2016-06
        request.setAccountNum("4111111111111111");
        PaymentInstrument result = null;
        result = mockAdyenPIService.add(request).get();
        Assert.assertNotNull(result.getExternalToken());
    }

    @Test(enabled = false)
    public void testAddPIAndAuthCapture(){
        /*
        PaymentInstrument request = buildPIRequest();
        //AccountNumber is the js encrypted value for card : 4111111111111111, 737, 2016-06
        request.setAccountNum("4111111111111111");
        PaymentInstrument result = null;
        result = mockAdyenPIService.add(request).get();
        Assert.assertNotNull(result.getExternalToken());
        */
        PaymentTransaction payment = new PaymentTransaction();
        payment.setBillingRefId("123");
        payment.setUserId(userId);
        //TODO: do a work around here for test use find an Id in DB with HPP:
        payment.setPaymentInstrumentId(2197815296L);
        //end of hack
        payment.setTrackingUuid(generateUUID());
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("USD");
                setAmount(new BigDecimal(1500.00));
            }
        });
        PaymentTransaction autResult = mockAdyenPaymentService.authorize(payment).get();
        Assert.assertNotNull(autResult.getExternalToken());
        Assert.assertEquals(autResult.getStatus(), PaymentStatus.AUTHORIZED.toString());

        PaymentTransaction capResult = mockAdyenPaymentService.capture(autResult.getId(), payment).get();
        Assert.assertEquals(capResult.getStatus(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
    }

    @Test(enabled = false)
    public void testAddPIAndChargeRefund(){
        /*
        PaymentInstrument request = buildPIRequest();
        //AccountNumber is the js encrypted value for card : 4111111111111111, 737, 2016-06
        request.setAccountNum("4111111111111111");
        PaymentInstrument result = null;
        result = mockAdyenPIService.add(request).get();
        Assert.assertNotNull(result.getExternalToken());
        */
        PaymentTransaction payment = new PaymentTransaction();
        payment.setBillingRefId("123");
        payment.setUserId(userId);
        //TODO: do a work around here for test use find an Id in DB with HPP:
        payment.setPaymentInstrumentId(2197815296L);
        //end of hack
        payment.setTrackingUuid(generateUUID());
        payment.setChargeInfo(new ChargeInfo() {
            {
                setCurrency("USD");
                setAmount(new BigDecimal(1500.00));
            }
        });
        PaymentTransaction autResult = mockAdyenPaymentService.charge(payment).get();
        Assert.assertNotNull(autResult.getExternalToken());
        Assert.assertEquals(autResult.getStatus(), PaymentStatus.SETTLEMENT_SUBMITTED.toString());
        payment.setChargeInfo(null);
        PaymentTransaction cancelResult = mockAdyenPaymentService.reverse(autResult.getId(), payment).get();
        Assert.assertEquals(cancelResult.getStatus(), PaymentStatus.REVERSED.toString());
    }
}
