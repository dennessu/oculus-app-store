package com.junbo.store.rest.browse.impl

import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.OfferId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.rest.browse.SectionHandler
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.ListRequest
import com.junbo.store.spec.model.browse.ListResponse
import com.junbo.store.spec.model.browse.SectionLayoutRequest
import com.junbo.store.spec.model.browse.SectionLayoutResponse
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The CategorySectionHandler class.
 */
@CompileStatic
@Component('store.categorySectionHandler')
class CategorySectionHandler implements SectionHandler {

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeCatalogBrowseUtils')
    private CatalogBrowseUtils catalogBrowseUtils

    @Resource(name = 'storeBrowseDataBuilder')
    private BrowseDataBuilder browseDataBuilder

    @Resource(name = 'storeLocaleUtils')
    private LocaleUtils localeUtils

    private LocaleId nameLookupLocale = new LocaleId('en_US')

    private int defaultPageSize = 10

    private String[] categories

    @Value('${store.browse.categories}')
    public void setCategories(String categories) {
        this.categories = categories.split(',')
    }

    @Override
    Boolean canHandle(String category, String criteria, ApiContext apiContext) {
        catalogBrowseUtils.getOfferCategoryByName(category, nameLookupLocale).get() != null
    }

    @Override
    Promise<List<SectionInfoNode>> getTopLevelSectionInfoNode(ApiContext apiContext) {
        List<SectionInfoNode> result = []
        Promise.each(Arrays.asList(categories)) { String category ->
            getSectionInfoNode(category, null, apiContext).then { SectionInfoNode node ->
                result << node
                return Promise.pure()
            }
        }.then {
            return Promise.pure(result)
        }
    }

    @Override
    Promise<SectionInfoNode> getSectionInfoNode(String category, String criteria, ApiContext apiContext) {
        catalogBrowseUtils.getOfferCategoryByName(category, nameLookupLocale).then { OfferAttribute offerAttribute ->
            if (offerAttribute == null) {
                throw AppErrors.INSTANCE.sectionNotFound().exception()
            }
            SimpleLocaleProperties simpleLocaleProperties =
                    localeUtils.getLocaleProperties(offerAttribute.locales, apiContext.locale, 'OfferAttribute', offerAttribute.getId(), 'locales') as SimpleLocaleProperties
            return Promise.pure(new SectionInfoNode(
                name: simpleLocaleProperties?.name,
                category: category,
                criteria: null as String,
                children: []
            ))
        }
    }

    @Override
    Promise<SectionLayoutResponse> getSectionLayout(SectionLayoutRequest request, ApiContext apiContext) {
        SectionLayoutResponse response = new SectionLayoutResponse()
        catalogBrowseUtils.getOfferCategoryByName(request.category, nameLookupLocale).then { OfferAttribute category ->
            if (category == null) {
                throw AppErrors.INSTANCE.sectionNotFound().exception()
            }
            SimpleLocaleProperties simpleLocaleProperties =
                    localeUtils.getLocaleProperties(category.locales, apiContext.locale, 'OfferAttribute', category.getId(), 'locales') as SimpleLocaleProperties

            response.children = []
            response.breadcrumbs = []
            response.name = simpleLocaleProperties?.name
            response.ordered = false

            innerGetList(category.getId(), new ListRequest(category: request.category, criteria: request.criteria, count: request.count), apiContext).then { ListResponse listResponse ->
                response.items = listResponse.items
                response.next = listResponse.next
                return Promise.pure(response)
            }
        }
    }

    @Override
    Promise<ListResponse> getSectionList(ListRequest request, ApiContext apiContext) {
        catalogBrowseUtils.getOfferCategoryByName(request.category, nameLookupLocale).then { OfferAttribute category ->
            if (category == null) {
                throw AppErrors.INSTANCE.sectionNotFound().exception()
            }
            return innerGetList(category.getId(), request, apiContext)
        }
    }

    private Promise<ListResponse> innerGetList(String categoryId, ListRequest listRequest, ApiContext apiContext) {
        ListResponse response = new ListResponse()
        def option = new OffersGetOptions(
                category: categoryId,
                published: true,
                cursor: listRequest.cursor,
                size: listRequest.count != null ? listRequest.count : defaultPageSize
        )
        resourceContainer.offerResource.getOffers(option).then { Results<Offer> offerResults ->
            String cursor = CommonUtils.getQueryParam(offerResults.next?.href, 'cursor')
            if (cursor != null) {
                response.next = new ListResponse.NextOption(
                    category: listRequest.category,
                    criteria: listRequest.criteria,
                    cursor: cursor,
                    count: listRequest.count
                )
            }

            response.items = []
            Promise.each(offerResults.items) { Offer catalogOffer ->
                catalogBrowseUtils.getItem(new OfferId(catalogOffer.getOfferId()), false, apiContext).then { Item item ->
                    if (item != null) {
                        response.items << item
                    }
                    return Promise.pure()
                }
            }
        }.then {
            return Promise.pure(response)
        }
    }

}
