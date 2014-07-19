/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.handler

import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.ItemRevisionResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
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

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    @Override
    void handle(String content) {
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

        logger.info("----loading item")
        Item item = catalogData.item
        item.id = null
        item.ownerId = ownerId
        String itemId = handle(item)

        logger.info("----loading itemRevision")
        ItemRevision itemRevision = catalogData.itemRevision
        itemRevision.id = null
        itemRevision.ownerId = ownerId
        itemRevision.itemId = itemId
        handle(itemRevision)

        logger.info("----loading offer")
        Offer offer = catalogData.offer
        offer.id = null
        offer.ownerId = ownerId
        String offerId = handle(offer)

        logger.info("----loading offerRevision")
        OfferRevision offerRevision = catalogData.offerRevision
        offerRevision.id = null
        offerRevision.ownerId = ownerId
        offerRevision.items.get(0).itemId = itemId
        offerRevision.offerId = offerId
        handle(offerRevision)

    }

    OrganizationId getOwner(String organizationName) {
        logger.debug('Get organization info')
        Organization organization

        //Get organization info
        try {
            Results<Organization> results = organizationResource.list(new OrganizationListOptions(name: organizationName)).get()
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
        } catch (Exception e) {
            logger.error("Error creating offer revision $offerRevision.revisionId.", e)
        }
    }

}
