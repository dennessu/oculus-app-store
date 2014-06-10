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
import com.junbo.common.cloudant.model.CloudantSearchResult;
import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.common.id.OfferId;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
        return super.cloudantPost(offer).get();
    }

    public Offer get(Long offerId) {
        return super.cloudantGet(offerId.toString()).get();
    }

    public List<Offer> getOffers(OffersGetOptions options) {
        List<Offer> offers = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getOfferIds())) {
            for (OfferId offerId : options.getOfferIds()) {
                Offer offer = super.cloudantGet(offerId.toString()).get();
                if (offer == null) {
                    continue;
                }else if (options.getCategory() != null
                        && (offer.getCategories() == null
                            || !offer.getCategories().contains(options.getCategory().getValue()))) {
                    continue;
                } else if (options.getPublished() != null && !options.getPublished().equals(offer.getPublished())) {
                    continue;
                } else if (options.getOwnerId() != null
                        && !options.getOwnerId().equals(offer.getOwnerId())) {
                    continue;
                } else {
                    offers.add(offer);
                }
            }
        } else if (!StringUtils.isEmpty(options.getQuery())) {
            CloudantSearchResult<Offer> searchResult =
                    super.search("search", options.getQuery(), options.getValidSize(), options.getBookmark()).get();
            offers = searchResult.getResults();
            options.setNextBookmark(searchResult.getBookmark());
            options.setStart(null);
        } else if (options.getCategory() != null || options.getPublished() != null || options.getOwnerId() != null) {
            StringBuilder sb = new StringBuilder();
            if (options.getCategory() != null) {
                sb.append("categoryId:'").append(options.getCategory().getValue()).append("'");
            }
            if (options.getPublished() != null) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("published:").append(options.getPublished());
            }
            if (options.getOwnerId() != null) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("ownerId:'").append(options.getOwnerId().getValue()).append("'");
            }
            CloudantSearchResult<Offer> searchResult =
                    super.search("search", sb.toString(), options.getValidSize(), options.getBookmark()).get();
            offers = searchResult.getResults();
            options.setNextBookmark(searchResult.getBookmark());
            options.setStart(null);
        } else {
            offers = super.queryView("by_offerId", null, options.getValidSize(), options.getValidStart(), false).get();
            options.setNextBookmark(null);
        }

        return offers;
    }

    public List<Offer> getOffers(Collection<Long> offerIds) {
        List<Offer> offers = new ArrayList<>();
        for (Long offerId : offerIds) {
            Offer offer = super.cloudantGet(offerId.toString()).get();
            if (offer != null) {
                offers.add(offer);
            }
        }

        return offers;
    }

    @Override
    public Offer update(Offer offer) {
        return super.cloudantPut(offer).get();
    }

    @Override
    public void delete(Long offerId) {
        super.cloudantDelete(offerId.toString()).get();
    }

    private CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();
        Map<String, CloudantIndex> indexMap = new HashMap<>();

        CloudantViews.CloudantView view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {emit(doc.offerId, doc._id)}");
        view.setResultClass(String.class);
        viewMap.put("by_offerId", view);

        CloudantIndex index = new CloudantIndex();
        index.setResultClass(String.class);
        index.setIndex("function(doc) {" +
                "index(\'offerId\', doc.offerId);" +
                "index(\'default\', doc.offerId);" +
                "if (doc.published != null && doc.published != undefined) {" +
                    "index(\'published\', doc.published);" +
                "}" +
                "if (doc.environment) {" +
                    "index(\'environment\', doc.environment);" +
                    "index(\'default\', doc.environment);" +
                "}" +
                "if (doc.categories) {" +
                    "for (var idx in doc.categories) {" +
                        "index(\'categoryId\', doc.categories[idx]);" +
                        "index(\'default\', doc.categories[idx]);" +
                    "}" +
                "}" +
                "index(\'ownerId\', doc.ownerId);" +
                "index(\'default\', doc.ownerId);" +
                "if (!doc.currentRevisionId) {" +
                    "index(\'scheduledPublish\', true);" +
                "}" +
                "if (doc.activeRevision) {" +
                    "index(\'revisionId\', doc.activeRevision.revisionId);" +
                    "index(\'default\', doc.activeRevision.revisionId);" +
                    "if (doc.activeRevision.items) {" +
                        "for (var itemIdx in doc.activeRevision.items) {" +
                            "if (doc.activeRevision.items[itemIdx].itemId) {" +
                                "index(\'itemId\', doc.activeRevision.items[itemIdx].itemId);" +
                            "}" +
                        "}" +
                    "}" +
                    "if (doc.activeRevision.subOffers) {" +
                        "for (var subOfferIdx in doc.activeRevision.subOffers) {" +
                            "if (doc.activeRevision.subOffers[subOfferIdx]) {" +
                                "index(\'subOfferId\', doc.activeRevision.subOffers[subOfferIdx]);" +
                            "}" +
                        "}" +
                    "}" +
                    "if (doc.activeRevision.locales) {" +
                        "for (var localeIdx in doc.activeRevision.locales) {" +
                            "var locale = doc.activeRevision.locales[localeIdx];" +
                            "if (locale.name) {" +
                                "index(\'name\', locale.name);" +
                                "index(\'default\', locale.name);" +
                            "}" +
                            "if (locale.revisionNotes) {" +
                                "index(\'revisionNotes\', locale.revisionNotes);" +
                                "index(\'default\', locale.revisionNotes);" +
                            "}" +
                            "if (locale.longDescription) {" +
                                "index(\'longDescription\', locale.longDescription);" +
                                "index(\'default\', locale.longDescription);" +
                            "}" +
                            "if (locale.shortDescription) {" +
                                "index(\'shortDescription\', locale.shortDescription);" +
                                "index(\'default\', locale.shortDescription);" +
                            "}" +
                        "}" +
                    "}" +
                "}" +
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
