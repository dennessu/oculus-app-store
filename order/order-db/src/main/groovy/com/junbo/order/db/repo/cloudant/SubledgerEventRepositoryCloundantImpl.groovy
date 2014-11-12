package com.junbo.order.db.repo.cloudant
import com.junbo.common.id.SubledgerEventId
import com.junbo.common.id.SubledgerId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.SubledgerEventRepository
import com.junbo.order.spec.model.SubledgerEvent
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
/**
 * Created by fzhang on 2015/1/18.
 */
@CompileStatic
@TypeChecked
class SubledgerEventRepositoryCloundantImpl extends BaseCloudantRepositoryForDualWrite<SubledgerEvent, SubledgerEventId> implements SubledgerEventRepository {

    @Override
    Promise<List<SubledgerEvent>> getBySubledgerId(SubledgerId subledgerId) {
        return super.queryView('by_subledger', subledgerId.toString(), null, null, false);
    }

}
