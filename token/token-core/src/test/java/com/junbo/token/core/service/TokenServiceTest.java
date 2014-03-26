package com.junbo.token.core.service;


import com.junbo.token.core.BaseTest;
import com.junbo.token.core.TokenService;
import com.junbo.token.spec.enums.CreateMethod;
import com.junbo.token.spec.enums.TokenLength;
import com.junbo.token.spec.model.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class TokenServiceTest extends BaseTest {
    @Autowired
    private TokenService tokenService;

    @Test
    public void testAdd() throws ExecutionException, InterruptedException {
        OrderRequest request = new OrderRequest(){
            {
                setActivation("yes");
                setCreateMethod(CreateMethod.GENERATION.toString());
                setDescription("ut");
                setExpiredTime(null);
                setGenerationLength(TokenLength.LEN16.toString());
                setGenerationSeed(123456L);
                setOfferIds(Arrays.asList(12345L));
                setOfferType("Promotion");
                setQuantity(2L);
                setUsageLimit(1L);
            }

        };
        OrderRequest result = tokenService.createOrderRequest(request).wrapped().get();
        Assert.assertNotNull(result.getTokenItems());
        Assert.assertNotNull(result.getId());
    }
}
