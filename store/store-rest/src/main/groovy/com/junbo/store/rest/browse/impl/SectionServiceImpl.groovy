package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.common.enumid.LocaleId
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.rest.browse.SectionService
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import com.junbo.store.spec.model.external.casey.cms.CaseyContentItemString
import com.junbo.store.spec.model.external.casey.cms.CmsContentSlot
import com.junbo.store.spec.model.external.casey.cms.CmsPage
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
/**
 * The SectionServiceImpl class.
 */
@CompileStatic
@Component('storeSectionService')
class SectionServiceImpl implements SectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SectionServiceImpl)

    private LocaleId nameLookupLocale = new LocaleId('en_US')

    @Resource(name = 'storeSectionInfoNodePrototype')
    private List<SectionInfoNode> sectionInfoNodesPrototype

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Value('${store.browse.featured.name.content}')
    private List<String> cmsHeaderContentNames

    @Value('${store.browse.featured.page.label}')
    private String cmsPageLabel

    @Value('${store.browse.featured.name.slot}')
    private String cmsFeaturedNameSlot

    void setCmsHeaderContentNames(String setCmsHeaderContentNames) {
        this.cmsHeaderContentNames = [] as List
        Arrays.asList(setCmsHeaderContentNames.split(',')).each { String s ->
            if (!StringUtils.isBlank(s.trim())) {
                this.cmsHeaderContentNames << s.trim()
            }
        }
    }

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
        sectionInfoNodesPrototype.each { SectionInfoNode prototype ->
            results << fromPrototype(prototype, apiContext)
        }
        return results
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

    private SectionInfoNode fromPrototype(SectionInfoNode prototype, ApiContext apiContext) {
        SectionInfoNode sectionInfoNode = new SectionInfoNode()
        sectionInfoNode.sectionType = prototype.sectionType
        sectionInfoNode.category = prototype.category
        sectionInfoNode.criteria = prototype.criteria
        sectionInfoNode.cmsSlot = prototype.cmsSlot
        sectionInfoNode.name = prototype.name
        sectionInfoNode.parent = null

        switch (sectionInfoNode.sectionType) {
            case SectionInfoNode.SectionType.CmsSection:
                handleCmsSection(sectionInfoNode, apiContext)
                break;
            case SectionInfoNode.SectionType.CategorySection:
                OfferAttribute offerAttribute = facadeContainer.catalogFacade.getOfferCategoryByName(sectionInfoNode.category, nameLookupLocale).get()
                if (offerAttribute == null) {
                    LOGGER.error('name=Store_Category_Not_Found, category={}', sectionInfoNode.category)
                }
                sectionInfoNode.categoryId = offerAttribute?.getId()
                if (sectionInfoNode.categoryId != null) {
                    OfferAttribute offerAttributeLocalized = facadeContainer.catalogFacade.getOfferAttribute(sectionInfoNode.categoryId, apiContext).get()
                    String localizedName = offerAttributeLocalized?.locales?.get(apiContext.locale.getId().value)?.name
                    if (localizedName != null) {
                        sectionInfoNode.name = localizedName
                    }
                }
                sectionInfoNode.children = [] as List
                break;
        }
        return sectionInfoNode
    }

    private void handleCmsSection(SectionInfoNode root, ApiContext apiContext) {
        assert root.cmsSlot == null
        root.cmsPageSearch = cmsPageLabel.toLowerCase()
        root.children = []
        CmsPage cmsPage = facadeContainer.caseyFacade.getCmsPage(null, cmsPageLabel, apiContext.country.getId().value, apiContext.locale.getId().value).get();
        if (cmsPage == null) {
            LOGGER.error('name=Store_CmsPage_NotFound, label={}', cmsPageLabel)
            return
        }
        if (!CollectionUtils.isEmpty(cmsPage.slots)) {
            String name = getNameFromSlot(cmsPage, cmsFeaturedNameSlot, apiContext.locale.getId().value)
            if (!StringUtils.isBlank(name)) {
                root.name = name
            }

            new TreeMap<>(cmsPage.slots).each { Map.Entry<String, CmsContentSlot> slotEntry ->
                String slot = slotEntry.key
                SectionInfoNode child = new SectionInfoNode()
                child.criteria = "${root.cmsPageSearch}-${slot}"
                child.cmsPageSearch = root.cmsPageSearch
                child.cmsSlot = slot
                child.name = getNameFromSlot(cmsPage, slot, apiContext.locale.getId().value)
                child.parent = root
                child.sectionType = root.sectionType
                child.children = [] as List
                root.children << child
            }
        }
    }

    private String getNameFromSlot(CmsPage cmsPage, String slotName, String locale) {
        String localized = null
        for (String name : cmsHeaderContentNames) {
            List<CaseyContentItemString> strings = cmsPage.slots?.get(slotName)?.contents?.get(name)?.getStrings()
            if (!CollectionUtils.isEmpty(strings)) {
                localized = strings[0]?.locales?.get(locale)
                break
            }
        }
        return localized == null ? '' : localized
    }
}
