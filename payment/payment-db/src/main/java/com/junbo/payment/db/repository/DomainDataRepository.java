/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.payment.db.dao.DomainDataDAOImpl;

import java.util.List;

/**
 * abstract domain data Repository.
 * @param <T> entity type
 * @param <DAO> dao type
 */
public abstract class DomainDataRepository<T, DAO extends DomainDataDAOImpl> {
    private List<T> domainData;

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    protected DAO dao;

    public void afterPropertiesSet(){
        preLoadData();
    }

    public void preLoadData(){
        domainData = dao.getAll();
    }

    public List<T> getDomainData(){
        if(domainData != null){
            return domainData;
        }else{
            preLoadData();
            return domainData;
        }
    }
}
