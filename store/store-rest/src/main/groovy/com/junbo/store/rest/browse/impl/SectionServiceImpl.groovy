package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.common.enumid.LocaleId
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.rest.browse.SectionService
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import com.junbo.store.spec.model.external.casey.cms.CaseyContentItemString
import com.junbo.store.spec.model.external.casey.cms.CmsContentSlot
import com.junbo.store.spec.model.external.casey.cms.CmsPage
import groovy.transform.CompileStatic
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
    private String cmsNameContent

    @Value('${store.browse.featured.page.path}')
    private String cmsPagePath;

    @Value('${store.browse.featured.page.label}')
    private String cmsPageLabel;

    @Value('${store.browse.featured.name.slot}')
    private String cmsFeaturedNameSlot

    @Override
    List<SectionInfoNode> getTopLevelSectionInfoNode() {
        return buildSectionInfoNode()
    }

    @Override
    public SectionInfoNode getSectionInfoNode(String category, String criteria) {
        List<SectionInfoNode> nodes = buildSectionInfoNode()
        lookup(category, criteria, nodes)
    }

    private List<SectionInfoNode> buildSectionInfoNode() {
        List<SectionInfoNode> results = []
        sectionInfoNodesPrototype.each { SectionInfoNode prototype ->
            results << fromPrototype(prototype)
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

    private SectionInfoNode fromPrototype(SectionInfoNode prototype) {
        SectionInfoNode sectionInfoNode = new SectionInfoNode()
        sectionInfoNode.sectionType = prototype.sectionType
        sectionInfoNode.category = prototype.category
        sectionInfoNode.criteria = prototype.criteria
        sectionInfoNode.cmsPageSearch = prototype.cmsPageSearch
        sectionInfoNode.cmsSlot = prototype.cmsSlot
        sectionInfoNode.name = prototype.name
        sectionInfoNode.parent = null

        switch (sectionInfoNode.sectionType) {
            case SectionInfoNode.SectionType.CmsSection:
                handleCmsSection(sectionInfoNode)
                break;
            case SectionInfoNode.SectionType.CategorySection:
                OfferAttribute offerAttribute = facadeContainer.catalogFacade.getOfferCategoryByName(sectionInfoNode.category, nameLookupLocale).get()
                if (offerAttribute == null) {
                    LOGGER.error('name=Store_Category_Not_Found, category={}', sectionInfoNode.category)
                }
                sectionInfoNode.categoryId = offerAttribute?.getId()
                sectionInfoNode.children = [] as List
                break;
        }
        return sectionInfoNode
    }

    private void handleCmsSection(SectionInfoNode root) {
        assert root.cmsSlot == null
        CmsPage cmsPage = facadeContainer.caseyFacade.getCmsPage(cmsPagePath, cmsPageLabel).get();
        if (cmsPage == null) {
            cmsPage = facadeContainer.caseyFacade.getCmsPage(cmsPagePath, null).get(); // try to get without label
        }
        root.children = []
        if (cmsPage == null) {
            LOGGER.error('name=Store_CmsPage_NotFound, path={}, label={}', cmsPagePath, cmsPageLabel)
            return
        }
        if (!CollectionUtils.isEmpty(cmsPage.slots)) {
            List<CaseyContentItemString> strings = cmsPage.slots[cmsFeaturedNameSlot]?.contents?.get(cmsNameContent)?.getStrings()
            root.cmsNames = CollectionUtils.isEmpty(strings) ? null : strings[0]?.locales

            new TreeMap<>(cmsPage.slots).each { Map.Entry<String, CmsContentSlot> slotEntry ->
                String slot = slotEntry.key
                SectionInfoNode child = new SectionInfoNode()
                child.criteria = "${root.cmsPageSearch}-${slot}"
                child.cmsPageSearch = root.cmsPageSearch
                child.cmsSlot = slot
                strings = slotEntry?.value?.getContents()?.get(cmsNameContent)?.getStrings()
                child.cmsNames = CollectionUtils.isEmpty(strings) ? null : strings[0]?.locales
                child.parent = root
                child.sectionType = root.sectionType
                child.children = [] as List
                root.children << child
            }
        }
    }

}
