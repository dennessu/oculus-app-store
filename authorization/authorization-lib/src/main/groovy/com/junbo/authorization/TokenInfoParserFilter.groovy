package com.junbo.authorization
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.context.JunboHttpContextScopeListener
import com.junbo.langur.core.profiling.ProfilingHelper
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
/**
 * Created by kg on 5/18/2014.
 */
@CompileStatic
class TokenInfoParserFilter implements JunboHttpContextScopeListener {

    private static final String TOKEN_INFO_SCOPE_PROPERTY = TokenInfoParserFilter.toString() + '.tokenInfoScope'

    @Autowired
    private TokenInfoParser tokenInfoParser

    @Override
    void begin() {
        def tokenInfo = tokenInfoParser.parse()
        if (tokenInfo != null && tokenInfo.debugEnabled && ProfilingHelper.hasProfileHeader()
                && !ProfilingHelper.isProfileOutputEnabled()) {
            // isProfileOutputEnabled result is cached. Now the token's debug info will change the result.
            // clear to trigger re-calculation of isProfilingEnabled.
            ProfilingHelper.clear();
        }
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
