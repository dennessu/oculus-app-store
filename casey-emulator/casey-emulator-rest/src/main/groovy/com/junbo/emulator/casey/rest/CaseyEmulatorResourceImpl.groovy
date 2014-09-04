package com.junbo.emulator.casey.rest

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.emulator.casey.spec.resource.CaseyEmulatorResource
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.external.casey.*
import com.junbo.store.spec.model.external.casey.cms.CmsCampaign
import com.junbo.store.spec.model.external.casey.cms.CmsContent
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The CaseyEmulatorResourceImpl class.
 */
@CompileStatic
@Component('defaultCaseyEmulatorResource')
class CaseyEmulatorResourceImpl implements CaseyEmulatorResource {

    @Resource(name = 'caseyEmulatorUtils')
    EmulatorUtils emulatorUtils

    @Resource(name = 'emulatorCaseyRepository')
    CaseyRepository caseyRepository

    @Override
    Promise<CaseyResults<JsonNode>> searchOffers(OfferSearchParams params) {
        throw new RuntimeException('not implemented yet')
    }

    @Override
    Promise<CaseyResults<CaseyAggregateRating>> getRatingByItemId(String itemId) {
        emulatorUtils.emulateLatency()
        def items = caseyRepository.getCaseyAggregateRating(itemId)
        if (items == null) {
            items = []
        }
        return Promise.pure(new CaseyResults<CaseyAggregateRating>(
                items: items,
                count: items.size(),
                totalCount: items.size() as long
        ))
    }

    @Override
    Promise<CaseyResults<CaseyReview>> getReviews(ReviewSearchParams params) {
        return Promise.pure(new CaseyResults<CaseyReview>(items: []))
    }

    @Override
    Promise<CaseyReview> addReview(CaseyReview review) {
        throw new RuntimeException('not implemented yet')
    }

    @Override
    Promise<CaseyResults<CmsCampaign>> getCmsCampaigns() {
        emulatorUtils.emulateLatency()
        def list = caseyRepository.getCmsCampaignList()
        return Promise.pure(new CaseyResults<CmsCampaign>(
                items: caseyRepository.getCmsCampaignList(),
                count: list.size(),
                totalCount: list.size() as long
        ))
    }

    @Override
    Promise<CmsContent> getCmsContent(String contentId) {
        emulatorUtils.emulateLatency()
        return Promise.pure(caseyRepository.getCmsContent(contentId))
    }
}
