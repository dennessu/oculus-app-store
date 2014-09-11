/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.utility;

import com.junbo.test.catalog.*;
import com.junbo.test.catalog.impl.*;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;

/**
 * The ServiceContainer class.
 */
public class ServiceContainer {

    private OfferService offerClient;
    private OfferRevisionService offerRevisionClient;
    private ItemService itemClient;
    private ItemRevisionService itemRevisionClient;
    private OfferAttributeService offerAttributeService;
    private ItemAttributeService itemAttributeService;
    private OrganizationService organizationService;

    private ServiceContainer() {
    }

    private static ServiceContainer innerInstance;

    public static ServiceContainer instance() {
        if (innerInstance != null) {
            return innerInstance;
        }

        innerInstance = new ServiceContainer();
        innerInstance.offerClient = OfferServiceImpl.instance();
        innerInstance.offerRevisionClient = OfferRevisionServiceImpl.instance();
        innerInstance.itemClient = ItemServiceImpl.instance();
        innerInstance.itemRevisionClient = ItemRevisionServiceImpl.instance();
        innerInstance.offerAttributeService = OfferAttributeServiceImpl.instance();
        innerInstance.itemAttributeService = ItemAttributeServiceImpl.instance();
        innerInstance.organizationService = OrganizationServiceImpl.instance();
        return innerInstance;
    }

    public OfferService getOfferClient() {
        return offerClient;
    }

    public OfferRevisionService getOfferRevisionClient() {
        return offerRevisionClient;
    }

    public ItemService getItemClient() {
        return itemClient;
    }

    public ItemRevisionService getItemRevisionClient() {
        return itemRevisionClient;
    }

    public OfferAttributeService getOfferAttributeService() {
        return offerAttributeService;
    }

    public ItemAttributeService getItemAttributeService() {
        return itemAttributeService;
    }

    public OrganizationService getOrganizationService() {
        return organizationService;
    }
}
