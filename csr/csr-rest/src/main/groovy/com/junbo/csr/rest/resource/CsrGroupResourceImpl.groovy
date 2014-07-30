package com.junbo.csr.rest.resource

import com.junbo.common.id.GroupId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.csr.core.service.IdentityService
import com.junbo.csr.spec.model.CsrGroup
import com.junbo.csr.spec.option.list.CsrGroupListOptions
import com.junbo.csr.spec.resource.CsrGroupResource
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-7-14.
 */
@CompileStatic
class CsrGroupResourceImpl implements CsrGroupResource {

    private IdentityService identityService
    private String organizationOwner
    private String organizationName
    private List<String> groupNameList

    @Required
    void setIdentityService(IdentityService identityService) {
        this.identityService = identityService
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
        if (listOptions != null && listOptions.userId != null) {
            // check userId
            identityService.getUserById(listOptions.userId).get()
        }

        return identityService.getUserByUsername(organizationOwner).then { User organizationOwner ->
            return identityService.getOrganizationByOwerIdAndOrgName(organizationOwner.id as UserId, organizationName).then { Organization organization ->
                return identityService.getGroupByOrganization(organization.id as OrganizationId).then { Results<Group> groupResults ->
                    // filter with csr group name list
                    groupResults.items.retainAll{ Group group ->
                        groupNameList.contains(group.name)
                    }

                    def resultList = new Results<CsrGroup>(items: [])

                    // filter with groupName parameter
                    if (listOptions.groupName != null) {
                        groupResults.items.removeAll { Group group ->
                            group.name != listOptions.groupName
                        }
                    }

                    groupResults.items.each { Group group ->
                        def csrGroup = new CsrGroup(groupId: group.id as GroupId, groupName: group.name)
                        String[] splits = group.name.split('_')
                        if (splits.size() > 1) {
                            csrGroup.tier = splits[1]
                        }
                        resultList.items.add(csrGroup)
                    }

                    // filter with userId parameter
                    if (listOptions.userId != null) {
                        List<GroupId> groupIds = identityService.getGroupIdByUserId(listOptions.userId)

                        resultList.items.retainAll { CsrGroup csrGroup ->
                            groupIds.contains(csrGroup.groupId)
                        }
                    }

                    return Promise.pure(resultList)
                }
            }
        }
    }
}
