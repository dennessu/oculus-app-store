/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.db.repo.OfferAttributeRepository;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.common.id.OfferAttributeId;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Offer repository.
 */
public class OfferAttributeRepositoryImpl  extends CloudantClient<OfferAttribute> implements OfferAttributeRepository {
    private IdGenerator idGenerator;

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public OfferAttribute create(OfferAttribute attribute) {
        if (attribute.getId() == null) {
            attribute.setId(idGenerator.nextId());
        }
        return super.cloudantPost(attribute);
    }

    public OfferAttribute get(Long attributeId) {
        return super.cloudantGet(attributeId.toString());
    }

    public List<OfferAttribute> getAttributes(OfferAttributesGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getAttributeIds())) {
            List<OfferAttribute> attributes = new ArrayList<>();
            for (OfferAttributeId attributeId : options.getAttributeIds()) {
                OfferAttribute attribute = super.cloudantGet(attributeId.getValue().toString());
                if (attribute != null) {
                    attributes.add(attribute);
                }
            }
            return attributes;
        } else if (!StringUtils.isEmpty(options.getAttributeType())){
            return super.queryView("by_type", options.getAttributeType());
        } else {
            List<OfferAttribute> attributes = super.cloudantGetAll();
            Iterator<OfferAttribute> iterator = attributes.iterator();
            while (iterator.hasNext()) {
                OfferAttribute attribute = iterator.next();
                if (attribute == null || attribute.getId() == null) {
                    iterator.remove();
                }
            }

            return attributes;
        }
    }

    public OfferAttribute update(OfferAttribute attribute) {
        return super.cloudantPut(attribute);
    }


    public void delete(Long attributeId) {
        super.cloudantDelete(attributeId.toString());
    }

    private CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();

        CloudantViews.CloudantView view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {emit(doc.type, doc._id)}");
        view.setResultClass(String.class);
        viewMap.put("by_type", view);

        setViews(viewMap);
    }};

    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews;
    }
}
