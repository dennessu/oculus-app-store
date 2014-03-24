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

    /**
     * Enum for user status.
     *
     * @author Jason
     */
    public enum CatalogAttributeType {
        TYPE("Type"),
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
    }

    /**
     * Enum for user status.
     *
     * @author Jason
     */
    public enum CatalogItemType {
        PHYSICAL("PHYSICAL"),
        APP("APP"),
        IAP("IAP");

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
