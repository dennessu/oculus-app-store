/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog.impl;

import com.junbo.catalog.spec.model.common.LocalizableProperty;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.UserId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.catalog.OfferService;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Offer related APIs
 */
public class OfferServiceImpl extends HttpClientBase implements OfferService {

    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "offers";
    private final String defaultOfferFileName = "defaultOffer";
    private LogHelper logger = new LogHelper(OfferServiceImpl.class);
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
    }

    public String getOffer(String offerId, HashMap<String, String> httpPara) throws Exception {
        return getOffer(offerId, httpPara, 200);
    }

    public String getOffer(String offerId, HashMap<String, String> httpPara, int expectedResponseCode)
            throws Exception {

        String url = catalogServerURL + "/" + offerId;
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode, httpPara);
        Offer offerGet = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {},
                responseBody);
        String offerRtnId = IdConverter.idLongToHexString(OfferId.class, offerGet.getOfferId());
        Master.getInstance().addOffer(offerRtnId, offerGet);

        return offerRtnId;
    }

    public List<String> getOffer(HashMap<String, String> httpPara) throws Exception {
        return getOffer(httpPara, 200);
    }

    public List<String> getOffer(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<Offer> offerGet = new JsonMessageTranscoder().decode(new TypeReference<Results<Offer>>() {},
                responseBody);
        List<String> listOfferId = new ArrayList<>();
        for (Offer offer : offerGet.getItems()){
            String offerRtnId = IdConverter.idLongToHexString(OfferId.class, offer.getOfferId());
            Master.getInstance().addOffer(offerRtnId, offer);
            listOfferId.add(offerRtnId);
        }

        return listOfferId;
    }

    public String postDefaultOffer(EnumHelper.CatalogItemType itemType) throws Exception {

        Offer offerForPost = prepareOfferEntity(defaultOfferFileName, itemType);
        return postOffer(offerForPost);
    }

    public Offer prepareOfferEntity(String fileName, EnumHelper.CatalogItemType itemType) throws Exception {

        String strOfferContent = readFileContent(fileName);
        Offer offerForPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {}, strOfferContent);
        String defaultItemId = itemService.postDefaultItem(itemType);
        //Release the item
        Item item =  Master.getInstance().getItem(defaultItemId);
        item.setCurated(true);
        itemService.updateItem(item);

        ItemEntry itemEntry = new ItemEntry();
        List<ItemEntry> itemEntities = new ArrayList<>();
        itemEntry.setItemId(item.getItemId());
        itemEntry.setQuantity(1);
        itemEntities.add(itemEntry);
        //offerForPost.setItems(itemEntities);
        offerForPost.setOwnerId(item.getOwnerId());

        return offerForPost;
    }

    public String postOffer(Offer offer) throws Exception {
        return postOffer(offer, 200);
    }

    public String postOffer(Offer offer, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, offer, expectedResponseCode);
        Offer offerPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {},
                responseBody);
        String offerRtnId = IdConverter.idLongToHexString(OfferId.class, offerPost.getOfferId());
        Master.getInstance().addOffer(offerRtnId, offerPost);

        return offerRtnId;
    }

    public String updateOffer(Offer offer) throws Exception {
        return updateOffer(offer, 200);
    }

    public String updateOffer(Offer offer, int expectedResponseCode) throws Exception {

        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferId.class, offer.getOfferId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, offer, expectedResponseCode);
        Offer offerPut = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {},
                responseBody);
        String offerRtnId = IdConverter.idLongToHexString(OfferId.class, offerPut.getOfferId());
        Master.getInstance().addOffer(offerRtnId, offerPut);

        return offerRtnId;
    }

    public String getOfferIdByName(String offerName) throws  Exception {

        if (!offerLoaded){
            this.loadAllOffers();
            this.loadAllItems();
            this.postPredefinedOffer();
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

    private void postPredefinedOffer() throws Exception {

        InputStream inStream = ClassLoader.getSystemResourceAsStream("testOffers/predefinedofferlist.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        try {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                logger.logInfo(sCurrentLine);
                String[] strLine = sCurrentLine.split(",");
                if (Master.getInstance().getOfferIdByName(strLine[0]) == null) {
                    Offer offer = this.preparePredefinedOffer(strLine[0], strLine[1], strLine[2], strLine[3]);
                    String offerId = this.postOffer(offer);
                    //Release the offer
                    Offer offerRtn = Master.getInstance().getOffer(offerId);
                    offerRtn.setCurated(true);
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

    private Offer preparePredefinedOffer(String offerName, String itemName, String userName, String offerType)
            throws  Exception {

        String strOfferContent = readFileContent(offerName);
        Offer offerForPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {}, strOfferContent);

        String itemId = Master.getInstance().getItemIdByName(itemName);
        List<String> userIdList = userService.GetUserByUserName(userName);
        String userId;

        if (userIdList == null || userIdList.isEmpty()) {
            User user = new User();
            user.setUsername(userName);
            user.setNickName(RandomFactory.getRandomStringOfAlphabet(10));
            user.setType("user");
            user.setCanonicalUsername(RandomFactory.getRandomStringOfAlphabet(10));
            user.setCurrency("USD");
            user.setLocale("en_US");
            user.setPreferredLanguage("en_US");
            userId = userService.PostUser(user);
        }
        else {
            userId = userIdList.get(0);
        }

        if (itemId == null) {
            Item item = itemService.prepareItemEntity(itemName);
            LocalizableProperty itemLocalProperty = new LocalizableProperty();
            itemLocalProperty.set("en_US", itemName);
            item.setName(itemLocalProperty);
            item.setType(offerType);
            item.setOwnerId(IdConverter.hexStringToId(UserId.class, userId));

            itemId = itemService.postItem(item);
            item = Master.getInstance().getItem(itemId);
            item.setCurated(true);
            itemService.updateItem(item);
        }

        ItemEntry itemEntry = new ItemEntry();
        List<ItemEntry> itemEntities = new ArrayList<>();
        itemEntry.setItemId(IdConverter.hexStringToId(ItemId.class, itemId));
        itemEntry.setQuantity(1);
        itemEntities.add(itemEntry);
        //offerForPost.setItems(itemEntities);
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
