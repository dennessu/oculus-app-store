/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.UserId;
import com.junbo.store.spec.model.ChallengeSolution;

/**
 * The UserProfileUpdateRequest class.
 */
public class UserProfileUpdateRequest {

    private ChallengeSolution challengeSolution;

    private UserId userId;

    private String action;

    private JsonNode updateValue;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * The UpdateAction enum.
     */
    public enum UpdateAction {
        ADD_EMAIL(false),
        REMOVE_EMAIL(true),
        UPDATE_DEFAULT_EMAIL(true),
        ADD_PHONE(false),
        REMOVE_PHONE(true),
        UPDATE_DEFAULT_PHONE(true),
        UPDATE_NAME(false);

        private UpdateAction(boolean requirePersonalInfoId) {
            this.requirePersonalInfoId = requirePersonalInfoId;
        }
        private boolean requirePersonalInfoId;

        public boolean isRequirePersonalInfoId() {
            return requirePersonalInfoId;
        }
    }

    public ChallengeSolution getChallengeSolution() {
        return challengeSolution;
    }

    public void setChallengeSolution(ChallengeSolution challengeSolution) {
        this.challengeSolution = challengeSolution;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public JsonNode getUpdateValue() {
        return updateValue;
    }

    public void setUpdateValue(JsonNode updateValue) {
        this.updateValue = updateValue;
    }
}
