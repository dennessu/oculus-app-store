package com.junbo.order.mock

import com.junbo.billing.spec.resource.BillingCurrencyResource
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component

import javax.ws.rs.PathParam

/**
 * Created by fzhang on 14-3-27.
 */
@CompileStatic
@TypeChecked
@Component('mockCurrencyResource')
class MockCurrencyResource implements BillingCurrencyResource {
    @Override
    Promise<Results<com.junbo.billing.spec.model.Currency>> getCurrencies() {
        return Promise.pure(null)
    }

    @Override
    Promise<com.junbo.billing.spec.model.Currency> getCurrency(@PathParam('name') String name) {
        return Promise.pure(null)
    }
}
