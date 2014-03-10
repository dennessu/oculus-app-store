/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.integration.customerscenarios.helper;

/**
 * Created by Jason on 3/7/14.
 */
public class EnumHelper {

    /**
    Enum for password Strength.
    @author Jason
    */
    public enum PasswordStrength {
        WEAK,
        STRONG
    }

    /**
    Enum for user status.
    @author Jason
    */
    public enum UserStatus {
        ACTIVE,
        SUSPENDED,
        BANNED,
        DELETED
    }

    /**
    Enum for user parameter when posting a user.
    @author Jason
     */
    public enum UserPara {
        userName,
        password,
        passwordStrength,
        status
    }

}
