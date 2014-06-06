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
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Item repository.
 */
public class ItemAttributeRepositoryImpl extends CloudantClient<ItemAttribute> implements ItemAttributeRepository {
    private IdGenerator idGenerator;

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public ItemAttribute create(ItemAttribute attribute) {
        if (attribute.getId() == null) {
            attribute.setId(idGenerator.nextId());
        }
        return super.cloudantPost(attribute);
    }

    public ItemAttribute get(Long attributeId) {
        return super.cloudantGet(attributeId.toString());
    }

    public List<ItemAttribute> getAttributes(ItemAttributesGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getAttributeIds())) {
            List<ItemAttribute> attributes = new ArrayList<>();
            for (ItemAttributeId attributeId : options.getAttributeIds()) {
                ItemAttribute attribute = super.cloudantGet(attributeId.getValue().toString());
                if (attribute != null) {
                    attributes.add(attribute);
                }
            }
            return attributes;
        } else if (!StringUtils.isEmpty(options.getAttributeType())){
            return super.queryView("by_type", options.getAttributeType(),
                    options.getValidSize(), options.getValidStart(), false);
        } else {
            return super.queryView("by_attributeId", null, options.getValidSize(), options.getValidStart(), false);
        }
    }

    public ItemAttribute update(ItemAttribute attribute) {
        return super.cloudantPut(attribute);
    }

    public void delete(Long attributeId) {
        super.cloudantDelete(attributeId.toString());
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
