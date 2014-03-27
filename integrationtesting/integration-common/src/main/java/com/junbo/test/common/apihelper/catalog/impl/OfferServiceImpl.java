/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog.impl;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.UserId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.user.User;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.catalog.OfferService;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RestUrl;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;
import junit.framework.Assert;

import java.io.*;
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

    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "offers";
    private final String defaultOfferFileName = "defaultOffer";
    private LogHelper logger = new LogHelper(OfferServiceImpl.class);
    private AsyncHttpClient asyncClient;
    private static OfferService instance;
    private boolean offerLoaded;

    private ItemService itemService = ItemServiceImpl.instance();
    private UserService userService = UserServiceImpl.instance();

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

        String url = catalogServerURL + "/" + offerId;

        RequestBuilder reqBuilder = new RequestBuilder("GET");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(url);
        if ((httpPara != null) && !httpPara.isEmpty()) {
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

        Offer offerGet = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {},
                nettyResponse.getResponseBody());
        String offerRtnId = IdConverter.idLongToHexString(OfferId.class, offerGet.getId());
        Master.getInstance().addOffer(offerRtnId, offerGet);

        return offerRtnId;
    }

    public List<String> getOffer(HashMap<String, String> httpPara) throws Exception {
        return getOffer(httpPara, 200);
    }

    public List<String> getOffer(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {

        RequestBuilder reqBuilder = new RequestBuilder("GET");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(catalogServerURL);
        if ((httpPara != null) && !httpPara.isEmpty()) {
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

        Results<Offer> offerGet = new JsonMessageTranscoder().decode(new TypeReference<Results<Offer>>() {},
                nettyResponse.getResponseBody());

        List<String> listOfferId = new ArrayList<>();
        for (Offer offer : offerGet.getItems()){
            String offerRtnId = IdConverter.idLongToHexString(OfferId.class, offer.getId());
            Master.getInstance().addOffer(offerRtnId, offer);
            listOfferId.add(offerRtnId);
        }

        return listOfferId;
    }

    public String postDefaultOffer(EnumHelper.CatalogItemType itemType) throws Exception {

        Offer offerForPost = prepareOfferEntity(defaultOfferFileName, itemType);

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

    public Offer prepareOfferEntity(String fileName, EnumHelper.CatalogItemType itemType) throws Exception {

        String strOfferContent = readFileContent(fileName);
        Offer offerForPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {}, strOfferContent);
        String defaultItemId = itemService.postDefaultItem(itemType);
        //Release the item
        Item item =  Master.getInstance().getItem(defaultItemId);
        item.setStatus(EnumHelper.CatalogEntityStatus.RELEASED.getEntityStatus());
        itemService.updateItem(item);

        ItemEntry itemEntry = new ItemEntry();
        List<ItemEntry> itemEntities = new ArrayList<>();
        itemEntry.setItemId(item.getId());
        itemEntry.setQuantity(1);
        itemEntities.add(itemEntry);
        offerForPost.setItems(itemEntities);
        offerForPost.setOwnerId(Master.getInstance().getItem(defaultItemId).getOwnerId());

        return offerForPost;
    }

    public String postOffer(Offer offer) throws Exception {
        return postOffer(offer, 200);
    }

    public String postOffer(Offer offer, int expectedResponseCode) throws Exception {

        RequestBuilder reqBuilder = new RequestBuilder("POST");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(catalogServerURL);
        reqBuilder.setBody(new JsonMessageTranscoder().encode(offer));
        Request req = reqBuilder.build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());

        Offer offerPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {},
                nettyResponse.getResponseBody());

        String offerRtnId = IdConverter.idLongToHexString(OfferId.class, offerPost.getId());
        Master.getInstance().addOffer(offerRtnId, offerPost);

        return offerRtnId;
    }

    public String updateOffer(Offer offer) throws Exception {
        return updateOffer(offer, 200);
    }

    public String updateOffer(Offer offer, int expectedResponseCode) throws Exception {

        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferId.class, offer.getId());
        RequestBuilder reqBuilder = new RequestBuilder("PUT");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(putUrl);
        reqBuilder.setBody(new JsonMessageTranscoder().encode(offer));
        Request req = reqBuilder.build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());

        Offer offerPut = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {},
                nettyResponse.getResponseBody());

        String offerRtnId = IdConverter.idLongToHexString(OfferId.class, offerPut.getId());
        Master.getInstance().addOffer(offerRtnId, offerPut);

        return offerRtnId;
    }

    public String getOfferIdByName(String offerName) throws  Exception {

        if (!offerLoaded){
            this.loadAllOffers();
            this.loadAllItems();
            this.loadAllUsers();
            this.postPredefindeOffer();
            offerLoaded = true;
        }

        return Master.getInstance().getOfferIdByName(offerName);
    }

    private void loadAllOffers() throws Exception {
        this.getOffer(null);
    }

    private void loadAllItems() throws Exception {
        itemService.getItem(null);
    }

    private void loadAllUsers() throws Exception {
        userService.GetUserByUserName(null);
    }

    private void postPredefindeOffer() throws Exception {

        InputStream inStream = ClassLoader.getSystemResourceAsStream("testOffers/predefinedofferlist.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        try {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                logger.logInfo(sCurrentLine);
                String[] strLine = sCurrentLine.split(",");
                if (Master.getInstance().getOfferIdByName(strLine[0]) == null) {
                    Offer offer = this.preparePredefindeOffer(strLine[0], strLine[1], strLine[2], strLine[3]);
                    String offerId = this.postOffer(offer);
                    //Release the offer
                Offer offerRtn = Master.getInstance().getOffer(offerId);
                offerRtn.setStatus(EnumHelper.CatalogEntityStatus.RELEASED.getEntityStatus());
                this.updateOffer(offerRtn);
            }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null){
                br.close();
            }
            if (inStream != null) {
                inStream.close();
            }
        }
    }

    private Offer preparePredefindeOffer(String offerName, String itemName, String userName, String offerType)
            throws  Exception {

        String strOfferContent = readFileContent(offerName);
        Offer offerForPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {}, strOfferContent);

        String itemId = Master.getInstance().getItemIdByName(itemName);
        String userId = Master.getInstance().getUserIdByName(userName);

        if (userId == null) {
            User user = new User();
            user.setUserName(userName);
            user.setPassword("password");
            user.setStatus(EnumHelper.UserStatus.ACTIVE.getStatus());
            userId = userService.PostUser(user);
        }

        if (itemId == null) {
            Item item = itemService.prepareItemEntity(itemName);
            item.setName(itemName);
            item.setType(offerType);
            item.setOwnerId(IdConverter.hexStringToId(UserId.class, userId));

            itemId = itemService.postItem(item);
            item = Master.getInstance().getItem(itemId);
            item.setStatus(EnumHelper.CatalogEntityStatus.RELEASED.getEntityStatus());
            itemService.updateItem(item);
        }

        ItemEntry itemEntry = new ItemEntry();
        List<ItemEntry> itemEntities = new ArrayList<>();
        itemEntry.setItemId(IdConverter.hexStringToId(ItemId.class, itemId));
        itemEntry.setQuantity(1);
        itemEntities.add(itemEntry);
        offerForPost.setItems(itemEntities);
        offerForPost.setOwnerId(IdConverter.hexStringToId(UserId.class, userId));

        return offerForPost;
    }

    private String readFileContent(String fileName) throws Exception {

        String resourceLocation = String.format("testOffers/%s.json", fileName);
        InputStream inStream = ClassLoader.getSystemResourceAsStream(resourceLocation);
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));

        StringBuilder strOffer = new StringBuilder();
        try {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                strOffer.append(sCurrentLine + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null){
                br.close();
            }
            if (inStream != null) {
                inStream.close();
            }
        }

        return strOffer.toString();
    }

}
