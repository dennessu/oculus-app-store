package com.junbo.fulfilment.clientproxy;

import com.junbo.catalog.spec.model.offer.Action;
import com.junbo.catalog.spec.model.offer.Event;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.fulfilment.spec.resource.FulfilmentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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

    private Long prepareOffer() {
        Offer offer = new Offer();
        offer.setName("TEST_OFFER");
        offer.setOwnerId(getRandomLong());

        offer.setEvents(new ArrayList<Event>() {{
            add(new Event() {{
                setName("PURCHASE_EVENT");
                setActions(new ArrayList<Action>() {{
                    add(new Action() {{
                        setType("GRANT_ENTITLEMENT");
                        setProperties(new HashMap<String, String>() {{
                            put("ENTITLMENT_GROUP", "TEST_GROUP");
                            put("ENTITLMENT_TAG", "TEST_TAG");
                            put("ENTITLMENT_TYPE", "DOWNLOAD");
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
