package com.junbo.data.handler.utils

import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions
import com.junbo.catalog.spec.resource.ItemAttributeResource
import com.junbo.catalog.spec.resource.OfferAttributeResource
import com.junbo.common.model.Results
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Required

/**
 * The CatalogUtils class.
 */
@CompileStatic
class CatalogUtils {

    private ItemAttributeResource itemAttributeResource

    private OfferAttributeResource offerAttributeResource

    private String nameLocale = 'en_US'

    @Required
    void setItemAttributeResource(ItemAttributeResource itemAttributeResource) {
        this.itemAttributeResource = itemAttributeResource
    }

    @Required
    void setOfferAttributeResource(OfferAttributeResource offerAttributeResource) {
        this.offerAttributeResource = offerAttributeResource
    }

    public ItemAttribute getItemAttributeByName(String name) {
        ItemAttribute result
        ItemAttributesGetOptions itemAttributesGetOptions = new ItemAttributesGetOptions()
        while (result == null) {
            Results<ItemAttribute> results = itemAttributeResource.getAttributes(itemAttributesGetOptions).get()
            result = results.items.find { ItemAttribute ItemAttribute ->
                ItemAttribute?.locales?.get(nameLocale)?.name == name
            }
            if (results.items.isEmpty() || StringUtils.isEmpty(itemAttributesGetOptions.nextCursor)) {
                break
            }
            itemAttributesGetOptions.cursor = itemAttributesGetOptions.nextCursor
        }
        return result
    }

    public OfferAttribute getOfferAttributeByName(String name) {
        OfferAttribute result
        OfferAttributesGetOptions offerAttributesGetOptions = new OfferAttributesGetOptions()
        while (result == null) {
            Results<OfferAttribute> results = offerAttributeResource.getAttributes(offerAttributesGetOptions).get()
            result = results.items.find { OfferAttribute offerAttribute ->
                offerAttribute?.locales?.get(nameLocale)?.name == name
            }

            if (results.items.isEmpty() || StringUtils.isEmpty(offerAttributesGetOptions.nextCursor)) {
                break
            }
            offerAttributesGetOptions.cursor = offerAttributesGetOptions.nextCursor
        }
        return result
    }
}
