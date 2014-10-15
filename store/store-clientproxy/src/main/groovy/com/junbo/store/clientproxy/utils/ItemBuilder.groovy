package com.junbo.store.clientproxy.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.junbo.catalog.spec.enums.PriceType
import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.common.RevisionNotes
import com.junbo.catalog.spec.model.item.Binary
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties
import com.junbo.common.id.ItemId
import com.junbo.common.id.ItemRevisionId
import com.junbo.common.id.OfferId
import com.junbo.common.id.OfferRevisionId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Platform
import com.junbo.store.spec.model.browse.Images
import com.junbo.store.spec.model.browse.document.*
import com.junbo.store.spec.model.catalog.data.ItemData
import com.junbo.store.spec.model.external.casey.search.CaseyItem
import com.junbo.store.spec.model.external.casey.search.CaseyOffer
import com.junbo.store.spec.model.external.casey.search.CatalogAttribute
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * The ItemBuilder class.
 */
@CompileStatic
@Component('storeItemBuilder')
class ItemBuilder {

    private final static Logger LOGGER = LoggerFactory.getLogger(ItemBuilder)

    private final static Pattern ImageDimensionTextPattern = Pattern.compile('\\s*(\\d+)\\s*[xX]\\s*(\\d+)\\s*')

    private final static List<ImageKey> ImageKeys_Details = Arrays.asList(
                    new ImageKey(ImageType.main, '1440x810'),
                    new ImageKey(ImageType.main, '230x129')
    )

    private final static List<ImageKey> ImageKeys_Details_Gallery = Arrays.asList(
            new ImageKey(ImageType.full, '2560x1440'),
            new ImageKey(ImageType.thumbnail, '1360x765'),
            new ImageKey(ImageType.thumbnail, '336x189'),
    )

    private final static List<ImageKey> ImageKeys_ItemList = Arrays.asList(
            new ImageKey(ImageType.thumbnail, '1440x810'),
            new ImageKey(ImageType.thumbnail, '690x388'),
            new ImageKey(ImageType.thumbnail, '216x122'),
    )

    private TreeMap<Integer, String> lengthToImageSizeGroup // (length -> sizeGroup)

    public static class ImageDimension {
        int width
        int height
    }

    public static enum ImageType {
        main,
        full,
        thumbnail
    }

    public static class ImageKey {
        ImageType imageType
        String sizeText
        public ImageKey(ImageType imageType, String sizeText) {
            this.imageType = imageType
            this.sizeText = sizeText
        }
    }

    @Value('${store.image.sizeGroups}')
    public void setLengthToImageSizeGroup(String text) {
        lengthToImageSizeGroup = new TreeMap<>()
        Map<String, String> val = (Map<String, String>) ObjectMapperProvider.instance().readValue(text, new TypeReference<Map<String, String>>() {})
        val.each { Map.Entry<String, String> entry ->
            lengthToImageSizeGroup[Integer.parseInt(entry.key)] = entry.value
        }
    }

    public Item buildItem(CaseyOffer caseyOffer, Map<String, AggregatedRatings> aggregatedRatings, Organization publisher, Organization developer, Images.BuildType type, ApiContext apiContext) {
        Item result = new Item()
        CaseyItem caseyItem = CollectionUtils.isEmpty(caseyOffer?.items) ? null : caseyOffer.items[0]
        result.currentRevision = caseyItem?.currentRevision
        result.title = caseyOffer?.name
        result.descriptionHtml = caseyOffer?.longDescription
        result.itemType = caseyItem?.type
        result.supportedLocales = caseyItem?.supportedLocales
        result.creator = developer?.name
        result.self = caseyItem?.self
        result.images = buildImages(caseyOffer?.images, caseyOffer?.self, type)
        result.appDetails = buildAppDetails(caseyOffer, caseyItem, publisher, developer, apiContext)
        result.offer = buildOffer(caseyOffer, apiContext)
        result.aggregatedRatings = aggregatedRatings
        return result
    }

    public Item buildItem(ItemData itemData, Images.BuildType type,  ApiContext apiContext) {
        if (itemData == null) {
            return null
        }
        Item item = new Item()
        item.self = new ItemId(itemData.item.getId())
        item.itemType = itemData.item.type
        item.currentRevision = itemData.item.currentRevisionId == null ? null : new ItemRevisionId(itemData.item.currentRevisionId)

        ItemRevisionLocaleProperties itemLocaleProperties = itemData.currentRevision?.locales?.get(apiContext.locale.getId().value)
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = itemData.offer?.offerRevision?.locales?.get(apiContext.locale.getId().value)
        item.title = offerRevisionLocaleProperties?.name
        item.descriptionHtml = offerRevisionLocaleProperties?.longDescription
        item.images = buildImages(offerRevisionLocaleProperties?.images, new OfferId(itemData.offer?.offer?.getId()), type)
        item.supportedLocales = itemData.currentRevision?.supportedLocales

        item.creator = itemData.developer?.name
        item.appDetails = buildAppDetails(itemData, itemLocaleProperties, apiContext)
        item.aggregatedRatings = itemData.caseyData.aggregatedRatings

        if (itemData.offer != null) {
            item.offer = new Offer()
            item.offer.self = new OfferId(itemData.offer.offer.getId())
            item.offer.currentRevision = itemData.offer.offer.currentRevisionId == null ? null : new OfferRevisionId(itemData.offer.offer.currentRevisionId)
            item.offer.currency = apiContext.currency.getId()
            OfferRevisionLocaleProperties localeProperties = itemData.offer?.offerRevision?.locales?.get(apiContext.locale.getId().value)
            item.offer.formattedDescription = localeProperties?.shortDescription
            item.offer.isFree = itemData.offer.offerRevision?.price?.priceType == PriceType.FREE.name()
        }
        return item
    }

    public Images buildImages(com.junbo.catalog.spec.model.common.Images catalogImages, OfferId offerId, Images.BuildType type) {
        if (catalogImages == null) {
            return null
        }
        Assert.notNull(type)
        Images result = new Images()
        switch (type) {
            case Images.BuildType.Item_Details:
                result.main = buildImageMap(offerId, ImageKeys_Details, convertImage(catalogImages))
                result.gallery = [] as List
                if (catalogImages?.gallery != null) {
                    result.gallery = catalogImages.gallery.collect { com.junbo.catalog.spec.model.common.ImageGalleryEntry entry ->
                        return buildImageMap(offerId, ImageKeys_Details_Gallery, convertImage(entry))
                    }
                }
                break
            case Images.BuildType.Item_List:
                result.main = buildImageMap(offerId, ImageKeys_ItemList, convertImage(catalogImages))
                break
        }
        return result
    }

    private AppDetails buildAppDetails(ItemData itemData, ItemRevisionLocaleProperties itemRevisionLocaleProperties, ApiContext apiContext) {
        ItemRevision itemRevision = itemData.currentRevision
        AppDetails result = new AppDetails()
        result.packageName = itemRevision?.packageName

        Binary binary = itemRevision?.binaries?.get(Platform.ANDROID.value)
        result.contentRating = null
        result.installationSize = binary?.size
        result.versionCode = getVersionCode(binary)
        result.versionString = binary?.version
        result.releaseDate = itemData.offer?.offerRevision?.getCountries()?.get(apiContext.country.getId().value)?.releaseDate
        result.revisionNotes = [buildRevisionNote(itemRevisionLocaleProperties?.releaseNotes, binary, itemData.offer?.offerRevision?.countries?.get(apiContext.country.getId().value)?.releaseDate)]


        // item revision attribute
        result.website = itemRevisionLocaleProperties?.website
        result.forumUrl = itemRevisionLocaleProperties?.communityForumLink
        result.developerEmail = itemRevisionLocaleProperties?.supportEmail

        result.categories = itemData?.offer?.categories?.collect { OfferAttribute offerAttribute ->
            return new CategoryInfo(
                    id: offerAttribute.getId(),
                    name: offerAttribute.locales?.get(apiContext.locale.getId().value)?.name
            )
        }
        result.genres = itemData?.genres?.collect { ItemAttribute itemAttribute ->
            return new GenreInfo(
                    id: itemAttribute.getId(),
                    name: itemAttribute.locales?.get(apiContext.locale.getId().value)?.name
            )
        }

        result.publisherName = itemData?.offer?.publisher?.name
        result.developerName = itemData?.developer?.name
        return result
    }

    private Map<ImageType, Map<String, com.junbo.catalog.spec.model.common.Image>> convertImage(com.junbo.catalog.spec.model.common.Images catalogImage) {
        Map<ImageType, Map<String, com.junbo.catalog.spec.model.common.Image>> result = [:] as Map
        if (catalogImage?.main != null) {
            result[ImageType.main] = catalogImage.main
        }
        if (catalogImage?.thumbnail != null) {
            result[ImageType.thumbnail] = catalogImage.thumbnail
        }
        return result
    }

    private Map<ImageType, Map<String, com.junbo.catalog.spec.model.common.Image>> convertImage(com.junbo.catalog.spec.model.common.ImageGalleryEntry galleryEntry) {
        Map<ImageType, Map<String, com.junbo.catalog.spec.model.common.Image>> result = [:] as Map
        if (galleryEntry?.full != null) {
            result[ImageType.full] = galleryEntry.full
        }
        if (galleryEntry?.thumbnail != null) {
            result[ImageType.thumbnail] = galleryEntry.thumbnail
        }
        return result
    }

    private Map<String, Image> buildImageMap(OfferId offerId, List<ImageKey> imageKeys,  Map<ImageType, Map<String, com.junbo.catalog.spec.model.common.Image>> imageMap) {
        Map<String, Image> result = [:] as Map<String, Image>
        imageKeys.each { ImageKey key ->
            buildImageMap(offerId, key.sizeText, imageMap?.get(key.imageType), result)
        }
        return result
    }

    private void buildImageMap(OfferId offerId, String sizeText, Map<String, com.junbo.catalog.spec.model.common.Image> catalogImageMap, Map<String, Image> result) {
        Assert.notNull(result)
        Assert.notNull(sizeText)
        com.junbo.catalog.spec.model.common.Image image = catalogImageMap?.get(sizeText)
        if (image == null) {
            return
        }
        String imageSizeGroup = getImageSizeGroup(sizeText, offerId)
        if (imageSizeGroup == null) {
            return
        }
        result.put(imageSizeGroup, buildImage(image))
    }

    private AppDetails buildAppDetails(CaseyOffer caseyOffer, CaseyItem caseyItem, Organization publisher, Organization developer, ApiContext apiContext) {
        String country = apiContext.country.getId().value
        AppDetails appDetails = new AppDetails()
        appDetails.packageName = caseyItem?.packageName
        Binary binary = caseyItem?.binaries?.get(apiContext.platform.value)
        appDetails.installationSize = binary?.size
        appDetails.versionCode = getVersionCode(binary)

        appDetails.versionString = binary?.version
        appDetails.releaseDate = caseyOffer?.regions?.get(country)?.releaseDate
        appDetails.revisionNotes = [buildRevisionNote(caseyItem?.releaseNotes, binary, appDetails.releaseDate)]
        appDetails.website = caseyItem?.website
        appDetails.forumUrl = caseyItem?.communityForumLink
        appDetails.developerEmail = caseyItem?.supportEmail
        appDetails.categories = caseyOffer?.categories?.collect { CatalogAttribute attribute ->
            return new CategoryInfo(
                    id: attribute.getAttributeId(),
                    name: attribute.name
            )
        }
        appDetails.genres = caseyItem?.genres?.collect { CatalogAttribute attribute ->
            return new GenreInfo(
                    id: attribute.getAttributeId(),
                    name: attribute.name
            )
        }
        appDetails.publisherName = publisher?.name
        appDetails.developerName = developer?.name
        return appDetails;
    }

    private RevisionNote buildRevisionNote(RevisionNotes revisionNotes, Binary binary, Date releaseDate) {
        return new RevisionNote(
                versionCode: getVersionCode(binary),
                releaseDate: releaseDate,
                versionString: binary?.version,
                title: revisionNotes?.shortNotes,
                description: revisionNotes?.longNotes
        )
    }

    private Offer buildOffer(CaseyOffer caseyOffer, ApiContext apiContext) {
        if (caseyOffer == null) {
            return null
        }
        Offer offer = new Offer()
        offer.currentRevision = caseyOffer.currentRevision
        offer.currency = apiContext.currency.getId()
        offer.price = caseyOffer.price?.amount
        offer.self = caseyOffer.self
        offer.isFree = caseyOffer.price?.isFree
        offer.formattedDescription = caseyOffer.shortDescription
        return offer
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

    private String getImageSizeGroup(String imageResolutionText, OfferId offerId) {
        ImageDimension dimension = parseImageDimension(imageResolutionText)
        if (dimension == null) {
            LOGGER.error('name=Store_Invalid_ImageResolutionText, value={}, offer={}', imageResolutionText, offerId?.value)
            return null
        }

        int length = Math.max(dimension.width, dimension.height)
        Map.Entry<Integer, String> entry = lengthToImageSizeGroup.ceilingEntry(length)
        if (entry == null) {
            entry = lengthToImageSizeGroup.lastEntry()
        }
        return entry.value
    }

    private ImageDimension parseImageDimension(String imageDimensionText) {
        if (StringUtils.isEmpty(imageDimensionText)) {
            return null
        }

        Matcher matcher = ImageDimensionTextPattern.matcher(imageDimensionText)
        if (!matcher.matches() || matcher.groupCount() != 2) {
            return null
        }

        ImageDimension dimension = new ImageDimension()
        try {
            dimension.width = Integer.parseInt(matcher.group(1))
            dimension.height = Integer.parseInt(matcher.group(2))
        } catch (NumberFormatException ex) {
            return null
        }
        return dimension
    }

    public Integer getVersionCode(Binary binary) {
        return binary?.getMetadata()?.get('versionCode')?.asInt()
    }
}
