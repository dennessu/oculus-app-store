/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.catalog.impl
import com.junbo.catalog.spec.model.common.EntityGetOptions
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.offer.ItemEntry
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.clientproxy.model.OrderOffer
import com.junbo.order.clientproxy.model.OrderOfferItem
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * Catalog facade implementation.
 */
@Component('orderCatalogFacade')
@CompileStatic
@TypeChecked
class CatalogFacadeImpl implements CatalogFacade {

    @Resource(name='order.offerClient')
    OfferResource offerResource

    @Resource(name='order.offerItemClient')
    ItemResource itemResource

    void setOfferResource(OfferResource offerResource) {
        this.offerResource = offerResource
    }

    @Override
    Promise<OrderOffer> getOffer(Long offerId, Date honoredTime) {
        def entityGetOption = EntityGetOptions.default
        entityGetOption.timestamp = honoredTime.time
        return offerResource.getOffer(new OfferId(offerId), entityGetOption).syncRecover {
            // TODO add logger and exception
        }.then { Offer offer ->
            assert (offer != null)
            def orderOffer = new OrderOffer(
                    catalogOffer: offer,
                    orderOfferItems: []
            )
            Promise.each(offer?.items?.iterator()) { ItemEntry ite ->
                itemResource.getItem(new ItemId(ite.itemId), entityGetOption).syncRecover {
                    // TODO add logger and exception
                }.syncThen { Item it ->
                    orderOffer.orderOfferItems.add(new OrderOfferItem(
                            catalogItem: it
                    ))
                }
            }.syncThen {
                return orderOffer
            }
        }
    }

    @Override
    Promise<OrderOffer> getOffer(Long offerId) {
        return getOffer(offerId, new Date())
    }
}
