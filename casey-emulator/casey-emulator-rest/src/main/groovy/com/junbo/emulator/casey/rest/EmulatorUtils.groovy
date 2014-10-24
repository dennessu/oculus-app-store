package com.junbo.emulator.casey.rest

import com.junbo.catalog.spec.enums.PriceType
import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.ItemAttributeGetOptions
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttributeGetOptions
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties
import com.junbo.catalog.spec.model.offer.ItemEntry
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.ItemId
import com.junbo.common.id.ItemRevisionId
import com.junbo.common.id.OfferId
import com.junbo.common.id.OfferRevisionId
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.store.spec.model.external.sewer.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.sewer.casey.CaseyResults
import com.junbo.store.spec.model.external.sewer.casey.CaseyReview
import com.junbo.store.spec.model.external.sewer.casey.search.CaseyItem
import com.junbo.store.spec.model.external.sewer.casey.search.CaseyOffer
import com.junbo.store.spec.model.external.sewer.casey.search.CaseyPrice
import com.junbo.store.spec.model.external.sewer.casey.search.CaseyRating
import com.junbo.store.spec.model.external.sewer.casey.search.CatalogAttribute
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource

/**
 * The EmulatorUtils class.
 */
@CompileStatic
@Component('caseyEmulatorUtils')
public class EmulatorUtils {

    @Resource(name = 'caseyResourceContainer')
    ResourceContainer resourceContainer

    @Value('${emulator.casey.latency}')
    private int defaultSimulateLatency

    void emulateLatency() {
        String latency = JunboHttpContext.getData().getRequestHeaders().getFirst(EmulatorHeaders.X_QA_CASEY_EMULATE_LATENCY.name())
        long val = defaultSimulateLatency
        if (!StringUtils.isEmpty(latency)) {
            try {
                val = Long.parseLong(latency)
            } catch (NumberFormatException ex) {
            }
        }
        if (val > 0) {
            Thread.sleep(val)
        }
    }

    void emulateError(String methodName) {
        String error = JunboHttpContext.getData().getRequestHeaders().getFirst(EmulatorHeaders.X_QA_CASEY_ERROR.name())
        if (!StringUtils.isBlank(error)) {
            if (error.split(',').contains(methodName)) {
                throw new RuntimeException("${methodName} test error");
            }

        }
    }

    CaseyOffer buildCaseyOffer(Offer offer, LocaleId localeId, CountryId countryId, Closure<CaseyResults<CaseyAggregateRating> > caseyAggregateRatingGetFunc) {
        CaseyOffer caseyOffer = new CaseyOffer()
        caseyOffer.self = new OfferId(offer.getId())
        caseyOffer.categories = offer.categories?.collect { String categoryId ->
            OfferAttribute offerAttribute = resourceContainer.offerAttributeResource.getAttribute(categoryId, new OfferAttributeGetOptions(locale: localeId.value)).get()
            CatalogAttribute catalogAttribute = new CatalogAttribute()
            catalogAttribute.attributeId = categoryId
            catalogAttribute.name = offerAttribute.locales?.get(localeId.value)?.getName()
            return catalogAttribute
        }

        if (offer.currentRevisionId != null) {
            OfferRevision offerRevision = resourceContainer.offerRevisionResource.getOfferRevision(offer.currentRevisionId, new OfferRevisionGetOptions(locale: localeId.value)).get();
            caseyOffer.currentRevision = new OfferRevisionId(offer.currentRevisionId)
            caseyOffer.images = offerRevision.locales?.get(localeId.value)?.images
            caseyOffer.name = offerRevision.locales?.get(localeId.value)?.name
            caseyOffer.longDescription = offerRevision.locales?.get(localeId.value)?.longDescription
            caseyOffer.shortDescription = offerRevision.locales?.get(localeId.value)?.shortDescription
            caseyOffer.regions = offerRevision.countries
            caseyOffer.price = new CaseyPrice()
            caseyOffer.price.isFree = offerRevision.price?.priceType == PriceType.FREE.name()
            // Set<String> currencies = offerRevision.price?.prices?.get(countryId.value)?.keySet()
            // todo set the price
            caseyOffer.items = offerRevision?.items?.collect { ItemEntry itemEntry ->
                return getCaseyItem(new ItemId(itemEntry.itemId), localeId, caseyAggregateRatingGetFunc)
            }
        }

        caseyOffer.publisher = offer.ownerId
        return caseyOffer
    }

    private Map<String, CaseyRating> buildRating(CaseyResults<CaseyAggregateRating> aggregateRatingCaseyResults) {
        Map<String, CaseyRating> caseyRatingMap = new HashMap<>()
        if (CollectionUtils.isEmpty(aggregateRatingCaseyResults?.items)) {
            return caseyRatingMap
        }
        aggregateRatingCaseyResults.items.each { CaseyAggregateRating aggregateRating ->
            CaseyRating caseyRating = new CaseyRating()
            caseyRating.count = aggregateRating.count
            caseyRating.stars = aggregateRating.average / 20.0 as double
            caseyRating.type = aggregateRating.type
            caseyRating.numOnes = aggregateRating.histogram[0] + aggregateRating.histogram[1]
            caseyRating.numTwos = aggregateRating.histogram[2] + aggregateRating.histogram[3]
            caseyRating.numThrees = aggregateRating.histogram[4] + aggregateRating.histogram[5]
            caseyRating.numFours = aggregateRating.histogram[6] + aggregateRating.histogram[7]
            caseyRating.numFives = aggregateRating.histogram[8] + aggregateRating.histogram[9]
            caseyRatingMap[aggregateRating.type] = caseyRating
        }
        return caseyRatingMap
    }

    private CaseyItem getCaseyItem(ItemId itemId, LocaleId localeId, Closure<CaseyResults<CaseyAggregateRating>> caseyAggregateRatingGetFunc) {
        CaseyItem caseyItem = new CaseyItem()
        Item item = resourceContainer.itemResource.getItem(itemId.value).get()
        caseyItem.self = itemId
        caseyItem.type = item.type
        caseyItem.developer = item.ownerId
        caseyItem.genres = item.genres?.collect { String genreId ->
            ItemAttribute itemAttribute = resourceContainer.itemAttributeResource.getAttribute(genreId, new ItemAttributeGetOptions(locale: localeId.value)).get()
            CatalogAttribute catalogAttribute = new CatalogAttribute()
            catalogAttribute.attributeId = genreId
            catalogAttribute.name = itemAttribute.locales?.get(localeId.value)?.getName()
            return catalogAttribute
        }

        if (item.currentRevisionId != null) {
            caseyItem.currentRevision = new ItemRevisionId(item.currentRevisionId)
            ItemRevision itemRevision = resourceContainer.itemRevisionResource.getItemRevision(item.currentRevisionId, new ItemRevisionGetOptions(locale: localeId.value)).get()
            caseyItem.packageName = itemRevision.packageName
            caseyItem.supportedLocales = itemRevision.supportedLocales
            caseyItem.binaries = itemRevision.binaries
            ItemRevisionLocaleProperties localeProperties = itemRevision.locales?.get(localeId.value)
            caseyItem.releaseNotes = localeProperties?.releaseNotes
            caseyItem.name = localeProperties?.name
            caseyItem.longDescription = localeProperties?.longDescription
            caseyItem.supportEmail = localeProperties?.supportEmail
            caseyItem.website = localeProperties?.website
            caseyItem.communityForumLink = localeProperties?.communityForumLink
            caseyItem.images = localeProperties?.images
        }


        Map<String, CaseyRating> ratingMap = buildRating(caseyAggregateRatingGetFunc.call(itemId))
        caseyItem.qualityRating = ratingMap[CaseyReview.RatingType.quality.name()]
        caseyItem.comfortRating = ratingMap[CaseyReview.RatingType.comfort.name()]
        return caseyItem
    }
}
