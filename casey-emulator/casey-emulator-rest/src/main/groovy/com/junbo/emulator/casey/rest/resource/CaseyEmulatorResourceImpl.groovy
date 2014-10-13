package com.junbo.emulator.casey.rest.resource
import com.fasterxml.jackson.core.type.TypeReference
import com.junbo.authorization.AuthorizeContext
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.emulator.casey.rest.*
import com.junbo.emulator.casey.spec.model.CaseyEmulatorData
import com.junbo.emulator.casey.spec.resource.CaseyEmulatorResource
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.external.casey.*
import com.junbo.store.spec.model.external.casey.cms.*
import com.junbo.store.spec.model.external.casey.search.CaseyOffer
import com.junbo.store.spec.model.external.casey.search.OfferSearchParams
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.Assert
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

    @Resource(name = 'caseyResourceContainer')
    ResourceContainer resourceContainer

    @Resource(name = 'randomCaseyEmulatorResource')
    CaseyEmulatorResource randomCaseyEmulatorResource

    @Resource(name = 'caseyEmulatorDataGenerator')
    private DataGenerator dataGenerator

    @Resource(name = 'caseyEmulatorLocaleUtils')
    LocaleUtils localeUtils

    @Value('${emulator.casey.random.enabled}')
    private boolean randomData

    private Map<String, CmsContent> cmsContentMap = [:]

    @Override
    Promise<CaseyResults<CaseyOffer>> searchOffers(OfferSearchParams params) {
        emulatorUtils.emulateLatency()
        emulatorUtils.emulateError('search')
        if (randomData) {
            return randomCaseyEmulatorResource.searchOffers(params)
        }

        params.count = params.count == null ? 10 : params.count
        if (params.offerId != null) {
            return Promise.pure(searchByOfferId(params))
        } else if (!StringUtils.isEmpty(params.cmsPage)) {
            return Promise.pure(searchFromCms(params))
        } else {
            return Promise.pure(searchOffer(params))
        }
    }

    @Override
    Promise<CaseyResults<CaseyAggregateRating>> getRatingByItemId(String itemId) {
        emulatorUtils.emulateLatency()
        emulatorUtils.emulateError('getRatings')
        if (randomData) {
            return randomCaseyEmulatorResource.getRatingByItemId(itemId)
        }

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
        Assert.isTrue(params.resourceType == 'item')
        emulatorUtils.emulateLatency()
        emulatorUtils.emulateError('getReviews')
        if (randomData) {
            return randomCaseyEmulatorResource.getReviews(params)
        }

        CaseyResults<CaseyReview> results = new CaseyResults<CaseyReview>()
        results.count = params.count
        results.items = []
        List<CaseyReview> reviews = caseyEmulatorDataRepository.get().caseyReviews
        if (CollectionUtils.isEmpty(reviews)) {
            return Promise.pure(results)
        }

        int offset = params.cursor == null ? 0 : Integer.valueOf(params.cursor)
        for (; offset < reviews.size(); ++offset) {
            if (params.count != null && results.items.size() >= params.count) {
                break
            }
            CaseyReview caseyReview = reviews[offset]
            if ((params.userId == null || caseyReview.user.getId() == IdFormatter.encodeId(params.userId))
                && (params.resourceId == null || params.resourceId == caseyReview.resource.getId())) {
                results.items << caseyReview
            }
        }

        if (offset < reviews.size()) {
            results.rawCursor = dataGenerator.genCursor(offset.toString())
        }
        return Promise.pure(results)
    }

    @Override
    Promise<CaseyReview> addReview(String authorization, CaseyReview review) {
        Assert.isTrue(review.resourceType == 'item')
        emulatorUtils.emulateLatency()
        emulatorUtils.emulateError('addReviews')
        Assert.isNull(review.getUser())
        Assert.isNull(review.getPostedDate())
        CaseyEmulatorData caseyEmulatorData = caseyEmulatorDataRepository.get()
        if (caseyEmulatorData.caseyReviews == null) {
            caseyEmulatorData.caseyReviews = []
        }

        review.postedDate = new Date()
        review.user = new CaseyLink(id: IdFormatter.encodeId(AuthorizeContext.currentUserId), href: IdUtil.toHref(AuthorizeContext.currentUserId))
        review.self = new CaseyLink(id: UUID.randomUUID().toString())
        caseyEmulatorData.caseyReviews << review
        return Promise.pure(review)
    }

    @Override
    Promise<CaseyReview> putReview(String authorization, String reviewId, CaseyReview review) {
        emulatorUtils.emulateLatency()
        CaseyEmulatorData caseyEmulatorData = caseyEmulatorDataRepository.get()
        CaseyReview oldReview = caseyEmulatorData.caseyReviews.find {CaseyReview e -> e.self.id == reviewId}
        Assert.notNull(oldReview)
        Assert.isTrue(oldReview.user.getId() == review.user.getId())
        Assert.isTrue(oldReview.country == review.country)
        Assert.isTrue(oldReview.locale == review.locale)
        Assert.isTrue(oldReview.reviewTitle == review.reviewTitle)
        Assert.isTrue(oldReview.review == review.review)
        Assert.isTrue(oldReview.resourceType == review.resourceType)
        Assert.isTrue(oldReview.resource.getId() == review.resource.getId())
        Assert.isTrue(((oldReview.postedDate.time / 1000) as long) == ((review.postedDate.time / 1000)) as long)
        Assert.isTrue(oldReview.getSelf().getId() == review.getSelf().getId())
        Assert.isTrue(oldReview.self.rev == review.self.rev)
        oldReview.ratings = review.ratings
        return Promise.pure(oldReview)
    }

    @Override
    Promise<CaseyResults<CmsCampaign>> getCmsCampaigns(CmsCampaignGetParam cmsCampaignGetParam) {
        emulatorUtils.emulateLatency()
        emulatorUtils.emulateError('getCmsCampaigns')
        assert cmsCampaignGetParam.expand == 'results/placements/content'
        emulatorUtils.emulateLatency()
        CaseyResults<CmsCampaign> results = new CaseyResults<>()
        results.items = caseyEmulatorDataRepository.get().cmsCampaigns
        return Promise.pure(results)
    }

    @Override
    Promise<CaseyResults<CmsPage>> getCmsPages(CmsPageGetParams pageGetParams) {
        emulatorUtils.emulateLatency()
        emulatorUtils.emulateError('getCmsPages')
        List<CmsPage> pages = caseyEmulatorDataRepository.get().cmsPages
        if (pages == null) {
            return Promise.pure(new CaseyResults<CmsPage>(items: []))
        }
        List<CmsPage> cmsPages = pages.findAll() { CmsPage cmsPage ->
            if (pageGetParams.path != null) {
                if ("\"${cmsPage.path}\"" != pageGetParams.path) {
                    return false
                }
            }
            if (pageGetParams.label != null) {
                if ("\"${cmsPage.label}\"" != pageGetParams.label) {
                    return false
                }
            }
            return true
        }.asList()

        return Promise.pure(new CaseyResults<CmsPage>(items: cmsPages))
    }

    @Override
    Promise<CmsContent> getCmsContent(String contentId) {
        emulatorUtils.emulateLatency()
        emulatorUtils.emulateError('getCmsContent')
        return Promise.pure(cmsContentMap[contentId])
    }

    @Override
    Promise<CmsSchedule> getCmsSchedules(String pageId, CmsScheduleGetParams cmsScheduleGetParams) {
        emulatorUtils.emulateLatency()
        emulatorUtils.emulateError('getCmsSchedules')
        Assert.notNull(cmsScheduleGetParams.locale)
        Assert.notNull(cmsScheduleGetParams.country)
        CmsSchedule cmsSchedule = caseyEmulatorDataRepository.get().cmsSchedules.find { CmsSchedule cmsSchedule ->
            return cmsSchedule.self?.id == pageId
        }
        if (cmsSchedule == null) {
            throw new RuntimeException("schedule error")
        }
        cmsSchedule = filterLocale(cmsSchedule, cmsScheduleGetParams.getLocale())
        return Promise.pure(cmsSchedule)
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
                return new CaseyResults<CaseyOffer>(items: offers, rawCursor:  dataGenerator.genCursor((offset + searchParams.count).toString()))
            }
        }
        return new CaseyResults<CaseyOffer>(items: [])
    }

    private CaseyResults<CaseyOffer> searchOffer(OfferSearchParams searchParams) {
        while (true) {
            Results<Offer> offerResults = resourceContainer.offerResource.getOffers(new OffersGetOptions(published: true,
                    itemId: searchParams.itemId?.value,
                    category: searchParams.category, cursor: searchParams.cursor, size: searchParams.count)).get()
            CaseyResults<CaseyOffer> results = new CaseyResults<CaseyOffer>()
            results.items = offerResults.items.collect { Offer offer ->
                buildCaseyOffer(offer, new LocaleId(searchParams.locale), new CountryId(searchParams.country))
            }

            String cursor = CommonUtils.getQueryParam(offerResults.next?.href, 'cursor')
            results.items = results.items.findAll { CaseyOffer offer ->
                checkOfferValid(offer, searchParams)
            }.asList()
            if (!results.items.isEmpty()) {
                results.rawCursor = dataGenerator.genCursor(cursor)
                return results
            }

            if (offerResults.items.isEmpty() || StringUtils.isEmpty(cursor)) {
                break;
            }
            searchParams.cursor = cursor
        }
        return new CaseyResults<CaseyOffer>(items: [], rawCursor: dataGenerator.genCursor('end'))
    }

    private CaseyResults<CaseyOffer> searchByOfferId(OfferSearchParams searchParams) {
        List<CaseyOffer> offers = []
        Offer offer = resourceContainer.offerResource.getOffer(searchParams.offerId).get()
        CaseyOffer caseyOffer = buildCaseyOffer(offer, new LocaleId(searchParams.locale), new CountryId(searchParams.country))
        if (checkOfferValid(caseyOffer, searchParams)) {
            offers << caseyOffer
        }
        return new CaseyResults<CaseyOffer>(items: offers, rawCursor: dataGenerator.genCursor('end'))
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

    @Override
    Promise<CaseyEmulatorData> postEmulatorData(CaseyEmulatorData caseyEmulatorData) {
        return Promise.pure(caseyEmulatorDataRepository.post(caseyEmulatorData)).then { CaseyEmulatorData result ->
            return Promise.pure(result)
        }
    }

    @Override
    Promise<Void> resetEmulatorData() {
        caseyEmulatorDataRepository.resetData()
        return Promise.pure()
    }

    private void addCmsContent(CmsContent cmsContent) {
        cmsContentMap[cmsContent.getSelf().getId()] = cmsContent
    }

    private CaseyOffer buildCaseyOffer(Offer offer, LocaleId localeId, CountryId countryId) {
        return emulatorUtils.buildCaseyOffer(offer, localeId, countryId) { ItemId itemId ->
            getRatingByItemId(itemId.value).get()
        }
    }

    private CmsSchedule filterLocale(CmsSchedule cmsSchedule, String localeId) {
        CmsSchedule result = ObjectMapperProvider.instance().readValue(ObjectMapperProvider.instance().writeValueAsString(cmsSchedule), new TypeReference<CmsSchedule>() {}) as CmsSchedule
        com.junbo.identity.spec.v1.model.Locale locale = resourceContainer.localeResource.get(new LocaleId(localeId), new LocaleGetOptions()).get()
        if (CollectionUtils.isEmpty(result.slots)) {
            return result
        }
        result.slots.values().each { CmsScheduleContent cmsScheduleContent ->
            if (CollectionUtils.isEmpty(cmsScheduleContent?.content?.contents?.values())) {
                return
            }
            cmsScheduleContent.content.contents.values().each { ContentItem contentItem ->
                if (contentItem.strings == null) {
                    return
                }
                contentItem.strings.each { CaseyContentItemString string ->
                    string.locales[localeId] = localeUtils.getLocaleProperties(string.locales, locale) as String
                }
            }
        }
        return result
    }
}

