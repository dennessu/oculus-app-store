package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViewQueryOptions
import com.junbo.common.id.ErrorIdentifier
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.ErrorInfoRepository
import com.junbo.identity.spec.v1.model.ErrorInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 7/23/14.
 */
@CompileStatic
class ErrorInfoRepositoryCloudantImpl  extends CloudantClient<ErrorInfo> implements ErrorInfoRepository {

    @Override
    Promise<ErrorInfo> get(ErrorIdentifier id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<ErrorInfo> create(ErrorInfo model) {
        return cloudantPost(model)
    }

    @Override
    Promise<ErrorInfo> update(ErrorInfo model, ErrorInfo oldModel) {
        return cloudantPut(model, oldModel)
    }

    @Override
    Promise<Results<ErrorInfo>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(new CloudantViewQueryOptions(
                limit: limit,
                skip: offset
        ))
    }

    @Override
    Promise<Void> delete(ErrorIdentifier id) {
        throw new IllegalStateException('Delete operation not supported for errorInfo')
    }
}
