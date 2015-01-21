package com.junbo.emulator.casey.rest
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.common.model.Results
import com.junbo.emulator.casey.spec.model.CaseyEmulatorData
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.external.sewer.casey.CaseyLink
import com.junbo.store.spec.model.external.sewer.casey.cms.*
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    @Value('${store.browse.cmsPage.section.path}')
    private String sectionCmsPagePath

    @Value('${store.browse.cmsPage.initialItems.path}')
    private String initialItemsCmsPagePath

    @Value('${store.browse.cmsPage.initialItems.slot}')
    private String initialItemsCmsPageSlot

    @Value('${store.browse.cmsPage.initialItems.contentName}')
    private String initialItemsCmsPageContentName

    @Value('${emulator.casey.initialItem.names}')
    private String initialItemNames

    @Resource(name = 'caseyResourceContainer')
    ResourceContainer resourceContainer

    private boolean initialized = false

    private CaseyEmulatorData caseyEmulatorData = new CaseyEmulatorData(
        caseyAggregateRatings: [],
        caseyReviews: []
    )

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass())

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
        if (!initialized) {
            resetData()
        }
        return caseyEmulatorData
    }

    public void resetData() {
        CmsPage page = generateDefaultCmsPage()
        CmsPage sectionPage = generateSectionCmsPage()
        CmsPage initialDownloadItemPage = generateInitialDownloadItemCmsPage()

        caseyEmulatorData = new CaseyEmulatorData(
                caseyAggregateRatings: [],
                caseyReviews: [],
                cmsPages: [page, sectionPage, initialDownloadItemPage] as List,
                cmsSchedules: [generateDefaultCmsSchedule(page.self.getId()),
                               generateSectionCmsSchedule(sectionPage.self.getId(), page.self.getId()),
                               generateInitialDownloadItemSchedule(initialDownloadItemPage.self.getId())] as List
        )
    }

    @Override
    void afterPropertiesSet() throws Exception {
        //resetData()
    }

    private CmsPage generateDefaultCmsPage() {
        CmsPage page =  new CmsPage(
                path: 'android',
                label: 'featured',
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

    private CmsPage generateInitialDownloadItemCmsPage() {
        CmsPage page =  new CmsPage(
                path: initialItemsCmsPagePath,
                label: UUID.randomUUID().toString(),
                slots: [:] as Map,
                self: new CaseyLink(
                        id:  UUID.randomUUID().toString()
                ))
        page.slots[initialItemsCmsPageSlot] = new CmsContentSlot(description: 'initial item slot')
        return page
    }

    private CmsSchedule generateDefaultCmsSchedule(String pageId) {
        CmsSchedule cmsSchedule = new CmsSchedule(self: new CaseyLink(id: pageId))
        cmsSchedule.slots = [:] as Map
        cmsSchedule.slots['slot1'] = generateCmsScheduleStringContent('Featured All');
        cmsSchedule.slots['slot2'] = generateCmsScheduleStringContent('Featured SS');
        return cmsSchedule
    }

    private CmsSchedule generateInitialDownloadItemSchedule(String pageId) {
        CmsSchedule cmsSchedule = new CmsSchedule(self: new CaseyLink(id: pageId))
        cmsSchedule.slots = [:] as LinkedHashMap<String, CmsScheduleContent>
        cmsSchedule.slots[initialItemsCmsPageSlot] = generateCmsScheduleItemContent();
        return cmsSchedule
    }

    private CmsSchedule generateSectionCmsSchedule(String pageId, String featuredPageId) {
        CmsSchedule cmsSchedule = new CmsSchedule(self: new CaseyLink(id: pageId))
        cmsSchedule.slots = [:] as LinkedHashMap<String, CmsScheduleContent>
        cmsSchedule.slots['featured'] = generateCmsMenuContent('Featured', featuredPageId);
        cmsSchedule.slots['game'] = generateCmsScheduleOfferAttributeContent('category', 'Game');
        cmsSchedule.slots['app'] = generateCmsScheduleOfferAttributeContent('category', 'App');
        cmsSchedule.slots['experience'] = generateCmsScheduleOfferAttributeContent('category', 'Experience');
        return cmsSchedule
    }

    private CmsScheduleContent generateCmsScheduleStringContent(String value) {
        CaseyContentItemString strings = new CaseyContentItemString()
        strings.locales = [:]
        strings.locales['en_US'] = value

        ContentItem contentItem = new ContentItem()
        contentItem.strings = [strings]
        contentItem.type = ContentItem.Type.string.name()

        CmsContent cmsContent = new CmsContent()
        cmsContent.contents = [:] as Map
        cmsContent.contents['title'] = contentItem

        return new CmsScheduleContent(content: cmsContent)
    }

    private CmsScheduleContent generateCmsMenuContent(String value, String featuredPageId) {
        CaseyContentItemString strings = new CaseyContentItemString()
        strings.locales = [:]
        strings.locales['en_US'] = value

        ContentItem contentItem = new ContentItem()
        contentItem.strings = [strings]
        contentItem.type = ContentItem.Type.string.name()

        CmsContent cmsContent = new CmsContent()
        cmsContent.contents = [:] as Map
        cmsContent.contents['title'] = contentItem

        CaseyLink link = new CaseyLink(id : featuredPageId)
        ContentItem linkContentItem = new ContentItem()
        linkContentItem.links = [link]
        linkContentItem.type = ContentItem.Type.cmsPage.name()
        cmsContent.contents['page'] = linkContentItem

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

    private CmsScheduleContent generateCmsScheduleItemContent() {
        ContentItem contentItem = new ContentItem()
        contentItem.type = ContentItem.Type.item.name()
        contentItem.links = [] as List
        for (String itemName: initialItemNames.split(',')) {
            String itemId = getItemIdViaName(itemName)
            if (itemId != null) {
                contentItem.links << new CaseyLink(id: itemId)
            }
        }
        CmsContent cmsContent = new CmsContent()
        cmsContent.contents = Collections.singletonMap(initialItemsCmsPageContentName, contentItem)
        return new CmsScheduleContent(content: cmsContent)
    }

    private String getItemIdViaName(String name) {
        List<Item> items = resourceContainer.itemResource.getItems(new ItemsGetOptions(query: "name:${name}")).get().items
        return items.isEmpty() ? null : items[0].getId()
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
