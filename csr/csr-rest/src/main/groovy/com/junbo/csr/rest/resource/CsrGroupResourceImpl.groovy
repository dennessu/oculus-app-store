package com.junbo.csr.rest.resource

import com.junbo.common.id.GroupId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.csr.spec.error.AppErrors
import com.junbo.csr.spec.model.CsrGroup
import com.junbo.csr.spec.option.list.CsrGroupListOptions
import com.junbo.csr.spec.resource.CsrGroupResource
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.list.GroupListOptions
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.resource.GroupResource
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-7-14.
 */
@CompileStatic
class CsrGroupResourceImpl implements CsrGroupResource {
    private UserResource userResource
    private OrganizationResource organizationResource
    private GroupResource groupResource
    private String organizationOwner
    private String organizationName
    private List<String> groupNameList

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Required
    void setGroupResource(GroupResource groupResource) {
        this.groupResource = groupResource
    }

    @Required
    void setOrganizationOwner(String organizationOwner) {
        this.organizationOwner = organizationOwner
    }

    @Required
    void setOrganizationName(String organizationName) {
        this.organizationName = organizationName
    }

    @Required
    void setGroupNameList(List<String> groupNameList) {
        this.groupNameList = groupNameList
    }

    @Override
    Promise<Results<CsrGroup>> list(CsrGroupListOptions listOptions) {
        return userResource.list(new UserListOptions(username: organizationOwner)).then { Results<User> userResults ->
            if (userResults == null || userResults.items == null || userResults.items.size() == 0) {
                throw AppErrors.INSTANCE.CsrGroupNotFound().exception()
            }
            User organizationOwner = userResults.items.get(0)

            return organizationResource.list(new OrganizationListOptions(ownerId: organizationOwner.id as UserId)).then { Results<Organization> organizationResults ->
                if (organizationResults == null || organizationResults.items == null) {
                    throw AppErrors.INSTANCE.CsrGroupNotFound().exception()
                }
                organizationResults.items.retainAll{ Organization org ->
                    org.name == organizationName
                }

                if (organizationResults.items.size() == 0) {
                    throw AppErrors.INSTANCE.CsrGroupNotFound().exception()
                }

                Organization organization = organizationResults.items.get(0)

                return groupResource.list(new GroupListOptions(organizationId: organization.id as OrganizationId)).then { Results<Group> groupResults ->
                    if (groupResults == null || groupResults.items == null) {
                        throw AppErrors.INSTANCE.CsrGroupNotFound().exception()
                    }

                    groupResults.items.retainAll{ Group group ->
                        groupNameList.contains(group.name)
                    }

                    def resultList = new Results<CsrGroup>(items: [])
                    groupResults.items.each { Group group ->
                        def csrGroup = new CsrGroup(id: group.id.toString(), groupName: group.name)
                        String[] splits = group.name.split('_')
                        if (splits.size() > 1) {
                            csrGroup.tier = splits[1]
                        }
                        resultList.items.add(csrGroup)
                    }

                    return Promise.pure(resultList)
                }
            }
        }
    }
}
