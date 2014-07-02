package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-11.
 */
@CompileStatic
class UserRepositoryCloudantImpl extends CloudantClient<User> implements UserRepository {
    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Override
    Promise<User> create(User user) {

        if (user.id == null) {
            user.id = new UserId(idGenerator.nextId())
        }
        return cloudantPost(user)
    }

    @Override
    Promise<User> update(User user) {
        return cloudantPut(user)
    }

    @Override
    Promise<User> get(UserId userId) {
        return cloudantGet(userId.toString())
    }

    @Override
    Promise<Void> delete(UserId userId) {
        return cloudantDelete(userId.toString())
    }

    @Override
    Promise<User> searchUserByCanonicalUsername(String canonicalUsername) {
        return queryView('by_canonical_username', canonicalUsername).then { List<User> list ->
            return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
        }
    }

    @Override
    Promise<User> searchUserByMigrateId(Long migratedUserId) {
        return super.queryView('by_migrate_user_id', migratedUserId.toString()).then { List<User> list ->
            return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
        }
    }
}
