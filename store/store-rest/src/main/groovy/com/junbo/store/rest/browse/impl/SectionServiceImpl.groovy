package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.common.enumid.LocaleId
import com.junbo.store.rest.browse.SectionService
import com.junbo.store.spec.model.browse.document.SectionInfoNode
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

    @Resource(name = 'storeCatalogBrowseUtils')
    private CatalogBrowseUtils catalogBrowseUtils

    private LocaleId nameLookupLocale = new LocaleId('en_US')

    private List<SectionInfoNode> sectionInfoNodesPrototype

    private List<SectionInfoNode> sectionInfoNodes

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

    public void refreshSectionInfoNode() {
        long start = System.currentTimeMillis()
        List<SectionInfoNode> newResults = []
        sectionInfoNodesPrototype.each { SectionInfoNode prototype ->
            newResults << fromPrototype(prototype, null)
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

    private SectionInfoNode fromPrototype(SectionInfoNode prototype, SectionInfoNode parent) {
        SectionInfoNode sectionInfoNode = new SectionInfoNode()
        sectionInfoNode.sectionType = prototype.sectionType
        sectionInfoNode.category = prototype.category
        sectionInfoNode.criteria = prototype.criteria
        sectionInfoNode.cmsPage = prototype.cmsPage
        sectionInfoNode.cmsSlot = prototype.cmsSlot
        sectionInfoNode.name = prototype.name
        sectionInfoNode.parent = parent

        OfferAttribute offerAttribute = catalogBrowseUtils.getOfferCategoryByName(sectionInfoNode.category, nameLookupLocale).get()
        sectionInfoNode.categoryLocales = offerAttribute?.locales
        sectionInfoNode.categoryId = offerAttribute?.getId()

        sectionInfoNode.children = []
        if (prototype.children != null) {
            for (SectionInfoNode child : prototype.children) {
                sectionInfoNode.children << fromPrototype(child, sectionInfoNode)
            }
        }
    }

    private void initialize() {
        if (sectionInfoNodes == null) {
            synchronized (lockObject) {
                if (sectionInfoNodes == null) {
                    refreshSectionInfoNode()
                }
            }
        }
    }

}
