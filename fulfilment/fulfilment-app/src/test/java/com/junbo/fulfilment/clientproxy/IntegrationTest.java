package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.model.common.LocalizableProperty;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemType;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.common.id.FulfilmentId;
import com.junbo.common.id.OrderId;
import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.spec.constant.FulfilmentStatus;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.fulfilment.spec.resource.FulfilmentResource;
import com.junbo.langur.core.client.ClientResponseException;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public class IntegrationTest extends AbstractTestNGSpringContextTests {
    @Autowired
    protected MegaGateway megaGateway;

    @Autowired
    @Qualifier("fulfilmentClient")
    private FulfilmentResource fulfilmentResource;

    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    @Test(enabled = false)
    public void testBVT() {
        Long offerId = prepareOffer();
        Assert.assertNotNull(offerId, "offerId should not be null.");

        internalBVT(offerId);
    }

    @Test(enabled = false)
    public void testEwalletBVT() {
        Long offerId = prepareEwalletOffer();
        Assert.assertNotNull(offerId, "offerId should not be null.");

        internalBVT(offerId);
    }

    private void internalBVT(Long offerId) {
        FulfilmentRequest request = prepareFulfilmentRequest(offerId);

        try {
            request = fulfilmentResource.fulfill(request).wrapped().get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(request, "fulfilmentRequest should not be null.");

        FulfilmentAction action = request.getItems().get(Constant.UNIQUE_RESULT).getActions().get(Constant.UNIQUE_RESULT);
        Assert.assertNotNull(action.getResult(), "Action result should not be null.");
        Assert.assertEquals(action.getStatus(), FulfilmentStatus.SUCCEED, "Fulfilment status should match.");

        // retrieve fulfilment request by order id
        Long orderId = request.getOrderId();

        FulfilmentRequest retrievedRequest = null;
        try {
            retrievedRequest = fulfilmentResource.getByOrderId(new OrderId(orderId)).wrapped().get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(retrievedRequest.getRequestId(), request.getRequestId(), "Request id should match.");

        // retrieve fulfilment item by fulfilment id
        Long fulfilmentId = request.getItems().get(Constant.UNIQUE_RESULT).getFulfilmentId();

        FulfilmentItem retrievedFulfilmentItem = null;
        try {
            retrievedFulfilmentItem = fulfilmentResource.getByFulfilmentId(new FulfilmentId(fulfilmentId)).wrapped().get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(retrievedFulfilmentItem.getFulfilmentId(), fulfilmentId, "Fulfilment id should match.");
    }

    @Test(enabled = false)
    public void testDuplicatedTrackignGuid() {
        Long offerId = prepareOffer();
        Assert.assertNotNull(offerId, "offerId should not be null.");

        String trackingGuid = UUID.randomUUID().toString();
        FulfilmentRequest request = prepareFulfilmentRequest(offerId);
        request.setTrackingGuid(trackingGuid);

        try {
            request = fulfilmentResource.fulfill(request).wrapped().get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(request, "fulfilmentRequest should not be null.");

        FulfilmentRequest request2 = prepareFulfilmentRequest(offerId);
        request2.setTrackingGuid(trackingGuid);

        try {
            request2 = fulfilmentResource.fulfill(request2).wrapped().get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(request2, "fulfilmentRequest should not be null.");

        Assert.assertEquals(request2.getRequestId(), request.getRequestId(), "fulfilmentRequest id should match.");
    }

    @Test(enabled = false)
    public void testDuplicatedOrderId() {
        Long offerId = prepareOffer();
        Assert.assertNotNull(offerId, "offerId should not be null.");

        Long orderId = getRandomLong();
        FulfilmentRequest request = prepareFulfilmentRequest(offerId);
        request.setOrderId(orderId);

        try {
            request = fulfilmentResource.fulfill(request).wrapped().get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(request, "fulfilmentRequest should not be null.");

        FulfilmentRequest request2 = prepareFulfilmentRequest(offerId);
        request2.setOrderId(orderId);

        try {
            fulfilmentResource.fulfill(request2).wrapped().get();
        } catch (ClientResponseException e) {
            Assert.fail("should not reach here");
        } catch (InterruptedException e) {
            Assert.fail("should not reach here");
        } catch (ExecutionException e) {
            //good
        }
    }

    private FulfilmentRequest prepareFulfilmentRequest(final Long offerId) {
        return new FulfilmentRequest() {{
            setTrackingGuid(UUID.randomUUID().toString());
            setRequester("SYSTEM_INTERNAL");
            setOrderId(getRandomLong());
            setUserId(getRandomLong());
            setShippingAddressId(null);
            setShippingMethodId(null);

            setItems(new ArrayList<FulfilmentItem>() {{
                add(new FulfilmentItem() {{
                    setOrderItemId(1111L);
                    setOfferId(offerId);
                    setQuantity(1);
                    setTimestamp(System.currentTimeMillis());
                }});
            }});
        }};
    }

    private Long prepareEntitlementDef() {
        EntitlementDefinition def = new EntitlementDefinition();
        def.setGroup("TEST_GROUP");
        def.setTag("TEST_TAG");
        def.setType("DOWNLOAD");
        def.setDeveloperId(12345L);

        return megaGateway.createEntitlementDef(def);
    }

    private Long prepareOffer() {
        final Long entitlementDefId = prepareEntitlementDef();

        Offer offer = new Offer();
        offer.setOwnerId(getRandomLong());

        LocalizableProperty name = new LocalizableProperty();
        name.set("en_US", "test_offer_name");
        offer.setName(name);
        Long offerId = megaGateway.createOffer(offer);
        junit.framework.Assert.assertNotNull(offerId);

        OfferRevision offerRevision = new OfferRevision();
        offerRevision.setOfferId(offerId);
        offerRevision.setOwnerId(12345L);
        offerRevision.setStatus(Status.DRAFT);

        Price price = new Price();
        price.setPriceType(Price.FREE);
        offerRevision.setPrice(price);
        offerRevision.setEvents(new HashMap<String, Event>() {{
            put(Constant.EVENT_PURCHASE.toLowerCase(), new Event() {{
                setName(Constant.EVENT_PURCHASE);
                setActions(new ArrayList<Action>() {{
                    add(new Action() {{
                        setType(Constant.ACTION_GRANT_ENTITLEMENT);
                        setEntitlementDefId(entitlementDefId);
                    }});
                }});
            }});
        }});

        Long offerRevisionId = megaGateway.createOfferRevision(offerRevision);
        junit.framework.Assert.assertNotNull(offerRevisionId);

        OfferRevision retrievedRevision = megaGateway.getOfferRevision(offerRevisionId);
        retrievedRevision.setStatus(Status.APPROVED);
        megaGateway.updateOfferRevision(retrievedRevision);

        return offerId;
    }

    private Long prepareEwalletOffer() {
        LocalizableProperty name = new LocalizableProperty();
        name.set("DEFAULT", "test_offer_name");
        Long ownerId = 123L;

        // create item
        Item item = new Item();
        item.setName(name);
        item.setType(ItemType.WALLET);
        item.setOwnerId(ownerId);
        item.setSku("test_sku");

        final Long itemId = megaGateway.createItem(item);
        junit.framework.Assert.assertNotNull(itemId);

        // create item revision
        ItemRevision itemRevision = new ItemRevision();
        itemRevision.setItemId(itemId);
        itemRevision.setName(name);
        itemRevision.setOwnerId(ownerId);
        itemRevision.setType(ItemType.WALLET);
        itemRevision.setWalletAmount(new BigDecimal(123.45));
        itemRevision.setWalletCurrency("USD");
        itemRevision.setWalletCurrencyType("REAL_CURRENCY");
        itemRevision.setStatus(Status.DRAFT);

        Long itemRevisionId = megaGateway.createItemRevision(itemRevision);
        junit.framework.Assert.assertNotNull(itemRevisionId);

        // approve item
        ItemRevision retrievedItemRevision = megaGateway.getItemRevision(itemRevisionId);
        retrievedItemRevision.setStatus(Status.APPROVED);
        megaGateway.updateItemRevision(retrievedItemRevision);

        // create offer
        Offer offer = new Offer();
        offer.setOwnerId(ownerId);

        offer.setName(name);
        Long offerId = megaGateway.createOffer(offer);
        junit.framework.Assert.assertNotNull(offerId);

        // create offer revision
        OfferRevision offerRevision = new OfferRevision();
        offerRevision.setOfferId(offerId);
        offerRevision.setOwnerId(ownerId);
        offerRevision.setStatus(Status.DRAFT);

        Price price = new Price();
        price.setPriceType(Price.FREE);
        offerRevision.setPrice(price);
        offerRevision.setEvents(new HashMap<String, Event>() {{
            put(Constant.EVENT_PURCHASE.toLowerCase(), new Event() {{
                setName(Constant.EVENT_PURCHASE);
                setActions(new ArrayList<Action>() {{
                    add(new Action() {{
                        setType(Constant.ACTION_CREDIT_WALLET);
                    }});
                }});
            }});
        }});

        offerRevision.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(itemId);
                setQuantity(1);
            }});
        }});

        Long offerRevisionId = megaGateway.createOfferRevision(offerRevision);
        junit.framework.Assert.assertNotNull(offerRevisionId);

        OfferRevision retrievedOfferRevision = megaGateway.getOfferRevision(offerRevisionId);
        retrievedOfferRevision.setStatus(Status.APPROVED);
        megaGateway.updateOfferRevision(retrievedOfferRevision);

        return offerId;
    }

    private Long getRandomLong() {
        return idGenerator.nextId();
    }
}
