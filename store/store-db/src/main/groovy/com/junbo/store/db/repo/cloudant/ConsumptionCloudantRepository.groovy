package com.junbo.store.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.store.db.repo.ConsumptionRepository
import com.junbo.store.spec.model.iap.Consumption
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by fzhang on 6/20/2014.
 */
@CompileStatic
class ConsumptionCloudantRepository extends CloudantClient<Consumption> implements ConsumptionRepository {

    @Override
    Promise<Consumption> create(Consumption consumption) {
        return cloudantPost(consumption)
    }

    @Override
    Promise<Consumption> get(String trackingGuid) {
        return cloudantGet(trackingGuid)
    }
}
