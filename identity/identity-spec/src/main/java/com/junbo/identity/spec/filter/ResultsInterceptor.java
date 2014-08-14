/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.filter;

import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import org.apache.commons.collections.CollectionUtils;
import org.glassfish.jersey.server.ContainerResponse;
import org.springframework.util.StringUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by liangfu on 3/4/14.
 */
public class ResultsInterceptor implements ContainerResponseFilter {
    private static final String CURSOR_FORMAT = "cursor=";
    private static final String COUNT_FORMAT = "count=";
    private static final String AND_FORMAT = "&";

    private String selfHrefPrfix = "https://api.oculusvr.com/v1";

    public ResultsInterceptor() {
        ConfigService configService = ConfigServiceManager.instance();
        if (configService != null) {
            String prefixFromConfig = configService.getConfigValue("common.conf.resourceUrlPrefix");
            if (prefixFromConfig != null) {
                selfHrefPrfix = prefixFromConfig;
            }
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        if(responseContext == null || responseContext.getStatus() != Response.Status.OK.getStatusCode()
        || responseContext.getEntity() == null || !(responseContext.getEntity() instanceof Results)) {
            return;
        }

        Results resultList = (Results)responseContext.getEntity();
        if (needResetNext(resultList)) {
            Link self = getSelf(responseContext);
            resultList.setSelf(self);

            if((resultList.hasNext())
            || (resultList.getTotal() != null && resultList.getItems() != null && resultList.getTotal() != resultList.getItems().size())) {
                resultList.setNext(getNext(resultList.getTotal(), self));
            }
        }
    }

    private Link getSelf(ContainerResponseContext responseContext) {
        if(!(responseContext instanceof ContainerResponse)) {
            return null;
        }

        ContainerResponse response = (ContainerResponse)responseContext;
        Link ref = new Link();

        String requestUri = response.getRequestContext().getRequestUri().toString();
        requestUri = requestUri.replace(response.getRequestContext().getBaseUri().toString(),
                selfHrefPrfix.endsWith("/") ? selfHrefPrfix : selfHrefPrfix + "/");

        ref.setHref(requestUri);
        ref.setId("");
        return ref;
    }

    private Link getNext(Long total, Link self) {
        if(self == null || StringUtils.isEmpty(self.getHref()))    {
            return null;
        }
        String selfUrl = self.getHref();

        Integer selfCount = extract(selfUrl, COUNT_FORMAT);
        Integer selfCursor = extract(selfUrl, CURSOR_FORMAT);

        Integer nextCursor = (selfCount == null ? 0 : selfCount) + (selfCursor == null ? 0 : selfCursor);
        if (nextCursor > total) {
            return null;
        }

        Link next = new Link();
        next.setId("");
        next.setHref(replaceExistsOrAppend(selfUrl, selfCursor, selfCount));

        return next;
    }

    private Integer extract(String url, String format) {
        String[] strings = url.split(format);
        if(strings != null && strings.length == 2) {
            String[] countValue = strings[1].split(AND_FORMAT);
            return Integer.parseInt(countValue[0]);
        }

        return null;
    }

    private Integer getNextCursor(Integer selfCursor, Integer selfCount) {
        if((selfCursor == null && selfCount == null) ||
           (selfCursor != null && selfCount == null)) {
            return null;
        }
        else if(selfCursor == null && selfCount != null) {
            return selfCount + 1;
        }
        else {
            return selfCount + selfCursor + 1;
        }
    }

    private String replaceExistsOrAppend(String url, Integer cursor, Integer count) {
        Integer nextCursor = getNextCursor(cursor, count);
        String nextURL = url;

        String[] str = url.split(CURSOR_FORMAT);
        if(str.length != 2) {
            // no cursor information in current url
            // append mode
            if(nextCursor != null) {
                nextURL += ("&cursor=" + nextCursor);
            }
        }
        else {
            // Replace mode
            if(nextCursor != null) {
                nextURL = nextURL.replaceAll("&cursor=" + cursor, "&cursor=" + nextCursor);
            }
            else {
                nextURL = nextURL.replaceAll("&cursor=" + cursor, "");
            }
        }

        return nextURL;
    }

    private Boolean needResetNext(Results results) {
        if (CollectionUtils.isEmpty(results.getItems())) {
            return false;
        }

        try {
            if (results.getItems().get(0).getClass().getPackage().getName().contains("com.junbo.identity.spec")) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}
