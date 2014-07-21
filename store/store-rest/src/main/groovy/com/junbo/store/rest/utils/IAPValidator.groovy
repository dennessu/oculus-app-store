package com.junbo.store.rest.utils

import com.junbo.catalog.spec.model.item.Item
import com.junbo.common.id.OfferId
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Validator class for IAP
 */
@CompileStatic
@Component('iapValidator')
class IAPValidator {

    Promise<Void> validateInAppOffer(OfferId inappOfferId, Item hostItem) {
        return Promise.pure(null)
    }

}
