package com.junbo.emulator.casey.rest
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions
import com.junbo.common.model.Results
import com.junbo.emulator.casey.spec.model.CaseyEmulatorData
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.external.casey.CaseyLink
import com.junbo.store.spec.model.external.casey.cms.*
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * The CaseyRepository class.
 */
@CompileStatic
@Component('caseyEmulatorDataRepository')
class CaseyEmulatorDataRepository implements InitializingBean {

    @Value('${store.browse.featured.page.label}')
    private String defaultPageLabel

    @Value('${store.browse.cmsPage.section.path}')
    private String sectionCmsPagePath

    @Resource(name = 'caseyResourceContainer')
    ResourceContainer resourceContainer

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

    public void resetData() {
        CmsPage page = generateDefaultCmsPage()
        CmsPage sectionPage = generateSectionCmsPage()

        caseyEmulatorData = new CaseyEmulatorData(
                caseyAggregateRatings: [],
                caseyReviews: [],
                cmsPages: [page, sectionPage] as List,
                cmsSchedules: [generateDefaultCmsSchedule(page.self.getId()), generateSectionCmsSchedule(sectionPage.self.getId())] as List
        )
    }

    @Override
    void afterPropertiesSet() throws Exception {
        resetData()
    }

    private CmsPage generateDefaultCmsPage() {
        CmsPage page =  new CmsPage(
                path: 'android',
                label: defaultPageLabel,
                slots: [:] as Map,
                self: new CaseyLink(
                        id:  UUID.randomUUID().toString()
                ))
        page.slots['slot1'] = new CmsContentSlot(description: 'slot1 description')
        page.slots['slot2'] = new CmsContentSlot(description: 'slot2 description')
        return page
    }

    private CmsPage generateSectionCmsPage() {
        CmsPage page =  new CmsPage(
                path: sectionCmsPagePath,
                label: 'test_label',
                slots: [:] as Map,
                self: new CaseyLink(
                        id:  UUID.randomUUID().toString()
                ))
        page.slots['featured'] = new CmsContentSlot(description: 'featured description')
        page.slots['game'] = new CmsContentSlot(description: 'game description')
        page.slots['app'] = new CmsContentSlot(description: 'app description')
        page.slots['experience'] = new CmsContentSlot(description: 'experience description')
        return page
    }

    private CmsSchedule generateDefaultCmsSchedule(String pageId) {
        CmsSchedule cmsSchedule = new CmsSchedule(self: new CaseyLink(id: pageId))
        cmsSchedule.slots = [:] as Map
        cmsSchedule.slots['slot1'] = generateCmsScheduleStringContent('category', 'Featured All');
        cmsSchedule.slots['slot2'] = generateCmsScheduleStringContent('category', 'Featured SS');
        return cmsSchedule
    }

    private CmsSchedule generateSectionCmsSchedule(String pageId) {
        CmsSchedule cmsSchedule = new CmsSchedule(self: new CaseyLink(id: pageId))
        cmsSchedule.slots = [:] as LinkedHashMap<String, CmsScheduleContent>
        cmsSchedule.slots['featured'] = generateCmsScheduleStringContent('title', 'Featured');
        cmsSchedule.slots['game'] = generateCmsScheduleOfferAttributeContent('category', 'Game');
        cmsSchedule.slots['app'] = generateCmsScheduleOfferAttributeContent('category', 'App');
        cmsSchedule.slots['experience'] = generateCmsScheduleOfferAttributeContent('category', 'Experience');
        return cmsSchedule
    }

    private CmsScheduleContent generateCmsScheduleStringContent(String name, String value) {
        CaseyContentItemString strings = new CaseyContentItemString()
        strings.locales = [:]
        strings.locales['en_US'] = value

        ContentItem contentItem = new ContentItem()
        contentItem.strings = [strings]
        contentItem.type = ContentItem.Type.string.name()

        CmsContent cmsContent = new CmsContent()
        cmsContent.contents = Collections.singletonMap(name, contentItem)
        return new CmsScheduleContent(content: cmsContent)
    }

    private CmsScheduleContent generateCmsScheduleOfferAttributeContent(String name, String offerAttributeName) {
        String attributeId = getOfferAttribute(offerAttributeName)
        ContentItem contentItem = new ContentItem()
        contentItem.type = ContentItem.Type.offerAttribute.name()
        contentItem.links = [new CaseyLink(id: attributeId)] as List
        CmsContent cmsContent = new CmsContent()
        cmsContent.contents = Collections.singletonMap(name, contentItem)
        return new CmsScheduleContent(content: cmsContent)
    }

    private String getOfferAttribute(String name) {
        String cursor = null
        while(true) {
            Results<OfferAttribute> offerAttributeResults = resourceContainer.offerAttributeResource.getAttributes(new OfferAttributesGetOptions(cursor: cursor)).get()
            OfferAttribute offerAttribute = offerAttributeResults.items.find { OfferAttribute offerAttribute ->
                offerAttribute.locales.get('en_US')?.name == name
            }
            if (offerAttribute != null) {
                return offerAttribute.getId()
            }

            cursor = CommonUtils.getQueryParam(offerAttributeResults?.next?.href, 'cursor')
            if (StringUtils.isBlank(cursor) || offerAttributeResults.items.isEmpty()) {
                break
            }
        }
        return null
    }
}
