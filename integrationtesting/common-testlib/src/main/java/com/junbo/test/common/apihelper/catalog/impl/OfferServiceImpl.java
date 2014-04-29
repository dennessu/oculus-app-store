/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog.impl;

import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.catalog.OfferRevisionService;
import com.junbo.test.common.apihelper.catalog.ItemRevisionService;
import com.junbo.test.common.apihelper.catalog.OfferService;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.blueprint.Master;
import com.junbo.common.model.Results;
import com.junbo.test.common.libs.*;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.*;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Offer related APIs
 */
public class OfferServiceImpl extends HttpClientBase implements OfferService {

    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "offers";
    private final String defaultOfferFileName = "defaultOffer";
    private final String defaultDigitalItemRevisionFileName = "defaultDigitalItemRevision";
    private final String defaultPhysicalItemRevisionFileName = "defaultPhysicalItemRevision";
    private final String defaultDigitalOfferRevisionFileName = "defaultDigitalOfferRevision";
    private final String defaultPhysicalOfferRevisionFileName = "defaultPhysicalOfferRevision";
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

    public String getOffer(String offerId) throws Exception {
        return getOffer(offerId, 200);
    }

    public String getOffer(String offerId, int expectedResponseCode)
            throws Exception {

        String url = catalogServerURL + "/" + offerId;
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        Offer offerGet = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {},
                responseBody);
        String offerRtnId = IdConverter.idLongToHexString(OfferId.class, offerGet.getOfferId());
        Master.getInstance().addOffer(offerRtnId, offerGet);

        return offerRtnId;
    }

    public List<String> getOffers(HashMap<String, String> httpPara) throws Exception {
        return getOffers(httpPara, 200);
    }

    public List<String> getOffers(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {

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

    public String postDefaultOffer() throws Exception {
        Offer offerForPost = prepareOfferEntity(defaultOfferFileName);
        return postOffer(offerForPost);
    }

    public Offer prepareOfferEntity(String fileName) throws Exception {
        String strOfferContent = readFileContent(String.format("testOffers/%s.json", fileName));
        Offer offerForPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {}, strOfferContent);
        offerForPost.setOwnerId(IdConverter.hexStringToId(UserId.class, getUserId()));
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

    public void deleteOffer(String offerId) throws Exception {
        this.deleteOffer(offerId, 204);
    }

    public void deleteOffer(String offerId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + offerId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
        Master.getInstance().removeOffer(offerId);
    }

    public String getOfferIdByName(String offerName) throws  Exception {

        if (!offerLoaded){
            this.loadAllOffers();
            this.loadAllOfferRevisions();
            this.loadAllItems();
            this.loadAllItemRevisions();
            this.postPredefinedOffer();
            offerLoaded = true;
        }

        return Master.getInstance().getOfferIdByName(offerName);
    }

    private void loadAllOffers() throws Exception {
        HashMap<String, String> paraMap = new HashMap<>();
        this.getOffers(paraMap);
    }

    private void loadAllOfferRevisions() throws Exception {
        HashMap<String, String> paraMap = new HashMap<>();
        OfferRevisionServiceImpl.instance().getOfferRevisions(paraMap);
    }

    private void loadAllItems() throws Exception {
        HashMap<String, String> paraMap = new HashMap<>();
        itemService.getItems(paraMap);
    }

    private void loadAllItemRevisions() throws Exception {
        HashMap<String, String> paraMap = new HashMap<>();
        ItemRevisionServiceImpl.instance().getItemRevisions(paraMap);
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
                    preparePredefinedOffer(strLine[0], strLine[1], strLine[2], strLine[3]);
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

    private void preparePredefinedOffer(String offerName, String itemName, String userName, String offerType)
            throws  Exception {

        String itemId = Master.getInstance().getItemIdByName(itemName);
        Item item = Master.getInstance().getItem(itemId);
        List<String> userIdList = userService.GetUserByUserName(userName);
        String userId;

        if (userIdList == null || userIdList.isEmpty()) {
            userId = getUserId();
        }
        else {
            userId = userIdList.get(0);
        }

        if (itemId == null) {
            item = prepareItem(userId, itemName, offerType);
        }

        //Post offer
        String strOfferContent = readFileContent(String.format("testOffers/%s.json", offerName));
        Offer offerForPost = new JsonMessageTranscoder().decode(new TypeReference<Offer>() {}, strOfferContent);
        offerForPost.setOwnerId(IdConverter.hexStringToId(UserId.class, userId));
        String offerId = this.postOffer(offerForPost);

        //Post offer revision
        String strOfferRevisionContent;
        if (offerType.equalsIgnoreCase("physical")) {
            strOfferRevisionContent = readFileContent(String.format("testOfferRevisions/%s.json",
                    defaultPhysicalOfferRevisionFileName));
        }
        else {
            strOfferRevisionContent = readFileContent(String.format("testOfferRevisions/%s.json",
                    defaultDigitalOfferRevisionFileName));
        }

        OfferRevision offerRevisionForPost = new JsonMessageTranscoder().decode(
                new TypeReference<OfferRevision>() {}, strOfferRevisionContent);

        //set locales
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = new OfferRevisionLocaleProperties();
        offerRevisionLocaleProperties.setName(offerName);
        HashMap<String, OfferRevisionLocaleProperties> locales = new HashMap<>();
        locales.put("en_US", offerRevisionLocaleProperties);
        offerRevisionForPost.setLocales(locales);

        //Add item related info
        ItemEntry itemEntry = new ItemEntry();
        List<ItemEntry> itemEntities = new ArrayList<>();
        itemEntry.setItemId(item.getItemId());
        itemEntry.setQuantity(1);
        itemEntities.add(itemEntry);
        offerRevisionForPost.setItems(itemEntities);
        offerRevisionForPost.setOwnerId(IdConverter.hexStringToId(UserId.class, userId));

        //Add offer related info
        offerRevisionForPost.setOfferId(IdConverter.hexStringToId(OfferId.class, offerId));

        //Post offer revision and update its status to 'Approved'
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();
        String offerRevisionId = offerRevisionService.postOfferRevision(offerRevisionForPost);
        OfferRevision offerRevisionGet = Master.getInstance().getOfferRevision(offerRevisionId);
        offerRevisionGet.setStatus(EnumHelper.CatalogEntityStatus.APPROVED.getEntityStatus());
        offerRevisionService.updateOfferRevision(offerRevisionGet);
    }

    private String getUserId() throws Exception {
        UserService us = UserServiceImpl.instance();
        return us.PostUser();
    }

    private Item prepareItem(String ownerId, String fileName, String itemType) throws Exception {

        ItemService itemService = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        Item item = itemService.prepareItemEntity(fileName);
        item.setOwnerId(IdConverter.hexStringToId(UserId.class, ownerId));
        String itemId = itemService.postItem(item);

        //Attach item revision to the item
        ItemRevision itemRevision;
        if (itemType.equalsIgnoreCase(EnumHelper.CatalogItemType.PHYSICAL.getItemType())) {
            itemRevision = itemRevisionService.prepareItemRevisionEntity(defaultPhysicalItemRevisionFileName);
        }
        else {
            itemRevision = itemRevisionService.prepareItemRevisionEntity(defaultDigitalItemRevisionFileName);
        }

        itemRevision.setItemId(IdConverter.hexStringToId(ItemId.class, itemId));
        itemRevision.setOwnerId(item.getOwnerId());

        //set locales
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = new ItemRevisionLocaleProperties();
        itemRevisionLocaleProperties.setName(fileName);
        HashMap<String, ItemRevisionLocaleProperties> locales = new HashMap<>();
        locales.put("en_US", itemRevisionLocaleProperties);
        itemRevision.setLocales(locales);

        String itemRevisionId = itemRevisionService.postItemRevision(itemRevision);

        //Approve the item revision
        itemRevision = Master.getInstance().getItemRevision(itemRevisionId);
        itemRevision.setStatus(EnumHelper.CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevision);

        String itemGetId = itemService.getItem(itemId);
        return Master.getInstance().getItem(itemGetId);
    }

}
