package com.junbo.rating.core.mock;

import com.junbo.common.id.OrganizationId;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.rating.clientproxy.OrganizationGateway;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizwu on 12/15/14.
 */
public class MockOrganizationGatewayImpl implements OrganizationGateway {
    Map<OrganizationId, Organization> organizationMap = new HashMap<OrganizationId, Organization>() {{
        put(new OrganizationId(100L), genOrg100());
        put(new OrganizationId(101L), genOrg101());
    }};
    @Override
    public Organization getOrganization(OrganizationId organizationId) {
        return organizationMap.get(organizationId);
    }

    private Organization genOrg100() {
        return new Organization() {{
            setPublisherRevenueRatio(Double.valueOf("0.8"));
        }};
    }

    private Organization genOrg101() {
        return new Organization() {{
            setPublisherRevenueRatio(Double.valueOf("0"));
        }};
    }
}
