package com.junbo.store.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.store.db.repo.TokenRepository
import com.junbo.store.spec.model.token.Token
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 8/27/14.
 */
@CompileStatic
class TokenRepositoryCloudantRepository extends CloudantClient<Token> implements TokenRepository  {

    @Override
    Promise<Token> create(Token token) {
        return cloudantPost(token)
    }

    @Override
    Promise<Token> get(String id) {
        return cloudantGet(id)
    }

    @Override
    Promise<Void> delete(String id) {
        return cloudantDelete(id)
    }

    @Override
    Promise<List<Token>> searchByUserIdAndType(UserId userId, String type, Integer limit, Integer offset) {
        def startKey = [userId.toString(), type]
        def endKey = [userId.toString(), type]
        return queryView('by_user_id_type', startKey.toArray(new String()), endKey.toArray(new String()), true, limit, offset, true)
    }
}
