package com.junbo.csr.core.service

import com.junbo.common.id.GroupId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserGroupId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-7-17.
 */
@CompileStatic
public interface IdentityService {
    Promise<Results<User>> getUserByUserEmail(String userEmail)
    Promise<Results<User>> getUserByUserFullName(String fullName)
    Promise<Results<User>> getUserByPhoneNumber(String phoneNumber)
    Promise<User> getUserByUsername(String username)
    Promise<User> getUserById(UserId id)

    Promise<User> getUserByVerifiedEmail(String userEmail)
    Promise<Results<User>> getUserByGroupId(GroupId groupId)
    Promise<Group> getGroupById(GroupId groupId)
    Promise<Organization> getOrganizationByOrgName(String organizationName)
    Promise<Results<Group>> getGroupByOrganization(OrganizationId organizationId)
    
    String getUserNameByUser(User user)
    List<GroupId> getGroupIdByUserId(UserId userId)

    UserGroup switchUserGroupMembershipWithinGroups(UserId userId, GroupId groupId, List<GroupId> groupIdList)
    UserGroup saveUserGroupMembership(UserId userId, GroupId groupId)
}