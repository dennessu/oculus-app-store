package com.junbo.store.rest.browse.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties
import com.junbo.catalog.spec.model.item.Binary
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties
import com.junbo.common.id.ItemId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.store.rest.utils.ResourceContainer
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Platform
import com.junbo.store.spec.model.browse.Images
import com.junbo.store.spec.model.browse.document.*
import com.junbo.store.spec.model.catalog.data.ItemData
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.util.regex.Matcher
import java.util.regex.Pattern
/**
 * The BrowseDataBuilder class.
 */
@CompileStatic
@Component('storeBrowseDataBuilder')
class BrowseDataBuilder {

    private final static Logger LOGGER = LoggerFactory.getLogger(BrowseDataBuilder)

    private final static Pattern ImageDimensionTextPattern = Pattern.compile('\\s*(\\d+)\\s*[xX]\\s*(\\d+)\\s*')

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeLocaleUtils')
    private LocaleUtils localeUtils

    private TreeMap<Integer, String> lengthToImageSizeGroup // (length -> sizeGroup)

    public static class ImageDimension {
        int width
        int height
    }

    @Value('${store.image.sizeGroups}')
    public void setLengthToImageSizeGroup(String text) {
        lengthToImageSizeGroup = new TreeMap<>()
        Map<String, String> val = (Map<String, String>) ObjectMapperProvider.instance().readValue(text, new TypeReference<Map<String, String>>() {})
        val.each { Map.Entry<String, String> entry ->
            lengthToImageSizeGroup[Integer.parseInt(entry.key)] = entry.value
        }
    }

    public Item buildItemFromItemData(ItemData itemData, ApiContext apiContext, Item item) {
        item.self = new ItemId(itemData.item.getId())
        item.itemType = itemData.item.type

        ItemRevisionLocaleProperties itemLocaleProperties = localeUtils.getLocaleProperties(itemData.currentRevision?.locales, apiContext.locale, 'item', item.self.value, 'locales') as ItemRevisionLocaleProperties
        item.title = itemLocaleProperties?.name
        item.descriptionHtml = itemLocaleProperties?.longDescription
        item.images = buildImages(itemLocaleProperties?.images, item.getSelf())
        item.supportedLocales = itemData.currentRevision?.supportedLocales

        item.creator = itemData.developer?.name
        item.appDetails = buildAppDetails(itemData, itemLocaleProperties, apiContext)
        item.aggregatedRatings = itemData.caseyData.aggregatedRatings
        item.reviews = itemData.caseyData.reviewsResponse
        return item
    }

    private AppDetails buildAppDetails(ItemData itemData, ItemRevisionLocaleProperties itemRevisionLocaleProperties, ApiContext apiContext) {
        ItemRevision itemRevision = itemData.currentRevision
        AppDetails result = new AppDetails()
        result.packageName = itemRevision?.packageName

        Binary binary = itemRevision?.binaries?.get(Platform.ANDROID.value)
        result.ageRatings = itemRevision?.ageRatings
        result.installationSize = binary?.size
        result.versionCode = null // todo set the version code
        result.versionString = binary?.version
        result.releaseDate = itemData.offer?.offerRevision?.getCountries()?.get(apiContext.country.getId().value)?.releaseDate
        result.revisionNotes = itemData?.revisions?.collect {ItemRevision r -> buildRevisionNote(r, itemRevisionLocaleProperties, apiContext)}


        // item revision attribute
        result.website = itemRevisionLocaleProperties?.website
        result.forumUrl = itemRevisionLocaleProperties?.communityForumLink
        result.developerEmail = itemRevisionLocaleProperties?.supportEmail

        result.categories = itemData?.offer?.categories?.collect { OfferAttribute offerAttribute ->
            return new CategoryInfo(
                    id: offerAttribute.getId(),
                    name: (localeUtils.getLocaleProperties(offerAttribute.locales, apiContext.locale, 'offerAttribute', offerAttribute.getId(), 'locales') as SimpleLocaleProperties)?.name
            )
        }
        result.genres = itemData?.genres?.collect { ItemAttribute itemAttribute ->
            return new GenreInfo(
                    id: itemAttribute.getId(),
                    name: (localeUtils.getLocaleProperties(itemAttribute.locales, apiContext.locale, 'itemAttribute', itemAttribute.getId(), 'locales') as SimpleLocaleProperties)?.name
            )
        }

        result.publisherName = itemData?.offer?.publisher?.name
        result.developerName = itemData?.developer?.name
        return result
    }

    private RevisionNote buildRevisionNote(ItemRevision itemRevision, ItemRevisionLocaleProperties itemRevisionLocaleProperties, ApiContext apiContext) {
        if (itemRevision?.binaries == null) {
            return null
        }
        Binary binary = itemRevision.binaries[apiContext.platform.value]
        return new RevisionNote(
            versionCode: null as Integer, // todo fill version code
            releaseDate: itemRevision.updatedTime, // todo use real release date
            versionString: binary?.version,
            title: itemRevisionLocaleProperties?.releaseNotes?.shortNotes,
            description: itemRevisionLocaleProperties?.releaseNotes?.longNotes
        )
    }

    private Images buildImages(com.junbo.catalog.spec.model.common.Images catalogImages, ItemId itemId) {
        if (catalogImages == null) {
            return null
        }
        return new Images(
                main: buildImageMap(catalogImages.main, itemId),
                thumbnail: buildImageMap(catalogImages.thumbnail, itemId),
                background: buildImageMap(catalogImages.background, itemId),
                featured: buildImageMap(catalogImages.featured, itemId),
                gallery: catalogImages.gallery == null ? null : catalogImages.gallery.collect { com.junbo.catalog.spec.model.common.ImageGalleryEntry entry ->
                    return new com.junbo.store.spec.model.browse.ImageGalleryEntry(
                            thumbnail: buildImageMap(entry.thumbnail, itemId),
                            full: buildImageMap(entry.full, itemId)
                    )
                }
        )
    }

    private Map<String, Image> buildImageMap(Map<String, com.junbo.catalog.spec.model.common.Image> catalogImageMap, ItemId itemId) {
        if (catalogImageMap == null) {
            return null
        }
        Map<String, Image> result = new HashMap<>()
        catalogImageMap.each { Map.Entry<String, com.junbo.catalog.spec.model.common.Image> entry ->
            String imageSizeGroup = getImageSizeGroup(entry.key, itemId)
            if (imageSizeGroup == null) {
                return
            }
            result.put(imageSizeGroup, buildImage(entry.getValue()));
        }
        return result
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

    private String getImageSizeGroup(String imageResolutionText, ItemId itemId) {
        ImageDimension dimension = parseImageDimension(imageResolutionText)
        if (dimension == null) {
            LOGGER.error('name=Store_Invalid_ImageResolutionText, value={}, item={}', imageResolutionText, itemId?.value)
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
}
