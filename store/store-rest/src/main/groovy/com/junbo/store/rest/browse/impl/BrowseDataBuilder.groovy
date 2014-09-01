package com.junbo.store.rest.browse.impl

import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.item.Binary
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.rating.spec.model.priceRating.RatingRequest
import com.junbo.store.rest.utils.ResourceContainer
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Platform
import com.junbo.store.spec.model.browse.Images
import com.junbo.store.spec.model.browse.ReviewsResponse
import com.junbo.store.spec.model.browse.document.AggregatedRatings
import com.junbo.store.spec.model.browse.document.AppDetails
import com.junbo.store.spec.model.browse.document.Image
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.browse.document.RevisionNote
import com.junbo.store.spec.model.catalog.data.ItemData
import com.junbo.store.spec.model.browse.document.Review
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.casey.CaseyResults
import com.junbo.store.spec.model.external.casey.CaseyReview
import com.junbo.store.spec.model.external.casey.ReviewSearchParams
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The BrowseDataBuilder class.
 */
@CompileStatic
@Component('storeBrowseDataBuilder')
class BrowseDataBuilder {

    private final static Logger LOGGER = LoggerFactory.getLogger(BrowseDataBuilder)

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    public Item buildItemFromItemData(ItemData itemData, ApiContext apiContext, Item item) {
        item.self = new ItemId(itemData.item.getId())
        item.itemType = itemData.item.type

        ItemRevisionLocaleProperties itemLocaleProperties = itemData.currentRevision?.locales?.get(apiContext.locale.getId().value)
        item.title = itemLocaleProperties?.name
        item.descriptionHtml = itemLocaleProperties?.longDescription
        item.images = buildImages(itemLocaleProperties?.images)
        item.supportedLocales = itemData.currentRevision?.supportedLocales

        item.creator = itemData.developer?.name
        item.appDetails = buildAppDetails(itemData, apiContext)
        item.aggregatedRatings = itemData.caseyData.aggregatedRatings
        item.reviews = itemData.caseyData.reviewsResponse
        return item
    }

    private AppDetails buildAppDetails(ItemData itemData, ApiContext apiContext) {
        // todo fill content rating
        ItemRevision itemRevision = itemData.currentRevision
        AppDetails result = new AppDetails()
        result.packageName = itemRevision?.packageName

        Binary binary = itemRevision?.binaries?.get(Platform.ANDROID.value)
        result.installationSize = binary?.size
        result.versionCode = null // todo set the version code
        result.versionString = binary?.version
        result.releaseDate = itemRevision?.updatedTime // todo use real release date
        result.revisionNotes = itemData?.revisions?.collect {ItemRevision r -> buildRevisionNote(r, apiContext)}

        // item revision attribute
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = itemRevision?.locales?.get(apiContext.locale.getId().value)
        result.website = itemRevisionLocaleProperties?.website
        result.forumUrl = itemRevisionLocaleProperties?.communityForumLink

        result.categories = itemData?.offer?.categories?.collect {OfferAttribute offerAttribute -> return offerAttribute.locales?.get(apiContext.locale.getId().value)?.name}
        result.genres = itemData?.genres?.collect {ItemAttribute itemAttribute -> return itemAttribute.locales?.get(apiContext.locale.getId().value)?.name}
        result.publisherName = itemData?.offer?.publisher?.name
        result.developerName = itemData?.developer?.name
        return result
    }

    private RevisionNote buildRevisionNote(ItemRevision itemRevision, ApiContext apiContext) {
        ItemRevisionLocaleProperties localeProperties = itemRevision.locales[apiContext.locale.getId().value]
        Binary binary = itemRevision.binaries[apiContext.platform.value]
        return new RevisionNote(
            versionCode: null as Integer, // todo fill version code
            releaseDate: itemRevision.updatedTime, // todo use real release date
            versionString: binary?.version,
            title: localeProperties?.releaseNotes?.shortNotes,
            description: localeProperties?.releaseNotes?.longNotes
        )
    }

    private Images buildImages(com.junbo.catalog.spec.model.common.Images catalogImages) {
        if (catalogImages == null) {
            return null
        }
        return new Images(
                main: buildImage(catalogImages.main),
                halfMain: buildImage(catalogImages.halfMain),
                thumbnail: buildImage(catalogImages.thumbnail),
                halfThumbnail: buildImage(catalogImages.halfThumbnail),
                background: buildImage(catalogImages.background),
                featured: buildImage(catalogImages.featured),
                gallery: catalogImages.gallery == null ? null : catalogImages.gallery.collect { com.junbo.catalog.spec.model.common.ImageGalleryEntry entry ->
                    return new com.junbo.store.spec.model.browse.ImageGalleryEntry(
                            thumbnail: buildImage(entry.thumbnail),
                            full: buildImage(entry.full)
                    )
                }
        )
    }

    private Image buildImage(com.junbo.catalog.spec.model.common.Image catalogImage) {
        if (catalogImage == null) {
            return null
        }
        return new Image(
                imageUrl: catalogImage.href,
                altText: catalogImage.altText
        )
    }

}
