package com.junbo.testing.common.apihelper.catalog.impl;

import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.common.id.AttributeId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.testing.common.apihelper.catalog.AttributeService;
import com.junbo.testing.common.blueprint.Master;
import com.junbo.testing.common.libs.IdConverter;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.libs.RestUrl;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Attribute related APIs
 */
public class AttributeServiceImpl implements AttributeService {

    private final String catalogServerURL = RestUrl.getRestUrl("catalog") +
            "attributes";

    private LogHelper logger = new LogHelper(ItemServiceImpl.class);
    private AsyncHttpClient asyncClient;

    private static AttributeService instance;

    public static synchronized AttributeService instance() {
        if (instance == null) {
            instance = new AttributeServiceImpl();
        }
        return instance;
    }

    private AttributeServiceImpl() {
        asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());
    }

    public String getAttribute(String attributeId) throws Exception {
        return getAttribute(attributeId, 200);
    }

    public String getAttribute(String attributeId, int expectedResponseCode) throws Exception {

        String url = catalogServerURL + "/" + attributeId;

        RequestBuilder reqBuilder = new RequestBuilder("GET");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(url);
        Request req = reqBuilder.build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());

        Attribute attributeGet = new JsonMessageTranscoder().decode(new TypeReference<Attribute>() {},
                nettyResponse.getResponseBody());
        String attributeRtnId = IdConverter.idLongToHexString(AttributeId.class, attributeGet.getId());
        Master.getInstance().addAttribute(attributeRtnId, attributeGet);

        return attributeRtnId;
    }

    public List<String> getAttribute(HashMap<String, String> httpPara) throws Exception {
        return getAttribute(httpPara, 200);
    }

    public List<String> getAttribute(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {

        RequestBuilder reqBuilder = new RequestBuilder("GET");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(catalogServerURL);
        if (!httpPara.isEmpty()) {
            for (String key: httpPara.keySet()) {
                reqBuilder.addQueryParameter(key, httpPara.get(key));
            }
        }
        Request req = reqBuilder.build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());

        ResultList<Attribute> attributeGet = new JsonMessageTranscoder().decode(new TypeReference<ResultList<Attribute>>() {},
                nettyResponse.getResponseBody());

        List<String> listItemId = new ArrayList<>();
        for (Attribute attribute : attributeGet.getResults()){
            String attributeRtnId = IdConverter.idLongToHexString(AttributeId.class, attribute.getId());
            Master.getInstance().addAttribute(attributeRtnId, attribute);
            listItemId.add(attributeRtnId);
        }
        return listItemId;
    }

    public String postAttribute(Attribute attribute) throws Exception {
        return postAttribute(attribute, 200);
    }

    public String postAttribute(Attribute attribute, int expectedResponseCode) throws Exception {

        RequestBuilder reqBuilder = new RequestBuilder("POST");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(catalogServerURL);
        reqBuilder.setBody(new JsonMessageTranscoder().encode(attribute));
        Request req = reqBuilder.build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);

        Attribute attributePost = new JsonMessageTranscoder().decode(new TypeReference<Attribute>() {},
                nettyResponse.getResponseBody());

        String attributeRtnId = IdConverter.idLongToHexString(AttributeId.class, attributePost.getId());
        Master.getInstance().addAttribute(attributeRtnId, attributePost);

        return attributeRtnId;
    }

}
