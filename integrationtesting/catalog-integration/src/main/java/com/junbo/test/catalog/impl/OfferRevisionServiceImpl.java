/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.catalog.spec.model.offer.*;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.enums.EventActionType;
import com.junbo.test.catalog.enums.EventType;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.common.blueprint.Master;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.test.catalog.ItemService;
import com.junbo.common.model.Results;

import com.junbo.test.common.libs.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jason
 * Time: 4/17/2014
 * The implementation for Offer Revision related APIs
 */
public class OfferRevisionServiceImpl extends HttpClientBase implements OfferRevisionService {

    private final String catalogServerURL = ConfigHelper.getSetting("defaultCatalogEndpointV1") + "offer-revisions";
    private final String defaultStoredValueItemRevisionFileName = "defaultStoredValueItemRevision";
    private final String defaultPhysicalItemRevisionFileName = "defaultPhysicalItemRevision";
    private final String defaultDigitalItemRevisionFileName = "defaultDigitalItemRevision";
    private final String defaultOfferRevisionFileName = "defaultOfferRevision";
    private final String defaultStoredValueOfferRevisionFileName = "defaultStoredValueOfferRevision";

    private LogHelper logger = new LogHelper(OfferRevisionServiceImpl.class);
    private static OfferRevisionService instance;

    public static synchronized OfferRevisionService instance() {
        if (instance == null) {
            instance = new OfferRevisionServiceImpl();
        }
        return instance;
    }

    private OfferRevisionServiceImpl() {
        componentType = ComponentType.CATALOG;
    }

    public OfferRevision getOfferRevision(String offerRevisionId) throws Exception {
        return getOfferRevision(offerRevisionId, 200);
    }

    public OfferRevision getOfferRevision(String offerRevisionId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idToUrlString(OfferRevisionId.class, offerRevisionId);
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        OfferRevision offerRevisionGet = new JsonMessageTranscoder().decode(new TypeReference<OfferRevision>() {},
                responseBody);
        String offerRevisionRtnId = IdConverter.idToUrlString(OfferRevisionId.class,
                offerRevisionGet.getRevisionId());
        Master.getInstance().addOfferRevision(offerRevisionRtnId, offerRevisionGet);
        return offerRevisionGet;
    }

    public Results<OfferRevision> getOfferRevisions(HashMap<String, List<String>> httpPara) throws Exception {
        return getOfferRevisions(httpPara, 200);
    }

    public Results<OfferRevision> getOfferRevisions(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<OfferRevision> offerRevisionGet = new JsonMessageTranscoder().decode(
                new TypeReference<Results<OfferRevision>>() {}, responseBody);
        for (OfferRevision offerRevision : offerRevisionGet.getItems()){
            String offerRevisionRtnId = IdConverter.idToUrlString(OfferRevisionId.class,
                    offerRevision.getRevisionId());
            Master.getInstance().addOfferRevision(offerRevisionRtnId, offerRevision);
        }
        return offerRevisionGet;
    }

    public OfferRevision postOfferRevision(OfferRevision offerRevision) throws Exception {
        return postOfferRevision(offerRevision, 200);
    }

    public OfferRevision postOfferRevision(OfferRevision offerRevision, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, offerRevision, expectedResponseCode);
        OfferRevision offerRevisionPost = new JsonMessageTranscoder().decode(
                new TypeReference<OfferRevision>() {}, responseBody);
        String offerRevisionRtnId = IdConverter.idToUrlString(OfferRevisionId.class,
                offerRevisionPost.getRevisionId());
        Master.getInstance().addOfferRevision(offerRevisionRtnId, offerRevisionPost);
        return offerRevisionPost;
    }

    public OfferRevision updateOfferRevision(String offerRevisionId, OfferRevision offerRevision) throws Exception {
        return updateOfferRevision(offerRevisionId, offerRevision, 200);
    }

    public OfferRevision updateOfferRevision(String offerRevisionId, OfferRevision offerRevision, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idToUrlString(OfferRevisionId.class,
                offerRevisionId);
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, offerRevision, expectedResponseCode);
        OfferRevision offerRevisionPut = new JsonMessageTranscoder().decode(
                new TypeReference<OfferRevision>() {}, responseBody);
        String offerRevisionRtnId = IdConverter.idToUrlString(OfferRevisionId.class,
                offerRevisionPut.getRevisionId());
        Master.getInstance().addOfferRevision(offerRevisionRtnId, offerRevisionPut);
        return offerRevisionPut;
    }

    public OfferRevision postDefaultOfferRevision() throws Exception {
        Offer offer = OfferServiceImpl.instance().postDefaultOffer();
        return postDefaultOfferRevision(offer);
    }

    public OfferRevision postDefaultOfferRevision(CatalogItemType itemType) throws Exception {
        Offer offer = OfferServiceImpl.instance().postDefaultOffer();
        return postDefaultOfferRevision(offer, itemType);
    }

    public OfferRevision postDefaultOfferRevision(Offer offer) throws Exception {
        Item item = prepareItem(CatalogItemType.getRandom(), offer.getOwnerId());
        return postDefaultOfferRevision(offer, item);
    }

    public OfferRevision postDefaultOfferRevision(Offer offer, Item item) throws Exception {
        if (offer == null) {
            throw new Exception("offer is null");
        }

        if (item == null) {
            throw new Exception("Item is null");
        }

        OfferRevision offerRevisionForPost;

        if (item.getType().equalsIgnoreCase(CatalogItemType.STORED_VALUE.getItemType())) {
            offerRevisionForPost = prepareOfferRevisionEntity(defaultStoredValueOfferRevisionFileName, offer.getOwnerId(), false);
        }
        else {
            offerRevisionForPost = prepareOfferRevisionEntity(defaultOfferRevisionFileName, offer.getOwnerId(), false);
        }

        if (item.getType().equalsIgnoreCase(CatalogItemType.CONSUMABLE_UNLOCK.getItemType())) {
            List<Action> purchaseActions = new ArrayList<>();
            Map<String, List<Action>> consumableEvent = new HashMap<>();
            Action action = new Action();
            action.setType(EventActionType.GRANT_ENTITLEMENT.name());
            action.setItemId(item.getItemId());
            action.setUseCount(10);
            purchaseActions.add(action);
            consumableEvent.put(EventType.PURCHASE.name(), purchaseActions);
            offerRevisionForPost.setEventActions(consumableEvent);
        }

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

    public OfferRevision prepareOfferRevisionEntity(String fileName, OrganizationId organizationId) throws Exception {
        return prepareOfferRevisionEntity(fileName, organizationId, true);
    }

    public OfferRevision prepareOfferRevisionEntity(String fileName, OrganizationId organizationId, Boolean addItemInfo)
            throws Exception {
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
            Item itemPrepared = prepareItem(CatalogItemType.getRandom(), organizationId);
            ItemEntry itemEntry = new ItemEntry();
            List<ItemEntry> itemEntities = new ArrayList<>();
            itemEntry.setItemId(itemPrepared.getItemId());
            itemEntry.setQuantity(1);
            itemEntities.add(itemEntry);
            offerRevisionForPost.setItems(itemEntities);
        }

        return offerRevisionForPost;
    }

    public void deleteOfferRevision(String offerRevisionId) throws Exception {
        deleteOfferRevision(offerRevisionId, 204);
    }

    public void deleteOfferRevision(String offerRevisionId, int expectedResponseCode) throws Exception {
        String strOfferRevisionId = IdConverter.idToUrlString(OfferRevisionId.class, offerRevisionId);
        String url = catalogServerURL + "/" + strOfferRevisionId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
        Master.getInstance().removeOfferRevision(strOfferRevisionId);
    }

    private OfferRevision postDefaultOfferRevision(Offer offer, CatalogItemType itemType) throws Exception {
        Item item = prepareItem(itemType, offer.getOwnerId());
        return postDefaultOfferRevision(offer, item);
    }

    private Item prepareItem(CatalogItemType itemType, OrganizationId organizationId) throws Exception {

        ItemService itemService = ItemServiceImpl.instance();
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        Item item  = itemService.postDefaultItem(itemType, organizationId);

        //Attach item revision to the item
        ItemRevision itemRevision;
        if (itemType.equals(CatalogItemType.APP) || itemType.equals(CatalogItemType.DOWNLOADED_ADDITION)) {
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
        itemRevisionService.updateItemRevision(itemRevisionPost.getRevisionId(), itemRevisionPost);

        return itemService.getItem(item.getItemId());
    }

}
