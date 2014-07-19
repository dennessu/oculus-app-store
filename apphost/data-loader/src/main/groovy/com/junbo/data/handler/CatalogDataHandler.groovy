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
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
import com.junbo.data.handler.catalog.ItemDataHandler
import com.junbo.data.handler.catalog.ItemRevisionDataHandler
import com.junbo.data.handler.catalog.OfferDataHandler
import com.junbo.data.handler.catalog.OfferRevisionDataHandler
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
    private ItemDataHandler itemDataHandler
    private ItemRevisionDataHandler itemRevisionDataHandler
    private OfferDataHandler offerDataHandler
    private OfferRevisionDataHandler offerRevisionDataHandler

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Required
    void setItemDataHandler(ItemDataHandler itemDataHandler) {
        this.itemDataHandler = itemDataHandler
    }

    @Required
    void setItemRevisionDataHandler(ItemRevisionDataHandler itemRevisionDataHandler) {
        this.itemRevisionDataHandler = itemRevisionDataHandler
    }

    @Required
    void setOfferDataHandler(OfferDataHandler offerDataHandler) {
        this.offerDataHandler = offerDataHandler
    }

    @Required
    void setOfferRevisionDataHandler(OfferRevisionDataHandler offerRevisionDataHandler) {
        this.offerRevisionDataHandler = offerRevisionDataHandler
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    @Override
    void handle(String content) {
        CatalogData catalogData
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

        Item item = catalogData.item
        item.id = null
        item.ownerId = ownerId
        String itemId = itemDataHandler.handle(item)

        ItemRevision itemRevision = catalogData.itemRevision
        itemRevision.id = null
        itemRevision.ownerId = ownerId
        itemRevision.itemId = itemId
        itemRevisionDataHandler.handle(itemRevision)

        Offer offer = catalogData.offer
        offer.id = null
        offer.ownerId = ownerId
        String offerId = offerDataHandler.handle(offer)

        OfferRevision offerRevision = catalogData.offerRevision
        offerRevision.id = null
        offerRevision.ownerId = ownerId
        offerRevision.items.get(0).itemId = itemId
        offerRevision.offerId = offerId
        offerRevisionDataHandler.handle(offerRevision)
    }

    private OrganizationId getOwner(String organizationName) {
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
}
