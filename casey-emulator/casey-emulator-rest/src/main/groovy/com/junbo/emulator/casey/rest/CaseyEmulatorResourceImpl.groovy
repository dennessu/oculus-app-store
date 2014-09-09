package com.junbo.emulator.casey.rest
import com.fasterxml.jackson.databind.JsonNode
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.emulator.casey.spec.model.CaseyEmulatorData
import com.junbo.emulator.casey.spec.resource.CaseyEmulatorResource
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.external.casey.*
import com.junbo.store.spec.model.external.casey.cms.CmsCampaign
import com.junbo.store.spec.model.external.casey.cms.CmsContent
import com.junbo.store.spec.model.external.casey.cms.ContentItem
import com.junbo.store.spec.model.external.casey.cms.Placement
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
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

    @Resource(name = 'casey.offerClient')
    OfferResource offerResource

    @Resource(name = 'casey.itemClient')
    ItemResource itemResource

    private CmsCampaign cmsCampaign

    private Map<String, CmsContent> cmsContentMap = [:]

    @Value('${store.browse.campaign.label}')
    private String label

    private List<String> offerNames = []

    private List<String> itemNames = []

    @Value('${emulator.casey.campaign.offers}')
    void setOfferNames(String offerNames) {
        this.offerNames = Arrays.asList(offerNames.split(','))
    }

    @Value('${emulator.casey.campaign.items}')
    void setItemNames(String itemNames) {
        this.itemNames = Arrays.asList(itemNames.split(','))
    }

    @Override
    Promise<CaseyResults<JsonNode>> searchOffers(OfferSearchParams params) {
        throw new RuntimeException('not implemented yet')
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
            return Promise.pure(results)
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
                return Promise.pure(results)
            }

            results.items = reviews.subList(offset, Math.min(reviews.size(), offset + pageSize))
            if (offset + pageSize < reviews.size()) {
                results.count = params.count
                results.cursor = (offset + pageSize).toString()
            }
            return Promise.pure(results)
        }
    }

    @Override
    Promise<CaseyReview> addReview(CaseyReview review) {
        throw new RuntimeException('not implemented yet')
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
    Promise<CmsContent> getCmsContent(String contentId) {
        emulatorUtils.emulateLatency()
        return Promise.pure(cmsContentMap[contentId])
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
        Results<Offer> offerResults = offerResource.getOffers(new OffersGetOptions(query: "name:${name}")).get()
        if (offerResults.items.isEmpty()) {
            return null
        }
        return offerResults.items[0].offerId
    }

    String geItemIdByName(String name) {
        Results<Item> itemResults = itemResource.getItems(new ItemsGetOptions(query: "name:${name}")).get()
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
            result.label = label
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
        return Promise.pure(caseyEmulatorDataRepository.post(caseyEmulatorData))
    }

    private void addCmsContent(CmsContent cmsContent) {
        cmsContentMap[cmsContent.getSelf().getId()] = cmsContent
    }
}
