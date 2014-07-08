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
        def resultList = new Results<CsrUpdate>(items: [])
        return cloudantGetAll(null, null, false).then { List<CsrUpdate> list ->
            if (listOptions.active != null) {
                list.removeAll { CsrUpdate csrUpdate ->
                    listOptions.active != csrUpdate.active
                }
            }

            list.each { CsrUpdate item ->
                if (item != null) {
                    resultList.items.add(item)
                }
            }

            resultList.total = list.size()
            return Promise.pure(resultList)
        }
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
}
