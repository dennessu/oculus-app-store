package com.junbo.emulator.casey.rest.resoure

import com.junbo.emulator.casey.spec.model.CaseyEmulatorData
import com.junbo.emulator.casey.spec.resource.CaseyEmulatorResource
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.casey.CaseyResults
import com.junbo.store.spec.model.external.casey.CaseyReview
import com.junbo.store.spec.model.external.casey.ReviewSearchParams
import com.junbo.store.spec.model.external.casey.cms.*
import com.junbo.store.spec.model.external.casey.search.CaseyOffer
import com.junbo.store.spec.model.external.casey.search.OfferSearchParams
import groovy.transform.CompileStatic

/**
 * The CaseyEmulatorResourceRandomImpl class.
 */
@CompileStatic
class CaseyEmulatorResourceRandomImpl implements CaseyEmulatorResource {

    private List<CaseyOffer> caseyOffers

    @Override
    Promise<CaseyResults<CaseyOffer>> searchOffers(OfferSearchParams params) {
        loadOffers()
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
        return Promise.pure()
    }

    @Override
    Promise<CaseyResults<CaseyReview>> getReviews(ReviewSearchParams params) {
        return Promise.pure()
    }

    @Override
    Promise<CaseyReview> addReview(String authorization, CaseyReview review) {
        return Promise.pure()
    }

    @Override
    Promise<CaseyResults<CmsCampaign>> getCmsCampaigns(CmsCampaignGetParam cmsCampaignGetParam) {
        return Promise.pure()
    }

    @Override
    Promise<CaseyResults<CmsPage>> getCmsPages(CmsPageGetParams pageGetParams) {
        return Promise.pure()
    }

    @Override
    Promise<CmsContent> getCmsContent(String contentId) {
        return Promise.pure()
    }

    @Override
    Promise<CaseyEmulatorData> postEmulatorData(CaseyEmulatorData caseyEmulatorData) {
        return Promise.pure()
    }

    private void loadOffers() {

    }
}
