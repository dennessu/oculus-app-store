package com.junbo.payment.core.provider;

import com.junbo.payment.core.BaseTest;
import com.junbo.payment.core.provider.facebook.FacebookPaymentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;

/**
 * Created by wenzhu on 9/16/14.
 */
public class FacebookPaymentServiceTest extends BaseTest {
    @Autowired
    private FacebookPaymentUtils facebookPaymentUtils;

    @Test(enabled = false)
    public void testGetAccessToken() throws ExecutionException, InterruptedException {
        String accessToken = facebookPaymentUtils.getAccessToken().get();
        Assert.assertNotNull(accessToken);
        Assert.assertNotEquals(accessToken, "");
        Assert.assertFalse(accessToken.contains("access_token"));
    }
}
