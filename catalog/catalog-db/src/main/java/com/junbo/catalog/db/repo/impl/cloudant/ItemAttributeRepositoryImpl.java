/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.db.repo.ItemAttributeRepository;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.common.id.ItemAttributeId;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Item repository.
 */
public class ItemAttributeRepositoryImpl extends CloudantClient<ItemAttribute> implements ItemAttributeRepository {

    public ItemAttribute create(ItemAttribute attribute) {
        return cloudantPost(attribute).get();
    }

    public ItemAttribute get(String attributeId) {
        if (attributeId == null) {
            return null;
        }
        return cloudantGet(attributeId).get();
    }

    public List<ItemAttribute> getAttributes(ItemAttributesGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getAttributeIds())) {
            List<ItemAttribute> attributes = new ArrayList<>();
            for (ItemAttributeId attributeId : options.getAttributeIds()) {
                ItemAttribute attribute = cloudantGet(attributeId.getValue()).get();
                if (attribute != null) {
                    attributes.add(attribute);
                }
            }
            return attributes;
        } else if (!StringUtils.isEmpty(options.getAttributeType())){
            return queryView("by_type", options.getAttributeType(),
                    options.getValidSize(), options.getValidStart(), false).get();
        } else {
            return queryView("by_attributeId", null, options.getValidSize(), options.getValidStart(), false).get();
        }
    }

    public ItemAttribute update(ItemAttribute attribute) {
        return cloudantPut(attribute).get();
    }

    public void delete(String attributeId) {
        cloudantDelete(attributeId).get();
    }

    private CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantViews.CloudantView> viewMap = new HashMap<>();

        CloudantViews.CloudantView view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {emit(doc.type, doc._id)}");
        view.setResultClass(String.class);
        viewMap.put("by_type", view);

        view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {emit(doc._id, doc._id)}");
        view.setResultClass(String.class);
        viewMap.put("by_attributeId", view);

        setViews(viewMap);
    }};

    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews;
    }
}
