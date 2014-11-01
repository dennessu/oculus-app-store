package com.junbo.store.rest.validator.impl
import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer

import com.junbo.store.rest.validator.ReviewValidator
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.AddReviewRequest
import com.junbo.store.spec.model.external.sewer.casey.CaseyReview
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
/**
 * The ReviewValidatorImpl class.
 */
@CompileStatic
@Component('storeReviewValidator')
class ReviewValidatorImpl implements ReviewValidator {

    private final static int MIN_STAR = 1

    private final static int MAX_STAR = 5

    private final static Set<String> ratingTypes = [CaseyReview.RatingType.quality.name(), CaseyReview.RatingType.comfort.name()] as Set

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Override
    Promise validateAddReview(AddReviewRequest request, ApiContext apiContext) {
        if (CollectionUtils.isEmpty(request.starRatings)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('starRatings').exception()
        }
        if (request.itemId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('item').exception()
        }

        request.starRatings.each { Map.Entry<String, Integer> entry ->
            if (entry.key == null || !ratingTypes.contains(entry.key)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('starRatings', "invalid rating type: ${entry.key}").exception()
            }
            if (entry.value == null || entry.value < MIN_STAR || entry.value > MAX_STAR) {
                throw AppCommonErrors.INSTANCE.fieldInvalid("starRatings.${entry.key}", "value must in range [${MIN_STAR},${MAX_STAR}]").exception()
            }
        }

        facadeContainer.entitlementFacade.checkEntitlements(apiContext.user, request.itemId, null).then { Boolean owned ->
            if (!owned) {
                throw AppErrors.INSTANCE.itemNotPurchased().exception()
            }
            return Promise.pure()

        }
    }
}
