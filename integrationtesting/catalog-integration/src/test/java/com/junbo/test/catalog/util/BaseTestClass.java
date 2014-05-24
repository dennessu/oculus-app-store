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
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.common.model.Results;

/**
 @author Jason
  * Time: 4/9/2014
  * Base test class for catalog-integration, holds some common functions
 */
public class BaseTestClass extends TestClass {

    protected void releaseItem(Item item) throws Exception {
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        //Attach item revision to the item
        ItemRevision itemRevision = itemRevisionService.postDefaultItemRevision(item);

        //Approve the item revision
        itemRevision.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);
    }

    protected void releaseOffer(Offer offer) throws Exception {
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

        //Attach offer revision to the offer
        OfferRevision offerRevision = offerRevisionService.postDefaultOfferRevision(offer);

        //Approve the item revision
        offerRevision.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        offerRevisionService.updateOfferRevision(offerRevision.getRevisionId(), offerRevision);
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

}