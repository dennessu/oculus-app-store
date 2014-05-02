/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.catalog.impl

import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions
import com.junbo.catalog.spec.model.offer.ItemEntry
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.ItemRevisionResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.clientproxy.model.OrderOfferItem
import com.junbo.order.clientproxy.model.OrderOfferItemRevision
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * Catalog facade implementation.
 */
@Component('orderCatalogFacade')
@CompileStatic
@TypeChecked
class CatalogFacadeImpl implements CatalogFacade {

    @Resource(name = 'order.offerClient')
    OfferResource offerResource

    @Resource(name = 'order.offerItemClient')
    ItemResource itemResource

    @Resource(name = 'order.offerRevisionClient')
    OfferRevisionResource offerRevisionResource

    @Resource(name = 'order.offerItemRevisionClient')
    ItemRevisionResource itemRevisionResource

    private final static Logger LOGGER = LoggerFactory.getLogger(CatalogFacadeImpl)


    void setOfferResource(OfferResource offerResource) {
        this.offerResource = offerResource
    }

    @Override
    Promise<OrderOfferRevision> getOfferRevision(Long offerId, Date honoredTime) {
        def entityGetOption = new OfferRevisionsGetOptions(
                timestamp: honoredTime.time,
                offerIds: [new OfferId(offerId)]
        )
        return offerRevisionResource.getOfferRevisions(entityGetOption).syncRecover {
            // TODO add logger and exception
        }.then { Results<OfferRevision> result ->
            List<OfferRevision> ors = result?.items
            if (CollectionUtils.isEmpty(ors)) {
                LOGGER.info('name=Can_Not_Get_OfferRevision. offerId: {}, timestamp: {}', offerId, honoredTime)
                return Promise.pure(null)
            }
            if (ors.size() != 1) {
                LOGGER.error('name=Too_Many_OfferRevision_Returned. offerId: {}, timestamp: {}, revisionCount: {}',
                        offerId, honoredTime, ors.size())
                return Promise.pure(null)
            }

            // only one offerRevision is returned here
            OfferRevision or = ors[0]
            assert (or != null)
            def orderOfferRevision = new OrderOfferRevision(
                    catalogOfferRevision: or,
                    orderOfferItems: []
            )

            return Promise.each(or.items?.iterator()) { ItemEntry ie ->
                return itemResource.getItem(new ItemId(ie.itemId)).syncRecover { Throwable ex ->
                    LOGGER.error('name=Failed_To_Get_Offer_Item. itemId: {}, timestamp: {}',
                            ie.itemId, honoredTime, ex)
                    throw AppErrors.INSTANCE.catalogConnectionError().exception()
                    // TODO add logger and exception
                }.syncThen { Item item ->
                    assert item != null
                    orderOfferRevision.orderOfferItems << new OrderOfferItem(item: item)

                }
            }.syncThen {
                return orderOfferRevision
            }
        }
    }

    Promise<OrderOfferItemRevision> getOfferItemRevision(Long itemId, Date honoredTime) {
        def entityGetOption = new ItemRevisionsGetOptions(
                timestamp: honoredTime.time,
                itemIds: [new ItemId(itemId)]
        )
        return itemRevisionResource.getItemRevisions(entityGetOption).syncRecover { Throwable ex ->
            LOGGER.error('name=Failed_To_Get_Item_Revision. itemId: {}, timestamp: {}',
                    itemId, honoredTime, ex)
            throw AppErrors.INSTANCE.catalogConnectionError().exception()
        }.then { Results<ItemRevision> results ->
            List<ItemRevision> irs = results?.items
            if (CollectionUtils.isEmpty(irs)) {
                LOGGER.info('name=Can_Not_Get_ItemRevision. itemId: {}, timestamp: {}', itemId, honoredTime)
                return Promise.pure(null)
            }
            if (irs.size() != 1) {
                LOGGER.error('name=Too_Many_itemRevision_Returned. itemId: {}, timestamp: {}, revisionCount: {}',
                        itemId, honoredTime, irs.size())
                return Promise.pure(null)
            }
            // only one itemRevision is returned here
            ItemRevision ir = irs[0]
            assert (ir != null)
            return Promise.pure(new OrderOfferItemRevision(
                    itemRevision: ir
            ))
        }
    }

    @Override
    Promise<OrderOfferRevision> getOfferRevision(Long offerId) {
        return getOfferRevision(offerId, new Date())
    }
}
