package com.junbo.csr.core.service.impl

import com.junbo.common.id.GroupId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserGroupId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.csr.core.service.IdentityService
import com.junbo.csr.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.*
import com.junbo.identity.spec.v1.option.model.GroupGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.*
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.util.Locale

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
    Promise<Results<User>> getUserByUserEmail(String userEmail) {
        return userPersonalInfoResource.list(new UserPersonalInfoListOptions(email: userEmail.toLowerCase(Locale.ENGLISH))).then {
            Results<UserPersonalInfo> results ->
            return getUsers(results)
        }
    }

    @Override
    Promise<Results<User>> getUserByUserFullName(String fullName) {
        return userPersonalInfoResource.list(new UserPersonalInfoListOptions(name: fullName)).then {
            Results<UserPersonalInfo> results ->
            return getUsers(results)
        }
    }

    @Override
    Promise<Results<User>> getUserByPhoneNumber(String phoneNumber) {
        return userPersonalInfoResource.list(new UserPersonalInfoListOptions(phoneNumber: phoneNumber)).then {
            Results<UserPersonalInfo> results ->
            return getUsers(results)
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
        return groupResource.get(groupId, new GroupGetOptions()).then { Group group ->
            if (group == null) {
                throw AppErrors.INSTANCE.groupNotFoundById(groupId).exception()
            }

            return Promise.pure(group)
        }
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

            return Promise.pure(groupResults)
        }
    }

    @Override
    List<GroupId> getGroupIdByUserId(UserId userId) {
        Results<Group> groups = groupResource.list(new GroupListOptions(userId: userId)).get()
        return groups.items.empty ? (List<GroupId>) Collections.emptyList() :
                groups.items.collect { Group group -> group.getId() }
    }

    @Override
    UserGroup switchUserGroupMembershipWithinGroups(UserId userId, GroupId groupId, List<GroupId> withinGroups) {
        if (!withinGroups.contains(groupId)) {
            throw AppErrors.INSTANCE.fieldInvalid('groupId').exception()
        }

        Results<UserGroup> results = userGroupMembershipResource.list(new UserGroupListOptions(userId: userId)).get()

        // retains only within groups
        results.items.retainAll { UserGroup userGroup ->
            withinGroups.contains(userGroup.groupId)
        }

        if (results.items.empty) {
            return userGroupMembershipResource.create(new UserGroup(userId: userId, groupId: groupId)).get()
        }
        else if (results.items.size() == 1) {
            UserGroup updated = results.items.get(0)
            updated.groupId = groupId
            return userGroupMembershipResource.put(updated.id as UserGroupId, updated).get()
        }
        else {
            for (UserGroup userGroup in results.items) {
                userGroupMembershipResource.delete(userGroup.id as UserGroupId).get()
            }
            return userGroupMembershipResource.create(new UserGroup(userId: userId, groupId: groupId)).get()
        }
    }

    @Override
    UserGroup saveUserGroupMembership(UserId userId, GroupId groupId) {
        Results<UserGroup> results = userGroupMembershipResource.list(new UserGroupListOptions(userId: userId, groupId: groupId)).get()

        if (!results.items.empty) {
            return results.items.get(0)
        }
        else {
            return userGroupMembershipResource.create(new UserGroup(userId: userId, groupId: groupId)).get()
        }
    }

    @Override
    UserGroup updateUserGroupMembership(UserGroupId userGroupId, UserId userId, GroupId groupId) {
        return userGroupMembershipResource.patch(userGroupId, new UserGroup(id:userGroupId, userId: userId, groupId: groupId)).get()
    }

    private Promise<Results<User>> getUsers(Results<UserPersonalInfo> results) {
        Results<User> users = new Results<User>(items: [])
        return Promise.each(results.items) { UserPersonalInfo userPersonalInfo ->
            return userResource.get(userPersonalInfo.userId as UserId, new UserGetOptions()).then { User user ->
                if (user != null) {
                    users.items.add(user)
                }
                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(users)
        }
    }
}
