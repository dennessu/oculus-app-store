/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.PriceTierService;
import com.junbo.catalog.db.repo.PriceTierRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.catalog.spec.model.pricetier.PriceTiersGetOptions;
import com.junbo.common.error.AppError;
import com.junbo.common.id.PriceTierId;
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
    private PriceTierRepository priceTierRepo;

    @Required
    public void setPriceTierRepo(PriceTierRepository priceTierRepo) {
        this.priceTierRepo = priceTierRepo;
    }

    @Override
    public PriceTier getPriceTier(Long tierId) {
        return priceTierRepo.get(tierId);
    }

    @Override
    public List<PriceTier> getPriceTiers(PriceTiersGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getPriceTierIds())) {
            List<PriceTier> priceTiers = new ArrayList<>();

            for (PriceTierId tierId : options.getPriceTierIds()) {
                PriceTier priceTier = priceTierRepo.get(tierId.getValue());

                if (priceTier != null) {
                    priceTiers.add(priceTier);
                }
            }
            return priceTiers;
        } else {
            options.ensurePagingValid();
            return priceTierRepo.getPriceTiers(options.getStart(), options.getSize());
        }
    }

    @Override
    public PriceTier create(PriceTier priceTier) {
        if (priceTier.getResourceAge() != null) {
            throw AppErrors.INSTANCE.validation("rev must be null at creation.").exception();
        }
        Long attributeId = priceTierRepo.create(priceTier);
        return priceTierRepo.get(attributeId);
    }

    @Override
    public PriceTier update(Long tierId, PriceTier priceTier) {
        priceTierRepo.update(priceTier);
        return priceTierRepo.get(tierId);
    }

    @Override
    public void delete(Long tierId) {
        PriceTier priceTier = priceTierRepo.get(tierId);
        if (priceTier==null) {
            throw AppErrors.INSTANCE.notFound("price-tier", Utils.encodeId(tierId)).exception();
        }
        priceTierRepo.delete(tierId);
    }

    private void validateCreation(PriceTier priceTier) {
        checkRequestNotNull(priceTier);
        List<AppError> errors = new ArrayList<>();
        if (priceTier.getResourceAge() != null) {
            errors.add(AppErrors.INSTANCE.unnecessaryField("rev"));
        }

        validateCommon(priceTier, errors);
        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateUpdate(PriceTier priceTier, PriceTier oldPriceTier) {
        checkRequestNotNull(priceTier);
        List<AppError> errors = new ArrayList<>();
        if (!oldPriceTier.getId().equals(priceTier.getId())) {
            errors.add(AppErrors.INSTANCE.fieldNotMatch("self.id", priceTier.getId(), oldPriceTier.getId()));
        }
        if (!oldPriceTier.getResourceAge().equals(priceTier.getResourceAge())) {
            errors.add(AppErrors.INSTANCE
                    .fieldNotMatch("rev", priceTier.getResourceAge(), oldPriceTier.getResourceAge()));
        }

        validateCommon(priceTier, errors);
        if (!errors.isEmpty()) {
            throw AppErrors.INSTANCE.validation(errors.toArray(new AppError[0])).exception();
        }
    }

    private void validateCommon(PriceTier priceTier, List<AppError> errors) {
        if (CollectionUtils.isEmpty(priceTier.getPrices())) {
            errors.add(AppErrors.INSTANCE.missingField("prices"));
        } else {
            for (String currency : priceTier.getPrices().keySet()) {
                if (priceTier.getPrices().get(currency) == null
                        || priceTier.getPrices().get(currency).compareTo(BigDecimal.ZERO) <= 0) {
                    errors.add(AppErrors.INSTANCE
                            .fieldNotCorrect("prices." + currency, "Price amount should greater than 0"));
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
            throw AppErrors.INSTANCE.invalidJson("Invalid json.").exception();
        }
    }
}
