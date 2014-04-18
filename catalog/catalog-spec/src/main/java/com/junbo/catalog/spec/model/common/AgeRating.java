/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Age rating.
 */
public class AgeRating {
    @ApiModelProperty(position = 1, required = true, allowableValues = "ESRB, PEGI, GRB")
    private String board;
    @ApiModelProperty(position = 2, required = true, value = "The rating category, differ based on the board")
    private String category;
    @ApiModelProperty(position = 3, required = true,
            value = "Array of text descriptors to further describe the reason for the rating")
    private List<String> descriptors;

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(List<String> descriptors) {
        this.descriptors = descriptors;
    }
}
