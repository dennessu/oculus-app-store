/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.handler

import com.junbo.catalog.common.util.CloneUtils
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.ItemRevisionResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
import com.junbo.data.handler.utils.CatalogUtils
import com.junbo.data.model.CatalogData
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.langur.core.client.TypeReference
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource

/**
 * Created by x on 7/19/14.
 */
class CatalogDataHandler extends BaseDataHandler {
    private OrganizationResource organizationResource
    private ItemResource itemResource
    private ItemRevisionResource itemRevisionResource
    private OfferResource offerResource
    private OfferRevisionResource offerRevisionResource
    private CatalogUtils catalogUtils

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Required
    void setItemResource(ItemResource itemResource) {
        this.itemResource = itemResource
    }

    @Required
    void setItemRevisionResource(ItemRevisionResource itemRevisionResource) {
        this.itemRevisionResource = itemRevisionResource
    }

    @Required
    void setOfferResource(OfferResource offerResource) {
        this.offerResource = offerResource
    }

    @Required
    void setOfferRevisionResource(OfferRevisionResource offerRevisionResource) {
        this.offerRevisionResource = offerRevisionResource
    }

    @Required
    void setCatalogUtils(CatalogUtils catalogUtils) {
        this.catalogUtils = catalogUtils
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    @Override
    void handle(String content) {
        String defaultLocale = "en_US"
        CatalogData catalogData = null
        try {
            catalogData = transcoder.decode(new TypeReference<CatalogData>() {}, content) as CatalogData
        } catch (Exception e) {
            logger.error("Error parsing catalogData $content", e)
            exit()
        }

        OrganizationId ownerId = getOwner("OrganizationCatalogTest")
        if (ownerId == null) {
            logger.error("there is no organization OrganizationCatalogTest")
            exit()
        }

        //loading item and its revision
        Item item = catalogData.item
        ItemRevision itemRevision = catalogData.itemRevision

        item.id = null
        item.ownerId = ownerId

        itemRevision.id = null
        itemRevision.ownerId = ownerId
        itemRevision.packageName = UUID.randomUUID().toString()
        String itemRevisionName = itemRevision.getLocales().get(defaultLocale).name

        //Judge if item and its revision have been loaded
        item.genres = getItemAttributeIds(item.genres)
        item.categories = getOfferAttributeIds(item.categories)
        Item itemExisting = null
        String itemId
        try {
            Results<Item> itemResults = itemResource.getItems(new ItemsGetOptions(query: "name:$itemRevisionName")).get()
            if (itemResults != null && itemResults.items != null && itemResults.items.size() > 0) {
                itemExisting = itemResults.items.get(0)
            }
        } catch (AppErrorException e) {
            logger.debug('error happens', e)
        }

        if (itemExisting == null) {
            logger.info("----loading item $itemRevisionName and its revision")
            if (itemRevisionName.contains("testItem_IAP")) {
                //Post an IAP host item firstly
                Item hostItem = CloneUtils.clone(item)
                ItemRevision hotsItemRevision = CloneUtils.clone(itemRevision)

                hostItem.type = ItemType.APP
                String hostItemId = handle(hostItem)

                hotsItemRevision.locales.get("en_US").name = "testItem_HostItem_" + UUID.randomUUID().toString()
                hotsItemRevision.packageName = UUID.randomUUID().toString()
                hotsItemRevision.itemId = hostItemId
                hotsItemRevision.downloadName = "download name"
                hotsItemRevision.distributionChannels = ["STORE"]
                handle(hotsItemRevision)

                //Post the IAP item
                itemId = handle(item)

                itemRevision.itemId = itemId
                itemRevision.binaries = null
                itemRevision.iapHostItemIds = []
                itemRevision.iapHostItemIds.add(hostItemId)
                handle(itemRevision)

            } else {
                itemId = handle(item)
                itemRevision.itemId = itemId
                handle(itemRevision)
            }
        } else {
            itemId = itemExisting.itemId
            if (attributeEquals(item.genres, itemExisting.genres) &&
                    attributeEquals(item.categories, itemExisting.categories)) {
                logger.info("----The item $itemRevisionName and its revision have been loaded, skip")
            } else {
                itemExisting.genres = item.genres
                itemExisting.categories = item.categories
                itemResource.update(itemExisting.getId(), itemExisting).get()
                logger.info("----The item $itemRevisionName and its revision have been loaded, greners changed, update")
            }
        }

        //loading offer and its revision
        Offer offer = catalogData.offer
        OfferRevision offerRevision = catalogData.offerRevision

        offer.id = null
        offer.ownerId = ownerId
        offer.categories = getOfferAttributeIds(offer.categories)
        offerRevision.id = null
        offerRevision.ownerId = ownerId
        offerRevision.items.get(0).itemId = itemId

        if (offerRevision.eventActions != null && offerRevision.getEventActions().size() > 0) {
            offerRevision.eventActions.get("PURCHASE").get(0).itemId = itemId
        }

        String offerRevisionName = offerRevision.getLocales().get(defaultLocale).name

        //Judge if offer and its revision have been loaded
        Offer offerExisting = null
        String offerId
        try {
            Results<Offer> offerResults = offerResource.getOffers(new OffersGetOptions(query: "name:$offerRevisionName")).get()
            if (offerResults != null && offerResults.items != null && offerResults.items.size() > 0) {
                offerExisting = offerResults.items.get(0)
            }
        } catch (AppErrorException e) {
            logger.debug('error happens', e)
        }

        if (offerExisting == null) {
            logger.info("----loading offer $offerRevisionName and its revision")
            offerId = handle(offer)
            offerRevision.offerId = offerId
            handle(offerRevision)
        } else {
            if (attributeEquals(offer.categories, offerExisting.categories)) {
                logger.info("----The offer $offerRevisionName and its revision have been loaded, skip")
            } else {
                offerExisting.categories = offer.categories
                offerResource.update(offerExisting.getId(), offerExisting).get()
                logger.info("----The offer $offerRevisionName and its revision have been loaded, categories changed, update")
            }

        }

    }

    OrganizationId getOwner(String organizationName) {
        logger.debug('Get organization info')
        Organization organization

        //Get organization info
        try {
            Results<Organization> results = organizationResource.list(new OrganizationListOptions(name: organizationName)).get()
            results.items.retainAll { Organization org ->
                org.isValidated
            }

            if (results != null && results.items != null && results.items.size() > 0) {
                organization = results.items.get(0)
            }
        } catch (Exception e) {
            logger.error("could not find organization OrganizationCatalogTest", e)
            return null
        }

        return organization.id as OrganizationId
    }

    String handle(Item item) {
        logger.debug('Create new item with this content')
        try {
            Item result = itemResource.create(item).get()
            return result.id
        } catch (Exception e) {
            logger.error("Error creating item $item.itemId.", e)
            return null
        }
    }

    void handle(ItemRevision itemRevision) {
        logger.debug('Create new item revision with this content')
        try {
            ItemRevision itemRevisionCreated = itemRevisionResource.createItemRevision(itemRevision).get()

            logger.debug('put the item revision to APPROVED')
            itemRevisionCreated.setStatus("APPROVED")
            itemRevisionResource.updateItemRevision(itemRevisionCreated.revisionId, itemRevisionCreated).get()
        } catch (Exception e) {
            logger.error("Error creating item revision $itemRevision.revisionId.", e)
        }
    }

    String handle(Offer offer) {
        logger.debug('Create new offer with this content')
        try {
            Offer result = offerResource.create(offer).get()
            return result.id
        } catch (Exception e) {
            logger.error("Error creating offer $offer.offerId.", e)
            return null
        }

    }

    void handle(OfferRevision offerRevision) {
        logger.debug('Create new offer revision with this content')
        try {
            OfferRevision offerRevisionCreated = offerRevisionResource.createOfferRevision(offerRevision).get()

            logger.debug('put the offer revision to APPROVED')
            offerRevisionCreated.setStatus("APPROVED")
            offerRevisionResource.updateOfferRevision(offerRevisionCreated.revisionId, offerRevisionCreated).get()

            //update due to bug SER-474
            Offer offer = offerResource.getOffer(offerRevisionCreated.getOfferId()).get()
            offer.published = true
            offerResource.update(offer.getOfferId(), offer)
        } catch (Exception e) {
            logger.error("Error creating offer revision $offerRevision.revisionId.", e)
        }
    }

    private List<String> getItemAttributeIds(List<String> itemAttributeNames) {
        List<String> result = []
        itemAttributeNames.each { String name ->
            ItemAttribute attribute = catalogUtils.getItemAttributeByName(name)
            if (attribute == null) {
                logger.warn('Item attribute not found, name={}', name)
            } else {
                result << attribute.getId()
            }
        }
        return result
    }

    private List<String> getOfferAttributeIds(List<String> offerAttributeNames) {
        List<String> result = []
        offerAttributeNames.each { String name ->
            OfferAttribute attribute = catalogUtils.getOfferAttributeByName(name)
            if (attribute == null) {
                logger.warn('Offer attribute not found, name={}', name)
            } else {
                result << attribute.getId()
            }
        }
        return result
    }

    private boolean attributeEquals(List<String> left, List<String> right) {
        if (left == null || right == null) {
            return left == right
        }
        return new HashSet<>(left).equals(new HashSet(right))
    }
}
