/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.entity;

import com.junbo.common.hibernate.StringJsonUserType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ItemAttributeEntity.
 */
@Entity
@Table(name="item_attribute")
@TypeDefs(@TypeDef(name="json-string", typeClass=StringJsonUserType.class))
public class ItemAttributeEntity extends AttributeEntity {
}
