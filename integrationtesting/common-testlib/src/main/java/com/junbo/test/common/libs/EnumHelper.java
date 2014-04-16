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
     * Enum for user status.
     *
     * @author Jason
     */
    public enum CatalogAttributeType {
        CATEGORY("Category"),
        GENRE("Genre"),
        COLOR("Color");

        private String type;

        private CatalogAttributeType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static String getRandom(){
            return values()[(int) (Math.random() * values().length)].getType();
        }
    }

    /**
     * Enum for user status.
     *
     * @author Jason
     */
    public enum CatalogItemType {
        PHYSICAL("PHYSICAL"),
        DIGITAL("DIGITAL"),
        EWALLET("EWALLET");

        private String itemType;

        private CatalogItemType(String type) {
            this.itemType = type;
        }

        public String getItemType() {
            return itemType;
        }
    }

    /**
     * Enum for user status.
     *
     * @author Jason
     */
    public enum CatalogEntityStatus {
        DESIGN("Design"),
        PENDING_REVIEW("PendingReview"),
        RELEASED("Released"),
        REJECTED("Rejected"),
        DELETED("Deleted");

        private String entityStatus;

        private CatalogEntityStatus(String entityStatus) {
            this.entityStatus = entityStatus;
        }

        public String getEntityStatus() {
            return entityStatus;
        }
    }

}
