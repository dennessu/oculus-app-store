package com.junbo.emulator.casey.rest

import com.junbo.store.spec.model.external.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.casey.cms.CmsCampaign
import com.junbo.store.spec.model.external.casey.cms.CmsContent
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentHashMap

/**
 * The CaseyRepository class.
 */
@CompileStatic
@Component('emulatorCaseyRepository')
class CaseyRepository {

    private List<CmsCampaign> cmsCampaignList = new ArrayList<>()

    private List<CmsContent> cmsContentList = new ArrayList<>()

    private Map<String, List<CaseyAggregateRating>> itemIdToAggregateRating = new ConcurrentHashMap<>()

    CmsCampaign addCmsCampaign(CmsCampaign campaign) {
        cmsCampaignList.add(campaign)
        return campaign
    }

    CmsContent addCmsContent(CmsContent cmsContent) {
        cmsContentList.add(cmsContent)
        return cmsContent
    }

    List<CmsCampaign> getCmsCampaignList() {
        return cmsCampaignList
    }

    CmsContent getCmsContent(String contentId) {
        return cmsContentList.find { CmsContent cmsContent ->
            return cmsContent?.self?.getId() == contentId
        }
    }

    CaseyAggregateRating addCaseyAggregateRating(String itemId, CaseyAggregateRating rating) {
        if (itemIdToAggregateRating[itemId] == null) {
            itemIdToAggregateRating[itemId] = [] as List
        }
        itemIdToAggregateRating[itemId] << rating
        return rating
    }

    List<CaseyAggregateRating> getCaseyAggregateRating(String itemId) {
        return itemIdToAggregateRating[itemId]
    }

    void clear() {
        cmsCampaignList.clear()
        cmsContentList.clear()
    }

}
