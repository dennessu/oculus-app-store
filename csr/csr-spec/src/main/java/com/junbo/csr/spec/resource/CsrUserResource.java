/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.resource;

import com.junbo.common.model.Results;
import com.junbo.csr.spec.model.CsrUser;
import com.junbo.csr.spec.option.list.CsrUserListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by haomin on 14-7-14.
 */
@Path("/csr-users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface CsrUserResource {
    @GET
    Promise<Results<CsrUser>> list(@BeanParam CsrUserListOptions listOptions);
}
