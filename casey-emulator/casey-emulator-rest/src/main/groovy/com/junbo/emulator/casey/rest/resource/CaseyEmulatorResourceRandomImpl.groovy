package com.junbo.emulator.casey.rest.resource
import com.junbo.authorization.AuthorizeContext
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.ItemId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.emulator.casey.rest.DataGenerator
import com.junbo.emulator.casey.rest.EmulatorHeaders
import com.junbo.emulator.casey.rest.EmulatorUtils
import com.junbo.emulator.casey.rest.ResourceContainer
import com.junbo.emulator.casey.spec.model.CaseyEmulatorData
import com.junbo.emulator.casey.spec.resource.CaseyEmulatorResource
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.casey.CaseyResults
import com.junbo.store.spec.model.external.casey.CaseyReview
import com.junbo.store.spec.model.external.casey.ReviewSearchParams
import com.junbo.store.spec.model.external.casey.cms.*
import com.junbo.store.spec.model.external.casey.search.CaseyOffer
import com.junbo.store.spec.model.external.casey.search.OfferSearchParams
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
/**
 * The CaseyEmulatorResourceRandomImpl class.
 */
@CompileStatic
@Component('randomCaseyEmulatorResource')
class CaseyEmulatorResourceRandomImpl implements CaseyEmulatorResource {

    private List<CaseyOffer> caseyOffers

    private Map<ItemId, CaseyOffer> itemIdCaseyOfferMap

    @Resource(name = 'caseyEmulatorDataGenerator')
    private DataGenerator dataGenerator

    @Resource(name = 'caseyResourceContainer')
    ResourceContainer resourceContainer

    @Resource(name = 'caseyEmulatorUtils')
    EmulatorUtils emulatorUtils

    @Override
    Promise<CaseyResults<CaseyOffer>> searchOffers(OfferSearchParams params) {
        loadOffers()
        if (params.itemId != null) {
            CaseyOffer offer = itemIdCaseyOfferMap.get(params.itemId)
            return Promise.pure(new CaseyResults<CaseyOffer>(items: offer == null ? [] : [offer]))
        }
        params.count = params.count == null ? 10 : params.count
        int offset = 0
        if (params.cursor != null) {
            offset = Integer.valueOf(params.cursor);
        }
        if (offset >= caseyOffers.size()) {
            return Promise.pure(new CaseyResults<CaseyOffer>(items: []))
        }
        CaseyResults<CaseyOffer> results = new CaseyResults<CaseyOffer>()
        results.items = caseyOffers.subList(offset, Math.min(offset + params.count, caseyOffers.size()))
        if (offset + params.count < caseyOffers.size()) {
            results.cursor = offset + params.count
        }
        return Promise.pure(results)
    }

    @Override
    Promise<CaseyResults<CaseyAggregateRating>> getRatingByItemId(String itemId) {
        return Promise.pure(
                new CaseyResults<CaseyAggregateRating>(
                        items: dataGenerator.generateCaseyAggregateRating(itemId)
                )
        )
    }

    @Override
    Promise<CaseyResults<CaseyReview>> getReviews(ReviewSearchParams params) {
        if (params.userId != null) {
            return Promise.pure(new CaseyResults<CaseyReview>(
                    items: dataGenerator.random.nextBoolean() ? [] as List :
                            [dataGenerator.generateCaseyReview(params.resourceType, params.resourceId, params.userId)] as List
            ))
        }

        String userIdText = JunboHttpContext.getData().getRequestHeaders().getFirst(EmulatorHeaders.X_REVIEW_USER_ID.name())
        ArrayList<UserId> userIds = [AuthorizeContext.currentUserId] as ArrayList
        if (!StringUtils.isBlank(userIdText)) {
            for(String s : userIdText.split(',')) {
                userIds.add(new UserId(IdFormatter.decodeId(UserId, s)));
            }
        }
        int num = 10
        CaseyResults<CaseyReview> results = new CaseyResults<>(items: [])
        for (int i = 0; i < num; ++i) {
            results.items << dataGenerator.generateCaseyReview(params.resourceType, params.resourceId, userIds.get(dataGenerator.random.nextInt(userIds.size())))
        }
    }

    @Override
    Promise<CaseyReview> addReview(String authorization, CaseyReview review) {
        throw new RuntimeException('not implemented')
    }

    @Override
    Promise<CaseyResults<CmsCampaign>> getCmsCampaigns(CmsCampaignGetParam cmsCampaignGetParam) {
        throw new RuntimeException('not implemented')
    }

    @Override
    Promise<CaseyResults<CmsPage>> getCmsPages(CmsPageGetParams pageGetParams) {
        throw new RuntimeException('not implemented')
    }

    @Override
    Promise<CmsContent> getCmsContent(String contentId) {
        throw new RuntimeException('not implemented')
    }

    @Override
    Promise<CaseyEmulatorData> postEmulatorData(CaseyEmulatorData caseyEmulatorData) {
        throw new RuntimeException('not implemented')
    }

    private void loadOffers() {
        if (this.caseyOffers != null) {
            return
        }
        List<CaseyOffer> caseyOffers = []
        Map<ItemId, CaseyOffer> itemIdCaseyOfferMap = [:]

        String cursor = null
        while (true) {
            Results<Offer> offerResults =  resourceContainer.offerResource.getOffers(new OffersGetOptions(published: true, cursor: cursor)).get()
            for (Offer offer: offerResults.items) {
                CaseyOffer caseyOffer = emulatorUtils.buildCaseyOffer(offer, new LocaleId('en_US'), new CountryId('US')) { ItemId itemId ->
                    getRatingByItemId(itemId.value).get()
                }
                caseyOffers.add(caseyOffer)
            }

            cursor = CommonUtils.getQueryParam(offerResults?.next?.href, 'cursor')
            if (CollectionUtils.isEmpty(offerResults.items) || StringUtils.isBlank(cursor)) {
                break
            }
        }

        caseyOffers.each { CaseyOffer caseyOffer ->
            if (CollectionUtils.isEmpty(caseyOffer?.items)) {
                itemIdCaseyOfferMap.put(caseyOffer.items[0].self, caseyOffer)
            }
        }

        this.caseyOffers = caseyOffers
        this.itemIdCaseyOfferMap = itemIdCaseyOfferMap
    }

}
