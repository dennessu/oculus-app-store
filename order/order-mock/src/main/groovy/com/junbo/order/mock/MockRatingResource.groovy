package com.junbo.order.mock

import com.junbo.langur.core.promise.Promise
import com.junbo.rating.spec.model.priceRating.*
import com.junbo.rating.spec.model.subscription.SubsRatingRequest
import com.junbo.rating.spec.resource.RatingResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Created by chriszhu on 2/21/14.
 */
@CompileStatic
@TypeChecked
@Scope('prototype')
@Component('mockRatingResource')
class MockRatingResource extends BaseMock implements RatingResource {

    @Override
    Promise<RatingRequest> priceRating(RatingRequest request) {
        request.ratingSummary = new RatingSummary()
        BigDecimal ten = BigDecimal.valueOf(10.00D)
        BigDecimal fifty = BigDecimal.valueOf(50.00D)
        BigDecimal sixty = BigDecimal.valueOf(60.00D)
        request.ratingSummary.discountAmount = ten
        request.ratingSummary.finalAmount = 0G
        request.ratingSummary.promotion = generateLong()
        request.shippingSummary = new ShippingSummary()
        request.shippingSummary.totalShippingFee = ten
        request.lineItems?.each { RatingItem item ->
            item.finalTotalAmount = fifty
            item.totalDiscountAmount = ten
            item.originalUnitPrice = sixty
            List<Long> proms = []
            proms.add(generateLong())
            item.promotions = ((String[])proms?.toArray()) as Set
            request.ratingSummary.finalAmount += item.finalTotalAmount
        }

        return Promise.pure(request)
    }

    @Override
    Promise<SubsRatingRequest> subsRating(SubsRatingRequest request) {
        return null
    }
}
