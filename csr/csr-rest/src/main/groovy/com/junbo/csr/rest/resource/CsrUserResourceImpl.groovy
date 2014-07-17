package com.junbo.csr.rest.resource

import com.junbo.common.id.GroupId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.csr.spec.error.AppErrors
import com.junbo.csr.spec.model.CsrGroup
import com.junbo.csr.spec.model.CsrInvitationRequest
import com.junbo.csr.spec.model.CsrUser
import com.junbo.csr.spec.option.list.CsrGroupListOptions
import com.junbo.csr.spec.option.list.CsrUserListOptions
import com.junbo.csr.spec.resource.CsrGroupResource
import com.junbo.csr.spec.resource.CsrUserResource
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

/**
 * Created by haomin on 14-7-14.
 */
@CompileStatic
class CsrUserResourceImpl implements CsrUserResource {
    private CsrGroupResource csrGroupResource
    private UserResource userResource
    private UserPersonalInfoResource userPersonalInfoResource

    @Required
    void setCsrGroupResource(CsrGroupResource csrGroupResource) {
        this.csrGroupResource = csrGroupResource
    }

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Override
    Promise<Results<CsrUser>> list(CsrUserListOptions listOptions) {
        return csrGroupResource.list(new CsrGroupListOptions()).then { Results<CsrGroup> csrGroupResults ->
            if (csrGroupResults == null || csrGroupResults.items == null) {
                throw AppErrors.INSTANCE.CsrGroupNotFound().exception()
            }

            def resultList = new Results<CsrUser>(items: [])
            return Promise.each(csrGroupResults.items) { CsrGroup csrGroup ->
                return userResource.list(new UserListOptions(groupId: new GroupId(csrGroup.id))).then { Results<User> userResults ->
                    if (userResults != null && userResults.items != null && userResults.items.size() > 0) {
                        for (User user in userResults.items) {
                            def csrUser = new CsrUser(id: user.getId().getValue() ,username: user.username, countryCode: user.countryOfResidence?.toString(), tier: csrGroup.tier)
                            if (user.name != null) {
                                UserPersonalInfo userPersonalInfo = userPersonalInfoResource.get(user.name.value, new UserPersonalInfoGetOptions()).get()

                                UserName name = ObjectMapperProvider.instance().treeToValue(userPersonalInfo.getValue(), UserName.class)
                                if (name != null) {
                                    csrUser.setName("$name.givenName $name.familyName")
                                }
                            }

                            resultList.items.add(csrUser)
                        }
                    }

                    return Promise.pure(null)
                }
            }.then {
                resultList.items = resultList.items.sort {CsrUser csrUser ->
                    csrUser.name == null ? csrUser.username : csrUser.name
                }
                resultList.total = resultList.items.size()
                return Promise.pure(resultList)
            }
        }
    }

    @Override
    Promise<Response> inviteCsr(CsrInvitationRequest request) {
        return null
    }

    @Override
    Promise<Response> confirmCsr(String code) {
        return null
    }

}
