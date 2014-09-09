package com.junbo.store.rest.browse.impl

import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.langur.core.promise.Promise
import com.junbo.store.rest.browse.SectionHandler
import com.junbo.store.rest.utils.ResourceContainer
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.ListRequest
import com.junbo.store.spec.model.browse.ListResponse
import com.junbo.store.spec.model.browse.SectionLayoutRequest
import com.junbo.store.spec.model.browse.SectionLayoutResponse
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.browse.document.SectionInfo
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import com.junbo.store.spec.model.catalog.data.ItemData
import com.junbo.store.spec.model.external.casey.CaseyLink
import com.junbo.store.spec.model.external.casey.CaseyResults
import com.junbo.store.spec.model.external.casey.cms.*
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * The FeaturedSectionHandler class.
 */
@CompileStatic
@Component('store.featureSectionHandler')
class FeaturedSectionHandler implements SectionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeaturedSectionHandler)

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeCatalogBrowseUtils')
    private CatalogBrowseUtils catalogBrowseUtils

    @Resource(name = 'storeBrowseDataBuilder')
    private BrowseDataBuilder browseDataBuilder

    @Value('${store.browse.campaign.label}')
    private String campaignLabel

    @Value('${store.browse.campaign.rootCriteria}')
    private String rootCriteria

    @Override
    Boolean canHandle(String category, String criteria, ApiContext apiContext) {
        if (criteria == rootCriteria) {
            return true
        }

        def campaign = getCmsCampaign(apiContext)
        if (campaign == null) {
            return false
        }

        Placement placement = campaign.placements.find { Placement p ->
            p?.contentData?.label == criteria
        }
        return placement != null
    }

    @Override
    Promise<List<SectionInfoNode>> getTopLevelSectionInfoNode(ApiContext apiContext) {
        return getSectionInfoNode(null, rootCriteria, apiContext).then { SectionInfoNode node ->
            return Promise.pure(Collections.singletonList(node))
        }
    }

    @Override
    Promise<SectionInfoNode> getSectionInfoNode(String category, String criteria, ApiContext apiContext) {
        if (!canHandle(category, criteria, apiContext)) {
            return Promise.pure()
        }

        CmsCampaign campaign = getCmsCampaign(apiContext)
        SectionInfoNode result
        if (criteria == rootCriteria) {
            result = new SectionInfoNode(getRootSectionInfo())
            if (campaign != null) {
                result.children = campaign.placements.collect {Placement placement -> toSectionInfoNode(placement)}
            } else {
                result.children = []
            }
        } else {
            if (campaign == null) {
                throw AppErrors.INSTANCE.sectionNotFound().exception()
            }
            result = toSectionInfoNode(getPlacementByCriteria(campaign, criteria))
        }

        return Promise.pure(result)
    }

    @Override
    Promise<SectionLayoutResponse> getSectionLayout(SectionLayoutRequest request, ApiContext apiContext) {
        if (!canHandle(request.category, request.criteria, apiContext)) {
            throw AppErrors.INSTANCE.sectionNotFound().exception()
        }

        SectionLayoutResponse response = new SectionLayoutResponse()
        getSectionInfoNode(request.category, request.criteria, apiContext).then { SectionInfoNode sectionInfoNode ->
            response.name = sectionInfoNode.name
            response.children = sectionInfoNode.children?.collect {SectionInfoNode e -> e.toSectionInfo() }
            response.breadcrumbs = request.criteria == rootCriteria ? [] as List<SectionInfo> : [getRootSectionInfo()]
            response.ordered = false
            return Promise.pure()
        }.then {
            getSectionList(new ListRequest(criteria: request.criteria, category: request.category, count: request.count), apiContext).then { ListResponse listResponse ->
                response.items = listResponse.items
                response.next = listResponse.next
                return Promise.pure(response)
            }
        }
    }

    @Override
    Promise<ListResponse> getSectionList(ListRequest request, ApiContext apiContext) {
        if (!canHandle(request.category, request.criteria, apiContext)) {
            throw AppErrors.INSTANCE.sectionNotFound().exception()
        }
        if (request.criteria == rootCriteria) {
            return Promise.pure(new ListResponse(items: []))
        }

        def campaign = getCmsCampaign(apiContext)
        if (campaign == null) {
            throw AppErrors.INSTANCE.sectionNotFound().exception()
        }

        CmsContent contentData = getPlacementByCriteria(campaign, request.criteria).contentData
        ListResponse response = new ListResponse(items: [])

        // get the content item
        ContentItem contentItem = contentData.contents.values().find( { ContentItem e ->
            return e.type == ContentItem.Type.offer.name() || e.type == ContentItem.Type.item.name()
        })
        if (contentItem == null) {
            return Promise.pure(response)
        }

        List<ItemData> itemDataList = []
        Promise.pure().then {
            if (contentItem.type == ContentItem.Type.offer.name()) {
                return Promise.each(contentItem.links) { CaseyLink caseyLink ->
                    catalogBrowseUtils.getItemData(new OfferId(caseyLink.getId())).recover { Throwable ex ->
                        LOGGER.error('name=Error_GetItem_From_Campaign, offer={}, campaign={}', caseyLink.getId(), campaign.getSelf().getId())
                        return Promise.pure()
                    }.then { ItemData itemData ->
                        if (itemData != null) {
                            itemDataList << itemData
                        }
                        return Promise.pure()
                    }
                }
            } else if (contentItem.type == ContentItem.Type.item.name()) {
                return Promise.each(contentItem.links) { CaseyLink caseyLink ->
                    catalogBrowseUtils.getItemData(new ItemId(caseyLink.getId())).then { ItemData itemData ->
                        itemDataList << itemData
                        return Promise.pure()
                    }
                }
            }
            return Promise.pure()
        }.then {
            response.items = itemDataList.collect { ItemData itemData ->
                Item item = new Item()
                browseDataBuilder.buildItemFromItemData(itemData, apiContext, item)
                return item
            }
            return Promise.pure(response)
        }
    }

    private CmsCampaign getCmsCampaign(ApiContext apiContext) {
        Date currentTime = new Date()
        if (apiContext.contextData.containsKey(ApiContext.ContextDataKey.CMS_CAMPAIGN)) {
            return apiContext.contextData.get(ApiContext.ContextDataKey.CMS_CAMPAIGN) as CmsCampaign
        }

        CaseyResults<CmsCampaign> results
        try {
            results = resourceContainer.caseyResource.getCmsCampaigns().get()
        } catch (Exception ex) {
            LOGGER.error('name=CmsCampaign_Get_Error', ex)
            return null
        }

        CmsCampaign campaign = results.items.find { CmsCampaign e ->
                return campaignLabel == e.label &&  e.status == CmsStatus.APPROVED.name() &&
                        e.eligibleCountries.find {CaseyLink country -> country.getId() == apiContext.country.getId().getValue()} != null // todo filter with time range
            }
        if (campaign == null) {
            LOGGER.info('name=Store_FeatureCampaign_NotFound, country={}, label={}', apiContext.country.getId().value, campaignLabel)
        }

        if (campaign != null) {
            campaign.placements.each { Placement placement ->
                placement.contentData = resourceContainer.caseyResource.getCmsContent(placement.content.id).get()
            }
        }

        apiContext.contextData.put(ApiContext.ContextDataKey.CMS_CAMPAIGN, campaign)
        return campaign
    }

    private SectionInfo getRootSectionInfo() {
        return new SectionInfo(name: 'Featured', category: null as String,  criteria: rootCriteria)
    }

    private static SectionInfoNode toSectionInfoNode(Placement placement) {
        if (placement == null) {
            return null
        }
        SectionInfoNode sectionInfoNode = new SectionInfoNode()
        sectionInfoNode.name = placement.contentData.label
        sectionInfoNode.criteria = placement.contentData.label
        sectionInfoNode.children = []
        return sectionInfoNode
    }

    private static Placement getPlacementByCriteria(CmsCampaign campaign, String criteria) {
        assert campaign != null
        return campaign.placements.find {Placement placement -> placement.contentData.label == criteria}
    }

}
