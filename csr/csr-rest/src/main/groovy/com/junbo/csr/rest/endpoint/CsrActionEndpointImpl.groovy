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
        switch (searchForm.type) {
            case SearchType.USERNAME:
                def user = identityService.getUserByUsername(searchForm.value).get()
                results.items.add(user)
                break
            case SearchType.USERID:
                Long userId = IdFormatter.decodeId(UserId, searchForm.value)
                def user = identityService.getUserById(new UserId(userId)).get()
                results.items.add(user)
                break
            case SearchType.FULLNAME:
                results = identityService.getUserByUserFullName(searchForm.value).get()
                break
            case SearchType.EMAIL:
                results = identityService.getUserByUserEmail(searchForm.value).get()
                break
            case SearchType.PHONENUMBER:
                results = identityService.getUserByPhoneNumber(searchForm.value).get()
                break
        }

        // unique results by user id
        results.items.unique { User user ->
            return user.getId()
        }

        return Promise.pure(results)
    }
}
