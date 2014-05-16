package com.junbo.sharding.dualwrite.test.model;

import com.junbo.common.id.UserId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.dualwrite.annotations.WriteMethod;
import com.junbo.sharding.repo.BaseRepository;

public interface UserRepository extends BaseRepository<User, UserId> {
    @ReadMethod
    Promise<User> myReadMethod();

    @WriteMethod
    Promise<User> myWriteMethod();
}
