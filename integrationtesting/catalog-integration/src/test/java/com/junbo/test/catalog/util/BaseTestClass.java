/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.util;

import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.test.catalog.impl.OfferRevisionServiceImpl;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.ItemService;
import com.junbo.common.model.Results;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 4/9/2014
  * Base test class for catalog-integration, holds some common functions
 */
public class BaseTestClass extends TestClass {

    protected Item releaseItem(Item item) throws Exception {
        ItemService itemService = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        //Attach item revision to the item
        ItemRevision itemRevision = itemRevisionService.postDefaultItemRevision(item);

        //Approve the item revision
        itemRevision.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        return itemService.getItem(item.getItemId());
    }

    protected ItemRevision releaseItemRevision(ItemRevision itemRevision) throws Exception {
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        //Approve the item revision
        itemRevision.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        return itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);
    }

    protected Offer releaseOffer(Offer offer) throws Exception {
        OfferService offerService = OfferServiceImpl.instance();
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

        //Attach offer revision to the offer
        OfferRevision offerRevision = offerRevisionService.postDefaultOfferRevision(offer);

        //Approve the offer revision
        offerRevision.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);

        return offerService.getOffer(offer.getOfferId());
    }

    protected OfferRevision releaseOfferRevision(OfferRevision offerRevision) throws Exception {
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

        //Approve the offer revision
        offerRevision.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        return offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);
    }

    protected <T> boolean isContain (Results<T> results, T entity) {
        boolean contain = false;
        for (T t : results.getItems()){
            if (t instanceof Offer) {
                if(((Offer) t).getOfferId().equals(((Offer) entity).getOfferId())) {
                    contain = true;
                }
            }
            else if (t instanceof OfferAttribute) {
                if(((OfferAttribute) t).getId().equals(((OfferAttribute) entity).getId())) {
                    contain = true;
                }
            }
            else if (t instanceof OfferRevision) {
                if(((OfferRevision) t).getRevisionId().equals(((OfferRevision) entity).getRevisionId())) {
                    contain = true;
                }
            }
            else if (t instanceof Item) {
                if(((Item) t).getItemId().equals(((Item) entity).getItemId())) {
                    contain = true;
                }
            }
            else if (t instanceof ItemAttribute) {
                if(((ItemAttribute) t).getId().equals(((ItemAttribute) entity).getId())) {
                    contain = true;
                }
            }
            else if (t instanceof ItemRevision) {
                if(((ItemRevision) t).getRevisionId().equals(((ItemRevision) entity).getRevisionId())) {
                    contain = true;
                }
            }
        }
        return contain;
    }

    protected void verifyGetItemsScenarios(HashMap<String, List<String>> paraMap, int expectedRtnSize, String... itemId) throws Exception {

        ItemService itemService = ItemServiceImpl.instance();
        Results<Item> itemRtn = itemService.getItems(paraMap);

        Assert.assertEquals(itemRtn.getItems().size(), expectedRtnSize);

        for (String itemGetId : itemId) {
            Item item = itemService.getItem(itemGetId);
            Assert.assertTrue(isContain(itemRtn, item));
        }
    }

    protected void verifyGetOffersScenarios(HashMap<String, List<String>> paraMap, int expectedRtnSize, String... offerId) throws Exception{
        OfferService offerService = OfferServiceImpl.instance();
        Results<Offer> offerRtn = offerService.getOffers(paraMap);

        Assert.assertEquals(offerRtn.getItems().size(), expectedRtnSize);

        for (String offerGetId : offerId) {
            Offer offer = offerService.getOffer(offerGetId);
            Assert.assertTrue(isContain(offerRtn, offer));
        }
    }
}
