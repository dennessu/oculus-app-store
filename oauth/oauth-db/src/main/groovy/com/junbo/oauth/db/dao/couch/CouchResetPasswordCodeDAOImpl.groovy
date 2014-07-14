package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.ResetPasswordCodeDAO
import com.junbo.oauth.db.entity.ResetPasswordCodeEntity
import groovy.transform.CompileStatic

/**
 * Created by minhao on 5/2/14.
 */
@CompileStatic
class CouchResetPasswordCodeDAOImpl extends CouchBaseDAO<ResetPasswordCodeEntity> implements ResetPasswordCodeDAO {
}
