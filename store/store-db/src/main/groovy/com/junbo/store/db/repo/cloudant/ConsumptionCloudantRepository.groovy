package com.junbo.store.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.ItemId
import com.junbo.langur.core.promise.Promise
import com.junbo.store.db.repo.ConsumptionRepository
import com.junbo.store.spec.model.iap.Consumption
import groovy.transform.CompileStatic
import org.springframework.util.Assert
/**
 * Created by fzhang on 6/20/2014.
 */
@CompileStatic
class ConsumptionCloudantRepository extends CloudantClient<Consumption> implements ConsumptionRepository {

    private static String formatId(ItemId hostItemId, String trackingGuid) {
        Assert.notNull(hostItemId?.value)
        Assert.notNull(trackingGuid)
        return "${hostItemId.value}_${trackingGuid}"
    }

    @Override
    Promise<Consumption> create(Consumption consumption) {
        consumption.setId(formatId(consumption.hostItem, consumption.trackingGuid))
        return cloudantPost(consumption)
    }

    @Override
    Promise<Consumption> get(ItemId hostItemId, String trackingGuid) {
        return cloudantGet(formatId(hostItemId, trackingGuid))
    }

    @Override
    Promise<Void> delete(ItemId hostItemId, String trackingGuid) {
        return cloudantDelete(formatId(hostItemId, trackingGuid))
    }
}
