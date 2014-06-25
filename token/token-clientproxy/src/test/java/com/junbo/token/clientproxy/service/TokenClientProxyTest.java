package com.junbo.token.clientproxy.service;

import com.junbo.common.id.TokenOrderId;
import com.junbo.common.id.UserId;
import com.junbo.token.clientproxy.BaseTest;
import com.junbo.token.common.exception.AppClientExceptions;
import com.junbo.token.spec.enums.ItemStatus;
import com.junbo.token.spec.enums.OrderStatus;
import com.junbo.token.spec.enums.TokenLength;
import com.junbo.token.spec.model.TokenRequest;
import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.resource.proxy.TokenResourceClientProxy;
import org.testng.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class TokenClientProxyTest extends BaseTest {
    @Autowired
    private TokenResourceClientProxy tokenClient;

    @Test(enabled = false)
    public void createOrderAndConsumeToken() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(generateLong());
        TokenRequest order = new TokenRequest(){
            {
                setActivation("yes");
                setCreateMethod("generation");
                setDescription("test");
                setGenerationLength(TokenLength.LEN20.toString());
                setOfferIds(Arrays.asList(10000L));
                setProductType("Offer");
                setQuantity(1L);
                setUsageLimit("1");
            }
        };
        TokenRequest result = tokenClient.postOrder(order).get();
        Assert.assertEquals(result.getStatus(), OrderStatus.COMPLETED.toString());
        Assert.assertEquals(result.getTokenItems().size(), 1);

        TokenRequest getResult = tokenClient.getOrderById(new TokenOrderId(result.getId())).get();
        Assert.assertEquals(result.getStatus(), OrderStatus.COMPLETED.toString());
        Assert.assertEquals(result.getId(), getResult.getId());
        final String tokenString = result.getTokenItems().get(0).getEncryptedString();
        TokenConsumption consumption= new TokenConsumption(){
            {
                setUserId(userId.getValue());
                setProduct(10000L);
                setTokenString(tokenString);
            }
        };
        TokenConsumption consumeResult = tokenClient.consumeToken(consumption).get();
        Assert.assertEquals(consumeResult.getProduct(), new Long(10000L));
    }

    @Test(enabled = false)
    public void updateToken() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(generateLong());
        TokenRequest order = new TokenRequest(){
            {
                setActivation("yes");
                setCreateMethod("generation");
                setDescription("test");
                setGenerationLength(TokenLength.LEN20.toString());
                setOfferIds(Arrays.asList(10000L));
                setProductType("Offer");
                setQuantity(1L);
                setUsageLimit("1");
            }
        };
        TokenRequest result = tokenClient.postOrder(order).get();
        final String tokenString = result.getTokenItems().get(0).getEncryptedString();
        TokenItem item = tokenClient.getToken(tokenString).get();
        Assert.assertEquals(item.getStatus(), ItemStatus.ACTIVATED.toString());
        item.setStatus(ItemStatus.BLACKLISTED.toString());
        item.setDisableReason("ut");
        TokenItem updatedItem = tokenClient.updateToken(tokenString, item).get();
        item = tokenClient.getToken(tokenString).get();
        Assert.assertEquals(item.getStatus(), ItemStatus.BLACKLISTED.toString());
        TokenConsumption consumption= new TokenConsumption(){
            {
                setUserId(userId.getValue());
                setProduct(10000L);
                setTokenString(tokenString);
            }
        };
        try{
            TokenConsumption consumeResult = tokenClient.consumeToken(consumption).get();
        }catch (Exception ex){
            if(ex instanceof AppClientExceptions){
                String code = ((AppClientExceptions) ex).invalidToken().getCode();
            }
        }
        item.setStatus(ItemStatus.ACTIVATED.toString());
        item.setDisableReason("ut");
        updatedItem = tokenClient.updateToken(tokenString, item).get();
        item = tokenClient.getToken(tokenString).get();
        Assert.assertEquals(item.getStatus(), ItemStatus.ACTIVATED.toString());
        TokenConsumption consumeResult = tokenClient.consumeToken(consumption).get();
        item = tokenClient.getToken(tokenString).get();
        Assert.assertEquals(item.getStatus(), ItemStatus.USED.toString());
    }
}
