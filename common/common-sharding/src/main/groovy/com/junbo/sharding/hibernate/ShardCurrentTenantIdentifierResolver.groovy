package com.junbo.sharding.hibernate

import groovy.transform.CompileStatic
import org.hibernate.context.spi.CurrentTenantIdentifierResolver

/**
 * Created by Shenhua on 3/31/2014.
 */
@CompileStatic
class ShardCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Override
    String resolveCurrentTenantIdentifier() {
        return ShardScope.currentShardId()
    }

    @Override
    boolean validateExistingCurrentSessions() {
        return false
    }
}
