package com.junbo.emulator.casey.rest

import com.junbo.emulator.casey.spec.model.CaseyEmulatorData
import com.junbo.store.spec.model.external.casey.CaseyLink
import com.junbo.store.spec.model.external.casey.cms.CaseyContentItemString
import com.junbo.store.spec.model.external.casey.cms.CmsCampaign
import com.junbo.store.spec.model.external.casey.cms.CmsContent
import com.junbo.store.spec.model.external.casey.cms.CmsContentSlot
import com.junbo.store.spec.model.external.casey.cms.CmsPage
import com.junbo.store.spec.model.external.casey.cms.CmsSchedule
import com.junbo.store.spec.model.external.casey.cms.CmsScheduleContent
import com.junbo.store.spec.model.external.casey.cms.ContentItem
import com.junbo.store.spec.model.external.casey.cms.Placement
import groovy.transform.CompileStatic
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * The CaseyRepository class.
 */
@CompileStatic
@Component('caseyEmulatorDataRepository')
class CaseyEmulatorDataRepository implements InitializingBean {

    @Value('${store.browse.featured.page.path}')
    private String defaultPagePath;

    @Value('${store.browse.featured.page.label}')
    private String defaultPageLabel

    private CaseyEmulatorData caseyEmulatorData = new CaseyEmulatorData(
        caseyAggregateRatings: [],
        caseyReviews: []
    )

    CaseyEmulatorData post(CaseyEmulatorData caseyEmulatorData) {
        if (caseyEmulatorData.cmsPages != null) {
            this.caseyEmulatorData.cmsPages = caseyEmulatorData.cmsPages
        }
        if (caseyEmulatorData.cmsPageOffers != null) {
            this.caseyEmulatorData.cmsPageOffers = caseyEmulatorData.cmsPageOffers
        }
        if (caseyEmulatorData.cmsCampaigns != null) {
            this.caseyEmulatorData.cmsCampaigns = caseyEmulatorData.cmsCampaigns
        }
        if (caseyEmulatorData.cmsSchedules != null) {
            this.caseyEmulatorData.cmsSchedules = caseyEmulatorData.cmsSchedules
        }
        this.caseyEmulatorData.caseyAggregateRatings = caseyEmulatorData.caseyAggregateRatings
        this.caseyEmulatorData.caseyReviews = caseyEmulatorData.caseyReviews
        return caseyEmulatorData
    }

    CaseyEmulatorData get() {
        return caseyEmulatorData
    }

    @Override
    void afterPropertiesSet() throws Exception {
        CmsPage page = generateDefaultCmsPage()
        caseyEmulatorData = new CaseyEmulatorData(
                caseyAggregateRatings: [],
                caseyReviews: [],
                cmsPages: [page] as List,
                cmsSchedules: [generateDefaultCmsSchedule(page.self.id)]
        )
    }

    private CmsPage generateDefaultCmsPage() {
        CmsPage page =  new CmsPage(
                path: defaultPagePath,
                label: defaultPageLabel,
                slots: [:] as Map,
                self: new CaseyLink(
                        id:  UUID.randomUUID().toString()
                ))
        page.slots['slot1'] = new CmsContentSlot(description: 'slot1 description')
        page.slots['slot2'] = new CmsContentSlot(description: 'slot2 description')
        return page
    }

    private CmsCampaign generateDefaultCmsCampaigns(String pageId) {
        CmsCampaign campaign = new CmsCampaign(
                status: 'APPROVED',
                placements: [
                        generatePlacement(pageId, 'slot1', 'category', 'Featured All'),
                        generatePlacement(pageId, 'slot2', 'category', 'Featured SS')
                ]
        )
        return campaign
    }

    private CmsSchedule generateDefaultCmsSchedule(String pageId) {
        CmsSchedule cmsSchedule = new CmsSchedule(self: new CaseyLink(id: pageId))
        cmsSchedule.slots = [:] as Map
        cmsSchedule.slots['slot1'] = generateCmsScheduleContent('category', 'Featured All');
        cmsSchedule.slots['slot2'] = generateCmsScheduleContent('category', 'Featured SS');
        return cmsSchedule
    }

    private CmsScheduleContent generateCmsScheduleContent(String name, String value) {
        CaseyContentItemString strings = new CaseyContentItemString()
        strings.locales = [:]
        strings.locales['en_US'] = value

        ContentItem contentItem = new ContentItem()
        contentItem.strings = [strings]

        CmsContent cmsContent = new CmsContent()
        cmsContent.contents = Collections.singletonMap(name, contentItem)
        return new CmsScheduleContent(content: cmsContent)
    }

    private Placement generatePlacement(String pageId, String slot, String name, String value) {
        Placement result = new Placement(
                page: new CaseyLink(
                    id: pageId
                ),
                slot: slot,
                content: new CmsContent(
                        contents: [:] as Map
                )
        )

        CaseyContentItemString strings = new CaseyContentItemString()
        strings.locales = [:]
        strings.locales['en_US'] = value

        ContentItem contentItem = new ContentItem()
        contentItem.strings = [strings]
        result.content.contents[name] = contentItem

        return result
    }

}
