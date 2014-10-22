package com.junbo.payment.core.provider;

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
        paymentResult = mockFBPaymentService.reverse(paymentResult.getId(), transaction).get();
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
