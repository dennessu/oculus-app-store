package com.junbo.data.handler

import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.common.id.OrganizationId
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.langur.core.client.TypeReference
import com.junbo.common.error.AppErrorException
import com.junbo.catalog.spec.model.item.Item
import com.junbo.common.model.Results

import org.springframework.beans.factory.annotation.Required
import groovy.transform.CompileStatic

import org.springframework.core.io.Resource

/**
 * Created by jasonfu on 14-7-17.
 */
@CompileStatic
class ItemDataHandler extends BaseDataHandler {
    private OrganizationResource organizationResource
    private ItemResource itemResource

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Required
    void setItemResource(ItemResource itemResource) {
        this.itemResource = itemResource
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    @Override
    void handle(String content) {
        Item item
        try {
            item = transcoder.decode(new TypeReference<Item>() {}, content) as Item
        } catch (Exception e) {
            logger.error("Error parsing item $content", e)
            exit()
        }

        logger.info("loading item $item.itemId")

        Results<Item> existing = null
        Set<String> itemIds = new HashSet()
        itemIds.add(item.itemId)
        try {
            existing = itemResource.getItems(new ItemsGetOptions(itemIds:itemIds)).get()
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

            item.ownerId = organization.id as OrganizationId

            logger.debug('Create new item with this content')
            try {
                itemResource.create(item).get()
            } catch (Exception e) {
                logger.error("Error creating item $item.itemId.", e)
            }
        } else {
            logger.debug("$item.itemId already exists, skipped!")
        }
    }
}
