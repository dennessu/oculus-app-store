package com.junbo.store.rest.filter
import com.fasterxml.jackson.core.type.TypeReference
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppError
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.configuration.ConfigService
import com.junbo.configuration.ConfigServiceManager
import com.junbo.store.spec.model.StoreApiHeader
import groovy.transform.CompileStatic

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider
/**
 * Created by acer on 2015/2/13.
 */
@Provider
@CompileStatic
class ClientCheckFilter implements ContainerRequestFilter {

    private final static String STORE_API_URL_PREFIX = '/horizon-api/'

    private Set<String> userAgentBlackList;

    public ClientCheckFilter() {
        ConfigService configService = ConfigServiceManager.instance();
        String userAgentBlackListText = configService.getConfigValue('store.client.blackList');
        List<String> list = (List<String>)ObjectMapperProvider.instance().readValue(userAgentBlackListText, new TypeReference<List<String>>() {})
        this.userAgentBlackList = [] as Set
        list.each { String entry ->
            this.userAgentBlackList << (entry.trim().toLowerCase())
        }
    }

    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        boolean isStoreApi = requestContext.uriInfo.path.startsWith(STORE_API_URL_PREFIX)
        if (!isStoreApi) {
            return
        }

        String userAgent = requestContext.getHeaders().getFirst(StoreApiHeader.USER_AGENT.value)
        if (userAgent != null && userAgentBlackList.contains(userAgent.trim().toLowerCase())) {
            AppError error = AppCommonErrors.INSTANCE.forbiddenWithMessage('Blocked client')
            requestContext.abortWith(Response.status(error.httpStatusCode).entity(error.error()).build())
        }
    }
}
