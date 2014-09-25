package com.junbo.store.clientproxy.utils
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.AddReviewRequest
import com.junbo.store.spec.model.browse.document.AggregatedRatings
import com.junbo.store.spec.model.browse.document.Review
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.casey.CaseyLink
import com.junbo.store.spec.model.external.casey.CaseyReview
import com.junbo.store.spec.model.external.casey.search.CaseyRating
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
/**
 * The ReviewBuilder class.
 */
@CompileStatic
@Component('storeReviewBuilder')
class ReviewBuilder {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReviewBuilder.class)

    private final static int RATING_SCALE = 20

    private final static int HISTOGRAM_SCALE = 2

    private final static int HISTOGRAM_NUM = 5

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

        review.ratings = []
        for (String type : request.starRatings.keySet()) {
            review.ratings << new CaseyReview.Rating(type: type, score: request.starRatings[type] * RATING_SCALE)
        }
        return review
    }

    Review buildReview(CaseyReview caseyReview, String nickName) {
        if (caseyReview == null) {
            return null
        }
        Review review = new Review(
                self: new Link(id: caseyReview.self.id, href: caseyReview.self.href),
                authorName: nickName,
                title: caseyReview.reviewTitle,
                content: caseyReview.review,
                starRatings: [:] as Map<String, Integer>,
                timestamp: caseyReview.postedDate
        )
        if (!CollectionUtils.isEmpty(caseyReview.ratings)) {
            for (CaseyReview.Rating rating : caseyReview.ratings) {
                review.starRatings[rating.type] = (rating.score / RATING_SCALE) as int
            }
        }
        return review
    }

    AggregatedRatings buildAggregatedRatings(CaseyAggregateRating caseyAggregateRating) {
        if (caseyAggregateRating == null) {
            return null
        }
        AggregatedRatings aggregatedRatings = new AggregatedRatings(
                averageRating: (caseyAggregateRating.average / RATING_SCALE) as double,
                ratingsCount: caseyAggregateRating.count,
        )

        Map<Integer, Long> histogram = new HashMap<>()
        for (int i = 0;i < HISTOGRAM_NUM; ++i) {
            histogram[i] = 0L
        }

        if (caseyAggregateRating.histogram != null) {
            for (int i = 0; i < caseyAggregateRating.histogram.length; i++) {
                int scaled = (i / HISTOGRAM_SCALE) as int
                histogram[scaled] = histogram.get(scaled).longValue() + CommonUtils.safeLong(caseyAggregateRating.histogram[i])
            }
        }
        aggregatedRatings.ratingsHistogram = histogram
        return aggregatedRatings
    }

    AggregatedRatings buildAggregatedRatings(CaseyRating caseyRating) {
        if (caseyRating == null) {
            return null
        }
        AggregatedRatings aggregatedRatings = new AggregatedRatings(
                ratingsCount: caseyRating.count,
        )

        Map<Integer, Long> histogram = new HashMap<>()
        histogram[0] = CommonUtils.safeLong(caseyRating.numOnes)
        histogram[1] = CommonUtils.safeLong(caseyRating.numTwos)
        histogram[2] = CommonUtils.safeLong(caseyRating.numThrees)
        histogram[3] = CommonUtils.safeLong(caseyRating.numFours)
        histogram[4] = CommonUtils.safeLong(caseyRating.numFives)
        aggregatedRatings.ratingsHistogram = histogram

        if (caseyRating.stars != null) {
            aggregatedRatings.averageRating = caseyRating.stars
        } else {
            aggregatedRatings.averageRating = 0
        }

        return aggregatedRatings
    }


}
