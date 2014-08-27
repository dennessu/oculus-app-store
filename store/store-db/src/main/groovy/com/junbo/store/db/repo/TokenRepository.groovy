package com.junbo.store.db.repo

import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.token.Token
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 8/26/14.
 */
@CompileStatic
interface TokenRepository {
    Promise<Token> create(Token token)

    Promise<Token> get(String id)

    Promise<Void> delete(String id)

    Promise<Token> searchByUserIdAndType(UserId userId, String type, Integer limit, Integer offset)
}

