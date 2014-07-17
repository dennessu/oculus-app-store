package com.junbo.data.handler

import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions
import com.junbo.common.id.OrganizationId
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions
import com.junbo.catalog.spec.resource.ItemRevisionResource
import com.junbo.catalog.spec.model.item.ItemRevision
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
class ItemRevisionDataHandler extends BaseDataHandler {
    private OrganizationResource organizationResource
    private ItemRevisionResource itemRevisionResource

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Required
    void setItemRevisionResource(ItemRevisionResource itemRevisionResource) {
        this.itemRevisionResource = itemRevisionResource
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    @Override
    void handle(String content) {
        String defaultLocale = "en_US"
        ItemRevision itemRevision
        try {
            itemRevision = transcoder.decode(new TypeReference<ItemRevision>() {}, content) as ItemRevision
        } catch (Exception e) {
            logger.error("Error parsing item revision $content", e)
            exit()
        }

        logger.info("loading item revision $itemRevision.revisionId")

        Results<ItemRevision> existing = null
        Set<String> itemRevisionIds = new HashSet()
        itemRevisionIds.add(itemRevision.revisionId)
        try {
            existing = itemRevisionResource.getItemRevisions(new ItemRevisionsGetOptions(revisionIds: itemRevisionIds)).get()
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

            itemRevision.ownerId = organization.id as OrganizationId

            logger.debug('Create new item revision with this content')
            try {
                ItemRevision itemRevisionCreated = itemRevisionResource.createItemRevision(itemRevision).get()

                logger.debug('put the item revision to APPROVED')
                itemRevisionCreated.setStatus("APPROVED")
                itemRevisionResource.updateItemRevision(itemRevisionCreated.revisionId, itemRevisionCreated).get()
            } catch (Exception e) {
                logger.error("Error creating item revision $itemRevision.revisionId.", e)
            }
        } else {
            logger.debug("$itemRevision.revisionId already exists, skipped!")
        }
    }
}
