package com.junbo.authorization.token

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.model.TokenInfo

/**
 * Created by Shenhua on 5/14/2014.
 */
interface TokenInfoParser {
    Promise<TokenInfo> parse()

    public <T> Promise<T> parseAndThen(Closure<Promise<T>> closure)

    public <T> Promise<T> parseAndThen(Promise.Func0<Promise<T>> closure)
}
