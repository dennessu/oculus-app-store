package com.junbo.order.mock
import com.junbo.langur.core.promise.Promise
import com.junbo.rating.spec.model.request.OfferRatingRequest
import com.junbo.rating.spec.model.request.OrderBenefit
import com.junbo.rating.spec.model.request.OrderRatingItem
import com.junbo.rating.spec.model.request.OrderRatingRequest
import com.junbo.rating.spec.model.request.ShippingBenefit
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
    Promise<OrderRatingRequest> orderRating(OrderRatingRequest request) {
        request.orderBenefit = new OrderBenefit()
        BigDecimal ten = BigDecimal.valueOf(10.00D)
        BigDecimal fifty = BigDecimal.valueOf(50.00D)
        BigDecimal sixty = BigDecimal.valueOf(60.00D)
        request.orderBenefit.discountAmount = ten
        request.orderBenefit.finalAmount = fifty
        request.shippingBenefit = new ShippingBenefit()
        request.shippingBenefit.shippingFee = ten
        request.lineItems?.each { OrderRatingItem item ->
            item.finalAmount = fifty
            item.discountAmount = ten
            item.originalAmount = sixty
        }

        return Promise.pure(request)
    }

    @Override
    Promise<OfferRatingRequest> offersRating(OfferRatingRequest request) {
        return null
    }
}
