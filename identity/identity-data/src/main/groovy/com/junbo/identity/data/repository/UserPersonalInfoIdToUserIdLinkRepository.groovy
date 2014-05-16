package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserPersonalInfoIdToUserIdLinkId
import com.junbo.identity.spec.v1.model.UserPersonalInfoIdToUserIdLink
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository

/**
 * Created by liangfu on 5/14/14.
 */
public interface UserPersonalInfoIdToUserIdLinkRepository extends
        BaseRepository<UserPersonalInfoIdToUserIdLink, UserPersonalInfoIdToUserIdLinkId> {

    @ReadMethod
    Promise<List<UserPersonalInfoIdToUserIdLink>> searchByUserId(UserId userId)

    @ReadMethod
    Promise<UserPersonalInfoIdToUserIdLink> searchByUserPersonalInfoId(UserPersonalInfoId userPersonalInfoId)
}