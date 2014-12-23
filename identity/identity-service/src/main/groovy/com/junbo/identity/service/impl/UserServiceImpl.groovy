package com.junbo.identity.service.impl

import com.junbo.common.id.UserId
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.service.UserService
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

@CompileStatic
public class UserServiceImpl implements UserService {
    private UserRepository userRepository

    @Override
    Promise<User> get(UserId userId) {
        return userRepository.get(userId)
    }

    @Override
    Promise<User> getActiveUser(UserId userId) {
        return userRepository.get(userId).then { User user ->
            if (user != null && user.status.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
                return Promise.pure(user)
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<User> getNonDeletedUser(UserId userId) {
        return userRepository.get(userId).then { User user ->
            if (user == null || user.status.equalsIgnoreCase(UserStatus.DELETED.toString())) {
                return Promise.pure(null)
            }

            return Promise.pure(user)
        }
    }

    @Override
    Promise<User> create(User user) {
        return userRepository.create(user)
    }

    @Override
    Promise<User> update(User user, User oldUser) {
        return userRepository.update(user, oldUser)
    }

    @Override
    Promise<Void> delete(UserId id) {
        return userRepository.delete(id)
    }

    @Override
    Promise<User> getActiveUsersByMigrateId(Long migrateId) {
        return userRepository.searchUserByMigrateId(migrateId).then { User user ->
            if (user != null && user.status.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
                return Promise.pure(user)
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<List<User>> getActiveUsersWithInvalidVatUser(Integer limit, Integer offset) {
        return userRepository.searchInvalidVatUser(limit, offset).then { List<User> userList ->
            if (CollectionUtils.isEmpty(userList)) {
                return Promise.pure(userList)
            }
            userList.removeAll { User existing ->
                if (existing == null || !existing.status.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
                    return true
                }

                return false
            }

            return Promise.pure(userList)
        }
    }

    @Override
    Promise<List<User>> getAllUsers(Integer limit, Integer offset) {
        return userRepository.searchAll(limit, offset)
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }
}