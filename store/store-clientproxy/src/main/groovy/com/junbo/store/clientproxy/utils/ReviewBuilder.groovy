package com.junbo.store.clientproxy.utils
import com.junbo.common.id.ItemId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.AddReviewRequest
import com.junbo.store.spec.model.browse.document.AggregatedRatings
import com.junbo.store.spec.model.browse.document.Review
import com.junbo.store.spec.model.external.sewer.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.sewer.casey.CaseyLink
import com.junbo.store.spec.model.external.sewer.casey.CaseyReview
import com.junbo.store.spec.model.external.sewer.casey.search.CaseyRating
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.stereotype.Component
/**
 * The ReviewBuilder class.
 */
@CompileStatic
@Component('storeReviewBuilder')
class ReviewBuilder {

    CaseyReview buildCaseyReview(AddReviewRequest request, ApiContext apiContext) {
        CaseyReview review = new CaseyReview(
                reviewTitle: request.title,
                review: request.content,
                resourceType: 'item',
                resource: new CaseyLink(
                        id: request.itemId.value,
                        href: IdUtil.toHref(request.itemId)
                ),
                country: apiContext.country.getId().value,
                locale: apiContext.locale.getId().value
        )

        review.ratings = buildCaseyRatings(request.starRatings, null)
        return review
    }

    List<CaseyReview.Rating> buildCaseyRatings(Map<String, Integer> starRatings, List<CaseyReview.Rating> oldRatings) {
        List<CaseyReview.Rating> newRatings = [] as List<CaseyReview.Rating>
        if (!org.springframework.util.CollectionUtils.isEmpty(starRatings)) {
            for (String type : starRatings.keySet()) {
                newRatings << new CaseyReview.Rating(type: type, score: starRatings[type])
            }
        }

        // merge the old one
        if (!org.springframework.util.CollectionUtils.isEmpty(oldRatings)) {
            oldRatings = oldRatings.findAll { CaseyReview.Rating oldRating ->
                return newRatings.find { CaseyReview.Rating newRating ->
                    newRating.type == oldRating.type
                } == null
            }.asList()
            newRatings.addAll(oldRatings)
        }
        return newRatings
    }

    Review buildItemReview(CaseyReview caseyReview, String nickName) {
        if (caseyReview == null) {
            return null
        }
        Review review = new Review(
                self: new Link(id: caseyReview.self.id, href: caseyReview.self.href),
                authorName: nickName,
                title: caseyReview.reviewTitle,
                content: caseyReview.review,
                starRatings: [:] as Map<String, Integer>,
                itemId: caseyReview.resource?.getId() == null ? null : new ItemId(caseyReview.resource.getId()),
                timestamp: caseyReview.postedDate
        )
        if (!CollectionUtils.isEmpty(caseyReview.ratings)) {
            for (CaseyReview.Rating rating : caseyReview.ratings) {
                review.starRatings[rating.type] = rating.score
            }
        }
        return review
    }

    Map<String, AggregatedRatings> buildAggregatedRatingsMap(List<CaseyAggregateRating> caseyAggregateRatingList) {
        Map<String, AggregatedRatings> aggregatedRatingsMap = [:]
        [CaseyReview.RatingType.quality.name(), CaseyReview.RatingType.comfort.name()].each { String type ->
            aggregatedRatingsMap[type] = buildAggregatedRatings(caseyAggregateRatingList?.find { CaseyAggregateRating e ->
                e.type == type
            })
        }
        return aggregatedRatingsMap
    }

    AggregatedRatings buildAggregatedRatings(CaseyAggregateRating caseyAggregateRating) {
        if (caseyAggregateRating == null) {
            return buildDefaultAggregatedRatings()
        }
        AggregatedRatings aggregatedRatings = new AggregatedRatings(
                averageRating: caseyAggregateRating.average,
                ratingsCount: caseyAggregateRating.count
        )

        return aggregatedRatings
    }

    AggregatedRatings buildAggregatedRatings(CaseyRating caseyRating) {
        if (caseyRating == null) {
            return buildDefaultAggregatedRatings()
        }
        AggregatedRatings aggregatedRatings = new AggregatedRatings(
                ratingsCount: CommonUtils.safeLong(caseyRating.count),
        )
        aggregatedRatings.averageRating = CommonUtils.safeDouble(caseyRating.averagePercent)
        return aggregatedRatings
    }

    AggregatedRatings buildDefaultAggregatedRatings() {
        AggregatedRatings result =  new AggregatedRatings(
                ratingsCount: 0L,
                averageRating: 0.0 as double,
        )
        return result
    }

    Map<String, AggregatedRatings> buildDefaultAggregatedRatingsMap() {
        Map<String, AggregatedRatings> result = [:]
        result[CaseyReview.RatingType.comfort.name()] = buildDefaultAggregatedRatings()
        result[CaseyReview.RatingType.quality.name()] = buildDefaultAggregatedRatings()
        return result
    }
}
