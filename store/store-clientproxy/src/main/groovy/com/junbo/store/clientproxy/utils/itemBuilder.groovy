package com.junbo.store.clientproxy.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.junbo.common.id.ItemId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.store.spec.model.browse.Images
import com.junbo.store.spec.model.browse.document.Image
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * The itemBuilder class.
 */
@CompileStatic
@Component('storeImageConvertor')
class itemBuilder {

    private final static Logger LOGGER = LoggerFactory.getLogger(itemBuilder)

    private final static Pattern ImageDimensionTextPattern = Pattern.compile('\\s*(\\d+)\\s*[xX]\\s*(\\d+)\\s*')

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

    public Images buildImages(com.junbo.catalog.spec.model.common.Images catalogImages, ItemId itemId) {
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
