/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog.impl;

import com.junbo.catalog.spec.model.common.LocalizableProperty;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.catalog.ItemRevisionService;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.catalog.OfferRevisionService;
import com.junbo.test.common.apihelper.catalog.OfferService;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.common.model.Results;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * Time: 4/17/2014
 * The implementation for Offer Revision related APIs
 */
public class OfferRevisionServiceImpl extends HttpClientBase implements OfferRevisionService {

    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "offer-revisions";
    private final String defaultDigitalOfferRevisionFileName = "defaultDigitalOfferRevision";
    private final String defaultPhysicalOfferRevisionFileName = "defaultPhysicalOfferRevision";
    private final String defaultItemRevisionFileName = "defaultItemRevision";
    private final String defaultOfferFileName = "defaultOffer";
    private LogHelper logger = new LogHelper(OfferRevisionServiceImpl.class);
    private static OfferRevisionService instance;

    public static synchronized OfferRevisionService instance() {
        if (instance == null) {
            instance = new OfferRevisionServiceImpl();
        }
        return instance;
    }

    private OfferRevisionServiceImpl() {
    }

    public String getOfferRevision(String offerRevisionId) throws Exception {
        return getOfferRevision(offerRevisionId, 200);
    }

    public String getOfferRevision(String offerRevisionId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + offerRevisionId;
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        OfferRevision offerRevisionGet = new JsonMessageTranscoder().decode(new TypeReference<OfferRevision>() {},
                responseBody);
        String offerRevisionRtnId = IdConverter.idLongToHexString(OfferRevisionId.class,
                offerRevisionGet.getRevisionId());
        Master.getInstance().addOfferRevision(offerRevisionRtnId, offerRevisionGet);

        return offerRevisionRtnId;
    }

    public List<String> getOfferRevisions(HashMap<String, String> httpPara) throws Exception {
        return getOfferRevisions(httpPara, 200);
    }

    public List<String> getOfferRevisions(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<OfferRevision> offerRevisionGet = new JsonMessageTranscoder().decode(
                new TypeReference<Results<OfferRevision>>() {}, responseBody);
        List<String> listOfferRevisionId = new ArrayList<>();
        for (OfferRevision offerRevision : offerRevisionGet.getItems()){
            String offerRevisionRtnId = IdConverter.idLongToHexString(OfferRevisionId.class,
                    offerRevision.getRevisionId());
            Master.getInstance().addOfferRevision(offerRevisionRtnId, offerRevision);
            listOfferRevisionId.add(offerRevisionRtnId);
        }

        return listOfferRevisionId;
    }

    public String postOfferRevision(OfferRevision offerRevision) throws Exception {
        return postOfferRevision(offerRevision, 200);
    }

    public String postOfferRevision(OfferRevision offerRevision, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, offerRevision, expectedResponseCode);
        OfferRevision offerRevisionPost = new JsonMessageTranscoder().decode(
                new TypeReference<OfferRevision>() {}, responseBody);
        String offerRevisionRtnId = IdConverter.idLongToHexString(OfferRevisionId.class,
                offerRevisionPost.getRevisionId());
        Master.getInstance().addOfferRevision(offerRevisionRtnId, offerRevisionPost);

        return offerRevisionRtnId;
    }

    public String updateOfferRevision(OfferRevision offerRevision) throws Exception {
        return updateOfferRevision(offerRevision, 200);
    }

    public String updateOfferRevision(OfferRevision offerRevision, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferRevisionId.class,
                offerRevision.getRevisionId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, offerRevision, expectedResponseCode);
        OfferRevision offerRevisionPut = new JsonMessageTranscoder().decode(
                new TypeReference<OfferRevision>() {}, responseBody);
        String offerRevisionRtnId = IdConverter.idLongToHexString(OfferRevisionId.class,
                offerRevisionPut.getOfferId());
        Master.getInstance().addOfferRevision(offerRevisionRtnId, offerRevisionPut);

        return offerRevisionRtnId;
    }

    public String postDefaultOfferRevision(EnumHelper.CatalogItemType itemType) throws Exception {
        OfferRevision offerRevisionForPost;
        if (itemType.equals(EnumHelper.CatalogItemType.PHYSICAL)) {
            offerRevisionForPost = prepareOfferRevisionEntity(defaultPhysicalOfferRevisionFileName, itemType);
        }
        else {
            offerRevisionForPost = prepareOfferRevisionEntity(defaultDigitalOfferRevisionFileName, itemType);
        }

        return postOfferRevision(offerRevisionForPost);
    }

    public OfferRevision prepareOfferRevisionEntity(String fileName, EnumHelper.CatalogItemType itemType)
            throws Exception {

        String strOfferRevisionContent = readFileContent(String.format("testOfferRevisions/%s.json", fileName));
        OfferRevision offerRevisionForPost = new JsonMessageTranscoder().decode(
                new TypeReference<OfferRevision>() {}, strOfferRevisionContent);

        //Set random name
        LocalizableProperty offerRevisionName = new LocalizableProperty();
        String value = "testOfferRevision_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10);
        offerRevisionName.set("DEFAULT", value);
        offerRevisionName.set("en_US", value);
        offerRevisionForPost.setName(offerRevisionName);

        //Add item related info
        Item itemPrepared = prepareItem(itemType);
        ItemEntry itemEntry = new ItemEntry();
        List<ItemEntry> itemEntities = new ArrayList<>();
        itemEntry.setItemId(itemPrepared.getItemId());
        itemEntry.setQuantity(1);
        itemEntities.add(itemEntry);
        offerRevisionForPost.setItems(itemEntities);
        offerRevisionForPost.setOwnerId(itemPrepared.getOwnerId());

        //Add offer related info
        String offerId = getOffer(itemType, itemPrepared.getOwnerId());
        offerRevisionForPost.setOfferId(IdConverter.hexStringToId(OfferId.class, offerId));

        return offerRevisionForPost;
    }

    private Item prepareItem(EnumHelper.CatalogItemType itemType) throws Exception {

        ItemService itemService = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        String itemId = itemService.postDefaultItem(itemType);
        Item item = Master.getInstance().getItem(itemId);

        //Attach item revision to the item
        ItemRevision itemRevision = itemRevisionService.prepareItemRevisionEntity(
                defaultItemRevisionFileName, itemType);
        itemRevision.setItemId(IdConverter.hexStringToId(ItemId.class, itemId));
        itemRevision.setType(itemType.getItemType());
        itemRevision.setOwnerId(item.getOwnerId());
        String itemRevisionId = itemRevisionService.postItemRevision(itemRevision);

        //Approve the item revision
        itemRevision = Master.getInstance().getItemRevision(itemRevisionId);
        itemRevision.setStatus(EnumHelper.CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevision);

        String itemGetId = itemService.getItem(itemId);
        return Master.getInstance().getItem(itemGetId);
    }

    private String getOffer(EnumHelper.CatalogItemType itemType, Long ownerId) throws Exception {
        OfferService offerService = OfferServiceImpl.instance();
        Offer offer = offerService.prepareOfferEntity(defaultOfferFileName, itemType);
        offer.setOwnerId(ownerId);
        return offerService.postOffer(offer);
    }

}
