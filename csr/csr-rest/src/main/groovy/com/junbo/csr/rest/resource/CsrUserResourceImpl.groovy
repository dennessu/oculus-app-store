package com.junbo.csr.rest.resource

import com.junbo.common.model.Results
import com.junbo.csr.spec.model.CsrUser
import com.junbo.csr.spec.option.list.CsrUserListOptions
import com.junbo.csr.spec.resource.CsrUserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-7-14.
 */
@CompileStatic
class CsrUserResourceImpl implements CsrUserResource {
    @Override
    Promise<Results<CsrUser>> list(CsrUserListOptions listOptions) {
        return null
    }
}
