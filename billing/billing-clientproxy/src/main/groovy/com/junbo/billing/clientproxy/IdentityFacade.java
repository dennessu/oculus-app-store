/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy;

import com.junbo.identity.spec.v1.model.Address;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by xmchen on 14-2-20.
 */

public interface IdentityFacade {

    Promise<User> getUser(Long userId);

    Promise<Address> getAddress(Long addressId);

    Promise<Organization> getOrganization(Long organizationId);
}
