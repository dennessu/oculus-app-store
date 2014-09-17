package com.junbo.emulator.casey.rest

import com.junbo.authorization.AuthorizeContext
import com.junbo.catalog.spec.enums.PriceType
import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.ItemAttributeGetOptions
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttributeGetOptions
import com.junbo.catalog.spec.model.item.*
import com.junbo.catalog.spec.model.offer.*
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.emulator.casey.spec.model.CaseyEmulatorData
import com.junbo.emulator.casey.spec.model.CaseyReviewExtend
import com.junbo.emulator.casey.spec.resource.CaseyEmulatorResource
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.rest.browse.SectionService
import com.junbo.store.spec.model.external.casey.*
import com.junbo.store.spec.model.external.casey.cms.*
import com.junbo.store.spec.model.external.casey.search.*
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
/**
 * The CaseyEmulatorResourceImpl class.
 */
@CompileStatic
@Component('defaultCaseyEmulatorResource')
class CaseyEmulatorResourceImpl implements CaseyEmulatorResource {

    private int defaultPageSize = 5

    @Resource(name = 'caseyEmulatorUtils')
    EmulatorUtils emulatorUtils

    @Resource(name = 'caseyEmulatorDataRepository')
    CaseyEmulatorDataRepository caseyEmulatorDataRepository

    @Resource(name = 'storeResourceContainer')
    ResourceContainer resourceContainer

    @Resource(name = 'storeSectionService')
    SectionService sectionService

    private CmsCampaign cmsCampaign

    private Map<String, CmsContent> cmsContentMap = [:]

    private List<String> offerNames = []

    private List<String> itemNames = []

    @Override
    Promise<CaseyResults<CaseyOffer>> searchOffers(OfferSearchParams params) {
        params.count = params.count == null ? 10 : params.count
        if (params.offerId != null) {
            return Promise.pure(searchByOfferId(params))
        } else if (!StringUtils.isEmpty(params.cmsPage)) {
            return Promise.pure(searchFromCms(params))
        } else {
            return Promise.pure(searchByCategory(params))
        }
    }

    @Override
    Promise<CaseyResults<CaseyAggregateRating>> getRatingByItemId(String itemId) {
        emulatorUtils.emulateLatency()
        def items = caseyEmulatorDataRepository.get().caseyAggregateRatings
        if (items == null) {
            items = []
        }
        items = items.collect { CaseyAggregateRating caseyAggregateRating ->
            caseyAggregateRating.resourceType = 'item'
            caseyAggregateRating.resourceId = itemId
            return caseyAggregateRating
        }
        return Promise.pure(new CaseyResults<CaseyAggregateRating>(
                items: items,
                count: items.size(),
                totalCount: items.size() as long
        ))
    }

    @Override
    Promise<CaseyResults<CaseyReview>> getReviews(ReviewSearchParams params) {
        CaseyResults<CaseyReview> results = new CaseyResults<CaseyReview>()
        results.items = []
        emulatorUtils.emulateLatency()
        List reviews = caseyEmulatorDataRepository.get().caseyReviews
        if (CollectionUtils.isEmpty(reviews)) {
            return Promise.pure(results)
        }

        if (params.userId != null) { // user review
            CaseyReview userReview = reviews.find { CaseyReview caseyReview ->
                caseyReview.user.getId() == IdFormatter.encodeId(params.userId)
            }
            if (userReview != null) {
                results.items << userReview
            }
            return Promise.pure(process(results))
        } else {
            int pageSize = params.count == null ? defaultPageSize : params.count
            int offset = 0
            if (params.cursor != null) {
                try {
                    offset = Integer.parseInt(params.cursor)
                    if (offset < 0) {
                        offset = 0
                    }
                } catch (NumberFormatException ex) {}
            }
            if (offset >= reviews.size()) {
                return Promise.pure(process(results))
            }

            results.items = reviews.subList(offset, Math.min(reviews.size(), offset + pageSize))
            if (offset + pageSize < reviews.size()) {
                results.count = params.count
                results.cursor = (offset + pageSize).toString()
            }
            return Promise.pure(process(results))
        }
    }

    @Override
    Promise<CaseyReview> addReview(CaseyReview review) {
        CaseyEmulatorData caseyEmulatorData = caseyEmulatorDataRepository.get()
        if (caseyEmulatorData.caseyReviews == null) {
            caseyEmulatorData.caseyReviews = []
        }

        review.postedDate = new Date()
        review.user = new CaseyLink(id: IdFormatter.encodeId(AuthorizeContext.currentUserId), href: IdUtil.toHref(AuthorizeContext.currentUserId))
        review.self = new CaseyLink(id: UUID.randomUUID().toString())
        caseyEmulatorData.caseyReviews << review
        return Promise.pure(new CaseyReviewExtend(review))
    }

    @Override
    Promise<CaseyResults<CmsCampaign>> getCmsCampaigns() {
        emulatorUtils.emulateLatency()
        return Promise.pure(new CaseyResults<CmsCampaign>(
                items: [getCmsCampaign()],
                count: 1,
                totalCount: 1L
        ))
    }

    @Override
    Promise<CaseyResults<CmsPage>> getCmsPages(CmsPageGetParams pageGetParams) {
        CmsPage cmsPage = caseyEmulatorDataRepository.get().cmsPages?.find { CmsPage cmsPage ->
            return "\"/${cmsPage.path}\"" == pageGetParams.path
        }

        if (cmsPage != null) {
            return Promise.pure(new CaseyResults<CmsPage>(items: [cmsPage]))
        } else {
            return Promise.pure(new CaseyResults<CmsPage>(items: []))
        }
    }

    @Override
    Promise<CmsContent> getCmsContent(String contentId) {
        emulatorUtils.emulateLatency()
        return Promise.pure(cmsContentMap[contentId])
    }

    private CaseyResults<CaseyOffer> searchFromCms(OfferSearchParams searchParams) {
        if (StringUtils.isEmpty(searchParams.cmsSlot)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('cmsSlot').exception()
        }
        String key = "${searchParams.cmsPage}-${searchParams.cmsSlot}"
        List<OfferId> offerIds = caseyEmulatorDataRepository.get().cmsPageOffers?.get(key)
        if (CollectionUtils.isEmpty(offerIds)) {
            return new CaseyResults<CaseyOffer>(items: [])
        }

        int offset = 0
        if (!StringUtils.isEmpty(searchParams.cursor)) {
            offset = Integer.parseInt(searchParams.cursor)
        }
        for (;offset < offerIds.size();offset += searchParams.count) {
            List<CaseyOffer> offers = offerIds.subList(offset, Math.min(offset + searchParams.count, offerIds.size())).collect { OfferId offerId ->
                Offer offer = resourceContainer.offerResource.getOffer(offerId.value).get();
                buildCaseyOffer(offer, new LocaleId(searchParams.locale), new CountryId(searchParams.country))
            }
            offers = offers.findAll { CaseyOffer offer ->
                checkOfferValid(offer, searchParams)
            }.asList()
            if (offers.size() > 0) {
                return new CaseyResults<CaseyOffer>(items: offers, cursor:  (offset + searchParams.count).toString())
            }
        }
        return new CaseyResults<CaseyOffer>(items: [])
    }

    private CaseyResults<CaseyOffer> searchByCategory(OfferSearchParams searchParams) {
        while (true) {
            Results<Offer> offerResults = resourceContainer.offerResource.getOffers(new OffersGetOptions(published: true, category: searchParams.category, cursor: searchParams.cursor, size: searchParams.count)).get()
            CaseyResults<CaseyOffer> results = new CaseyResults<CaseyOffer>()
            results.items = offerResults.items.collect { Offer offer ->
                buildCaseyOffer(offer, new LocaleId(searchParams.locale), new CountryId(searchParams.country))
            }

            String cursor = CommonUtils.getQueryParam(offerResults.next?.href, 'cursor')
            results.items = results.items.findAll { CaseyOffer offer ->
                checkOfferValid(offer, searchParams)
            }.asList()
            if (!results.items.isEmpty()) {
                results.cursor = cursor
                return results
            }

            if (offerResults.items.isEmpty() || StringUtils.isEmpty(cursor)) {
                break;
            }
            searchParams.cursor = cursor
        }
        return new CaseyResults<CaseyOffer>(items: [], cursor: 'end')
    }

    private CaseyResults<CaseyOffer> searchByOfferId(OfferSearchParams searchParams) {
        List<CaseyOffer> offers = []
        Offer offer = resourceContainer.offerResource.getOffer(searchParams.offerId).get()
        CaseyOffer caseyOffer = buildCaseyOffer(offer, new LocaleId(searchParams.locale), new CountryId(searchParams.country))
        if (checkOfferValid(caseyOffer, searchParams)) {
            offers << caseyOffer
        }
        return new CaseyResults<CaseyOffer>(items: offers, cursor: 'end')
    }

    private boolean checkOfferValid(CaseyOffer offer,  OfferSearchParams searchParams) {
        if (!offer.regions.containsKey(searchParams.country)) {
            return false
        }
        return true
    }

    private CmsContent createContent(List<String> resourceNames, String resourceType, String contentsName, String label, String description) {
        CmsContent cmsContent = new CmsContent()
        cmsContent.self = new CaseyLink(id: UUID.randomUUID().toString())
        cmsContent.label = label
        cmsContent.description = description
        cmsContent.contents = [:]
        ContentItem contentItem = new ContentItem()
        cmsContent.contents[contentsName] = contentItem
        contentItem.type = resourceType
        contentItem.links = []
        resourceNames.each { String name ->
            String id
            if (resourceType == 'item') {
                id = geItemIdByName(name)
            } else if (resourceType == 'offer') {
                id = getOfferIdByName(name)
            } else {
                assert false, 'should not reach here'
            }
            if (id != null) {
                contentItem.links << new CaseyLink(id: id)
            }
        }
        return cmsContent
    }

    String getOfferIdByName(String name) {
        Results<Offer> offerResults = resourceContainer.offerResource.getOffers(new OffersGetOptions(query: "name:${name}")).get()
        if (offerResults.items.isEmpty()) {
            return null
        }
        return offerResults.items[0].offerId
    }

    String geItemIdByName(String name) {
        Results<Item> itemResults = resourceContainer.itemResource.getItems(new ItemsGetOptions(query: "name:${name}")).get()
        if (itemResults.items.isEmpty()) {
            return null
        }
        return itemResults.items[0].itemId
    }

    private CmsCampaign getCmsCampaign() {
        if (cmsCampaign != null) {
            return cmsCampaign
        }

        synchronized (this) {
            if (cmsCampaign != null) {
                return cmsCampaign
            }
            CmsCampaign result = new CmsCampaign()
            result.self = new CaseyLink(id: UUID.randomUUID().toString(), href: 'abc')
            result.eligibleCountries = [new CaseyLink(id: 'US')]
            result.status = 'APPROVED'

            result.placements = []
            CmsContent cmsContent= createContent(offerNames, 'offer', 'offers', 'feature-offers', 'feature offers')
            addCmsContent(cmsContent)
            result.placements << new Placement(content: new CaseyLink(id: cmsContent.self.id))

            cmsContent= createContent(itemNames, 'item', 'items', 'feature-items', 'feature items')
            addCmsContent(cmsContent)
            result.placements << new Placement(content: new CaseyLink(id: cmsContent.self.id))
            cmsCampaign = result
        }
        return cmsCampaign
    }

    @Override
    Promise<CaseyEmulatorData> postEmulatorData(CaseyEmulatorData caseyEmulatorData) {
        return Promise.pure(caseyEmulatorDataRepository.post(caseyEmulatorData)).then { CaseyEmulatorData result ->
            sectionService.refreshSectionInfoNode()
            return Promise.pure(result)
        }
    }

    private void addCmsContent(CmsContent cmsContent) {
        cmsContentMap[cmsContent.getSelf().getId()] = cmsContent
    }

    private CaseyOffer buildCaseyOffer(Offer offer, LocaleId localeId, CountryId countryId) {
        CaseyOffer caseyOffer = new CaseyOffer()
        caseyOffer.self = new OfferId(offer.getId())
        caseyOffer.categories = offer.categories?.collect { String categoryId ->
            OfferAttribute offerAttribute = resourceContainer.offerAttributeResource.getAttribute(categoryId, new OfferAttributeGetOptions(locale: localeId.value)).get()
            CatalogAttribute catalogAttribute = new CatalogAttribute()
            catalogAttribute.attributeId = categoryId
            catalogAttribute.name = offerAttribute.locales?.get(localeId.value)?.getName()
            return catalogAttribute
        }

        if (offer.currentRevisionId != null) {
            OfferRevision offerRevision = resourceContainer.offerRevisionResource.getOfferRevision(offer.currentRevisionId, new OfferRevisionGetOptions(locale: localeId.value)).get();
            caseyOffer.longDescription = offerRevision.locales?.get(localeId.value)?.longDescription
            caseyOffer.shortDescription = offerRevision.locales?.get(localeId.value)?.shortDescription
            caseyOffer.regions = offerRevision.countries
            caseyOffer.price = new CaseyPrice()
            caseyOffer.price.isFree = offerRevision.price?.priceType == PriceType.FREE.name()
            // Set<String> currencies = offerRevision.price?.prices?.get(countryId.value)?.keySet()
            // todo set the price
            caseyOffer.items = offerRevision?.items?.collect { ItemEntry itemEntry ->
                return getCaseyItem(new ItemId(itemEntry.itemId), localeId)
            }
        }

        caseyOffer.publisher = offer.ownerId
        return caseyOffer
    }


    private CaseyItem getCaseyItem(ItemId itemId, LocaleId localeId) {
        CaseyItem caseyItem = new CaseyItem()
        Item item = resourceContainer.itemResource.getItem(itemId.value).get()
        caseyItem.self = itemId
        caseyItem.type = item.type
        caseyItem.developer = item.ownerId
        caseyItem.genres = item.genres?.collect { String genreId ->
            ItemAttribute itemAttribute = resourceContainer.itemAttributeResource.getAttribute(genreId, new ItemAttributeGetOptions(locale: localeId.value)).get()
            CatalogAttribute catalogAttribute = new CatalogAttribute()
            catalogAttribute.attributeId = genreId
            catalogAttribute.name = itemAttribute.locales?.get(localeId.value)?.getName()
            return catalogAttribute
        }

        if (item.currentRevisionId != null) {
            ItemRevision itemRevision = resourceContainer.itemRevisionResource.getItemRevision(item.currentRevisionId, new ItemRevisionGetOptions(locale: localeId.value)).get()
            caseyItem.packageName = itemRevision.packageName
            caseyItem.supportedLocales = itemRevision.supportedLocales
            caseyItem.binaries = itemRevision.binaries
            ItemRevisionLocaleProperties localeProperties = itemRevision.locales?.get(localeId.value)
            caseyItem.releaseNotes = localeProperties?.releaseNotes
            caseyItem.name = localeProperties?.name
            caseyItem.longDescription = localeProperties?.longDescription
            caseyItem.supportEmail = localeProperties?.supportEmail
            caseyItem.website = localeProperties?.website
            caseyItem.communityForumLink = localeProperties?.communityForumLink
            caseyItem.images = localeProperties?.images
        }

        return caseyItem
    }

    private CaseyResults<CaseyReview> process(CaseyResults<CaseyReview> caseyReviewResults) {
        caseyReviewResults.items = caseyReviewResults.items.collect { CaseyReview caseyReview ->
            return new CaseyReviewExtend(caseyReview)
        }
        return caseyReviewResults
    }
}

