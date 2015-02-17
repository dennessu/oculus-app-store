package com.junbo.identity.service

import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

@CompileStatic
public interface UserService {
    Promise<User> get(UserId userId)

    Promise<User> getActiveUser(UserId userId)

    Promise<User> getNonDeletedUser(UserId userId)

    Promise<User> create(User user)

    Promise<User> update(User user, User oldUser)

    Promise<Void> delete(UserId id)

    Promise<User> getActiveUsersByMigrateId(Long migrateId)

    Promise<List<User>> getActiveUsersWithInvalidVatUser(Integer limit, Integer offset)

    Promise<Results<User>> getAllUsers(Integer limit, String cursor)
}