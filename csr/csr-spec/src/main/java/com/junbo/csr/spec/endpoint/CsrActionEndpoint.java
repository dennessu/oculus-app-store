/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.endpoint;

import com.junbo.common.model.Results;
import com.junbo.csr.spec.model.SearchForm;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by haomin on 14-7-8.
 */
@Path("/csr-action")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CsrActionEndpoint {
    @POST
    @Path("/search")
    Promise<Results<User>> searchUsers(SearchForm searchForm);
}
