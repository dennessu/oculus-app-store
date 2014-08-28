package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.enums.PriceType
import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties
import com.junbo.catalog.spec.model.item.Binary
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.rating.spec.model.priceRating.RatingRequest
import com.junbo.store.rest.browse.BrowseContext
import com.junbo.store.rest.utils.ResourceContainer
import com.junbo.store.spec.model.Platform
import com.junbo.store.spec.model.browse.Images
import com.junbo.store.spec.model.browse.document.AppDetails
import com.junbo.store.spec.model.browse.document.Image
import com.junbo.store.spec.model.browse.document.Item
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

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

    Promise buildItemFromCatalogItem(com.junbo.catalog.spec.model.item.Item catalogItem, BrowseContext browseContext, Item item) { // todo implement this
        Offer offer
        OfferRevision offerRevision
        ItemRevision itemRevision
        item.self = new ItemId(catalogItem.getId())
        item.itemType = catalogItem.type

        resourceContainer.offerResource.getOffers(new OffersGetOptions(itemId: catalogItem.itemId)).then { Results<Offer> offerResults ->
            if (offerResults.items.size() > 1) {
                LOGGER.warn('name=Store_Multiple_Offers_Found, item={}', catalogItem.itemId)
            }
            if (!CollectionUtils.isEmpty(offerResults.items)) {
                offer = offerResults.items[0]
            }
            if (offer?.currentRevisionId == null) {
                return Promise.pure()
            }
            resourceContainer.offerRevisionResource.getOfferRevision(offer.currentRevisionId, new OfferRevisionGetOptions(locale: browseContext.locale.getId().value)).then { OfferRevision e ->
                offerRevision = e
                return Promise.pure()
            }.then {
                if (catalogItem.currentRevisionId == null) {
                    return Promise.pure()
                }
                resourceContainer.itemRevisionResource.getItemRevision(catalogItem.currentRevisionId, new ItemRevisionGetOptions(locale: browseContext.locale.getId().value)).then { ItemRevision e ->
                    itemRevision = e
                    return Promise.pure()
                }
            }
        }.then {
            if (itemRevision == null || offer == null || offerRevision == null) {
                return Promise.pure()
            }
            return buildItemFromCatalogItem(catalogItem, itemRevision, offer, offerRevision, browseContext, item)
        }
    }

    Promise buildItemFromCatalogItem(com.junbo.catalog.spec.model.item.Item catalogItem,
                                     ItemRevision itemRevision, Offer offer, OfferRevision offerRevision,
                                     BrowseContext browseContext, Item item) {

        ItemRevisionLocaleProperties localeProperties = itemRevision.locales[browseContext.locale.getId().value]
        item.title = localeProperties?.name
        item.descriptionHtml = localeProperties.longDescription
        item.images = buildImages(localeProperties.images)

        resourceContainer.organizationResource.get(catalogItem.ownerId, new OrganizationGetOptions()).then { Organization organization ->
            item.creator = organization.name
            return Promise.pure()
        }.then {
            buildAppDetails(catalogItem, itemRevision, offer, offerRevision, browseContext).then { AppDetails appDetails ->
                item.appDetails = appDetails
                return Promise.pure()
            }
        }.then {
            buildOffer(offerRevision, browseContext).then { com.junbo.store.spec.model.browse.document.Offer e ->
                item.offer = e
                return Promise.pure()
            }
        }
    }

    private Promise<AppDetails> buildAppDetails(com.junbo.catalog.spec.model.item.Item catalogItem, ItemRevision itemRevision, Offer offer, OfferRevision offerRevision, BrowseContext browseContext) {
        // todo fill content rating, release date, website, forumUrl, revisionNotes
        AppDetails result = new AppDetails()
        result.categories = []
        result.genres = []
        result.packageName = itemRevision.packageName

        Binary binary = itemRevision.binaries?.get(Platform.ANDROID.value)
        result.installationSize = binary?.size
        result.versionCode = binary?.version
        result.versionString = binary?.version

        Promise.each(offer.categories) { String categoryId -> // get categories
            resourceContainer.offerAttributeResource.getAttribute(categoryId).then { OfferAttribute offerAttribute ->
                SimpleLocaleProperties properties = offerAttribute.locales?.get(browseContext.locale.getId().value)
                result.categories << properties?.name
                return Promise.pure()
            }
        }.then { // get genres
            Promise.each(catalogItem.genres) { String genresId ->
                resourceContainer.itemAttributeResource.getAttribute(genresId).then { ItemAttribute itemAttribute ->
                    SimpleLocaleProperties properties = itemAttribute.locales?.get(browseContext.locale.getId().value)
                    result.genres << properties?.name
                    return Promise.pure()
                }
            }
        }.then { // set publisher & developer
            resourceContainer.organizationResource.get(offer.ownerId, new OrganizationGetOptions()).then { Organization organization->
                result.publisherName = organization.name
                return Promise.pure()
            }.then {
                resourceContainer.organizationResource.get(catalogItem.ownerId, new OrganizationGetOptions()).then { Organization organization->
                    result.developerName = organization.name
                    return Promise.pure()
                }
            }
        }.then {
            return Promise.pure(result)
        }
    }

    private Promise<com.junbo.store.spec.model.browse.document.Offer> buildOffer(OfferRevision offerRevision, BrowseContext browseContext) {
        com.junbo.store.spec.model.browse.document.Offer result = new com.junbo.store.spec.model.browse.document.Offer()
        result.self = new OfferId(offerRevision.offerId)
        result.currency = browseContext.currency.getId()
        result.formattedDescription = offerRevision.locales?.get(browseContext.locale.getId().value)?.shortDescription
        result.isFree = offerRevision?.price?.priceType == PriceType.FREE.name()
        resourceContainer.ratingResource.priceRating(new RatingRequest(
                includeCrossOfferPromos: false,
                userId: browseContext.user.getId().value,
                country: browseContext.country.getId().value,
                currency: browseContext.currency.getId().value,
                lineItems: [
                   new RatingItem(
                           offerId: offerRevision.offerId,
                           quantity: 1
                   )
                ] as Set
        )).then { RatingRequest ratingResult ->
            result.price = ratingResult.lineItems[0].finalTotalAmount
            return Promise.pure(result)
        }
    }

    private Images buildImages(com.junbo.catalog.spec.model.common.Images catalogImages) {
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
