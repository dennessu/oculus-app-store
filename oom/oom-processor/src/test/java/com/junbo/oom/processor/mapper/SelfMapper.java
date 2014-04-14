package com.junbo.oom.processor.mapper;

import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.processor.model.users.User;

/**
 * Created by kg on 3/21/2014.
 */
@Mapper
public interface SelfMapper {

    User mergeUser(User user, User oldUser, MappingContext mappingContext);
}
