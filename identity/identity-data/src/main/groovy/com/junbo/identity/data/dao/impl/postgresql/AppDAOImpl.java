/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.common.id.AppId;
import com.junbo.oom.core.MappingContext;
import com.junbo.identity.data.dao.AppDAO;
import com.junbo.identity.data.entity.app.AppEntity;
import com.junbo.identity.data.entity.app.AppGroupEntity;
import com.junbo.identity.data.entity.app.AppGroupUserAssocEntity;
import com.junbo.identity.data.entity.app.AppSecretEntity;
import com.junbo.identity.data.entity.user.UserEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.spec.model.app.App;
import com.junbo.sharding.IdGeneratorFacade;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by liangfu on 2/19/14.
 */
@Component
public class AppDAOImpl implements AppDAO {

    @Autowired
    @Qualifier("identitySessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IdGeneratorFacade idGenerator;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public App save(App app) {
        AppEntity appEntity = modelMapper.toAppEntity(app, new MappingContext());

        appEntity.setId(idGenerator.nextId(AppId.class));
        save(appEntity);
        return get(appEntity.getId());
    }

    @Override
    public App update(App app) {
        delete(app.getId().getValue());
        AppEntity appEntity = modelMapper.toAppEntity(app, new MappingContext());
        save(appEntity);
        return get(appEntity.getId());
    }

    @Override
    public App get(Long appId) {
        AppEntity appEntity = getAppEntity(appId);

        return modelMapper.toApp(appEntity, new MappingContext());
    }

    @Override
    public void delete(Long appId) {
        AppEntity appEntity = getAppEntity(appId);

        if(appEntity != null && !CollectionUtils.isEmpty(appEntity.getGroups())) {
            for(AppGroupEntity appGroupEntity : appEntity.getGroups()) {
                List<AppGroupUserAssocEntity> appGroupUserAssocEntities =  currentSession().
                        createSQLQuery("select * from app_group_assoc where app_group_id = :appGroupId")
                        .addEntity(AppGroupUserAssocEntity.class).setParameter("appGroupId",
                                appGroupEntity.getId()).list();

                if(!CollectionUtils.isEmpty(appGroupUserAssocEntities)) {
                    for(AppGroupUserAssocEntity appGroupUserAssocEntity : appGroupUserAssocEntities) {
                        currentSession().delete(appGroupUserAssocEntity);
                    }
                }

                currentSession().delete(appGroupEntity);
            }
        }

        if(appEntity != null && !CollectionUtils.isEmpty(appEntity.getAppSecrets())) {
            for(AppSecretEntity appSecretEntity : appEntity.getAppSecrets()) {
                currentSession().delete(appSecretEntity);
            }
        }

        currentSession().delete(appEntity);
    }

    private AppEntity getAppEntity(Long appId) {
        AppEntity appEntity = (AppEntity)currentSession().get(AppEntity.class, appId);
        // In case this is load from the cache, remove this
        appEntity.getAppSecrets().clear();
        appEntity.getGroups().clear();
        if(appEntity == null) {
            return null;
        }

        appEntity.setAppSecrets(currentSession().createSQLQuery("select * from app_secrect where app_id = :appId").
                addEntity(AppSecretEntity.class).setParameter("appId", appEntity.getId()).list());

        appEntity.setGroups(currentSession().createSQLQuery("select * from app_group where app_id = :appId").
                addEntity(AppGroupEntity.class).setParameter("appId", appEntity.getId()).list());

        if(!CollectionUtils.isEmpty(appEntity.getGroups())) {
            for(AppGroupEntity entity : appEntity.getGroups()) {
                List<AppGroupUserAssocEntity> appGroupUserAssocEntities = currentSession().
                        createSQLQuery("select * from app_group_assoc where app_group_id = :appGroupId")
                        .addEntity(AppGroupUserAssocEntity.class).setParameter("appGroupId", entity.getId()).list();

                entity.getMembers().clear();
                if(!CollectionUtils.isEmpty(appGroupUserAssocEntities)) {
                    for(AppGroupUserAssocEntity appGroupUserAssocEntity : appGroupUserAssocEntities) {
                        UserEntity userEntity = (UserEntity)currentSession()
                                .get(UserEntity.class, appGroupUserAssocEntity.getUserId());
                        if(userEntity != null) {
                            entity.getMembers().add(userEntity);
                        }
                    }
                }
            }
        }

        return appEntity;
    }

    private void save(AppEntity appEntity) {
        currentSession().save(appEntity);

        if(!CollectionUtils.isEmpty(appEntity.getAppSecrets())) {
            for(int i = 0; i < appEntity.getAppSecrets().size(); i++) {
                appEntity.getAppSecrets().get(i).setId(idGenerator.nextId(AppId.class, appEntity.getId()));
                appEntity.getAppSecrets().get(i).setAppId(appEntity.getId());
                currentSession().save(appEntity.getAppSecrets().get(i));
            }
        }

        if(!CollectionUtils.isEmpty(appEntity.getGroups())) {
            for(int i = 0; i < appEntity.getGroups().size(); i++) {
                appEntity.getGroups().get(i).setId(idGenerator.nextId(AppId.class, appEntity.getId()));
                appEntity.getGroups().get(i).setAppId(appEntity.getId());
                currentSession().save(appEntity.getGroups().get(i));

                if(!CollectionUtils.isEmpty(appEntity.getGroups().get(i).getMembers())) {
                    for(int j = 0; j < appEntity.getGroups().get(i).getMembers().size(); j++) {
                        AppGroupUserAssocEntity appGroupUserAssocEntity = new AppGroupUserAssocEntity();
                        appGroupUserAssocEntity.setId(idGenerator.nextId(AppId.class, appEntity.getId()));
                        appGroupUserAssocEntity.setGroupId(appEntity.getGroups().get(i).getId());
                        appGroupUserAssocEntity.setUserId(appEntity.getGroups().get(i).getMembers().get(j).getId());
                        currentSession().save(appGroupUserAssocEntity);
                    }
                }
            }
        }
    }
}
