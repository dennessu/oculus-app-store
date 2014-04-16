/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.dao.EntitlementTypeDao;
import com.junbo.catalog.db.entity.EntitlementTypeEntity;
import com.junbo.catalog.db.mapper.EntitlementDefinitionMapper;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Repository of EntitlementType.
 */
public class EntitlementTypeRepository {
    private List<EntitlementTypeEntity> data;
    @Autowired
    private EntitlementTypeDao entitlementTypeDao;

    public EntitlementType getByName(String name) {
        if (data == null) {
            loadData();
        }
        for (EntitlementTypeEntity type : data) {
            if (type.getName().equalsIgnoreCase(name)) {
                return EntitlementDefinitionMapper.toEntitlementType(type);
            }
        }
        return null;
    }

    private void loadData() {
        data = entitlementTypeDao.getAll();
    }
}
