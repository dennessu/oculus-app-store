package com.junbo.authorization
import com.junbo.common.id.UserId
import com.junbo.langur.core.track.TrackContext
import com.junbo.langur.core.track.TrackContextManager
import com.junbo.configuration.ConfigService
import com.junbo.configuration.ConfigServiceManager
import com.junbo.oauth.spec.model.TokenInfo
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils
/**
 * Created by Shenhua on 5/14/2014.
 */
@CompileStatic
class AuthorizeContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeContext)

    private static final ThreadLocal<TokenInfo> CURRENT_TOKEN_INFO = new ThreadLocal<>()

    private static final ThreadLocal<Set<String>> CURRENT_RIGHTS = new ThreadLocal<>()

    public static final String SUPER_RIGHT = '_SUPER_RIGHT_'

    public static final String SUPER_SCOPE = '_SUPER_SCOPE_'

    private static boolean authorizeDisabled = false

    private static boolean globalDebugMode = false

    static void setAuthorizeDisabled(boolean disabled) {
        authorizeDisabled = disabled
    }

    static void setCurrentTokenInfo(TokenInfo tokenInfo) {
        CURRENT_TOKEN_INFO.set(tokenInfo)
    }

    @PackageScope
    static TokenInfo getCurrentTokenInfo() {
        return CURRENT_TOKEN_INFO.get()
    }

    static UserId getCurrentUserId() {
        TokenInfo tokenInfo = CURRENT_TOKEN_INFO.get()
        return tokenInfo == null ? null : tokenInfo.sub
    }

    static String getCurrentClientId() {
        TokenInfo tokenInfo = CURRENT_TOKEN_INFO.get()
        return tokenInfo == null ? null : tokenInfo.clientId
    }

    static boolean getDebugEnabled() {
        TokenInfo tokenInfo = CURRENT_TOKEN_INFO.get()
        return globalDebugMode || (tokenInfo == null ? false : (tokenInfo.debugEnabled ?: false))
    }

    @PackageScope
    static Set<String> getCurrentScopes() {
        TokenInfo tokenInfo = CURRENT_TOKEN_INFO.get()
        return (tokenInfo == null || tokenInfo.scopes == null) ?
                Collections.emptySet() : tokenInfo.scopes.split(' ') as Set
    }

    static boolean hasScopes(String... scopes) {
        if (authorizeDisabled) {
            return true;
        }

        if (scopes == null || scopes.length == 0) {
            throw new IllegalArgumentException('scopes is null or empty')
        }

        TokenInfo tokenInfo = CURRENT_TOKEN_INFO.get()
        if (tokenInfo == null || tokenInfo.scopes == null) {
            return false
        }

        def currentScopes = tokenInfo.scopes.split(' ') as Set

        if (currentScopes.contains(SUPER_SCOPE)) {
            return true
        }

        for (String scope : scopes) {
            if (!currentScopes.contains(scope) && scope != '*') {
                return false
            }
        }

        return true
    }

    static boolean hasAnyScope(String[] scopes) {
        if (authorizeDisabled) {
            return true;
        }

        if (scopes == null || scopes.length == 0) {
            throw new IllegalArgumentException('scopes is null or empty')
        }

        for (String scope : scopes) {
            if (scope == '*') {
                return true
            }
        }

        TokenInfo tokenInfo = CURRENT_TOKEN_INFO.get()
        if (tokenInfo == null || tokenInfo.scopes == null) {
            return false
        }

        def currentScopes = tokenInfo.scopes.split(' ') as Set

        if (currentScopes.contains(SUPER_SCOPE)) {
            return true
        }

        for (String scope : scopes) {
            if (currentScopes.contains(scope) || scope == '*') {
                return true
            }
        }

        return false
    }

    static void setCurrentRights(Set<String> rights) {
        CURRENT_RIGHTS.set(rights)
    }

    @PackageScope
    static Set<String> getCurrentRights() {
        def rights = CURRENT_RIGHTS.get()
        return rights == null ? Collections.emptySet() : rights
    }

    static boolean hasRights(String... rights) {
        if (authorizeDisabled) {
            return true;
        }

        if (rights == null || rights.length == 0) {
            throw new IllegalArgumentException('rights is null or empty')
        }

        def currentRights = CURRENT_RIGHTS.get()
        if (currentRights == null) {
            LOGGER.info('The current rights is empty, hasRights return false')
            return false
        }

        if (currentRights.contains(SUPER_RIGHT)) {
            return true
        }

        for (String right : rights) {
            if (!currentRights.contains(right)) {
                LOGGER.info("Current rights do not contain $right")
                return false
            }
        }

        return true
    }

    private AuthorizeContext() {
    }

    static {
        ConfigService configService = ConfigServiceManager.instance();
        if (configService != null) {
            String authorizeDisabled = configService.getConfigValue('authorization.lib.service.disabled');
            if (authorizeDisabled != null) {
                this.authorizeDisabled = Boolean.parseBoolean(authorizeDisabled);
            }

            String globalDebugMode = configService.getConfigValue('common.conf.debugMode');
            if (!StringUtils.isEmpty(globalDebugMode)) {
                this.globalDebugMode = Boolean.parseBoolean(globalDebugMode);
            }
        }

        // make it globally available in common-lib
        TrackContextManager.set(new TrackContext() {
            @Override
            Long getCurrentUserId() {
                return AuthorizeContext.getCurrentUserId()?.value
            }

            @Override
            String getCurrentClientId() {
                return AuthorizeContext.getCurrentClientId()
            }

            @Override
            String getCurrentScopes() {
                TokenInfo tokenInfo = CURRENT_TOKEN_INFO.get()
                return (tokenInfo == null || tokenInfo.scopes == null) ?
                        null : tokenInfo.scopes
            }

            @Override
            boolean getDebugEnabled() {
                return AuthorizeContext.getDebugEnabled()
            }
        })
    }
}
