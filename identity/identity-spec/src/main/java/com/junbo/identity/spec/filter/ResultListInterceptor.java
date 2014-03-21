/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.filter;

import com.junbo.common.model.Link;
import com.junbo.identity.spec.model.common.ResultList;
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
public class ResultListInterceptor implements ContainerResponseFilter {
    private static final String CURSOR_FORMAT = "cursor=";
    private static final String COUNT_FORMAT = "count=";
    private static final String AND_FORMAT = "&";

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        if(responseContext == null || responseContext.getStatus() != Response.Status.OK.getStatusCode()
        || responseContext.getEntity() == null || !(responseContext.getEntity() instanceof ResultList)) {
            return;
        }

        ResultList resultList = (ResultList)responseContext.getEntity();
        Link self = getSelf(responseContext);
        resultList.setSelf(self);

        if(resultList.getHasNext() != null && resultList.getHasNext().booleanValue()) {
            resultList.setNext(getNext(self));
        }
        resultList.setHasNext(null);

    }

    private Link getSelf(ContainerResponseContext responseContext) {
        if(!(responseContext instanceof ContainerResponse)) {
            return null;
        }

        ContainerResponse response = (ContainerResponse)responseContext;
        Link ref = new Link();
        ref.setHref(response.getRequestContext().getRequestUri().toString());
        ref.setId("");
        return ref;
    }

    private Link getNext(Link self) {
        if(self == null || StringUtils.isEmpty(self.getHref()))    {
            return null;
        }
        String selfUrl = self.getHref();

        Integer selfCount = extract(selfUrl, COUNT_FORMAT);
        Integer selfCursor = extract(selfUrl, CURSOR_FORMAT);

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
        String str[ ] = url.split(CURSOR_FORMAT);
        if(str.length != 2) {
            //no cursor information in current url
            // Append mode
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
}
