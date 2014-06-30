package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.enums.EntitlementType;
import com.junbo.catalog.spec.enums.ItemType;
import com.junbo.catalog.spec.enums.PriceType;
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.item.*;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.common.id.FulfilmentId;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.OrganizationId;
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
import java.util.List;
import java.util.UUID;

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
        String offerId = prepareOffer();
        Assert.assertNotNull(offerId, "offerId should not be null.");

        internalBVT(offerId);
    }

    @Test(enabled = false)
    public void testEwalletBVT() {
        String offerId = prepareEwalletOffer();
        Assert.assertNotNull(offerId, "offerId should not be null.");

        internalBVT(offerId);
    }

    private void internalBVT(String offerId) {
        FulfilmentRequest request = prepareFulfilmentRequest(offerId);

        try {
            request = fulfilmentResource.fulfill(request).get();
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
            retrievedRequest = fulfilmentResource.getByOrderId(new OrderId(orderId)).get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(retrievedRequest.getRequestId(), request.getRequestId(), "Request id should match.");

        // retrieve fulfilment item by fulfilment id
        Long fulfilmentId = request.getItems().get(Constant.UNIQUE_RESULT).getFulfilmentId();

        FulfilmentItem retrievedFulfilmentItem = null;
        try {
            retrievedFulfilmentItem = fulfilmentResource.getByFulfilmentId(new FulfilmentId(fulfilmentId)).get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(retrievedFulfilmentItem.getFulfilmentId(), fulfilmentId, "Fulfilment id should match.");
        Assert.assertNotNull(retrievedFulfilmentItem.getActions(), "Fulfilment actions should not be null.");
    }

    @Test(enabled = false)
    public void testDuplicatedTrackignGuid() {
        String offerId = prepareOffer();
        Assert.assertNotNull(offerId, "offerId should not be null.");

        String trackingGuid = UUID.randomUUID().toString();
        FulfilmentRequest request = prepareFulfilmentRequest(offerId);
        request.setTrackingGuid(trackingGuid);

        try {
            request = fulfilmentResource.fulfill(request).get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(request, "fulfilmentRequest should not be null.");

        FulfilmentRequest request2 = prepareFulfilmentRequest(offerId);
        request2.setTrackingGuid(trackingGuid);

        try {
            request2 = fulfilmentResource.fulfill(request2).get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(request2, "fulfilmentRequest should not be null.");

        Assert.assertEquals(request2.getRequestId(), request.getRequestId(), "fulfilmentRequest id should match.");
    }

    @Test(enabled = false)
    public void testDuplicatedOrderId() {
        String offerId = prepareOffer();
        Assert.assertNotNull(offerId, "offerId should not be null.");

        Long orderId = getRandomLong();
        FulfilmentRequest request = prepareFulfilmentRequest(offerId);
        request.setOrderId(orderId);

        try {
            request = fulfilmentResource.fulfill(request).get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(request, "fulfilmentRequest should not be null.");

        FulfilmentRequest request2 = prepareFulfilmentRequest(offerId);
        request2.setOrderId(orderId);

        try {
            fulfilmentResource.fulfill(request2).get();
        } catch (ClientResponseException e) {
            Assert.fail("should not reach here");
        }
    }

    private FulfilmentRequest prepareFulfilmentRequest(final String offerId) {
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

    private String prepareOffer() {
        OrganizationId ownerId = new OrganizationId(123L);

        // create item
        Item item = new Item();
        item.setType(ItemType.APP.name());
        item.setOwnerId(ownerId);

        final String itemId = megaGateway.createItem(item);
        Assert.assertNotNull(itemId);

        // create item revision
        ItemRevision itemRevision = new ItemRevision();
        itemRevision.setItemId(itemId);
        itemRevision.setOwnerId(ownerId);
        itemRevision.setStatus(Status.DRAFT.name());
        itemRevision.setBinaries(new HashMap<String, Binary>() {{
            put("key", new Binary());
        }});
        itemRevision.setLocales(new HashMap<String, ItemRevisionLocaleProperties>() {{
            put("en_US", new ItemRevisionLocaleProperties() {{
                setName("test-offer");
            }});
        }});
        itemRevision.setSku("test_sku");
        itemRevision.setEntitlementDefs(new ArrayList<EntitlementDef>() {{
            add(new EntitlementDef() {{
                setType(EntitlementType.DOWNLOAD.toString());
                setConsumable(false);
            }});
            add(new EntitlementDef() {{
                setType(EntitlementType.RUN.toString());
                setConsumable(false);
            }});
        }});

        String itemRevisionId = megaGateway.createItemRevision(itemRevision);
        Assert.assertNotNull(itemRevisionId);

        // approve item
        ItemRevision retrievedItemRevision = megaGateway.getItemRevision(itemRevisionId);
        retrievedItemRevision.setStatus(Status.APPROVED.name());
        megaGateway.updateItemRevision(retrievedItemRevision);

        // create offer
        Offer offer = new Offer();
        offer.setOwnerId(new OrganizationId(getRandomLong()));

        String offerId = megaGateway.createOffer(offer);
        Assert.assertNotNull(offerId);

        // create offer revision
        OfferRevision offerRevision = new OfferRevision();
        offerRevision.setOfferId(offerId);
        offerRevision.setOwnerId(new OrganizationId(12345L));
        offerRevision.setStatus(Status.DRAFT.name());
        offerRevision.setLocales(new HashMap<String, OfferRevisionLocaleProperties>() {{
            put("en_US", new OfferRevisionLocaleProperties() {{
                setName("test-offer");
            }});
        }});
        offerRevision.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setQuantity(1);
                setItemId(itemId);
            }});
        }});

        Price price = new Price();
        price.setPriceType(PriceType.FREE.name());
        offerRevision.setPrice(price);
        offerRevision.setEventActions(new HashMap<String, List<Action>>() {{
            put(Constant.EVENT_PURCHASE, new ArrayList<Action>() {{
                add(new Action() {{
                    setType(Constant.ACTION_GRANT_ENTITLEMENT);
                    setItemId(itemId);
                }});
            }});
        }});

        String offerRevisionId = megaGateway.createOfferRevision(offerRevision);
        Assert.assertNotNull(offerRevisionId);

        OfferRevision retrievedRevision = megaGateway.getOfferRevision(offerRevisionId);
        retrievedRevision.setStatus(Status.APPROVED.name());
        megaGateway.updateOfferRevision(retrievedRevision);

        return offerId;
    }

    private String prepareEwalletOffer() {
        OrganizationId ownerId = new OrganizationId(123L);

        // create item
        Item item = new Item();
        item.setType(ItemType.STORED_VALUE.name());
        item.setOwnerId(ownerId);

        final String itemId = megaGateway.createItem(item);
        Assert.assertNotNull(itemId);

        // create item revision
        ItemRevision itemRevision = new ItemRevision();
        itemRevision.setItemId(itemId);
        itemRevision.setOwnerId(ownerId);
        itemRevision.setStatus(Status.DRAFT.name());
        itemRevision.setLocales(new HashMap<String, ItemRevisionLocaleProperties>() {{
            put("en_US", new ItemRevisionLocaleProperties() {{
                setName("test-offer");
            }});
        }});
        itemRevision.setBinaries(new HashMap<String, Binary>() {{
            put("key", new Binary());
        }});
        itemRevision.setSku("test_sku");

        String itemRevisionId = megaGateway.createItemRevision(itemRevision);
        Assert.assertNotNull(itemRevisionId);

        // approve item
        ItemRevision retrievedItemRevision = megaGateway.getItemRevision(itemRevisionId);
        retrievedItemRevision.setStatus(Status.APPROVED.name());
        megaGateway.updateItemRevision(retrievedItemRevision);

        // create offer
        Offer offer = new Offer();
        offer.setOwnerId(ownerId);

        String offerId = megaGateway.createOffer(offer);
        Assert.assertNotNull(offerId);

        // create offer revision
        OfferRevision offerRevision = new OfferRevision();
        offerRevision.setOfferId(offerId);
        offerRevision.setOwnerId(ownerId);
        offerRevision.setStatus(Status.DRAFT.name());
        offerRevision.setLocales(new HashMap<String, OfferRevisionLocaleProperties>() {{
            put("en_US", new OfferRevisionLocaleProperties() {{
                setName("test-offer");
            }});
        }});

        Price price = new Price();
        price.setPriceType(PriceType.FREE.name());
        offerRevision.setPrice(price);
        offerRevision.setEventActions(new HashMap<String, List<Action>>() {{
            put(Constant.EVENT_PURCHASE, new ArrayList<Action>() {{
                add(new Action() {{
                    setType(Constant.ACTION_CREDIT_WALLET);
                    setStoredValueAmount(new BigDecimal("123.45"));
                    setStoredValueCurrency("USD");
                    setItemId(itemId);
                }});
            }});
        }});
        offerRevision.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setQuantity(1);
                setItemId(itemId);
            }});
        }});

        offerRevision.setItems(new ArrayList<ItemEntry>() {{
            add(new ItemEntry() {{
                setItemId(itemId);
                setQuantity(1);
            }});
        }});

        String offerRevisionId = megaGateway.createOfferRevision(offerRevision);
        Assert.assertNotNull(offerRevisionId);

        OfferRevision retrievedOfferRevision = megaGateway.getOfferRevision(offerRevisionId);
        retrievedOfferRevision.setStatus(Status.APPROVED.name());
        megaGateway.updateOfferRevision(retrievedOfferRevision);

        return offerId;
    }

    private Long getRandomLong() {
        return idGenerator.nextId();
    }
}
