package com.junbo.common.error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Shenhua on 4/14/2014.
 */
public class AppErrorException extends WebApplicationException {
    private final AppError error;

    public AppError getError() {
        return error;
    }

    public AppErrorException(AppError error) {
        super(Response.status(error.getHttpStatusCode()).entity(error.error()).
                type(MediaType.APPLICATION_JSON_TYPE).build());

        this.error = error;
    }
}
