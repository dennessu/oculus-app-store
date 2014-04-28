/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.entity;

import com.junbo.catalog.db.dao.StringJsonUserType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * OfferAttributeEntity.
 */
@Entity
@Table(name="offer_attribute")
@TypeDefs(@TypeDef(name="json-string", typeClass=StringJsonUserType.class))
public class OfferAttributeEntity extends AttributeEntity {
}
