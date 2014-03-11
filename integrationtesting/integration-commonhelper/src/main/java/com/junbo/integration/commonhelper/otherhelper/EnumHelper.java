/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.integration.commonhelper.otherhelper;

/**
 * @author Jason
 * Time: 3/11/204
 * For holding enum values
 */
public class EnumHelper {

    /**
     Enum for password Strength.
     @author Jason
     */
    public enum PasswordStrength {
        WEAK,
        FAIR,
        STRONG
    }

    /**
     Enum for user status.
     @author Jason
     */
    public enum UserStatus {
        ACTIVE,
        SUSPEND,
        BANNED,
        DELETED
    }

}
