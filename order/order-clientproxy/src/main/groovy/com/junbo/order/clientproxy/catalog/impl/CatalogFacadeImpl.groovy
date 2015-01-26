/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.catalog.impl

import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions
import com.junbo.catalog.spec.model.offer.ItemEntry
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
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
                    revisionId: or.id,
                    countryReleaseDates: new HashMap<String, Date>(),
                    locales: new HashMap<String, OfferLocale>(),
                    price: or.price
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
            HashMap itemMap = new HashMap<String, String>()
            return Promise.each(or.items) { ItemEntry ie ->
                return itemResource.getItem(ie.itemId).syncRecover { Throwable throwable ->
                    LOGGER.error('name=CatalogFacadeImpl_Get_Offer_Item_Error. itemId: {}, timestamp: {}',
                            ie.itemId, honoredTime, throwable)
                    throw convertError(throwable).exception()
                }.then { Item item ->
                    assert item != null
                    def itemRevisionGetOption = new ItemRevisionsGetOptions(
                            timestamp: honoredTime.time,
                            itemIds: [ie.itemId] as Set
                    )
                    items << new com.junbo.order.clientproxy.model.ItemEntry(item: item, quantity: ie.quantity)
                    return itemRevisionResource.getItemRevisions(itemRevisionGetOption).syncRecover {
                        Throwable throwable ->
                            LOGGER.error('CatalogFacadeImpl_Get_Item_Revision_Error, itemId: {}', ie.itemId, throwable)
                            throw convertError(throwable).exception()
                    }.syncThen { Results<ItemRevision> itemRevisionResults ->
                        List<ItemRevision> itemRevisions = itemRevisionResults?.items
                        ItemRevision itemRevision = itemRevisions?.get(0)
                        if (itemRevision != null) {
                            itemMap.put(ie.itemId, itemRevision.revisionId)
                        }
                    }
                }
            }.then {
                offer.type = getType(items.collect {com.junbo.order.clientproxy.model.ItemEntry entry -> entry.item}.asList())
                offer.items = items
                offer.itemIds = itemMap
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

    @Override
    Promise<Offer> getLatestOfferRevision(String offerId) {
        return offerResource.getOffer(offerId).then { com.junbo.catalog.spec.model.offer.Offer offer ->
            if (offer == null) {
                throw AppErrors.INSTANCE.offerNotFound(offerId).exception()
            }
            return Promise.pure(offer)
        }.then { com.junbo.catalog.spec.model.offer.Offer o ->
            return offerRevisionResource.getOfferRevision(o.currentRevisionId, new OfferRevisionGetOptions()).then {
                OfferRevision or ->
                    if (or == null) {
                        LOGGER.info('name=Can_Not_Get_OfferRevision. offerRevisionId: {}', o.currentRevisionId)
                        throw AppErrors.INSTANCE.offerNotFound(offerId).exception()
                    }
                    def offer = new Offer(
                            id: or.offerId,
                            revisionId: or.id,
                            countryReleaseDates: new HashMap<String, Date>(),
                            locales: new HashMap<String, OfferLocale>(),
                            price: or.price
                    )
                    or.countries?.keySet()?.each { String key ->
                        offer.countryReleaseDates.put(key, or.countries.get(key)?.releaseDate)
                    }
                    or.locales?.keySet()?.each { String key ->
                        def properties = or.locales.get(key)
                        offer.locales.put(key, new OfferLocale(
                                name: properties?.name,
                                shortDescription: properties?.shortDescription,
                                longDescription: properties?.longDescription
                        ))
                    }
                    def items = []
                    HashMap itemMap = new HashMap<String, String>()
                    return Promise.each(or.items) { ItemEntry ie ->
                        return itemResource.getItem(ie.itemId).then { Item item ->
                            assert item != null
                            items << new com.junbo.order.clientproxy.model.ItemEntry(item: item, quantity: ie.quantity)
                            return itemRevisionResource.getItemRevision(item.currentRevisionId, new ItemRevisionGetOptions()).syncThen {
                                ItemRevision itemRevision ->
                                    if (itemRevision != null) {
                                        itemMap.put(ie.itemId, itemRevision.revisionId)
                                    }
                            }
                        }
                    }.then {
                        offer.type = getType(items.collect {com.junbo.order.clientproxy.model.ItemEntry entry -> entry.item}.asList())
                        offer.items = items
                        offer.itemIds = itemMap
                        return identityFacade.getOrganization(or.ownerId?.value).syncThen { Organization org ->
                            offer.owner = org
                            return offer
                        }
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
            items.type == com.junbo.catalog.spec.enums.ItemType.EWALLET.name()
        }) {
            return ItemType.EWALLET
        } else {
            def isDownloadable = items.any { Item item ->
                item.type == com.junbo.catalog.spec.enums.ItemType.APP.name()
            }
            def isDigitalContent = items.any { Item item ->
                item.type == com.junbo.catalog.spec.enums.ItemType.ADDITIONAL_CONTENT.name()
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

    private AppError convertError(Throwable throwable) {
        if (throwable instanceof AppErrorException) {
            return AppErrors.INSTANCE.catalogConnectionError()
        } else {
            return AppCommonErrors.INSTANCE.internalServerError(new Exception(throwable))
        }
    }
}
