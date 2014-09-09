package com.junbo.data.handler

import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.resource.OfferAttributeResource
import com.junbo.data.handler.utils.CatalogUtils
import com.junbo.data.model.AttributeData
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
/**
 * The OfferAttributeDataHandler class.
 */
@CompileStatic
class OfferAttributeDataHandler extends BaseAttributeDataHandler {

    private OfferAttributeResource offerAttributeResource

    private CatalogUtils catalogUtils

    @Required
    void setOfferAttributeResource(OfferAttributeResource offerAttributeResource) {
        this.offerAttributeResource = offerAttributeResource
    }

    @Required
    void setCatalogUtils(CatalogUtils catalogUtils) {
        this.catalogUtils = catalogUtils
    }

    @Override
    protected void save(AttributeData attributeData, String parentId) {
        String name = attributeData.locales.get(nameLocale).name
        OfferAttribute existing = catalogUtils.getOfferAttributeByName(name)
        if (existing != null) {
            logger.debug("update existing offer attribute, id={}", existing.getId())
            existing.type = attributeData.type
            existing.parentId = parentId
            existing.locales = attributeData.locales
            offerAttributeResource.update(existing.getId(), existing).get()
        } else {
            logger.debug("create offer attribute, name={}", name)
            offerAttributeResource.createAttribute(new OfferAttribute(
                    type: attributeData.type,
                    parentId: attributeData.parent,
                    locales: attributeData.locales
            )).get()
        }
    }

    @Override
    protected String getIdByName(String name) {
        return catalogUtils.getOfferAttributeByName(name)?.getId()
    }

}
