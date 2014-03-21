/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service;

import com.junbo.billing.db.repository.CurrencyRepository;
import com.junbo.billing.spec.model.Currency;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xmchen on 14-2-13.
 */
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private EhCacheCacheManager ehCacheCacheManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyServiceImpl.class);

    @Override
    public Currency getCurrencyByName(String name) {

        CacheManager cacheManager = ehCacheCacheManager.getCacheManager();
        Cache cache = null;

        if (cacheManager.getStatus() != Status.STATUS_ALIVE) {
            LOGGER.error("name=Cache_Manager_Invalid. Currency name: " + name);
        } else {
            cache = cacheManager.getCache("CURRENCY");
            if (cache == null) {
                LOGGER.error("name=Currency_Cache_Manager_Not_Found");
            } else {
                Element element = cache.get(name);
                if (element == null) {
                    LOGGER.info("name=Currency_Missing_In_Cache. Currency name: " + name);
                } else {
                    LOGGER.info("name=Currency_Load_From_Cache. Currency name: " + name);
                    return (Currency) element.getObjectValue();
                }
            }
        }

        LOGGER.info("name=Currency_Load_Without_Cache. Currency name: " + name);
        Currency currency = currencyRepository.getCurrency(name);
        if(currency != null) {
            Element newElement = new Element(name, currency);
            if (cache != null) {
                LOGGER.info("name=Currency_Cached. Currency name: " + name);
                cache.put(newElement);
            }
        }

        return currency;
    }

    @Override
    public List<Currency> getCurrencies() {
        List<Currency> currencies = currencyRepository.getCurrencies();

        CacheManager cacheManager = ehCacheCacheManager.getCacheManager();

        if (cacheManager.getStatus() != Status.STATUS_ALIVE) {
            LOGGER.error("name=Cache_Manager_Invalid.");
        } else {
            Cache cache = cacheManager.getCache("CURRENCY");
            if (cache == null) {
                LOGGER.error("name=Currency_Cache_Manager_Not_Found");
            } else {
                List<Element> elements = new ArrayList<>();
                for(Currency currency : currencies) {
                    if(currency != null) {
                        Element newElement = new Element(currency.getName(), currency);
                        elements.add(newElement);
                    }
                }
                LOGGER.info("name=Currency_Cache_Refresh.");
                cache.putAll(elements);
            }
        }

        return currencies;
    }

    @Override
    public Boolean exists(String name) {
        Currency currency = getCurrencyByName(name);
        return currency != null;
    }
}
