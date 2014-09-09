package com.junbo.authorization
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.oauth.spec.endpoint.TokenInfoEndpoint
import com.junbo.oauth.spec.model.TokenInfo
import com.junbo.oauth.spec.model.TokenType
import groovy.transform.CompileStatic
import net.sf.ehcache.Cache
import net.sf.ehcache.Ehcache
import net.sf.ehcache.Element
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils
/**
 * Created by Shenhua on 5/14/2014.
 */
@CompileStatic
public class TokenInfoParserImpl implements TokenInfoParser, InitializingBean {

    private static final String AUTHORIZATION_HEADER = 'Authorization'

    private static final DEFAULT_TOKEN = '00000000000000000000'

    private static final String DEFAULT_CLIENT = 'anonymous'

    private static final int TOKENS_LENGTH = 2

    private TokenInfoEndpoint tokenInfoEndpoint

    private Boolean allowTestAccessToken

    private Ehcache tokenInfoCache

    private String defaultScopes

    private TokenInfo defaultToken

    private Long defaultAccessTokenExpiration

    @Required
    void setTokenInfoEndpoint(TokenInfoEndpoint tokenInfoEndpoint) {
        this.tokenInfoEndpoint = tokenInfoEndpoint
    }

    @Required
    void setAllowTestAccessToken(Boolean allowTestAccessToken) {
        this.allowTestAccessToken = allowTestAccessToken
    }

    @Required
    void setTokenInfoCache(Cache tokenInfoCache) {
        this.tokenInfoCache = tokenInfoCache
    }

    @Required
    void setDefaultScopes(String defaultScopes) {
        this.defaultScopes = defaultScopes
    }

    @Required
    void setDefaultAccessTokenExpiration(Long defaultAccessTokenExpiration) {
        this.defaultAccessTokenExpiration = defaultAccessTokenExpiration
    }

    TokenInfo parse() {

        String accessToken = parseAccessToken()

        if (accessToken == null) {
            return null
        }

        if (allowTestAccessToken) {

            if (accessToken.startsWith('TEST:')) { // TEST:USERID:CLIENTID:SCOPES
                def parts = accessToken.split(':')

                UserId sub = (parts.length <= 1 || parts[1].empty) ? null : new UserId(IdFormatter.decodeId(UserId, parts[1]))
                String clientId = (parts.length <= 2 || parts[2].empty) ? 'unknownClient' : parts[2]
                String scopes = (parts.length <= 3 || parts[3].empty) ? '' : parts[3]

                def tokenInfo = new TokenInfo(
                        sub: sub,
                        expiresIn: Long.MAX_VALUE,
                        scopes: scopes,
                        clientId: clientId
                )

                return tokenInfo
            }
        }

        if (accessToken == DEFAULT_TOKEN) {
            return defaultToken
        }

        Element cachedElement = tokenInfoCache.get(accessToken)
        if (cachedElement != null) {
            return (TokenInfo) cachedElement.objectValue
        }

        try {
            def tokenInfo = tokenInfoEndpoint.getTokenInfo(accessToken).get();

            if (StringUtils.hasText(tokenInfo.ipAddress) && JunboHttpContext.requestIpAddress != tokenInfo.ipAddress) {
                throw AppCommonErrors.INSTANCE.forbiddenWithMessage('The request user\'s ip address is ' +
                        'different from the access token\'s ip address').exception()
            }

            tokenInfoCache.put(new Element(accessToken, tokenInfo))

            return tokenInfo
        } catch (AppErrorException ex) {
            if ((int) (ex.error.httpStatusCode / 100) == 4) {
                throw ex
            } else {
                throw new RuntimeException('Failed to parse access token', ex)
            }
        }
    }

    private static String parseAccessToken() {

        String authorization = JunboHttpContext.requestHeaders.getFirst(AUTHORIZATION_HEADER)

        if (authorization == null) {
            return null
        }

        String accessToken = extractAccessToken(authorization)
        return accessToken
    }

    private static String extractAccessToken(String authorization) {
        Assert.notNull(authorization, 'authorization is null')

        String[] tokens = authorization.split(' ')
        if (tokens.length != TOKENS_LENGTH || !tokens[0].equalsIgnoreCase(TokenType.BEARER.name())) {
            return null
        }

        return tokens[1]
    }

    @Override
    void afterPropertiesSet() throws Exception {
        defaultToken = new TokenInfo(
                sub: new UserId(0L),
                clientId: DEFAULT_CLIENT,
                scopes: defaultScopes,
                expiresIn: defaultAccessTokenExpiration
        )
    }
}
