package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.offer.Action;
import com.junbo.catalog.spec.model.offer.Event;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.fulfilment.spec.resource.FulfilmentResource;
import com.junbo.langur.core.client.ClientResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public class IntegrationTest extends AbstractTestNGSpringContextTests {
    @Autowired
    protected MegaGateway megaGateway;

    @Autowired
    private FulfilmentResource fulfilmentResource;

    @Test(enabled = false)
    public void testBVT() {
        Long offerId = prepareOffer();
        Assert.assertNotNull(offerId, "offerId should not be null.");

        FulfilmentRequest request = prepareFulfilmentRequest(offerId);

        try {
            request = fulfilmentResource.fulfill(request).wrapped().get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(request, "fulfilmentRequest should not be null.");
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
        offer.setName("TEST_OFFER");
        offer.setOwnerId(getRandomLong());

        offer.setEvents(new ArrayList<Event>() {{
            add(new Event() {{
                setName(Constant.EVENT_PURCHASE);
                setActions(new ArrayList<Action>() {{
                    add(new Action() {{
                        setType(Constant.ACTION_GRANT_ENTITLEMENT);
                        setProperties(new HashMap<String, String>() {{
                            put(Constant.ENTITLEMENT_DEF_ID, entitlementDefId + "");
                        }});
                    }});
                }});
            }});
        }});

        Long offerId = megaGateway.createOffer(offer);

        offer.setId(offerId);
        offer.setStatus("RELEASED");
        megaGateway.updateOffer(offer);

        return offerId;
    }

    private Long getRandomLong() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            //ignore
        }

        return System.currentTimeMillis();
    }
}
