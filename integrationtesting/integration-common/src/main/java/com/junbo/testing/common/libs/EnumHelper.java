/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.libs;

/**
 * @author Jason
 *         Time: 3/11/204
 *         For holding enum values
 */
public class EnumHelper {

    /**
     * Enum for password Strength.
     *
     * @author Jason
     */
    public enum PasswordStrength {
        WEAK("WEAK"),
        FAIR("FAIR"),
        STRONG("STRONG");

        private String strength;

        private PasswordStrength(String strength) {
            this.strength = strength;
        }

        public String getStrength() {
            return strength;
        }

    }


    /**
     * Enum for user status.
     *
     * @author Jason
     */
    public enum UserStatus {
        ACTIVE("ACTIVE"),
        SUSPEND("SUSPEND"),
        BANNED("BANNED"),
        DELETED("DELETED");

        private String status;

        private UserStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

}
