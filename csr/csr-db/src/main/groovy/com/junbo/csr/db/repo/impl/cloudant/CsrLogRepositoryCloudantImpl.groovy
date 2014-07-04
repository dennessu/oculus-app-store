package com.junbo.csr.db.repo.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.CsrLogId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.csr.db.repo.CsrLogRepository
import com.junbo.csr.spec.model.CsrLog
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-7-4.
 */
@CompileStatic
class CsrLogRepositoryCloudantImpl extends CloudantClient<CsrLog> implements CsrLogRepository {
    @Override
    Promise<Results<CsrLog>> searchByLastHours(Integer lastHours, UserId userId, String action, String countryCode, Integer limit, Integer offset) {
        return null
    }

    @Override
    Promise<Results<CsrLog>> searchByDateRange(Date utcFrom, Date utcTo, UserId userId, String action, String countryCode, Integer limit, Integer offset) {
        return null
    }

    @Override
    Promise<CsrLog> get(CsrLogId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<CsrLog> create(CsrLog model) {
        return cloudantPost(model)
    }

    @Override
    Promise<CsrLog> update(CsrLog model) {
        return cloudantPut(model)
    }

    @Override
    Promise<Void> delete(CsrLogId id) {
        return cloudantDelete(id.toString())
    }
}
