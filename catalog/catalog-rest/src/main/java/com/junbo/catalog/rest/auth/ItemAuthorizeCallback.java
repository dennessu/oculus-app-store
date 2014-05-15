package com.junbo.catalog.rest.auth;

import com.junbo.authorization.AbstractAuthorizeCallback;
import com.junbo.authorization.AbstractAuthorizeCallbackFactory;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.common.id.UserId;
import groovy.transform.CompileStatic;

/**
 * Created by Shenhua on 5/14/2014.
 */
@CompileStatic
class ItemAuthorizeCallback extends AbstractAuthorizeCallback<Item> {

    ItemAuthorizeCallback(AbstractAuthorizeCallbackFactory<Item> factory, String apiName, Item entity) {
        super(factory, apiName, entity);
    }

    @Override
    protected UserId getUserOwnerId() {
        return new UserId(getEntity().getOwnerId());
    }

    @Override
    protected Object getEntityIdByPropertyPath(String propertyPath) {
        if ("developer".equals(propertyPath) || "owner".equals(propertyPath)) {
            return new UserId(getEntity().getOwnerId());
        }

        return super.getEntityIdByPropertyPath(propertyPath);
    }
}
