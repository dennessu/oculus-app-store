package com.junbo.csr.rest.endpoint

import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.csr.core.service.IdentityService
import com.junbo.csr.spec.def.SearchType
import com.junbo.csr.spec.endpoint.CsrActionEndpoint
import com.junbo.csr.spec.error.AppErrors
import com.junbo.csr.spec.model.SearchForm
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-7-8.
 */
@CompileStatic
class CsrActionEndpointImpl implements CsrActionEndpoint {
    private IdentityService identityService

    @Required
    void setIdentityService(IdentityService identityService) {
        this.identityService = identityService
    }

    @Override
    Promise<Results<User>> searchUsers(SearchForm searchForm) {
        if (searchForm == null || searchForm.type == null || searchForm.value == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception()
        }

        def results = new Results<User>(items: [])
        User user = null
        switch (searchForm.type) {
            case SearchType.USERNAME:
                user = identityService.getUserByUsername(searchForm.value).get()
                break
            case SearchType.USERID:
                Long userId = IdFormatter.decodeId(UserId, searchForm.value)
                user = identityService.getUserById(new UserId(userId)).get()
                break
            case SearchType.FULLNAME:
                return identityService.getUserByUserFullName(searchForm.value)
            case SearchType.EMAIL:
                return identityService.getUserByUserEmail(searchForm.value)
            case SearchType.PHONENUMBER:
                return identityService.getUserByPhoneNumber(searchForm.value)
        }

        if (user != null) {
            results.items.add(user)
        }
        return Promise.pure(results)
    }
}
