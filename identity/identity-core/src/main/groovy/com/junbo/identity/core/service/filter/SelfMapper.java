/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.filter;

import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.model.users.*;
import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.MappingContext;

/**
 * Created by kg on 3/26/2014.
 */
@Mapper
public interface SelfMapper {

    User filterUser(User user, MappingContext context);
    User mergeUser(User source, User base, MappingContext context);

    Group filterGroup(Group group, MappingContext context);
    Group mergeGroup(Group source, Group base, MappingContext context);

    SecurityQuestion filterSecurityQuestion(SecurityQuestion securityQuestion, MappingContext context);
    SecurityQuestion mergeSecurityQuestion(SecurityQuestion source, SecurityQuestion base, MappingContext context);

    UserAuthenticator filterUserAuthenticator(UserAuthenticator userAuthenticator, MappingContext context);
    UserAuthenticator mergeUserAuthenticator(UserAuthenticator source, UserAuthenticator base, MappingContext context);

    UserDevice filterUserDevice(UserDevice userDevice, MappingContext context);
    UserDevice mergeUserDevice(UserDevice source, UserDevice base, MappingContext context);

    UserGroup filterUserGroup(UserGroup userGroup, MappingContext context);
    UserGroup mergeUserGroup(UserGroup source, UserGroup base, MappingContext context);
}
