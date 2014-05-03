package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.ResetPasswordCodeDAO
import com.junbo.oauth.db.entity.ResetPasswordCodeEntity

/**
 * Created by minhao on 5/2/14.
 */
class CouchResetPasswordCodeDAOImpl extends CouchBaseDAO<ResetPasswordCodeEntity> implements ResetPasswordCodeDAO {
    @Override
    protected CouchViews getCouchViews() {
        return null
    }
}
