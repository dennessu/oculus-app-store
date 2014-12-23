package com.junbo.identity.data.repository.migration

import com.junbo.common.id.UsernameMailBlockerId
import com.junbo.identity.spec.v1.model.migration.UsernameMailBlocker
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/9/14.
 */
@CompileStatic
public interface UsernameEmailBlockerRepository extends BaseRepository<UsernameMailBlocker, UsernameMailBlockerId>  {
    @ReadMethod
    Promise<List<UsernameMailBlocker>> searchByUsername(String username, Integer limit, Integer offset);

    @ReadMethod
    Promise<List<UsernameMailBlocker>> searchByEmail(String email, Integer limit, Integer offset);
}
