package com.junbo.csr.core.service.impl

import com.junbo.common.id.GroupId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.csr.core.service.IdentityService
import com.junbo.csr.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.GroupListOptions
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.identity.spec.v1.option.model.GroupGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.GroupResource
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.identity.spec.v1.resource.UserGroupMembershipResource
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-7-17.
 */
@CompileStatic
class IdentityServiceImpl implements IdentityService {
    private UserPersonalInfoResource userPersonalInfoResource
    private UserResource userResource
    private OrganizationResource organizationResource
    private GroupResource groupResource
    private UserGroupMembershipResource userGroupMembershipResource

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

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
    void setUserGroupMembershipResource(UserGroupMembershipResource userGroupMembershipResource) {
        this.userGroupMembershipResource = userGroupMembershipResource
    }

    @Override
    Promise<List<User>> getUserByUserEmail(String userEmail) {
        return userPersonalInfoResource.list(new UserPersonalInfoListOptions(email: userEmail.toLowerCase(Locale.ENGLISH))).then { Results<UserPersonalInfo> results ->
            if (results == null || results.items == null || results.items.isEmpty()) {
                throw AppErrors.INSTANCE.userNotFoundByEmail(userEmail).exception()
            }

            List<User> users = new ArrayList<>()
            return Promise.each(results.items) { UserPersonalInfo userPersonalInfo ->
                return userResource.get(userPersonalInfo.userId as UserId, new UserGetOptions()).then { User user ->
                    if (user != null) {
                        users.add(user)
                    }
                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(users)
            }
        }
    }

    @Override
    Promise<User> getUserByUsername(String username) {
        return userResource.list(new UserListOptions(username: username)).then { Results<User> userResults ->
            if (userResults == null || userResults.items == null || userResults.items.isEmpty()) {
                throw AppErrors.INSTANCE.userNotFoundByUsername(username).exception()
            }

            return Promise.pure(userResults.items.get(0))
        }
    }

    @Override
    Promise<User> getUserByVerifiedEmail(String userEmail) {
        return userPersonalInfoResource.list(new UserPersonalInfoListOptions(email: userEmail.toLowerCase(Locale.ENGLISH), isValidated: true)).then { Results<UserPersonalInfo> results ->
            if (results == null || results.items == null || results.items.isEmpty()) {
                throw AppErrors.INSTANCE.userNotFoundByEmail(userEmail).exception()
            }

            return userResource.get(results.items.get(0).userId as UserId, new UserGetOptions()).then { User user ->
                return Promise.pure(user)
            }
        }
    }

    @Override
    Promise<User> getUserById(UserId id) {
        return userResource.get(id, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFoundById(id).exception()
            }

            return Promise.pure(user)
        }
    }

    @Override
    Promise<Results<User>> getUserByGroupId(GroupId groupId) {
        return userResource.list(new UserListOptions(groupId: groupId))
    }

    @Override
    String getUserNameByUser(User user) {
        String nameStr = ''
        if (user.name != null) {
            UserPersonalInfo userPersonalInfo = userPersonalInfoResource.get(user.name.value, new UserPersonalInfoGetOptions()).get()
            UserName name = ObjectMapperProvider.instance().treeToValue(userPersonalInfo.getValue(), UserName.class)
            if (name != null) {
                nameStr = "$name.givenName $name.familyName"
            }
        }

        return nameStr
    }

    @Override
    Promise<Group> getGroupById(GroupId groupId) {
        return groupResource.get(groupId, new GroupGetOptions())
    }

    @Override
    Promise<Organization> getOrganizationByOwerIdAndOrgName(UserId organizationOwner, String organizationName) {
        return organizationResource.list(new OrganizationListOptions(ownerId: organizationOwner)).then { Results<Organization> organizationResults ->
            if (organizationResults == null || organizationResults.items == null) {
                throw AppErrors.INSTANCE.organizationNotFound(organizationOwner, organizationName).exception()
            }
            organizationResults.items.retainAll { Organization org ->
                org.name == organizationName
            }

            if (organizationResults.items.isEmpty()) {
                throw AppErrors.INSTANCE.organizationNotFound(organizationOwner, organizationName).exception()
            }

            Organization organization = organizationResults.items.get(0)

            return Promise.pure(organization)
        }
    }

    @Override
    Promise<Results<Group>> getGroupByOrganization(OrganizationId organizationId) {
        return groupResource.list(new GroupListOptions(organizationId: organizationId)).then { Results<Group> groupResults ->
            if (groupResults == null || groupResults.items == null || groupResults.items.isEmpty()) {
                throw AppErrors.INSTANCE.csrGroupNotLoaded().exception()
            }

            return Promise.pure(groupResults.items)
        }
    }

    @Override
    List<GroupId> getGroupIdByUserId(UserId userId) {
        Results<Group> groups = groupResource.list(new GroupListOptions(userId: userId)).get()
        return groups.items.empty ? (List<GroupId>) Collections.emptyList() :
                groups.items.collect { Group group -> group.getId() }
    }
}
