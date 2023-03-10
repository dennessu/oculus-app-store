package com.junbo.apphost.core.logging

import com.junbo.langur.core.HashUtil
import com.junbo.langur.core.IpUtil
import groovy.transform.CompileStatic
import org.glassfish.grizzly.Connection
import org.glassfish.grizzly.http.Protocol
import org.glassfish.grizzly.http.server.HttpServerFilter
import org.glassfish.grizzly.http.server.HttpServerProbe
import org.glassfish.grizzly.http.server.Request
import org.glassfish.grizzly.http.server.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by Shenhua on 4/23/2014.
 */
@CompileStatic
class AccessLogProbe extends HttpServerProbe.Adapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogProbe)

    private static final String ATTRIBUTE_TIME_STAMP = AccessLogProbe.name + '.timeStamp'

    private static final SimpleDateFormatThreadLocal DATE_FORMAT =
            new SimpleDateFormatThreadLocal('[yyyy-MM-dd HH:mm:ss.SSS Z]')

    private static final Pattern ACCESS_TOKEN_PATTERN = Pattern.compile("access_token=([0-9A-Za-z\\-~,]+)")

    @Override
    void onRequestReceiveEvent(HttpServerFilter filter, Connection connection, Request request) {
        def requestMillis = System.currentTimeMillis()
        request.setAttribute(ATTRIBUTE_TIME_STAMP, requestMillis)

        if (LOGGER.isDebugEnabled()) {
            // %h %D %u %t \"%r\" %s %b \"%{Referer}i\" \"%{User-agent}i\ \"[ALL_HEADERS]\""

            def remoteHost = getRemoteHost(request)
            def remoteUser = getRemoteUser(request)
            def requestTimestamp = DATE_FORMAT.get().format(new Date(requestMillis))
            def method = getRequestMethod(request)
            def uri = getRequestURI(request)
            def query = getRequestQuery(request)
            def protocol = getRequestProtocol(request)
            def referer = getRequestHeader(request, 'Referer')
            def userAgent = getRequestHeader(request, 'User-agent')
            def requestHeaders = getRequestHeaders(request)

            String record = "$remoteHost - $remoteUser $requestTimestamp \"$method $uri$query $protocol\"" +
                    " - - \"$referer\" \"$userAgent\" \"[$requestHeaders]\""

            LOGGER.debug(record)
        }
    }

    @Override
    void onRequestCompleteEvent(HttpServerFilter filter, Connection connection, Response response) {

        /* Calculate request timing */
        long requestMillis = (long) response.request.getAttribute(ATTRIBUTE_TIME_STAMP)
        long responseMillis = System.currentTimeMillis()

        // %h %D %u %t \"%r\" %s %b \"%{Referer}i\" \"%{User-agent}i\ \"[ALL_HEADERS]\""

        def remoteHost = getRemoteHost(response.request)
        def latencyMillis = responseMillis - requestMillis
        def remoteUser = getRemoteUser(response.request)
        def responseTimestamp = DATE_FORMAT.get().format(new Date(responseMillis))
        def method = getRequestMethod(response.request)
        def uri = getRequestURI(response.request)
        def query = getRequestQuery(response.request)
        def protocol = getRequestProtocol(response.request)
        def status = getResponseStatus(response)
        def size = getResponseSize(response)
        def referer = getRequestHeader(response.request, 'Referer')
        def userAgent = getRequestHeader(response.request, 'User-agent')
        def requestHeaders = getRequestHeaders(response.request)

        String record = "$remoteHost $latencyMillis $remoteUser $responseTimestamp \"$method $uri$query $protocol\"" +
                " $status $size \"$referer\" \"$userAgent\" \"[$requestHeaders]\""

        LOGGER.info(record)
    }

    private static String getRemoteHost(Request request) {

        String value = null

        try {
            value = IpUtil.getClientIpFromRequest(request)
        } catch (Exception ignore) {
        }

        return value == null ? '-' : value
    }

    private static String getRemoteUser(Request request) {

        String value = request.remoteUser
        return value == null ? '-' : value
    }

    private static String getRequestMethod(Request request) {

        String value = request.method
        return value == null ? '-' : value
    }

    private static String getRequestURI(Request request) {

        String value = request.requestURI
        return value == null ? '-' : value
    }

    private static String getRequestQuery(Request request) {
        String value = request.queryString
        if (value != null) {
            Matcher matcher = ACCESS_TOKEN_PATTERN.matcher(value)
            StringBuffer sb = new StringBuffer('?')
            while (matcher.find()) {
                String tokenValue = matcher.group(1)
                String[] tokens = tokenValue.split(',')
                StringBuilder stringBuilder = new StringBuilder()
                for (String token : tokens) {
                    stringBuilder.append(HashUtil.hash(token))
                    stringBuilder.append(',')
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1)

                matcher.appendReplacement(sb, "access_token=$stringBuilder")
            }
            matcher.appendTail(sb)
            return sb.toString()
        } else {
            return ''
        }
    }

    private static String getRequestProtocol(Request request) {

        Protocol protocol = request.protocol
        if (protocol == null) {
            return '-'
        }

        switch (protocol) {
            case Protocol.HTTP_0_9: return 'HTTP/0.9'
            case Protocol.HTTP_1_0: return 'HTTP/1.0'
            case Protocol.HTTP_1_1: return 'HTTP/1.1'
            default: return '-'
        }
    }

    private static String getRequestHeader(Request request, String header) {
        return request.getHeaders(header).join(', ')
    }

    private static String getRequestHeaders(Request request) {
        StringBuilder sb = new StringBuilder()
        Iterator<String> headerIterator = request.getHeaderNames().iterator()
        while (headerIterator.hasNext()) {
            String headerName = headerIterator.next()
            String header = getRequestHeader(request, headerName)
            if (headerName.equalsIgnoreCase('Authorization') && header.startsWith('Bearer ')) {
                sb.append("$headerName: Bearer ")
                String tokenValue = header.substring(7)
                String[] tokens = tokenValue.split(',')

                for (String token : tokens) {
                    sb.append(HashUtil.hash(token))
                    sb.append(',')
                }
                sb.deleteCharAt(sb.length() - 1)
                sb.append('; ')
            } else {
                sb.append("$headerName: $header; ")
            }
        }
        return sb.toString()
    }

    private static String getResponseStatus(Response response) {
        return Integer.toString(response.status)
    }

    private static String getResponseSize(Response response) {
        return response.contentLength < 0 ? '-' : Integer.toString(response.contentLength)
    }

    @CompileStatic
    static class SimpleDateFormatThreadLocal extends ThreadLocal<SimpleDateFormat> {

        private final SimpleDateFormat format

        SimpleDateFormatThreadLocal(String format) {
            this.format = new SimpleDateFormat(format, Locale.ENGLISH)
        }

        @Override
        protected SimpleDateFormat initialValue() {
            return (SimpleDateFormat) format.clone()
        }
    }
}
