/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.libs;

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

    /**
     * Enum for Tos type.
     *
     * @author Jason
     */
    public enum TosType {
        EULA("EULA"),
        TOS("TOS"),
        PP("PP");

        private String type;

        private TosType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static String getRandom(){
            return values()[(int) (Math.random() * values().length)].getType();
        }

        public static TosType getRandomType(){
            return values()[(int) (Math.random() * values().length)];
        }

    }

    /**
     * Enum for Tos type.
     *
     * @author Jason
     */
    public enum TosState {
        DRAFT("DRAFT"),
        APPROVED("APPROVED"),
        OBSOLETE("OBSOLETE");

        private String type;

        private TosState(String type) {
            this.type = type;
        }

        public String getState() {
            return type;
        }

    }

}
