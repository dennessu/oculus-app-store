package com.junbo.store.clientproxy.utils
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.common.RevisionNotes
import com.junbo.catalog.spec.model.item.Binary
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties
import com.junbo.common.id.ItemId
import com.junbo.common.id.ItemRevisionId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Platform
import com.junbo.store.spec.model.browse.Images
import com.junbo.store.spec.model.browse.document.*
import com.junbo.store.spec.model.external.sewer.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.sewer.casey.search.CaseyItem
import com.junbo.store.spec.model.external.sewer.casey.search.CaseyOffer
import com.junbo.store.spec.model.external.sewer.casey.search.CatalogAttribute
import com.junbo.store.spec.model.external.sewer.catalog.SewerItem
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
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

    @Resource(name = 'storeReviewBuilder')
    private ReviewBuilder reviewBuilder

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
        Map<String, String> val = (Map<String, String>) ObjectMapperProvider.instanceNotStrict().readValue(text, new TypeReference<Map<String, String>>() {})
        val.each { Map.Entry<String, String> entry ->
            lengthToImageSizeGroup[Integer.parseInt(entry.key)] = entry.value
        }
    }

    public Item buildItem(CaseyOffer caseyOffer, Map<String, AggregatedRatings> aggregatedRatings, String publisher, String developer, Images.BuildType type, ApiContext apiContext) {
        Item result = new Item()
        CaseyItem caseyItem = CollectionUtils.isEmpty(caseyOffer?.items) ? null : caseyOffer.items[0]
        result.currentRevision = caseyItem?.currentRevision
        result.title = caseyOffer?.name
        result.descriptionHtml = caseyOffer?.longDescription
        result.itemType = caseyItem?.type
        result.rank = caseyOffer?.rank
        result.supportedLocales = caseyItem?.supportedLocales
        result.creator = developer
        result.self = caseyItem?.self
        result.images = buildImages(caseyOffer?.images, type)
        result.appDetails = buildAppDetails(caseyOffer, caseyItem, publisher, developer, apiContext)
        if (result.itemType == ItemType.ADDITIONAL_CONTENT.name()) {
            result.iapDetails = buildIAPDetails(caseyItem?.sku)
        } else {
            result.appDetails = buildAppDetails(caseyOffer, caseyItem, publisher, developer, apiContext)
        }
        result.offer = buildOffer(caseyOffer, apiContext)
        result.aggregatedRatings = aggregatedRatings
        fillNullValueWithDefault(result, true)
        return result
    }

    public Item buildItem(SewerItem sewerItem,  Images.BuildType type,  ApiContext apiContext) {
        if (sewerItem == null) {
            return null
        }
        ItemRevision currentRevision = null
        Organization developer = null
        List<OfferAttribute> categories = []
        List<ItemAttribute> genres = []
        List<CaseyAggregateRating> aggregateRatingCaseyResults = [] as List
        try {
            if (sewerItem?.currentRevisionNode != null) {
                currentRevision = ObjectMapperProvider.instanceNotStrict().treeToValue(sewerItem.currentRevisionNode, ItemRevision)
            }
            if (sewerItem?.developerNode != null) {
                developer = ObjectMapperProvider.instanceNotStrict().treeToValue(sewerItem.developerNode, Organization)
            }
            if (sewerItem?.categoriesNode != null) {
                sewerItem.categoriesNode.each { JsonNode node ->
                    categories <<  ObjectMapperProvider.instanceNotStrict().treeToValue(node, OfferAttribute)
                }
            }
            if (sewerItem?.genresNode != null) {
                sewerItem.genresNode.each { JsonNode node ->
                    genres <<  ObjectMapperProvider.instanceNotStrict().treeToValue(node, ItemAttribute)
                }
            }
            if (sewerItem.ratingNode != null) {
                aggregateRatingCaseyResults = (List<CaseyAggregateRating>) ObjectMapperProvider.instanceNotStrict().readValue(sewerItem.ratingNode.traverse(),
                        new TypeReference<List<CaseyAggregateRating>>() {})
            }
        } catch (JsonProcessingException ex) {
            LOGGER.error('name=Bad_Item_Node, payload:\n{}', String.valueOf(sewerItem), ex)
            throw new RuntimeException(ex)
        }

        Item item = new Item()
        item.self = new ItemId(sewerItem.getId())
        item.itemType = sewerItem.type
        item.currentRevision = currentRevision?.getId() == null ? null : new ItemRevisionId(currentRevision?.getId())

        ItemRevisionLocaleProperties itemLocaleProperties = currentRevision?.locales?.get(apiContext.locale.getId().value)

        item.title = itemLocaleProperties?.name
        item.descriptionHtml = itemLocaleProperties?.longDescription
        item.images = buildImages(itemLocaleProperties?.images, type)
        item.supportedLocales = currentRevision?.supportedLocales
        item.creator = developer?.name
        if (item.itemType == ItemType.ADDITIONAL_CONTENT.name()) {
            item.iapDetails = buildIAPDetails(currentRevision?.sku)
        } else {
            item.appDetails = buildAppDetails(currentRevision, categories, genres, developer, itemLocaleProperties, apiContext)
        }
        item.aggregatedRatings = reviewBuilder.buildAggregatedRatingsMap(aggregateRatingCaseyResults)

        fillNullValueWithDefault(item, false)
        return item
    }

    public Images buildImages(com.junbo.catalog.spec.model.common.Images catalogImages, Images.BuildType type) {
        if (catalogImages == null) {
            return null
        }
        Assert.notNull(type)
        Images result = new Images()
        switch (type) {
            case Images.BuildType.Item_Details:
                result.main = buildImageMap(ImageKeys_Details, convertImage(catalogImages))
                result.gallery = [] as List
                if (catalogImages?.gallery != null) {
                    result.gallery = catalogImages.gallery.collect { com.junbo.catalog.spec.model.common.ImageGalleryEntry entry ->
                        return buildImageMap(ImageKeys_Details_Gallery, convertImage(entry))
                    }
                }
                break
            case Images.BuildType.Item_List:
                result.main = buildImageMap(ImageKeys_ItemList, convertImage(catalogImages))
                break
        }
        return result
    }

    public RevisionNote buildRevisionNote(RevisionNotes revisionNotes, Binary binary, Date releaseDate) {
        return new RevisionNote(
                versionCode: getVersionCode(binary),
                releaseDate: releaseDate,
                versionString: binary?.version,
                title: revisionNotes?.shortNotes,
                description: revisionNotes?.longNotes
        )
    }

    private IAPDetails buildIAPDetails(String sku) {
        return new IAPDetails(
                sku: sku
        )
    }

    private AppDetails buildAppDetails(ItemRevision itemRevision, List<OfferAttribute> categories, List<ItemAttribute> genres,
                                        Organization developer, ItemRevisionLocaleProperties itemRevisionLocaleProperties, ApiContext apiContext) {
        AppDetails result = new AppDetails()
        result.packageName = itemRevision?.packageName

        Binary binary = itemRevision?.binaries?.get(Platform.ANDROID.value)
        result.installationSize = binary?.size
        result.requiredSpace = binary?.requiredSpace
        result.requiredInputDevices = itemRevision?.requiredInputDevices
        result.versionCode = getVersionCode(binary)
        result.permissions = getPermissions(binary)
        result.versionString = binary?.version
        result.revisionNotes = [buildRevisionNote(itemRevisionLocaleProperties?.releaseNotes,
                itemRevision?.binaries?.get(apiContext.platform.value), itemRevision.updatedTime)]

        // item revision attribute
        result.website = itemRevisionLocaleProperties?.website
        result.forumUrl = itemRevisionLocaleProperties?.communityForumLink
        result.developerEmail = itemRevisionLocaleProperties?.supportEmail

        result.categories = categories?.collect { OfferAttribute offerAttribute ->
            return new CategoryInfo(
                    id: offerAttribute.getId(),
                    name: offerAttribute.locales?.get(apiContext.locale.getId().value)?.name
            )
        }
        result.genres = genres?.collect { ItemAttribute itemAttribute ->
            return new GenreInfo(
                    id: itemAttribute.getId(),
                    name: itemAttribute.locales?.get(apiContext.locale.getId().value)?.name
            )
        }

        result.developerName = developer?.name
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

    private Map<String, Image> buildImageMap(List<ImageKey> imageKeys,  Map<ImageType, Map<String, com.junbo.catalog.spec.model.common.Image>> imageMap) {
        Map<String, Image> result = [:] as Map<String, Image>
        imageKeys.each { ImageKey key ->
            buildImageMap(key.sizeText, imageMap?.get(key.imageType), result)
        }
        return result
    }

    private void buildImageMap(String sizeText, Map<String, com.junbo.catalog.spec.model.common.Image> catalogImageMap, Map<String, Image> result) {
        Assert.notNull(result)
        Assert.notNull(sizeText)
        com.junbo.catalog.spec.model.common.Image image = catalogImageMap?.get(sizeText)
        if (image == null) {
            return
        }
        String imageSizeGroup = getImageSizeGroup(sizeText)
        if (imageSizeGroup == null) {
            return
        }
        result.put(imageSizeGroup, buildImage(image))
    }

    private AppDetails buildAppDetails(CaseyOffer caseyOffer, CaseyItem caseyItem, String publisher, String developer, ApiContext apiContext) {
        String country = apiContext.country.getId().value
        AppDetails appDetails = new AppDetails()
        appDetails.packageName = caseyItem?.packageName
        Binary binary = caseyItem?.binaries?.get(apiContext.platform.value)
        appDetails.requiredInputDevices = caseyItem?.requiredInputDevices
        appDetails.installationSize = binary?.size
        appDetails.requiredSpace = binary?.requiredSpace
        appDetails.versionCode = getVersionCode(binary)
        appDetails.permissions = getPermissions(binary)

        appDetails.versionString = binary?.version
        appDetails.releaseDate = caseyOffer?.regions?.get(country)?.releaseDate
        appDetails.revisionNotes = [buildRevisionNote(caseyItem?.releaseNotes, binary, caseyItem?.updatedTime)]
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
        appDetails.publisherName = publisher
        appDetails.developerName = developer
        return appDetails;
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

    private String getImageSizeGroup(String imageResolutionText) {
        ImageDimension dimension = parseImageDimension(imageResolutionText)
        if (dimension == null) {
            LOGGER.error('name=Store_Invalid_ImageResolutionText, value={}', imageResolutionText)
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

    public List<String> getPermissions(Binary binary) {
        JsonNode jsonNode =  binary?.getMetadata()?.get('permissions')
        if (jsonNode == null || jsonNode.isNull()) {
            return [] as List
        }
        try {
            return ObjectMapperProvider.instanceNotStrict().readValue(jsonNode.traverse(), new TypeReference<List<String>>() {}) as List<String>
        } catch (JsonProcessingException ex) {
            LOGGER.error('name=Invalid_Permission_Value', ex)
            return [] as List
        }
    }

    private void fillNullValueWithDefault(Item item, boolean offerAvailable) {
        item.title = CommonUtils.toDefaultIfNull(item.title)
        item.descriptionHtml = CommonUtils.toDefaultIfNull(item.descriptionHtml)
        item.creator = CommonUtils.toDefaultIfNull(item.creator)
        item.supportedLocales = CommonUtils.toDefaultIfNull(item.supportedLocales)
        if (item.images == null) {
            item.images = new Images(main: [:] as Map, gallery: [] as List)
        }
        if (item.appDetails != null) {
            fillNullValueWithDefault(item.appDetails, offerAvailable)
        }
        if (offerAvailable && item.offer != null) {
            fillNullValueWithDefault(item.offer)
        }
    }

    private void fillNullValueWithDefault(AppDetails appDetails, boolean offerAvailable) {
        appDetails.categories = CommonUtils.toDefaultIfNull(appDetails.categories)
        appDetails.genres = CommonUtils.toDefaultIfNull(appDetails.genres)
        if (offerAvailable) {
            appDetails.releaseDate = CommonUtils.toDefaultIfNull(appDetails.releaseDate)
        }
        appDetails.developerName = CommonUtils.toDefaultIfNull(appDetails.developerName)
        appDetails.publisherName = CommonUtils.toDefaultIfNull(appDetails.publisherName)
        appDetails.packageName = CommonUtils.toDefaultIfNull(appDetails.packageName)
        appDetails.versionString = CommonUtils.toDefaultIfNull(appDetails.versionString)
        appDetails.versionCode = CommonUtils.toDefaultIfNull(appDetails.versionCode)
        appDetails.installationSize = CommonUtils.toDefaultIfNull(appDetails.installationSize)
        if (appDetails.revisionNotes != null) {
            for (RevisionNote revisionNote : appDetails.revisionNotes) {
                fillNullValueWithDefault(revisionNote, offerAvailable)
            }
        }
    }

    private void fillNullValueWithDefault(RevisionNote revisionNote, boolean offerAvailable) {
        revisionNote.description = CommonUtils.toDefaultIfNull(revisionNote.description)
        if (offerAvailable) {
            revisionNote.releaseDate = CommonUtils.toDefaultIfNull(revisionNote.releaseDate)
        }
        revisionNote.title = CommonUtils.toDefaultIfNull(revisionNote.title)
        revisionNote.versionCode = CommonUtils.toDefaultIfNull(revisionNote.versionCode)
        revisionNote.versionString = CommonUtils.toDefaultIfNull(revisionNote.versionString)
    }

    private void fillNullValueWithDefault(Offer offer) {
        offer.formattedDescription = CommonUtils.toDefaultIfNull(offer.formattedDescription)
    }
}
