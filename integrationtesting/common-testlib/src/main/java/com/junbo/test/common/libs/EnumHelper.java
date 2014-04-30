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
     * Enum for catalog item attribute type.
     *
     * @author Jason
     */
    public enum CatalogItemAttributeType {
        GENRE("GENRE");

        private String type;

        private CatalogItemAttributeType(String type) {
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
     * Enum for catalog offer attribute type.
     *
     * @author Jason
     */
    public enum CatalogOfferAttributeType {
        CATEGORY("CATEGORY");

        private String type;

        private CatalogOfferAttributeType(String type) {
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
     * Enum for catalog item type.
     *
     * @author Jason
     */
    public enum CatalogItemType {
        PHYSICAL("PHYSICAL"),
        DIGITAL("DIGITAL"),
        STORED_VALUE("STORED_VALUE"),
        SUBSCRIPTION("SUBSCRIPTION"),
        VIRTUAL("VIRTUAL");

        private String itemType;

        private CatalogItemType(String type) {
            this.itemType = type;
        }

        public String getItemType() {
            return itemType;
        }

        public static CatalogItemType getRandom(){
            return values()[(int) (Math.random() * values().length)];
        }

        public static CatalogItemType getByIndex(int index) {
            return values()[index];
        }
    }

    /**
     * Enum for catalog entity status.
     *
     * @author Jason
     */
    public enum CatalogEntityStatus {
        DRAFT("DRAFT"),
        PENDING_REVIEW("PENDING_REVIEW"),
        REJECTED("REJECTED"),
        DELETED("DELETED"),
        APPROVED("APPROVED");

        private String entityStatus;

        private CatalogEntityStatus(String entityStatus) {
            this.entityStatus = entityStatus;
        }

        public String getEntityStatus() {
            return entityStatus;
        }
    }

    /**
     * Enum for entitlement type.
     *
     * @author Jason
     */
    public enum EntitlementType {
        DEVELOPER("DEVELOPER"),
        DEVELOPER_SUBSCRIPTION("DEVELOPER_SUBSCRIPTION"),
        DOWNLOAD("DOWNLOAD"),
        DOWNLOAD_SUBSCRIPTION("DOWNLOAD_SUBSCRIPTION"),
        ONLINE_ACCESS("ONLINE_ACCESS"),
        ONLINE_ACCESS_SUBSCRIPTION("ONLINE_ACCESS_SUBSCRIPTION"),
        SUBSCRIPTION("SUBSCRIPTION");

        private String type;

        private EntitlementType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static String getRandom(){
            return values()[(int) (Math.random() * values().length)].getType();
        }

        public static EntitlementType getRandomType(){
            return values()[(int) (Math.random() * values().length)];
        }
    }

}
