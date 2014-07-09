package com.junbo.token.clientproxy.service;

import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.TokenOrderId;
import com.junbo.common.id.UserId;
import com.junbo.crypto.spec.model.CryptoMessage;
import com.junbo.crypto.spec.resource.CryptoResource;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.resource.UserResource;
import com.junbo.token.clientproxy.BaseTest;
import com.junbo.token.common.exception.AppClientExceptions;
import com.junbo.token.spec.enums.ItemStatus;
import com.junbo.token.spec.enums.OrderStatus;
import com.junbo.token.spec.enums.TokenLength;
import com.junbo.token.spec.model.ProductDetail;
import com.junbo.token.spec.model.TokenRequest;
import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.resource.proxy.TokenResourceClientProxy;
import org.testng.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class TokenClientProxyTest extends BaseTest {
    @Autowired
    private TokenResourceClientProxy tokenClient;
    @Autowired
    private OfferResource tokenOfferClient;
    @Autowired
    private UserResource tokenUserClient;
    @Autowired
    private OfferRevisionResource tokenOfferRevisionClient;
    @Autowired
    private CryptoResource cryptoResource;

    @Test(enabled = false)
    public void createOrderAndConsumeToken() throws ExecutionException, InterruptedException {
        Offer offer = new Offer();
        offer.setOwnerId(new OrganizationId(generateLong()));
        final Offer resultOffer = tokenOfferClient.create(offer).get();
        OfferRevision revision = new OfferRevision();
        revision.setOfferId(resultOffer.getOfferId());
        revision.setStatus("DRAFT");
        revision.setOwnerId(new OrganizationId(generateLong()));
        Price price = new Price();
        price.setPriceType("CUSTOM");
        Map<String, Map<String, BigDecimal>> prices = new HashMap<String, Map<String, BigDecimal>>();
        Map<String, BigDecimal> detailPrices = new HashMap<String, BigDecimal>();
        detailPrices.put("USD", new BigDecimal("10.00"));
        prices.put("US", detailPrices);
        price.setPrices(prices);
        revision.setPrice(price);
        Map<String, OfferRevisionLocaleProperties> locales = new HashMap<String, OfferRevisionLocaleProperties>();
        OfferRevisionLocaleProperties locale = new OfferRevisionLocaleProperties();
        locale.setName("en_US");
        locales.put("en_US", locale);
        revision.setLocales(locales);
        tokenOfferRevisionClient.createOfferRevision(revision).get();
        User userRequest = new User(){
            {
                setUsername("ut" + generateLong());
            }
        };
        final User userResult = tokenUserClient.create(userRequest).get();
        final ProductDetail product = new ProductDetail();
        product.setDefaultOffer(resultOffer.getOfferId());
        TokenRequest order = new TokenRequest(){
            {
                setActivation("yes");
                setCreateMethod("generation");
                setDescription("test");
                setGenerationLength(TokenLength.LEN20.toString());
                setProductDetail(product);
                setProductType("Offer");
                setQuantity(1L);
                setUsageLimit("1");
            }
        };
        TokenRequest result = tokenClient.postOrder(order).get();
        Assert.assertEquals(result.getStatus(), OrderStatus.COMPLETED.toString());
        Assert.assertEquals(result.getTokenItems().size(), 1);

        TokenRequest getResult = tokenClient.getOrderById(result.getId()).get();
        Assert.assertEquals(result.getStatus(), OrderStatus.COMPLETED.toString());
        Assert.assertEquals(result.getId(), getResult.getId());
        String encryptedTokenString = result.getTokenItems().get(0).getEncryptedString();
        CryptoMessage msg = new CryptoMessage();
        msg.setValue(encryptedTokenString);
        final String tokenString = cryptoResource.decrypt(msg).get().getValue();

        TokenConsumption consumption= new TokenConsumption(){
            {
                setUserId(userResult.getId().getValue());
                setProduct(resultOffer.getId());
                setTokenString(tokenString);
            }
        };
        TokenConsumption consumeResult = tokenClient.consumeToken(consumption).get();
        Assert.assertEquals(consumeResult.getProduct(), resultOffer.getOfferId());
        TokenItem itemResult = tokenClient.getToken(tokenString).get();
        Assert.assertEquals(itemResult.getStatus(), ItemStatus.USED.toString());
    }

    @Test(enabled = false)
    public void updateToken() throws ExecutionException, InterruptedException {
        Offer offer = new Offer();
        offer.setOwnerId(new OrganizationId(generateLong()));
        final Offer resultOffer = tokenOfferClient.create(offer).get();
        OfferRevision revision = new OfferRevision();
        revision.setOfferId(resultOffer.getOfferId());
        revision.setStatus("DRAFT");
        revision.setOwnerId(new OrganizationId(generateLong()));
        Price price = new Price();
        price.setPriceType("CUSTOM");
        Map<String, Map<String, BigDecimal>> prices = new HashMap<String, Map<String, BigDecimal>>();
        Map<String, BigDecimal> detailPrices = new HashMap<String, BigDecimal>();
        detailPrices.put("USD", new BigDecimal("10.00"));
        prices.put("US", detailPrices);
        price.setPrices(prices);
        revision.setPrice(price);
        Map<String, OfferRevisionLocaleProperties> locales = new HashMap<String, OfferRevisionLocaleProperties>();
        OfferRevisionLocaleProperties locale = new OfferRevisionLocaleProperties();
        locale.setName("en_US");
        locales.put("en_US", locale);
        revision.setLocales(locales);
        tokenOfferRevisionClient.createOfferRevision(revision).get();
        User userRequest = new User(){
            {
                setUsername("ut" + generateLong());
            }
        };
        final User userResult = tokenUserClient.create(userRequest).get();
        final ProductDetail product = new ProductDetail();
        product.setDefaultOffer(resultOffer.getId());
        TokenRequest order = new TokenRequest(){
            {
                setActivation("yes");
                setCreateMethod("generation");
                setDescription("test");
                setGenerationLength(TokenLength.LEN20.toString());
                setProductDetail(product);
                setProductType("Offer");
                setQuantity(1L);
                setUsageLimit("1");
            }
        };
        TokenRequest result = tokenClient.postOrder(order).get();
        String encryptedTokenString = result.getTokenItems().get(0).getEncryptedString();
        CryptoMessage msg = new CryptoMessage();
        msg.setValue(encryptedTokenString);
        final String tokenString = cryptoResource.decrypt(msg).get().getValue();

        TokenItem item = tokenClient.getToken(tokenString).get();
        Assert.assertEquals(item.getStatus(), ItemStatus.ACTIVATED.toString());
        item.setStatus(ItemStatus.BLACKLISTED.toString());
        item.setDisableReason("ut");
        TokenItem updatedItem = tokenClient.updateToken(tokenString, item).get();
        item = tokenClient.getToken(tokenString).get();
        Assert.assertEquals(item.getStatus(), ItemStatus.BLACKLISTED.toString());
        TokenConsumption consumption= new TokenConsumption(){
            {
                setUserId(userResult.getId().getValue());
                setProduct(resultOffer.getId());
                setTokenString(tokenString);
            }
        };
        try{
            TokenConsumption consumeResult = tokenClient.consumeToken(consumption).get();
        }catch (Exception ex){
            if(ex instanceof AppClientExceptions){
                String code = ((AppClientExceptions) ex).invalidToken().error().getCode();
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
