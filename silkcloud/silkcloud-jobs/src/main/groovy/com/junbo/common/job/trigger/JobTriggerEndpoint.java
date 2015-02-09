/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.job.trigger;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The JobTriggerEndpoint interface.
 */
@Path("/trigger-job")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface JobTriggerEndpoint {

    @POST
    Promise<Response> triggerJob(JobTriggerRequest jobTriggerRequest);
}
