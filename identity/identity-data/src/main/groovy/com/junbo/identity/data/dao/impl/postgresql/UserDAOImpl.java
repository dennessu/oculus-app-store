/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserDAO;
import com.junbo.identity.data.entity.user.*;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.util.Constants;
import com.junbo.identity.data.util.PasswordDAOUtil;
import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.spec.model.user.User;
import com.junbo.oom.core.MappingContext;
import com.junbo.sharding.IdGenerator;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Implementation for User DAO..
 */
@Component
public class UserDAOImpl implements UserDAO {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IdGenerator idGenerator;

    private int maxRetryCount;

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public User saveUser(User user) {
        UserEntity userEntity = modelMapper.toUserEntity(user, new MappingContext());

        saveUserEntity(userEntity);
        saveUserPassword(fillUserPasswordEntity(userEntity));

        return getUser(userEntity.getId());
    }

    @Override
    public User updateUser(User user) {
        UserEntity userEntity = modelMapper.toUserEntity(user, new MappingContext());

        return modelMapper.toUser(updateUserEntity(userEntity), new MappingContext());
    }

    @Override
    public User getUser(Long userId) {
        UserEntity userEntity = findById(userId);
        return modelMapper.toUser(userEntity, new MappingContext());
    }

    @Override
    public List<User> findByUserName(String userName, String status) {
        final ArrayList userList = new ArrayList();
        UserStatus userStatus = UserStatus.valueOf(UserStatus.class, status);
        List<UserEntity> userEntities = findEntitiesByUserName(userName, userStatus.getId());
        DefaultGroovyMethods.each(userEntities, new Closure<Boolean>(this, this) {
            public Boolean doCall(Object k) {
                return userList.add(modelMapper.toUser((UserEntity) k, new MappingContext()));
            }

        });

        return ((List<User>) (userList));
    }

    @Override
    public List<User> searchUsers(String userNamePrefix, String status, Integer cursor, Integer count) {
        List<UserEntity> userEntities = searchEntitiesByUserName(userNamePrefix, status, cursor, count);
        final ArrayList userList = new ArrayList();
        DefaultGroovyMethods.each(userEntities, new Closure<Boolean>(this, this) {
            public Boolean doCall(Object k) {
                return userList.add(modelMapper.toUser((UserEntity) k, new MappingContext()));
            }

        });

        return ((List<User>) (userList));
    }

    @Override
    public void deleteUser(Long userId) {
        User user = getUser(userId);
        currentSession().delete(user);
    }

    @Override
    public User authenticate(String userName, String password) {
        UserPasswordEntity entity = getActivePasswordEntity(userName);
        if(entity.getRetryFailureCount() > maxRetryCount) {
            throw AppErrors.INSTANCE.maximumFailureRetryCountReached().exception();
        }
        String passwordHash = PasswordDAOUtil.hashPassword(password, entity.getPasswordSalt());

        entity.setLastLoginTime(new Date());
        entity.setLastLoginIp(Constants.DEFAULT_IP_ADDRESS);
        if(!entity.getPasswordHash().equals(passwordHash)) {
            // Need to change the retry count
            entity.setRetryFailureCount(entity.getRetryFailureCount() + 1);
            currentSession().update(entity);

            throw AppErrors.INSTANCE.userNamePasswordNotMatch(userName).exception();
        }
        else {
            // Need to clear the retry count
            entity.setRetryFailureCount(Constants.INIT_RETRY_COUNT);
            currentSession().update(entity);
            return findByUserName(userName, UserStatus.ACTIVE.toString()).get(0);
        }
    }

    @Override
    public void savePassword(String userName, String password) {
        UserPasswordEntity entity = getActivePasswordEntity(userName);

        UserPasswordStrength userPasswordStrength = PasswordDAOUtil.calcPwdStrength(password);
        if(userPasswordStrength.getId() < entity.getPasswordStrength()) {
            throw AppErrors.INSTANCE.updatePasswordToWeaker().exception();
        }

        entity.setLastLoginIp(Constants.DEFAULT_IP_ADDRESS);
        entity.setLastLoginTime(new Date());
        entity.setStatus(UserPasswordStatus.RETIRE.getId());
        // retire current password
        currentSession().update(entity);

        entity.setPasswordHash(PasswordDAOUtil.hashPassword(password, entity.getPasswordSalt()));
        entity.setStatus(UserPasswordStatus.ACTIVE.getId());
        entity.setRetryFailureCount(Constants.INIT_RETRY_COUNT);
        entity.setCreatedBy(Constants.DEFAULT_CLIENT_ID);
        entity.setCreatedTime(new Date());
        entity.setPasswordStrength(userPasswordStrength.getId());
        // create new password
        currentSession().persist(entity);
    }

    @Override
    public List<UserPasswordBlacklist> getPasswordBlacklists() {
        return new ArrayList<UserPasswordBlacklist>();
    }

    private UserEntity saveUserEntity(UserEntity entity) {
        entity.setId(idGenerator.nextId());
        entity.setCreatedBy(Constants.DEFAULT_CLIENT_ID);
        entity.setCreatedTime(new Date());
        currentSession().persist(entity);
        return findById(entity.getId());
    }

    private UserEntity updateUserEntity(UserEntity entity) {
        UserEntity userEntity = findById(entity.getId());
        currentSession().evict(userEntity);
        entity.setUpdatedBy(Constants.DEFAULT_CLIENT_ID);
        entity.setCreatedBy(userEntity.getCreatedBy());
        entity.setCreatedTime(userEntity.getCreatedTime());
        entity.setUpdatedTime(new Date());
        currentSession().update(entity);
        currentSession().flush();
        return findById(entity.getId());
    }

    private UserEntity findById(Long id) {
        return ((UserEntity) (currentSession().get(UserEntity.class, id)));
    }

    private List<UserEntity> findEntitiesByUserName(String userName, Short status) {
        List result = currentSession().
                createSQLQuery("select * from user_account where user_name = :userName and status = :status").
                addEntity(UserEntity.class).setParameter("userName", userName).
                setParameter("status", status).list();

        return result;
    }

    private List<UserEntity> searchEntitiesByUserName(String userNamePrefix,
                                                      String status, Integer cursor, Integer count) {
        UserStatus userStatus = UserStatus.valueOf(UserStatus.class, status);
        String query = "select * from user_account where user_name like :userNamePrefix and status = :status" +
                " order by id limit " + (count == null ? "ALL" : count.toString())
                + " offset " + (cursor == null ? "0" : cursor.toString());
        List result = currentSession().createSQLQuery(query).addEntity(UserEntity.class).
                setParameter("userNamePrefix", (userNamePrefix == null? "" : userNamePrefix) + "%").
                setParameter("status", userStatus.getId()).list();

        return result;
    }

    private UserPasswordEntity saveUserPassword(UserPasswordEntity entity) {
        entity.setCreatedTime(new Date());
        entity.setCreatedBy(Constants.DEFAULT_CLIENT_ID);
        entity.setLastLoginIp(Constants.DEFAULT_IP_ADDRESS);
        entity.setLastLoginTime(new Date());
        currentSession().persist(entity);
        return getPasswordEntity(entity.getKey());
    }

    private UserPasswordEntity getPasswordEntity(Long id) {
        return ((UserPasswordEntity) (currentSession().get(UserPasswordEntity.class, id)));
    }

    private UserPasswordEntity getActivePasswordEntity(String userName) {
        List<User> users = findByUserName(userName, UserStatus.ACTIVE.toString());

        if(CollectionUtils.isEmpty(users) || users.size() > 1) {
            throw AppErrors.INSTANCE.notExistingUser("userName = " + userName).exception();
        }

        User activeUser = users.get(0);
        List result = currentSession().
                createSQLQuery("select * from user_password where user_id = :userId and status = :status").
                addEntity(UserPasswordEntity.class).setParameter("userId", activeUser.getId().getValue()).
                setParameter("status", UserPasswordStatus.ACTIVE.getId()).list();

        if(CollectionUtils.isEmpty(result) || result.size() > 1) {
            throw AppErrors.INSTANCE.userStatusError("userName = " + userName).exception();
        }
        return (UserPasswordEntity)result.get(0);
    }

    private UserPasswordEntity fillUserPasswordEntity(final UserEntity userEntity) {
        UserPasswordEntity userPasswordEntity = (UserPasswordEntity) DefaultGroovyMethods.
                with(new UserPasswordEntity(), new Closure<UserPasswordEntity>(this, this) {
                    public UserPasswordEntity doCall(UserPasswordEntity it) {
                        it.setKey(idGenerator.nextId(userEntity.getId()));
                        it.setPasswordSalt(UUID.randomUUID().toString());
                        it.setPasswordHash(
                                PasswordDAOUtil.hashPassword(userEntity.getPassword(), it.getPasswordSalt()));
                        it.setPasswordStrength(PasswordDAOUtil.calcPwdStrength(userEntity.getPassword()).getId());
                        it.setUserId(userEntity.getId());
                        it.setRetryFailureCount(0);
                        it.setStatus(UserPasswordStatus.ACTIVE.getId());
                        return it;
                    }
                });

        return userPasswordEntity;
    }
}
