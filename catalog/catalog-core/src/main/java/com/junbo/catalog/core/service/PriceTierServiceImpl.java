/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.PriceTierService;
import com.junbo.catalog.db.repo.PriceTierRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.catalog.spec.model.pricetier.PriceTiersGetOptions;
import com.junbo.common.error.AppError;
import com.junbo.common.error.AppErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Price tier service implementation.
 */
public class PriceTierServiceImpl implements PriceTierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PriceTierServiceImpl.class);
    private PriceTierRepository priceTierRepo;

    @Required
    public void setPriceTierRepo(PriceTierRepository priceTierRepo) {
        this.priceTierRepo = priceTierRepo;
    }

    @Override
    public PriceTier getPriceTier(String tierId) {
        PriceTier priceTier = priceTierRepo.get(tierId);
        if (priceTier==null) {
            AppErrorException exception = AppErrors.INSTANCE.notFound("price-tiers", tierId).exception();
            LOGGER.error("price-tier not found.", exception);
            throw exception;
        }
        return priceTier;
    }

    @Override
    public List<PriceTier> getPriceTiers(PriceTiersGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getPriceTierIds())) {
            List<PriceTier> priceTiers = new ArrayList<>();

            for (String tierId : options.getPriceTierIds()) {
                PriceTier priceTier = priceTierRepo.get(tierId);
                LOGGER.warn("price-tier not found: " + tierId);
                if (priceTier != null) {
                    priceTiers.add(priceTier);
                }
            }
            return priceTiers;
        } else {
            return priceTierRepo.getPriceTiers(options.getValidStart(), options.getValidSize());
        }
    }

    @Override
    public PriceTier create(PriceTier priceTier) {
        validateCreation(priceTier);
        return priceTierRepo.create(priceTier);
    }

    @Override
    public PriceTier update(String tierId, PriceTier priceTier) {
        validateUpdate(priceTier);
        return priceTierRepo.update(priceTier);
    }

    @Override
    public void delete(String tierId) {
        PriceTier priceTier = priceTierRepo.get(tierId);
        if (priceTier == null) {
            AppErrorException exception = AppErrors.INSTANCE.notFound("price-tiers", tierId).exception();
            LOGGER.error("price-tier not found.", exception);
            throw exception;
        }
        priceTierRepo.delete(tierId);
    }

    private void validateCreation(PriceTier priceTier) {
        checkRequestNotNull(priceTier);
        List<AppError> errors = new ArrayList<>();
        if (priceTier.getRev() != null) {
            errors.add(AppErrors.INSTANCE.unnecessaryField("rev"));
        }

        validateCommon(priceTier, errors);
        if (!errors.isEmpty()) {
            AppErrorException exception = AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
            LOGGER.error("Error creating price-tier.", exception);
            throw exception;
        }
    }

    private void validateUpdate(PriceTier priceTier) {
        checkRequestNotNull(priceTier);
        List<AppError> errors = new ArrayList<>();
        if (priceTier.getRev() == null) {
            errors.add(AppErrors.INSTANCE.missingField("rev"));
        }
        validateCommon(priceTier, errors);
        if (!errors.isEmpty()) {
            AppErrorException exception = AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
            LOGGER.error("Error updating price-tier.", exception);
            throw exception;
        }
    }

    private void validateCommon(PriceTier priceTier, List<AppError> errors) {
        if (CollectionUtils.isEmpty(priceTier.getPrices())) {
            errors.add(AppErrors.INSTANCE.missingField("prices"));
        } else {
            for (String country : priceTier.getPrices().keySet()) {
                if (priceTier.getPrices().get(country) == null) {
                    errors.add(AppErrors.INSTANCE
                            .fieldNotCorrect("prices." + country, "Prices should be configured"));
                } else {
                    for (String currency : priceTier.getPrices().get(country).keySet()) {
                        BigDecimal amount = priceTier.getPrices().get(country).get(currency);
                        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                            errors.add(AppErrors.INSTANCE
                                    .fieldNotCorrect("prices." + country + "." + currency,
                                            "Price amount should greater than 0"));
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(priceTier.getLocales())) {
            errors.add(AppErrors.INSTANCE.missingField("locales"));
        } else {
            for (String locale : priceTier.getLocales().keySet()) {
                if (priceTier.getLocales().get(locale) == null) {
                    errors.add(AppErrors.INSTANCE.missingField("locales." + locale));
                } else {
                    SimpleLocaleProperties properties = priceTier.getLocales().get(locale);
                    if (StringUtils.isEmpty(properties.getName())) {
                        errors.add(AppErrors.INSTANCE.missingField("locales." + locale + ".name"));
                    }
                }
            }
        }
    }

    private void checkRequestNotNull(PriceTier priceTier) {
        if (priceTier == null) {
            AppErrorException exception = AppErrors.INSTANCE.invalidJson("Invalid json.").exception();
            LOGGER.error("Invalid json.", exception);
            throw exception;
        }
    }
}
