/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Offer repository.
 */
public class OfferRepositoryImpl extends CloudantClient<Offer> implements OfferRepository {
    private IdGenerator idGenerator;

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Offer create(Offer offer) {
        if (offer.getOfferId() == null) {
            offer.setOfferId(idGenerator.nextId());
        }
        return super.cloudantPost(offer);
    }

    public Offer get(Long offerId) {
        return super.cloudantGet(offerId.toString());
    }

    public List<Offer> getOffers(OffersGetOptions options) {
        List<Offer> offers = new ArrayList<>();
        // TODO: search

        return offers;
    }

    public List<Offer> getOffers(Collection<Long> offerIds) {
        List<Offer> offers = new ArrayList<>();
        for (Long offerId : offerIds) {
            Offer offer = super.cloudantGet(offerId.toString());
            if (offer != null) {
                offers.add(offer);
            }
        }

        return offers;
    }

    @Override
    public Offer update(Offer offer) {
        return super.cloudantPut(offer);
    }

    @Override
    public void delete(Long offerId) {
        super.cloudantDelete(offerId.toString());
    }

    private CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();
        Map<String, CloudantIndex> indexMap = new HashMap<>();

        CloudantViews.CloudantView view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {emit(doc.type, doc._id)}");
        view.setResultClass(String.class);
        viewMap.put("by_type", view);

        view = new CloudantViews.CloudantView();
        view.setMap(
                "function(doc) {" +
                    "if (doc.genres) {" +
                        "for (var idx in doc.genres) {" +
                            "emit(doc.genres[idx], doc._id);" +
                        "}" +
                    "}" +
                "}");
        view.setResultClass(String.class);
        viewMap.put("by_genreId", view);

        CloudantIndex index = new CloudantIndex();
        index.setResultClass(String.class);
        index.setIndex("function(doc) {" +
                "index(\'type\', doc.type);" +
                "index(\'genreId\', doc.genres);" +
                "index(\'itemId\', doc.itemId);" +
            "}");
        indexMap.put("search", index);

        setIndexes(indexMap);
        setViews(viewMap);
    }};

    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews;
    }
}
