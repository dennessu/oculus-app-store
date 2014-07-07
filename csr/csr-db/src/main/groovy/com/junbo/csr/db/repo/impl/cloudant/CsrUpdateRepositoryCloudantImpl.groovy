package com.junbo.csr.db.repo.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.CsrUpdateId
import com.junbo.common.model.Results
import com.junbo.csr.db.repo.CsrUpdateRepository
import com.junbo.csr.spec.model.CsrUpdate
import com.junbo.csr.spec.option.list.CsrUpdateListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-7-3.
 */
@CompileStatic
class CsrUpdateRepositoryCloudantImpl extends CloudantClient<CsrUpdate> implements CsrUpdateRepository {
    @Override
    Promise<Results<CsrUpdate>> searchByListOptions(CsrUpdateListOptions listOptions) {
        return null
    }

    @Override
    Promise<CsrUpdate> get(CsrUpdateId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<CsrUpdate> create(CsrUpdate model) {
        return cloudantPost(model)
    }

    @Override
    Promise<CsrUpdate> update(CsrUpdate model) {
        return cloudantPut(model)
    }

    @Override
    Promise<Void> delete(CsrUpdateId id) {
        return cloudantDelete(id.toString())
    }

    private Promise<Results<CsrUpdate>> getCsrUpdateByStatus(Boolean isActive, Integer limit, Integer offset) {
        return null
    }
}
