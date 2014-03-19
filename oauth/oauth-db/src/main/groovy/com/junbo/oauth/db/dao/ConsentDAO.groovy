/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao

import com.junbo.oauth.db.entity.ConsentEntity
import groovy.transform.CompileStatic

/**
 * ConsentDAO.
 */
@CompileStatic
interface ConsentDAO extends BaseDAO<ConsentEntity, String> {
}
