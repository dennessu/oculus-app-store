package com.junbo.identity.data.repository.impl.cloudant.migration

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UsernameMailBlockerId
import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.hash.PiiHashFactory
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.repository.migration.UsernameEmailBlockerRepository
import com.junbo.identity.spec.v1.model.migration.UsernameMailBlocker
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/9/14.
 */
@CompileStatic
class UsernameEmailBlockerRepositoryCloudantImpl extends CloudantClient<UsernameMailBlocker> implements UsernameEmailBlockerRepository {
    private PiiHashFactory piiHashFactory

    @Override
    Promise<List<UsernameMailBlocker>> searchByUsername(String canonicalUsername, Integer limit, Integer offset) {
        PiiHash piiHash = getPiiHash(UserPersonalInfoType.USERNAME.toString())
        return queryView('by_user_name_hash', piiHash.generateHash(canonicalUsername), limit, offset, false)
    }

    @Override
    Promise<List<UsernameMailBlocker>> searchByEmail(String email, Integer limit, Integer offset) {
        PiiHash piiHash = getPiiHash(UserPersonalInfoType.EMAIL.toString())
        return queryView('by_email_hash', piiHash.generateHash(email.toLowerCase(Locale.ENGLISH)), limit, offset, false)
    }

    @Override
    Promise<UsernameMailBlocker> get(UsernameMailBlockerId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<UsernameMailBlocker> create(UsernameMailBlocker model) {
        PiiHash piiHash = getPiiHash(UserPersonalInfoType.EMAIL.toString())
        model.hashEmail = piiHash.generateHash(model.email.toLowerCase(Locale.ENGLISH))
        piiHash = getPiiHash(UserPersonalInfoType.USERNAME.toString())
        model.hashUsername = piiHash.generateHash(model.canonicalUsername.toString())

        return cloudantPost(model).then { UsernameMailBlocker created ->
            return get(created.getId())
        }
    }

    @Override
    Promise<UsernameMailBlocker> update(UsernameMailBlocker model, UsernameMailBlocker oldModel) {
        return get(model.getId()).then { UsernameMailBlocker usernameMailBlocker ->
            PiiHash piiHash = getPiiHash(UserPersonalInfoType.EMAIL.toString())
            usernameMailBlocker.hashEmail = piiHash.generateHash(model.email.toLowerCase(Locale.ENGLISH))
            piiHash = getPiiHash(UserPersonalInfoType.USERNAME.toString())
            usernameMailBlocker.hashUsername = piiHash.generateHash(model.canonicalUsername.toString())

            return cloudantPut(usernameMailBlocker, oldModel).then { UsernameMailBlocker updated ->
                return get(updated.getId())
            }
        }
    }

    @Override
    Promise<Void> delete(UsernameMailBlockerId id) {
        throw new IllegalStateException('Delete is not supported')
    }

    private PiiHash getPiiHash(String type) {
        PiiHash hash = piiHashFactory.getAllPiiHashes().find { PiiHash piiHash ->
            return piiHash.handles(type)
        }
        if (hash == null) {
            throw new IllegalStateException('No hash implementation for type ' + type)
        }

        return hash
    }

    @Required
    void setPiiHashFactory(PiiHashFactory piiHashFactory) {
        this.piiHashFactory = piiHashFactory
    }
}
