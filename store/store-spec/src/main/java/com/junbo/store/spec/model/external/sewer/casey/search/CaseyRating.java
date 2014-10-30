/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey.search;

/**
 * The CaseyRating class.
 */
public class CaseyRating {

    private String type;
    private Double stars;
    private Long count;
    private Long numOnes;
    private Long numTwos;
    private Long numThrees;
    private Long numFours;
    private Long numFives;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getStars() {
        return stars;
    }

    public void setStars(Double stars) {
        this.stars = stars;
    }

    public Long getNumOnes() {
        return numOnes;
    }

    public void setNumOnes(Long numOnes) {
        this.numOnes = numOnes;
    }

    public Long getNumTwos() {
        return numTwos;
    }

    public void setNumTwos(Long numTwos) {
        this.numTwos = numTwos;
    }

    public Long getNumThrees() {
        return numThrees;
    }

    public void setNumThrees(Long numThrees) {
        this.numThrees = numThrees;
    }

    public Long getNumFours() {
        return numFours;
    }

    public void setNumFours(Long numFours) {
        this.numFours = numFours;
    }

    public Long getNumFives() {
        return numFives;
    }

    public void setNumFives(Long numFives) {
        this.numFives = numFives;
    }
}