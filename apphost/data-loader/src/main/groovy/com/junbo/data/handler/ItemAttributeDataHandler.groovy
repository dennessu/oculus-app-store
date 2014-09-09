package com.junbo.data.handler

import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.resource.ItemAttributeResource
import com.junbo.data.handler.utils.CatalogUtils
import com.junbo.data.model.AttributeData
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * The ItemAttributeDataHandler class.
 */
@CompileStatic
class ItemAttributeDataHandler extends BaseAttributeDataHandler {

    private ItemAttributeResource itemAttributeResource

    private CatalogUtils catalogUtils

    @Required
    void setItemAttributeResource(ItemAttributeResource itemAttributeResource) {
        this.itemAttributeResource = itemAttributeResource
    }

    @Required
    void setCatalogUtils(CatalogUtils catalogUtils) {
        this.catalogUtils = catalogUtils
    }

    @Override
    protected void save(AttributeData attributeData, String parentId) {
        String name = attributeData.locales.get(nameLocale).name
        ItemAttribute existing = catalogUtils.getItemAttributeByName(name)
        if (existing != null) {
            logger.debug("update existing item attribute, id={}", existing.getId())
            existing.type = attributeData.type
            existing.parentId = parentId
            existing.locales = attributeData.locales
            itemAttributeResource.update(existing.getId(), existing).get()
        } else {
            logger.debug("create offer attribute, name={}", name)
            itemAttributeResource.createAttribute(new ItemAttribute(
                    type: attributeData.type,
                    parentId: attributeData.parent,
                    locales: attributeData.locales
            )).get()
        }
    }

    @Override
    protected String getIdByName(String name) {
        return catalogUtils.getItemAttributeByName(name)?.getId()
    }
}
