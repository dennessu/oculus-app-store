package com.junbo.order.mock

import com.junbo.common.id.EntitlementId
import com.junbo.common.model.Results
import com.junbo.entitlement.spec.model.Entitlement
import com.junbo.entitlement.spec.model.EntitlementSearchParam
import com.junbo.entitlement.spec.model.PageMetadata
import com.junbo.entitlement.spec.model.RevokeRequest
import com.junbo.entitlement.spec.resource.EntitlementResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.ws.rs.core.Response

/**
 * Created by LinYi on 2014/8/14.
 */
@CompileStatic
@Component('mockEntitlementResource')
class MockEntitlementResource extends BaseMock implements EntitlementResource {
    @Override
    public Promise<Entitlement> getEntitlement(EntitlementId entitlementId) {
        return null
    }

    @Override
    public Promise<Entitlement> postEntitlement(Entitlement entitlement) {
        return null
    }

    @Override
    Promise<Map<Long, List<Entitlement>>> postEntitlements(Map<Long, List<Entitlement>> entitlements) {
        return null
    }

    @Override
    public Promise<Entitlement> updateEntitlement(EntitlementId entitlementId, Entitlement entitlement) {
        return null
    }

    @Override
    public Promise<Response> deleteEntitlement(EntitlementId entitlementId) {
        return null
    }

    @Override
    public Promise<Results<Entitlement>> searchEntitlements(EntitlementSearchParam searchParam, PageMetadata pageMetadata) {
        return Promise.pure(null)
    }

    @Override
    Promise<Response> revokeEntitlement(RevokeRequest revokeRequest) {
        return null
    }
}
