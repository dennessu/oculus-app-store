package com.junbo.identity.service

import com.junbo.common.id.UsernameMailBlockerId
import com.junbo.identity.spec.v1.model.migration.UsernameMailBlocker
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
public interface UsernameEmailBlockerService {
    Promise<UsernameMailBlocker> get(UsernameMailBlockerId id)

    Promise<UsernameMailBlocker> create(UsernameMailBlocker model)

    Promise<UsernameMailBlocker> update(UsernameMailBlocker model, UsernameMailBlocker oldModel)

    Promise<Void> delete(UsernameMailBlockerId id)

    Promise<List<UsernameMailBlocker>> searchByUsername(String username, Integer limit, Integer offset)

    Promise<List<UsernameMailBlocker>> searchByEmail(String email, Integer limit, Integer offset)
}