package com.junbo.payment.clientproxy.facebook;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Created by wenzhu on 12/2/14.
 */
@RestResource
@Path("/")
public interface FacebookBatchApi {
    @POST
    Promise<String> batchInvoke(@QueryParam("access_token") String accessToken, @QueryParam("batch") String batchRequest);
}

