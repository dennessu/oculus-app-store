package com.junbo.token.core.service;


import com.junbo.token.core.BaseTest;
import com.junbo.token.core.TokenService;
import com.junbo.token.spec.enums.CreateMethod;
import com.junbo.token.spec.enums.ItemStatus;
import com.junbo.token.spec.enums.ProductType;
import com.junbo.token.spec.enums.TokenLength;
import com.junbo.token.spec.model.TokenRequest;
import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class TokenServiceTest extends BaseTest {
    private TokenService tokenService;
    @Autowired
    public void setTokenService(@Qualifier("mockTokenService")TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Test(enabled = true)
    public void testGenerate() throws ExecutionException, InterruptedException {
        TokenRequest request = new TokenRequest(){
            {
                setActivation("yes");
                setCreateMethod(CreateMethod.GENERATION.toString());
                setDescription("ut");
                setExpiredTime(null);
                setGenerationLength(TokenLength.LEN16.toString());
                setOfferIds(Arrays.asList(12345L));
                setProductType(ProductType.OFFER.toString());
                setQuantity(2L);
                setUsageLimit("1");
            }
        };
        TokenRequest result = tokenService.createOrderRequest(request).get();
        Assert.assertNotNull(result.getTokenItems());
        Assert.assertNotNull(result.getId());
        TokenRequest getResult = tokenService.getOrderRequest(result.getId()).get();
        Assert.assertEquals(getResult.getQuantity(), (Long)2L);
        Assert.assertEquals(result.getOfferIds().get(0), getResult.getOfferIds().get(0));
    }

    @Test(enabled = true)
    public void testUpload() throws ExecutionException, InterruptedException {
        final TokenItem item = new TokenItem(){
            {
                setEncryptedString("item1" + generateLong());
            }
        };
        final TokenItem item2 = new TokenItem(){
            {
                setEncryptedString("item2" + generateLong());
            }
        };
        TokenRequest request = new TokenRequest(){
            {
                setActivation("yes");
                setCreateMethod(CreateMethod.UPLOAD.toString());
                setDescription("ut");
                setExpiredTime(null);
                setOfferIds(Arrays.asList(12345L));
                setProductType(ProductType.OFFER.toString());
                setUsageLimit("1");
                setTokenItems(Arrays.asList(item, item2));
            }

        };
        TokenRequest result = tokenService.createOrderRequest(request).get();
        Assert.assertNotNull(result.getTokenItems());
        Assert.assertNotNull(result.getId());
        TokenRequest getResult = tokenService.getOrderRequest(result.getId()).get();
        Assert.assertEquals(getResult.getQuantity(), (Long)2L);
        Assert.assertEquals(result.getOfferIds().get(0), getResult.getOfferIds().get(0));
    }

    @Test(enabled = true)
    public void testConsume() throws ExecutionException, InterruptedException {
        final TokenItem item = new TokenItem(){
            {
                setEncryptedString("item1" + generateLong());
            }
        };
        final TokenItem item2 = new TokenItem(){
            {
                setEncryptedString("item2" + generateLong());
            }
        };
        TokenRequest request = new TokenRequest(){
            {
                setActivation("yes");
                setCreateMethod(CreateMethod.UPLOAD.toString());
                setDescription("ut");
                setExpiredTime(null);
                setOfferIds(Arrays.asList(12345L));
                setProductType(ProductType.OFFER.toString());
                setUsageLimit("1");
                setTokenItems(Arrays.asList(item, item2));
            }

        };
        TokenRequest result = tokenService.createOrderRequest(request).get();
        Assert.assertNotNull(result.getTokenItems());
        Assert.assertNotNull(result.getId());
        TokenConsumption consumption = new TokenConsumption(){
            {
                setProduct(12345L);
                setUserId(generateLong());
            }
        };
        TokenConsumption consumedItem = tokenService.consumeToken(item.getEncryptedString(), consumption).get();
        TokenItem getItem = tokenService.getToken(item.getEncryptedString()).get();
        Assert.assertEquals(getItem.getStatus(), ItemStatus.USED.toString());
    }
}
