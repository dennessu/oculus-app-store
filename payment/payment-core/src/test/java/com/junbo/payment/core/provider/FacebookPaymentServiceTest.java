package com.junbo.payment.core.provider;

import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.mock.MockPaymentProviderServiceImpl;
import com.junbo.payment.core.provider.facebook.FacebookCCProviderServiceImpl;
import com.junbo.payment.core.provider.facebook.FacebookPaymentUtils;
import com.junbo.payment.spec.model.PaymentInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;

/**
 * Created by wenzhu on 9/16/14.
 */
public class FacebookPaymentServiceTest extends BaseTest {
    private FacebookCCProviderServiceImpl fbProviderService;
    @Autowired
    private FacebookPaymentUtils facebookPaymentUtils;
    private PaymentInstrumentService mockFBPiService;

    @Autowired
    public void setMockFBPiService(@Qualifier("mockFBPaymentInstrumentService")PaymentInstrumentService mockFBPiService) {
        this.mockFBPiService = mockFBPiService;
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
    public void testAddFacebookPI() throws ExecutionException, InterruptedException {
        PaymentInstrument request = buildPIRequest();
        //hard code user to avoid create too many test users
        request.setUserId(83886144L);
        PaymentInstrument result = null;
        result = mockFBPiService.add(request).get();
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getTypeSpecificDetails().getCreditCardType(), "VISA");
        Assert.assertEquals(result.getExternalToken(), MockPaymentProviderServiceImpl.piExternalToken);
        PaymentInstrument getResult = mockFBPiService.getById(result.getId()).get();
        Assert.assertEquals(getResult.getAccountName(), result.getAccountName());
    }
}
