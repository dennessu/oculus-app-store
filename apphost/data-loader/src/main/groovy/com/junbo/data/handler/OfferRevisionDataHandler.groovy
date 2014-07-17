package com.junbo.data.handler

import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions
import com.junbo.common.id.OrganizationId
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.langur.core.client.TypeReference
import com.junbo.common.error.AppErrorException
import com.junbo.common.model.Results

import org.springframework.beans.factory.annotation.Required
import groovy.transform.CompileStatic

import org.springframework.core.io.Resource

/**
 * Created by jasonfu on 14-7-17.
 */
@CompileStatic
class OfferRevisionDataHandler extends BaseDataHandler {
    private OrganizationResource organizationResource
    private OfferRevisionResource offerRevisionResource

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
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
        String defaultLocale = "en_US"
        OfferRevision offerRevision
        try {
            offerRevision = transcoder.decode(new TypeReference<OfferRevision>() {}, content) as OfferRevision
        } catch (Exception e) {
            logger.error("Error parsing offer revision $content", e)
            exit()
        }

        logger.info("loading offer revision $offerRevision.revisionId")

        Results<OfferRevision> existing = null
        Set<String> offerRevisionIds = new HashSet()
        offerRevisionIds.add(offerRevision.revisionId)

        try {
            existing = offerRevisionResource.getOfferRevisions(new OfferRevisionsGetOptions(revisionIds: offerRevisionIds)).get()
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

            offerRevision.ownerId = organization.id as OrganizationId

            logger.debug('Create new offer revision with this content')
            try {
                OfferRevision offerRevisionCreated = offerRevisionResource.createOfferRevision(offerRevision).get()

                logger.debug('put the item revision to APPROVED')
                offerRevisionCreated.setStatus("APPROVED")
                offerRevisionResource.updateOfferRevision(offerRevisionCreated.revisionId, offerRevisionCreated).get()
            } catch (Exception e) {
                logger.error("Error creating offer revision $offerRevision.revisionId.", e)
            }
        } else {
            logger.debug("$offerRevision.revisionId already exists, skipped!")
        }
    }
}
