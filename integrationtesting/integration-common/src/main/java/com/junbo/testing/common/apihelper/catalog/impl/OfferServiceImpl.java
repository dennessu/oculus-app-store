/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.catalog.impl;

import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.common.id.OfferId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.testing.common.apihelper.catalog.ItemService;
import com.junbo.testing.common.apihelper.catalog.OfferService;
import com.junbo.testing.common.blueprint.Master;
import com.junbo.testing.common.libs.IdConverter;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.libs.RestUrl;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;
import junit.framework.Assert;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Offer related APIs
 */
public class OfferServiceImpl implements OfferService {

    private final String catalogServerURL = RestUrl.getRestUrl("catalog") +
            "offers";
    private static String defaultOfferFileName = "defaultOffer";
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    private LogHelper logger = new LogHelper(ItemServiceImpl.class);
    private AsyncHttpClient asyncClient;

    private static OfferService instance;

    public static synchronized OfferService instance() {
        if (instance == null) {
            instance = new OfferServiceImpl();
        }
        return instance;
    }

    private OfferServiceImpl() {
        asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());
    }

    public String getOffer(String offerId, HashMap<String, String> httpPara) throws Exception {
        return getOffer(offerId, httpPara, 200);
    }

    public String getOffer(String offerId, HashMap<String, String> httpPara, int expectedResponseCode)
            throws Exception {
        return null;
    }

    public List<String> getOffer(HashMap<String, String> httpPara) throws Exception {
        return getOffer(httpPara, 200);
    }

    public List<String> getOffer(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {
        return null;
    }

    public String postDefaultOffer() throws Exception {

        String resourceLocation = String.format("classpath:testOffers/%s.json", defaultOfferFileName);
        Resource resource = resolver.getResource(resourceLocation);
        Assert.assertNotNull(resource);

        BufferedReader br = new BufferedReader(new FileReader(resource.getFile().getPath()));
        StringBuilder strDefaultOffer = new StringBuilder();
        try {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                strDefaultOffer.append(sCurrentLine + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null){
                br.close();
            }
        }

        Offer offerForPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {},
                strDefaultOffer.toString());
        ItemService itemService = ItemServiceImpl.instance();
        String defaultItemId = itemService.postDefaultItem();
        ItemEntry itemEntry = new ItemEntry();
        List<ItemEntry> itemEntities = new ArrayList<>();
        itemEntry.setItemId(Long.valueOf(defaultItemId));
        itemEntry.setQuantity(1);
        itemEntities.add(itemEntry);
        offerForPost.setItems(itemEntities);
        offerForPost.setOwnerId(Master.getInstance().getItem(defaultItemId).getOwnerId());

        RequestBuilder reqBuilder = new RequestBuilder("POST");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(catalogServerURL);
        reqBuilder.setBody(new JsonMessageTranscoder().encode(offerForPost));
        Request req = reqBuilder.build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);

        offerForPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {},
                nettyResponse.getResponseBody());

        String offerRtnId = IdConverter.idLongToHexString(OfferId.class, offerForPost.getId());
        Master.getInstance().addOffer(offerRtnId, offerForPost);

        return offerRtnId;
    }

    public String postOffer(Offer offer) throws Exception {
        return postOffer(offer, 200);
    }

    public String postOffer(Offer offer, int expectedResponseCode) throws Exception {
        return null;
    }

    public String updateOffer(String offerId, Offer offer) throws Exception {
        return updateOffer(offerId, offer, 200);
    }

    public String updateOffer(String offerId, Offer offer, int expectedResponseCode) throws Exception {
        return null;
    }

}
