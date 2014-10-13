package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.rest.browse.SectionService
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import com.junbo.store.spec.model.external.casey.cms.CmsContentSlot
import com.junbo.store.spec.model.external.casey.cms.CmsPage
import com.junbo.store.spec.model.external.casey.cms.ContentItem
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

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Value('${store.browse.featured.page.label}')
    private String cmsPageLabel

    @Value('${store.browse.cmsPage.section.path}')
    private String sectionCmsPagePath

    @Override
    List<SectionInfoNode> getTopLevelSectionInfoNode(ApiContext apiContext) {
        return buildSectionInfoNode(apiContext)
    }

    @Override
    public SectionInfoNode getSectionInfoNode(String category, String criteria, ApiContext apiContext) {
        List<SectionInfoNode> nodes = buildSectionInfoNode(apiContext)
        lookup(category, criteria, nodes)
    }

    private List<SectionInfoNode> buildSectionInfoNode(ApiContext apiContext) {
        List<SectionInfoNode> results = []

        CmsPage menuCmsPage = facadeContainer.caseyFacade.getCmsPage(sectionCmsPagePath, null, apiContext.country.getId().value, apiContext.locale.getId().value).get();
        if (menuCmsPage == null) {
            LOGGER.error('name=Store_SectionCmsPage_NotFound, path={}', sectionCmsPagePath)
            return results
        }

        menuCmsPage.slots.entrySet().each { Map.Entry<String, CmsContentSlot> entry ->
            SectionInfoNode sectionInfoNode = buildSectionInfoNode(entry.key, entry.value, apiContext)
            if (sectionInfoNode != null) {
                results << sectionInfoNode
            }
        }
        return results
    }

    private SectionInfoNode buildSectionInfoNode(String slot, CmsContentSlot cmsContentSlot, ApiContext apiContext) {
        ContentItem offerAttributeContentItem = cmsContentSlot?.contents?.values()?.find { ContentItem contentItem ->
            ContentItem.Type.offerAttribute.name().equalsIgnoreCase(contentItem.type)
        }
        if (offerAttributeContentItem != null) {
            return buildCategorySectionInfoNode(sectionCmsPagePath, slot, offerAttributeContentItem, apiContext)
        }

        ContentItem stringContentItem = cmsContentSlot?.contents?.values()?.find { ContentItem contentItem ->
            ContentItem.Type.string.name().equalsIgnoreCase(contentItem.type)
        }
        if (stringContentItem != null) {
            return buildCmsSectionInfoNode(stringContentItem, apiContext)
        }

        LOGGER.warn('name=Store_Unsupported_Section_SlotContent, path={}, slot={}', sectionCmsPagePath, slot)
        return null
    }

    private SectionInfoNode buildCategorySectionInfoNode(String path, String slot, ContentItem offerAttributeContentItem, ApiContext apiContext) {
        Assert.notNull(offerAttributeContentItem)
        String attributeId = null
        if (!CollectionUtils.isEmpty(offerAttributeContentItem.links)) {
            attributeId = offerAttributeContentItem.links[0].getId()
        }
        if (StringUtils.isBlank(attributeId)) {
            LOGGER.warn('name=Store_OfferAttribute_ContentItem_Invalid, path={}, slot={}', path, slot)
            return null
        }

        OfferAttribute offerAttribute = facadeContainer.catalogFacade.getOfferAttribute(attributeId, apiContext).get()
        if (offerAttribute == null) {
            return null
        }

        return new SectionInfoNode(
            name: offerAttribute?.locales?.get(apiContext.locale.getId().value)?.name,
            category: offerAttribute.getId(),
            children: [],
            ordered: false,
            sectionType: SectionInfoNode.SectionType.CategorySection
        )
    }

    private SectionInfoNode buildCmsSectionInfoNode(ContentItem stringContentItem, ApiContext apiContext) {
        Assert.notNull(stringContentItem)

        CmsPage cmsPage = facadeContainer.caseyFacade.getCmsPage(null, cmsPageLabel, apiContext.country.getId().value, apiContext.locale.getId().value).get();
        if (cmsPage == null) {
            LOGGER.warn('name=Store_CmsPage_NotFound, label={}', cmsPageLabel)
            return null
        }

        SectionInfoNode root = new SectionInfoNode(
            name: CollectionUtils.isEmpty(stringContentItem.strings) ? null : stringContentItem.strings[0].locales?.get(apiContext.locale.getId().value),
            criteria: "${Cms_Criteria}.${cmsPage.self.getId()}",
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
        return root
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
        ContentItem stringItem = cmsPage.slots?.get(slotName)?.contents?.values()?.find { ContentItem contentItem ->
            return ContentItem.Type.string.name().equalsIgnoreCase(contentItem?.type)
        }
        return CollectionUtils.isEmpty(stringItem?.strings) ? null : stringItem.strings[0].locales?.get(locale)
    }
}
