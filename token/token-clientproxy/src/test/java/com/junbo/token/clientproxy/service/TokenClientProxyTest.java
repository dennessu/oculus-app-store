package com.junbo.token.clientproxy.service;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.TokenOrderId;
import com.junbo.common.id.UserId;
import com.junbo.token.clientproxy.BaseTest;
import com.junbo.token.common.exception.AppClientExceptions;
import com.junbo.token.spec.enums.ItemStatus;
import com.junbo.token.spec.enums.OrderStatus;
import com.junbo.token.spec.enums.TokenLength;
import com.junbo.token.spec.model.OrderRequest;
import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.resource.proxy.TokenResourceClientProxy;
import junit.framework.Assert;
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
        OrderRequest order = new OrderRequest(){
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
        OrderRequest result = tokenClient.postOrder(order).get();
        Assert.assertEquals(result.getStatus(), OrderStatus.COMPLETED.toString());
        Assert.assertEquals(result.getTokenItems().size(), 1);

        OrderRequest getResult = tokenClient.getOrderById(new TokenOrderId(result.getId())).get();
        Assert.assertEquals(result.getStatus(), OrderStatus.COMPLETED.toString());
        Assert.assertEquals(result.getId(), getResult.getId());
        TokenConsumption consumption= new TokenConsumption(){
            {
                setUserId(userId.getValue());
                setProduct(10000L);
            }
        };
        String tokenString = result.getTokenItems().get(0).getEncryptedString();
        TokenItem consumeResult = tokenClient.consumeToken(tokenString, consumption).get();
        Assert.assertEquals(consumeResult.getStatus(), ItemStatus.USED.toString());
        Assert.assertEquals(consumeResult.getTokenConsumptions().size(), 1);
        Assert.assertEquals(consumeResult.getTokenConsumptions().get(0).getProduct(), new Long(10000L));
    }

    @Test(enabled = false)
    public void updateToken() throws ExecutionException, InterruptedException {
        final UserId userId = new UserId(generateLong());
        OrderRequest order = new OrderRequest(){
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
        OrderRequest result = tokenClient.postOrder(order).get();
        String tokenString = result.getTokenItems().get(0).getEncryptedString();
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
            }
        };
        try{
            TokenItem consumeResult = tokenClient.consumeToken(tokenString, consumption).get();
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
        TokenItem consumeResult = tokenClient.consumeToken(tokenString, consumption).get();
        item = tokenClient.getToken(tokenString).get();
        Assert.assertEquals(item.getStatus(), ItemStatus.USED.toString());
    }
}
