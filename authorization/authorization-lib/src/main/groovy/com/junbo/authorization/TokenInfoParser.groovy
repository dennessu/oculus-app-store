package com.junbo.authorization

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.model.TokenInfo

/**
 * Created by Shenhua on 5/14/2014.
 */
interface TokenInfoParser {
    TokenInfo parse()
}
