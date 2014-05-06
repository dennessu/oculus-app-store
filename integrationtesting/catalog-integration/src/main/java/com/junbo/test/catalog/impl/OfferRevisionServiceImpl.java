/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.common.model.Results;

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
    private final String defaultStoredValueItemRevisionFileName = "defaultStoredValueItemRevision";
    private final String defaultPhysicalItemRevisionFileName = "defaultPhysicalItemRevision";
    private final String defaultDigitalItemRevisionFileName = "defaultDigitalItemRevision";
    private final String defaultOfferRevisionFileName = "defaultOfferRevision";

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

    public OfferRevision getOfferRevision(Long offerRevisionId) throws Exception {
        return getOfferRevision(offerRevisionId, 200);
    }

    public OfferRevision getOfferRevision(Long offerRevisionId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferRevisionId.class, offerRevisionId);
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<OfferRevision>() {}, responseBody);
    }

    public Results<OfferRevision> getOfferRevisions(HashMap<String, List<String>> httpPara) throws Exception {
        return getOfferRevisions(httpPara, 200);
    }

    public Results<OfferRevision> getOfferRevisions(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<OfferRevision>>() {}, responseBody);
    }

    public OfferRevision postOfferRevision(OfferRevision offerRevision) throws Exception {
        return postOfferRevision(offerRevision, 200);
    }

    public OfferRevision postOfferRevision(OfferRevision offerRevision, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, offerRevision, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<OfferRevision>() {}, responseBody);
    }

    public OfferRevision updateOfferRevision(OfferRevision offerRevision) throws Exception {
        return updateOfferRevision(offerRevision, 200);
    }

    public OfferRevision updateOfferRevision(OfferRevision offerRevision, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferRevisionId.class,
                offerRevision.getRevisionId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, offerRevision, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<OfferRevision>() {}, responseBody);
    }

    public OfferRevision postDefaultOfferRevision() throws Exception {
        Offer offer = OfferServiceImpl.instance().postDefaultOffer();
        return postDefaultOfferRevision(offer);
    }

    public OfferRevision postDefaultOfferRevision(Offer offer) throws Exception {
        Item item = prepareItem(CatalogItemType.getRandom());
        return postDefaultOfferRevision(offer, item);
    }

    public OfferRevision postDefaultOfferRevision(Offer offer, Item item) throws Exception {
        if (offer == null) {
            throw new Exception("offer is null");
        }

        if (item == null) {
            throw new Exception("Item is null");
        }

        OfferRevision offerRevisionForPost = prepareOfferRevisionEntity(defaultOfferRevisionFileName, false);
        offerRevisionForPost.setOfferId(offer.getOfferId());
        offerRevisionForPost.setOwnerId(offer.getOwnerId());

        //add Item info
        ItemEntry itemEntry = new ItemEntry();
        List<ItemEntry> itemEntities = new ArrayList<>();
        itemEntry.setItemId(item.getItemId());
        itemEntry.setQuantity(1);
        itemEntities.add(itemEntry);
        offerRevisionForPost.setItems(itemEntities);

        return postOfferRevision(offerRevisionForPost);
    }

    public OfferRevision prepareOfferRevisionEntity(String fileName) throws Exception {
        return prepareOfferRevisionEntity(fileName, true);
    }

    public OfferRevision prepareOfferRevisionEntity(String fileName, Boolean addItemInfo) throws Exception {
        String strOfferRevisionContent = readFileContent(String.format("testOfferRevisions/%s.json", fileName));
        OfferRevision offerRevisionForPost = new JsonMessageTranscoder().decode(
                new TypeReference<OfferRevision>() {}, strOfferRevisionContent);

        //set locales
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = new OfferRevisionLocaleProperties();
        offerRevisionLocaleProperties.setName("testOfferRevision_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        HashMap<String, OfferRevisionLocaleProperties> locales = new HashMap<>();
        locales.put("en_US", offerRevisionLocaleProperties);
        offerRevisionForPost.setLocales(locales);

        //Add item related info
        if (addItemInfo) {
            Item itemPrepared = prepareItem(CatalogItemType.getRandom());
            ItemEntry itemEntry = new ItemEntry();
            List<ItemEntry> itemEntities = new ArrayList<>();
            itemEntry.setItemId(itemPrepared.getItemId());
            itemEntry.setQuantity(1);
            itemEntities.add(itemEntry);
            offerRevisionForPost.setItems(itemEntities);
        }

        return offerRevisionForPost;
    }

    private Item prepareItem(CatalogItemType itemType) throws Exception {

        ItemService itemService = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        Item item  = itemService.postDefaultItem(itemType);

        //Attach item revision to the item
        ItemRevision itemRevision;
        if (itemType.equals(CatalogItemType.DIGITAL)) {
            itemRevision = itemRevisionService.prepareItemRevisionEntity(defaultDigitalItemRevisionFileName);
        }
        else if (itemType.equals(CatalogItemType.STORED_VALUE)) {
            itemRevision = itemRevisionService.prepareItemRevisionEntity(defaultStoredValueItemRevisionFileName);
        }
        else {
            itemRevision = itemRevisionService.prepareItemRevisionEntity(defaultPhysicalItemRevisionFileName);
        }

        itemRevision.setItemId(item.getItemId());
        itemRevision.setOwnerId(item.getOwnerId());
        ItemRevision itemRevisionPost = itemRevisionService.postItemRevision(itemRevision);

        //Approve the item revision
        itemRevisionPost.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevisionPost);

        return itemService.getItem(item.getItemId());
    }

    public void deleteOfferRevision(Long offerRevisionId) throws Exception {
        deleteOfferRevision(offerRevisionId, 204);
    }

    public void deleteOfferRevision(Long offerRevisionId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferRevisionId.class, offerRevisionId);
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
    }

}