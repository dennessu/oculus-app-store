/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.interceptor;

import com.junbo.identity.data.entity.common.ResourceMetaEntity;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by liangfu on 3/24/14.
 */
public class AuditInterceptor extends EmptyInterceptor {
    private static final String CREATED_TIME_FIELD = "createdTime";
    private static final String CREATED_BY_FILED = "createdBy";
    private static final String UPDATED_TIME_FIELD = "updatedTime";
    private static final String UPDATED_BY_FIELD = "updatedBy";

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if(entity.getClass().getSuperclass() == ResourceMetaEntity.class) {
            //todo: need to get the userName from current threadLocal storage
            ((ResourceMetaEntity)entity).setCreatedTime(new Date());
            ((ResourceMetaEntity)entity).setCreatedBy("todo-liangfuxia");

            for(int i = 0; i < propertyNames.length; i++) {
                if(propertyNames[i].equals(CREATED_TIME_FIELD)) {
                    state[i] = new Date();
                }
                if(propertyNames[i].equals(CREATED_BY_FILED)) {
                    state[i] = "todo-liangfuxia";
                }
            }

            return true;
        }
        return super.onSave(entity, id, state, propertyNames, types);
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState,
                                Object[] previousState, String[] propertyNames, Type[] types) {
        if(entity.getClass().getSuperclass() == ResourceMetaEntity.class) {
            //todo: need to get the userName from current threadLocal storage
            ((ResourceMetaEntity)entity).setUpdatedTime(new Date());
            ((ResourceMetaEntity)entity).setUpdatedBy("todo");

            for(int i = 0; i < propertyNames.length; i++) {
                if(propertyNames[i].equals(CREATED_TIME_FIELD)) {
                    currentState[i] = previousState[i];
                    ((ResourceMetaEntity)entity).setCreatedTime((Date)previousState[i]);
                }
                if(propertyNames[i].equals(CREATED_BY_FILED)) {
                    currentState[i] = previousState[i];
                    ((ResourceMetaEntity)entity).setCreatedBy((String) previousState[i]);
                }
                if(propertyNames[i].equals(UPDATED_TIME_FIELD)) {
                    currentState[i] = new Date();
                }
                if(propertyNames[i].equals(UPDATED_BY_FIELD)) {
                    currentState[i] = "todo-liangfuxia";
                }
            }
            return true;
        }
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if(entity.getClass().getSuperclass() == ResourceMetaEntity.class) {
            //todo: need to get the userName from current threadLocal storage
            ((ResourceMetaEntity)entity).setUpdatedTime(new Date());
            ((ResourceMetaEntity)entity).setUpdatedBy("todo");
            return;
        }
        super.onDelete(entity, id, state, propertyNames, types);
    }
}
