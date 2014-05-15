package com.junbo.authorization.token

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.endpoint.TokenInfoEndpoint
import com.junbo.oauth.spec.model.TokenInfo
import com.junbo.oauth.spec.model.TokenType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.util.Assert

import javax.ws.rs.core.HttpHeaders

/**
 * Created by Shenhua on 5/14/2014.
 */
@CompileStatic
public class TokenInfoParserImpl implements TokenInfoParser, ApplicationContextAware {

    private static final String AUTHORIZATION_HEADER = 'Authorization'

    private static final int TOKENS_LENGTH = 2

    private ApplicationContext applicationContext

    private TokenInfoEndpoint tokenInfoEndpoint

    private Boolean useDummyTokenInfo

    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext
    }

    @Required
    void setTokenInfoEndpoint(TokenInfoEndpoint tokenInfoEndpoint) {
        this.tokenInfoEndpoint = tokenInfoEndpoint
    }

    @Required
    void setUseDummyTokenInfo(Boolean useDummyTokenInfo) {
        this.useDummyTokenInfo = useDummyTokenInfo
    }

    Promise<TokenInfo> parse() {

        if (useDummyTokenInfo) {
            def tokenInfo = new TokenInfo(
                    sub: new UserId(1234567890),
                    expiresIn: Long.MAX_VALUE,
                    scopes: AuthorizeContext.SUPER_RIGHT,
                    clientId: "dummyClientId"
            )

            return Promise.pure(tokenInfo)
        }

        String accessToken = parseAccessToken()

        // todo: try catch and throw 401 if invalid
        return tokenInfoEndpoint.getTokenInfo(accessToken)
    }

    public <T> Promise<T> parseAndThen(Closure<Promise<T>> closure) {
        return parse().then { TokenInfo tokenInfo ->
            return TokenInfoScope.with(tokenInfo, closure)
        }
    }

    @Override
    def <T> Promise<T> parseAndThen(Promise.Func0<Promise<T>> closure) {
        return parse().then { TokenInfo tokenInfo ->
            return TokenInfoScope.with(tokenInfo, closure)
        }
    }

    private String parseAccessToken() {
        HttpHeaders httpHeaders = applicationContext.getBean(HttpHeaders)
        String authorization = httpHeaders.requestHeaders.getFirst(AUTHORIZATION_HEADER)

        if (authorization == null) {
            return null
        }

        String accessToken = extractAccessToken(authorization)
        return accessToken
    }

    static String extractAccessToken(String authorization) {
        Assert.notNull(authorization, 'authorization is null')

        String[] tokens = authorization.split(' ')
        if (tokens.length != TOKENS_LENGTH || !tokens[0].equalsIgnoreCase(TokenType.BEARER.name())) {
            return null
        }

        return tokens[1]
    }
}
