package com.junbo.identity.service

import com.junbo.common.id.ErrorIdentifier
import com.junbo.identity.spec.v1.model.ErrorInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface ErrorInfoService {
    Promise<ErrorInfo> get(ErrorIdentifier id)

    Promise<ErrorInfo> create(ErrorInfo model)

    Promise<ErrorInfo> update(ErrorInfo model, ErrorInfo oldModel)

    Promise<Void> delete(ErrorIdentifier id)

    Promise<List<ErrorInfo>> searchAll(Integer limit, Integer offset)
}
