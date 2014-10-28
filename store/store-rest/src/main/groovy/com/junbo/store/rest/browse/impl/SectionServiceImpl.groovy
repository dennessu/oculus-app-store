package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.common.cache.Cache
import com.junbo.store.rest.browse.SectionService
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import com.junbo.store.spec.model.external.sewer.casey.cms.CmsContent
import com.junbo.store.spec.model.external.sewer.casey.cms.CmsContentSlot
import com.junbo.store.spec.model.external.sewer.casey.cms.CmsPage
import com.junbo.store.spec.model.external.sewer.casey.cms.CmsScheduleContent
import com.junbo.store.spec.model.external.sewer.casey.cms.ContentItem
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
/**
 * The SectionServiceImpl class.
 */
@CompileStatic
@Component('storeSectionService')
class SectionServiceImpl implements SectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SectionServiceImpl)

    private static final String Cms_Criteria = 'cms'

    private static final String SectionInfo_Cache_Key = 'section_info'

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Value('${store.browse.cmsPage.section.path}')
    private String sectionCmsPagePath

    @Resource(name = 'storeSectionInfoCache')
    private Cache<String, List<SectionInfoNode>> sectionCache

    @Override
    Promise<List<SectionInfoNode>> getTopLevelSectionInfoNode(ApiContext apiContext) {
        return buildSectionInfoNode(apiContext)
    }

    @Override
    public Promise<SectionInfoNode> getSectionInfoNode(String category, String criteria, ApiContext apiContext) {
        buildSectionInfoNode(apiContext).then { List<SectionInfoNode> nodes ->
            return Promise.pure(lookup(category, criteria, nodes))
        }
    }

    private Promise<List<SectionInfoNode>> buildSectionInfoNode(ApiContext apiContext) {
        List<SectionInfoNode> cached = sectionCache.get(SectionInfo_Cache_Key)
        if (cached != null) {
            return Promise.pure(cached)
        }

        List<SectionInfoNode> results = []
        facadeContainer.caseyFacade.getCmsPage(sectionCmsPagePath, null, apiContext.country.getId().value, apiContext.locale.getId().value).then { CmsPage menuCmsPage ->
            if (menuCmsPage == null) {
                LOGGER.error('name=Store_SectionCmsPage_NotFound, path={}', sectionCmsPagePath)
                return Promise.pure(results)
            }
            if (menuCmsPage.slots == null) {
                return Promise.pure(results)
            }

            Promise.each(menuCmsPage?.schedule?.slots?.entrySet()) { Map.Entry<String, CmsScheduleContent> entry ->
                buildSectionInfoNode(entry.key, entry.value?.content, apiContext).then { SectionInfoNode sectionInfoNode ->
                    if (sectionInfoNode != null) {
                        results << sectionInfoNode
                    }
                    return Promise.pure()
                }
            }.then {
                sectionCache.put(SectionInfo_Cache_Key, results)
                return Promise.pure(results)
            }
        }
    }

    private Promise<SectionInfoNode> buildSectionInfoNode(String slot, CmsContent cmsContent, ApiContext apiContext) {
        // category section
        ContentItem offerAttributeContentItem = cmsContent?.contents?.values()?.find { ContentItem contentItem ->
            ContentItem.Type.offerAttribute.name().equalsIgnoreCase(contentItem.type)
        }
        if (offerAttributeContentItem != null) {
            return buildCategorySectionInfoNode(sectionCmsPagePath, slot, offerAttributeContentItem, apiContext)
        }

        // cms section
        ContentItem cmsPageLink = cmsContent?.contents?.values()?.find { ContentItem contentItem ->
            ContentItem.Type.cmsPage.name().equalsIgnoreCase(contentItem.type)
        }
        ContentItem stringContentItem = cmsContent?.contents?.values()?.find { ContentItem contentItem ->
            ContentItem.Type.string.name().equalsIgnoreCase(contentItem.type)
        }
        if (!CollectionUtils.isEmpty(cmsPageLink?.links)) {
            String cmsPageId = cmsPageLink.links[0].getId()
            if (!StringUtils.isBlank(cmsPageId)) {
                return buildCmsSectionInfoNode(cmsPageId, stringContentItem, apiContext)
            }
        }

        LOGGER.warn('name=Store_Unsupported_Section_SlotContent, path={}, slot={}', sectionCmsPagePath, slot)
        return Promise.pure()
    }

    private Promise<SectionInfoNode> buildCategorySectionInfoNode(String path, String slot, ContentItem offerAttributeContentItem, ApiContext apiContext) {
        Assert.notNull(offerAttributeContentItem)
        String attributeId = null
        if (!CollectionUtils.isEmpty(offerAttributeContentItem.links)) {
            attributeId = offerAttributeContentItem.links[0].getId()
        }
        if (StringUtils.isBlank(attributeId)) {
            LOGGER.warn('name=Store_OfferAttribute_ContentItem_Invalid, path={}, slot={}', path, slot)
            return Promise.pure()
        }

        facadeContainer.catalogFacade.getOfferAttribute(attributeId, apiContext).then { OfferAttribute offerAttribute ->
            if (offerAttribute == null) {
                return Promise.pure()
            }
            return Promise.pure(new SectionInfoNode(
                    name: offerAttribute?.locales?.get(apiContext.locale.getId().value)?.name,
                    category: offerAttribute.getId(),
                    children: [],
                    ordered: false,
                    sectionType: SectionInfoNode.SectionType.CategorySection
            ))
        }
    }

    private Promise<SectionInfoNode> buildCmsSectionInfoNode(String cmsPageId, ContentItem stringContentItem, ApiContext apiContext) {
        Assert.notNull(cmsPageId)
        String cmsCriteria = Cms_Criteria
        facadeContainer.caseyFacade.getCmsPage(cmsPageId, apiContext.country.getId().value, apiContext.locale.getId().value).then { CmsPage cmsPage ->
            if (cmsPage == null) {
                LOGGER.warn('name=Store_CmsPage_NotFound, pageId={}', cmsPageId)
                return Promise.pure()
            }

            SectionInfoNode root = new SectionInfoNode(
                    name: CollectionUtils.isEmpty(stringContentItem?.strings) ? null : stringContentItem.strings[0].locales?.get(apiContext.locale.getId().value),
                    criteria: "${cmsCriteria}.${cmsPage.self.getId()}",
                    children: [],
                    ordered: false,
                    sectionType: SectionInfoNode.SectionType.CmsSection,
                    cmsPageSearch: cmsPage.label?.toLowerCase()
            )

            cmsPage.slots.each { Map.Entry<String, CmsContentSlot> slotEntry ->
                SectionInfoNode child = new SectionInfoNode()
                child.criteria = "${root.criteria}.${slotEntry.key}"
                child.cmsPageSearch = root.cmsPageSearch
                child.cmsSlot = slotEntry.key
                child.name = getNameFromSlot(cmsPage, slotEntry.key, apiContext.locale.getId().value)
                child.parent = root
                child.sectionType = root.sectionType
                child.children = [] as List
                root.children << child
            }
            return Promise.pure(root)
        }
    }

    private SectionInfoNode lookup(String category, String criteria, List<SectionInfoNode> sectionInfoNodes) {
        for (SectionInfoNode sectionInfoNode : sectionInfoNodes) {
            if (sectionInfoNode.criteria == criteria && sectionInfoNode.category == category) {
                return sectionInfoNode
            }
            SectionInfoNode fromChild = lookup(category, criteria, sectionInfoNode.children)
            if (fromChild != null) {
                return fromChild
            }
        }
        return null
    }

    private String getNameFromSlot(CmsPage cmsPage, String slotName, String locale) {
        ContentItem stringItem = cmsPage?.schedule?.slots?.get(slotName)?.content?.contents?.values()?.find { ContentItem contentItem ->
            return ContentItem.Type.string.name().equalsIgnoreCase(contentItem?.type)
        }
        return CollectionUtils.isEmpty(stringItem?.strings) ? null : stringItem.strings[0].locales?.get(locale)
    }
}
