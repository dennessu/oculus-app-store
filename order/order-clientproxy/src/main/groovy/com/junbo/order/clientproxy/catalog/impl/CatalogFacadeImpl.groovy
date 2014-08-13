/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.catalog.impl

import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.offer.ItemEntry
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.ItemRevisionResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppError
import com.junbo.common.error.AppErrorException
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.clientproxy.identity.IdentityFacade
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.clientproxy.model.OfferLocale
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.enums.ItemType
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

    @Resource(name = 'orderIdentityFacade')
    IdentityFacade identityFacade

    private final static Logger LOGGER = LoggerFactory.getLogger(CatalogFacadeImpl)


    void setOfferResource(OfferResource offerResource) {
        this.offerResource = offerResource
    }

    @Override
    Promise<Offer> getOfferRevision(String offerId, Date honoredTime) {
        def entityGetOption = new OfferRevisionsGetOptions(
                timestamp: honoredTime.time,
                offerIds: [offerId] as Set
        )
        return offerRevisionResource.getOfferRevisions(entityGetOption).syncRecover { Throwable throwable ->
            LOGGER.error('CatalogFacadeImpl_Get_Offer_Revision_Error, offerId: {}', offerId, throwable)
            throw convertError(throwable).exception()
        }.then { Results<OfferRevision> result ->
            List<OfferRevision> ors = result?.items
            if (CollectionUtils.isEmpty(ors)) {
                LOGGER.info('name=Can_Not_Get_OfferRevision. offerId: {}, timestamp: {}', offerId, honoredTime)
                throw AppErrors.INSTANCE.offerNotFound(offerId).exception()
            }
            if (ors.size() != 1) {
                LOGGER.error('name=Too_Many_OfferRevision_Returned. offerId: {}, timestamp: {}, revisionCount: {}',
                        offerId, honoredTime, ors.size())
                throw AppErrors.INSTANCE.catalogResultInvalid('Too many offer revision returned').exception()
            }

            // only one offerRevision is returned here
            OfferRevision or = ors[0]
            assert (or != null)
            def offer = new Offer(
                    id: or.offerId,
                    countryReleaseDates: new HashMap<String, Date>(),
                    locales: new HashMap<String, OfferLocale>()
            )
            or.countries?.keySet().each { String key ->
                offer.countryReleaseDates.put(key, or.countries.get(key)?.releaseDate)
            }
            or.locales?.keySet().each { String key ->
                def properties = or.locales.get(key)
                offer.locales.put(key, new OfferLocale(
                        name: properties?.name,
                        shortDescription: properties?.shortDescription,
                        longDescription: properties?.longDescription
                ))
            }
            def items = []
            return Promise.each(or.items) { ItemEntry ie ->
                return itemResource.getItem(ie.itemId).syncRecover { Throwable throwable ->
                    LOGGER.error('name=CatalogFacadeImpl_Get_Offer_Item_Error. itemId: {}, timestamp: {}',
                            ie.itemId, honoredTime, throwable)
                    throw convertError(throwable).exception()
                }.syncThen { Item item ->
                    assert item != null
                    items << item
                }
            }.then {
                offer.type = getType(items)
                return identityFacade.getOrganization(or.ownerId?.value).syncRecover {
                    /* organization is not required, return offer directly if the organization is unavailable.*/
                    LOGGER.error('CatalogFacadeImpl_Get_Organization_Error, offerId: {}', offerId)
                    return null
                }.syncThen { Organization org ->
                    offer.owner = org
                    return offer
                }
            }
        }
    }

    private ItemType getType(List<Item> items) {
        if (items.any { Item item ->
            item.type == com.junbo.catalog.spec.enums.ItemType.PHYSICAL.name()
        }) {
            return ItemType.PHYSICAL_GOODS
        } else if (items.any { Item item ->
            items.type == com.junbo.catalog.spec.enums.ItemType.STORED_VALUE.name()
        }) {
            return ItemType.GIFT_CARD
        } else {
            def isDownloadable = items.any { Item item ->
                item.type == com.junbo.catalog.spec.enums.ItemType.APP.name() ||
                        item.type == com.junbo.catalog.spec.enums.ItemType.DOWNLOADED_ADDITION.name()
            }
            def isDigitalContent = items.any { Item item ->
                item.type == com.junbo.catalog.spec.enums.ItemType.PERMANENT_UNLOCK.name() ||
                        item.type == com.junbo.catalog.spec.enums.ItemType.CONSUMABLE_UNLOCK.name()
            }
            if (isDownloadable && isDigitalContent) {
                throw AppErrors.INSTANCE.offerItemTypeNotValid().exception()
            }
            if (isDownloadable) {
                return ItemType.DOWNLOADABLE_SOFTWARE
            }
            if (isDigitalContent) {
                return ItemType.DIGITAL_CONTENT
            }
            return ItemType.DIGITAL
        }
    }

    @Override
    Promise<Offer> getOfferRevision(String offerId) {
        return getOfferRevision(offerId, new Date())
    }

    private AppError convertError(Throwable throwable) {
        if (throwable instanceof AppErrorException) {
            return AppErrors.INSTANCE.catalogConnectionError()
        } else {
            return AppCommonErrors.INSTANCE.internalServerError(new Exception(throwable))
        }
    }
}
