package com.junbo.data.handler

import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.id.OrganizationId
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.langur.core.client.TypeReference
import com.junbo.common.error.AppErrorException
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.common.model.Results

import org.springframework.beans.factory.annotation.Required
import groovy.transform.CompileStatic

import org.springframework.core.io.Resource

/**
 * Created by jasonfu on 14-7-17.
 */
@CompileStatic
class OfferDataHandler extends BaseDataHandler {
    private OrganizationResource organizationResource
    private OfferResource offerResource

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Required
    void setOfferResource(OfferResource offerResource) {
        this.offerResource = offerResource
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    @Override
    void handle(String content) {
        Offer offer
        try {
            offer = transcoder.decode(new TypeReference<Offer>() {}, content) as Offer
        } catch (Exception e) {
            logger.error("Error parsing offer $content", e)
            exit()
        }

        logger.info("loading offer $offer.offerId")

        Results<Offer> existing = null
        Set<String> offerIds = new HashSet()
        offerIds.add(offer.offerId)

        try {
            existing = offerResource.getOffers(new OffersGetOptions(offerIds: offerIds)).get()
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        if (existing == null || existing.items.size() <= 0) {
            logger.debug('Get organization info')
            String organizationName = "OrganizationCatalogTest"
            Organization organization

            //Get organization info
            try {
                Results<Organization> results = organizationResource.list(new OrganizationListOptions(name: organizationName)).get()
                if (results != null && results.items != null && results.items.size() > 0) {
                    organization = results.items.get(0)
                }
            } catch (Exception e) {
                logger.error("could not find organization OrganizationCatalogTest", e)
            }

            offer.ownerId = organization.id as OrganizationId

            logger.debug('Create new offer with this content')
            try {
                offerResource.create(offer).get()
            } catch (Exception e) {
                logger.error("Error creating offer $offer.offerId.", e)
            }
        } else {
            logger.debug("$offer.offerId already exists, skipped!")
        }
    }
}
