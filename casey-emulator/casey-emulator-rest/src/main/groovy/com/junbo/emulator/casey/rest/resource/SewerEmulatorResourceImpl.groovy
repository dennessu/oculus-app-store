package com.junbo.emulator.casey.rest.resource

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.ItemAttributeGetOptions
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttributeGetOptions
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions
import com.junbo.common.id.EntitlementId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.emulator.casey.rest.ResourceContainer
import com.junbo.emulator.casey.spec.resource.CaseyEmulatorResource
import com.junbo.emulator.casey.spec.resource.SewerEmulatorResource
import com.junbo.entitlement.spec.model.Entitlement
import com.junbo.entitlement.spec.model.EntitlementSearchParam
import com.junbo.entitlement.spec.model.PageMetadata
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.external.sewer.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.sewer.catalog.SewerItem
import com.junbo.store.spec.model.external.sewer.entitlement.SewerEntitlement
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
import org.springframework.util.Assert

import javax.annotation.Resource

/**
 * The SewerEmulatorResourceImpl class.
 */
@CompileStatic
@Component('defaultSewerEmulatorResource')
class SewerEmulatorResourceImpl implements SewerEmulatorResource {

    @Resource(name = 'caseyResourceContainer')
    ResourceContainer resourceContainer

    @Resource(name = 'defaultCaseyEmulatorResource')
    CaseyEmulatorResource caseyEmulatorResource

    @Override
    Promise<SewerEntitlement> getEntitlement(EntitlementId entitlementId, String expand, String locale) {
        Assert.isTrue("(item(developer,currentRevision,categories,genres,rating))".equals(expand))
        resourceContainer.entitlementResource.getEntitlement(entitlementId).then { Entitlement entitlement ->
            return Promise.pure(toSewerEntitlement(entitlement, locale))
        }
    }

    @Override
    Promise<Results<SewerEntitlement>> searchEntitlements(EntitlementSearchParam searchParam, PageMetadata pageMetadata,
                                                          String expand, String locale) {
        Assert.isTrue("(results(item(developer,currentRevision,categories,genres,rating)))".equals(expand))
        resourceContainer.entitlementResource.searchEntitlements(searchParam, pageMetadata).then { Results<Entitlement> searchResults ->
            Results<SewerEntitlement> sewerEntitlementResults = new Results<SewerEntitlement>(
                    next: searchResults.next,
                    total: searchResults.total,
                    self: searchResults.self,
                    items: searchResults.items.collect {Entitlement entitlement -> toSewerEntitlement(entitlement, locale)}
            )
            return Promise.pure(sewerEntitlementResults)
        }
    }

    SewerEntitlement toSewerEntitlement(Entitlement entitlement, String locale) {
        SewerEntitlement sewerEntitlement = new SewerEntitlement(
                id: entitlement.id,
                isActive: entitlement.isActive,
                isBanned: entitlement.isBanned,
                userId: entitlement.userId,
                grantTime: entitlement.grantTime,
                expirationTime: entitlement.expirationTime,
                useCount: entitlement.useCount,
                type: entitlement.type
        )
        Item item = resourceContainer.itemResource.getItem(entitlement.itemId).get()
        SewerItem sewerItem = toSewerItem(item, locale)
        sewerEntitlement.itemNode = ObjectMapperProvider.instance().valueToTree(sewerItem)
        return sewerEntitlement
    }

    SewerItem toSewerItem(Item item, String locale) {
        JsonNode jsonNode = ObjectMapperProvider.instance().valueToTree(item)
        SewerItem sewerItem = ObjectMapperProvider.instance().readValue(jsonNode.traverse(), new TypeReference<SewerItem>() {}) as SewerItem
        ItemRevision itemRevision = resourceContainer.itemRevisionResource.getItemRevision(item.currentRevisionId, new ItemRevisionGetOptions(locale: locale)).get()
        Organization developer = resourceContainer.organizationResource.get(item.ownerId, new OrganizationGetOptions()).get()
        List<OfferAttribute> offerAttributes = []
        if (item.categories != null) {
            item.categories.each { String offerAttributeId ->
                offerAttributes << resourceContainer.offerAttributeResource.getAttribute(offerAttributeId, new OfferAttributeGetOptions(locale: locale)).get()
            }
        }
        List<ItemAttribute> itemAttributes = []
        if (item.genres != null) {
            item.genres.each { String itemAttributeId ->
                itemAttributes << resourceContainer.itemAttributeResource.getAttribute(itemAttributeId, new ItemAttributeGetOptions(locale: locale)).get()
            }
        }
        List<CaseyAggregateRating> aggregateRatingList = caseyEmulatorResource.getRatingByItemId(item.getId()).get().items
        sewerItem.currentRevisionNode = ObjectMapperProvider.instance().valueToTree(itemRevision)
        sewerItem.developerNode = ObjectMapperProvider.instance().valueToTree(developer)
        sewerItem.ratingNode = ObjectMapperProvider.instance().valueToTree(aggregateRatingList)
        sewerItem.categoriesNode = [] as  List
        offerAttributes.each { OfferAttribute offerAttribute ->
            sewerItem.categoriesNode << ObjectMapperProvider.instance().valueToTree(offerAttribute)
        }
        sewerItem.genresNode = [] as List
        itemAttributes.each { ItemAttribute itemAttribute ->
            sewerItem.genresNode << ObjectMapperProvider.instance().valueToTree(itemAttribute)
        }
        return sewerItem
    }
}
