package com.junbo.payment.clientproxy.facebook;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by wenzhu on 12/1/14.
 */
@RestResource
@Path("/")
public interface FacebookCreditCardTokenApi {
    @POST
    @Path("payments/generate_token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Promise<String> getCCToken(FacebookCreditCardTokenRequest request);
}
