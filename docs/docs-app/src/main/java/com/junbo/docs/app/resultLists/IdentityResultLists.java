/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.resultlists;

import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.user.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * The non-generic ResultList types for identity.
 */
public class IdentityResultLists {

    /**
     * Find the non-generic ResultList type.
     */
    public static Class getClass(ParameterizedType type) {
        Type actualType = type.getActualTypeArguments()[0];
        return resultListMap.get(actualType);
    }

    private IdentityResultLists() {}
    private static Map<Class, Class> resultListMap = ResultListUtils.getMap(
            UserResultList.class,
            UserDeviceProfileResultList.class,
            UserFederationResultList.class,
            UserOptInResultList.class,
            UserProfileResultList.class,
            UserTosAcceptanceResultList.class);
}

class UserResultList extends ResultList<User> {
    @Override
    public List<User> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<User> items) {
        super.setItems(items);
    }
}
class UserDeviceProfileResultList extends ResultList<UserDeviceProfile> {
    @Override
    public List<UserDeviceProfile> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<UserDeviceProfile> items) {
        super.setItems(items);
    }
}
class UserFederationResultList extends ResultList<UserFederation> {
    @Override
    public List<UserFederation> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<UserFederation> items) {
        super.setItems(items);
    }
}
class UserOptInResultList extends ResultList<UserOptIn> {
    @Override
    public List<UserOptIn> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<UserOptIn> items) {
        super.setItems(items);
    }
}
class UserProfileResultList extends ResultList<UserProfile> {
    @Override
    public List<UserProfile> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<UserProfile> items) {
        super.setItems(items);
    }
}
class UserTosAcceptanceResultList extends ResultList<UserTosAcceptance> {
    @Override
    public List<UserTosAcceptance> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<UserTosAcceptance> items) {
        super.setItems(items);
    }
}
