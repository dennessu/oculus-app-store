package com.junbo.authorization

import com.junbo.common.id.UserId
import com.junbo.oauth.spec.model.TokenInfo
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * Created by Shenhua on 5/14/2014.
 */
@CompileStatic
class AuthorizeContext {

    private static final ThreadLocal<TokenInfo> CURRENT_TOKEN_INFO = new ThreadLocal<>()

    private static final ThreadLocal<Set<String>> CURRENT_RIGHTS = new ThreadLocal<>()

    public static final String SUPER_RIGHT = '_SUPER_RIGHT_'

    public static final String SUPER_SCOPE = '_SUPER_SCOPE_'

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

    static Set<String> getCurrentScopes() {
        TokenInfo tokenInfo = CURRENT_TOKEN_INFO.get()
        return (tokenInfo == null || tokenInfo.scopes == null) ?
                Collections.emptySet() : tokenInfo.scopes.split(' ') as Set
    }

    static boolean hasScopes(String... scopes) {
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
            if (!currentScopes.contains(scope)) {
                return false
            }
        }

        return true
    }

    static void setCurrentRights(Set<String> rights) {
        CURRENT_RIGHTS.set(rights)
    }

    static Set<String> getCurrentRights() {
        def rights = CURRENT_RIGHTS.get()
        return rights == null ? Collections.emptySet() : rights
    }

    static boolean hasRights(String... rights) {
        if (rights == null || rights.length == 0) {
            throw new IllegalArgumentException('rights is null or empty')
        }

        def currentRights = CURRENT_RIGHTS.get()
        if (currentRights == null) {
            return false
        }

        if (currentRights.contains(SUPER_RIGHT)) {
            return true
        }

        for (String right : rights) {
            if (!currentRights.contains(right)) {
                return false
            }
        }

        return true
    }

    private AuthorizeContext() {
    }
}
