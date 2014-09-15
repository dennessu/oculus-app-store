package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.common.enumid.LocaleId
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.rest.browse.SectionService
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import com.junbo.store.spec.model.external.casey.cms.CmsContentSlot
import com.junbo.store.spec.model.external.casey.cms.CmsPage
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * The SectionServiceImpl class.
 */
@CompileStatic
@Component('storeSectionService')
class SectionServiceImpl implements SectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SectionServiceImpl)

    private final Object lockObject = new Object()

    private LocaleId nameLookupLocale = new LocaleId('en_US')

    private List<SectionInfoNode> sectionInfoNodes

    @Resource(name = 'storeSectionInfoNodePrototype')
    private List<SectionInfoNode> sectionInfoNodesPrototype

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Override
    List<SectionInfoNode> getTopLevelSectionInfoNode() {
        initialize()
        return sectionInfoNodes
    }

    @Override
    public SectionInfoNode getSectionInfoNode(String category, String criteria) {
        initialize()
        lookup(category, criteria, sectionInfoNodes)
    }

    @Override
    public void refreshSectionInfoNode() {
        long start = System.currentTimeMillis()
        List<SectionInfoNode> newResults = []
        sectionInfoNodesPrototype.each { SectionInfoNode prototype ->
            newResults << fromPrototype(prototype)
        }
        LOGGER.info('name=Store_SectionInfoNode_Refresh, latency={}ms', System.currentTimeMillis() - start)
        sectionInfoNodes = newResults
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
        sectionInfoNode.cmsPage = prototype.cmsPage
        sectionInfoNode.cmsSlot = prototype.cmsSlot
        sectionInfoNode.name = prototype.name
        sectionInfoNode.parent = null

        switch (sectionInfoNode.sectionType) {
            case SectionInfoNode.SectionType.CmsSection:
                handleCmsSection(sectionInfoNode)
                break;
            case SectionInfoNode.SectionType.CategorySection:
                OfferAttribute offerAttribute = facadeContainer.catalogFacade.getOfferCategoryByName(sectionInfoNode.category, nameLookupLocale).get()
                sectionInfoNode.categoryId = offerAttribute?.getId()
                sectionInfoNode.children = [] as List
                break;
        }
        return sectionInfoNode
    }

    private void handleCmsSection(SectionInfoNode root) {
        assert root.cmsSlot == null
        CmsPage cmsPage = facadeContainer.caseyFacade.getCmsPage(root.cmsPage).get()
        root.children = []
        if (cmsPage == null) {
            LOGGER.error('name=Store_CmsPage_NotFound, name={}', cmsPage)
            return
        }
        if (cmsPage.slots != null) {
            // make sure in lexicographical order
            new TreeMap<>(cmsPage.slots).each { Map.Entry<String, CmsContentSlot> entry ->
                SectionInfoNode child = new SectionInfoNode()
                child.criteria = "${root.cmsPage}-${entry.key}"
                child.category = null
                child.cmsPage = root.cmsPage
                child.cmsSlot = entry.key
                child.name = entry.value?.description
                child.parent = root
                child.sectionType = root.sectionType
                child.children = [] as List
                root.children << child
            }
        }
    }

    private void initialize() {
        if (sectionInfoNodes == null) {
            try {
                refreshSectionInfoNode()
            } catch (Exception ex) {
                sectionInfoNodes = []
                LOGGER.error('name=Store_Section_Initialize_Error', ex)
            }
        }
    }

}
