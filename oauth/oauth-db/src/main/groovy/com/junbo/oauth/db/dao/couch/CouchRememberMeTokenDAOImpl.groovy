/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.RememberMeTokenDAO
import com.junbo.oauth.db.entity.RememberMeTokenEntity
import groovy.transform.CompileStatic

/**
 * CouchRememberMeTokenDAOImpl.
 */
@CompileStatic
class CouchRememberMeTokenDAOImpl extends CouchBaseDAO<RememberMeTokenEntity> implements RememberMeTokenDAO {
}
