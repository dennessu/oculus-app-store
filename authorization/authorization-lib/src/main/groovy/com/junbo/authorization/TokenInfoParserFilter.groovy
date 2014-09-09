package com.junbo.authorization
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.context.JunboHttpContextScopeListener
import org.springframework.beans.factory.annotation.Autowired
/**
 * Created by kg on 5/18/2014.
 */
class TokenInfoParserFilter implements JunboHttpContextScopeListener {

    private static final TOKEN_INFO_SCOPE_PROPERTY = TokenInfoParserFilter.toString() + '.tokenInfoScope'

    @Autowired
    private TokenInfoParser tokenInfoParser

    @Override
    void begin() {
        def tokenInfo = tokenInfoParser.parse()
        JunboHttpContext.properties.put(TOKEN_INFO_SCOPE_PROPERTY, new TokenInfoScope(tokenInfo))
    }

    @Override
    void end() {
        def tokenInfoScope = (TokenInfoScope) JunboHttpContext.properties.remove(TOKEN_INFO_SCOPE_PROPERTY)
        if (tokenInfoScope != null) {
            tokenInfoScope.close()
        }
    }
}
