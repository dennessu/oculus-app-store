package com.junbo.identity.core.service.filter;

import com.junbo.identity.spec.model.users.User;
import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.MappingContext;

/**
 * Created by kg on 3/26/2014.
 */
@Mapper
public interface SelfMapper {

    User filterUser(User user, MappingContext context);

    User mergeUser(User source, User base, MappingContext context);

}
