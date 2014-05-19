package com.junbo.authorization

import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter

/**
 * Created by kg on 5/18/2014.
 */
class TokenInfoParserFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final TOKEN_INFO_SCOPE_PROPERTY = TokenInfoParserFilter.toString() + '.tokenInfoScope'

    @Autowired
    private TokenInfoParser tokenInfoParser

    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {

        def tokenInfo = tokenInfoParser.parse()

        requestContext.setProperty(TOKEN_INFO_SCOPE_PROPERTY, new TokenInfoScope(tokenInfo))
    }

    @Override
    void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        def tokenInfoScope = (TokenInfoScope) requestContext.getProperty(TOKEN_INFO_SCOPE_PROPERTY)
        requestContext.setProperty(TOKEN_INFO_SCOPE_PROPERTY, null)

        if (tokenInfoScope != null) {
            tokenInfoScope.close()
        }
    }
}
