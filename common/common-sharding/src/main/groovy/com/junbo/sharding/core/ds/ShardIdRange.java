/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.ds;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haomin on 14-3-18.
 */
public class ShardIdRange {
    private static final String PATTERN_STR = "^(0|[1-9][0-9]*)[\\.]{2}(0|[1-9][0-9]*)$";
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STR);

    private int start;
    private int end;

    public ShardIdRange(int start, int end) {
        if (start > end) {
            throw new RuntimeException("shardId range exception, start is greater than end");
        }
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public static ShardIdRange parse(String expr) {
        Matcher matcher = PATTERN.matcher(expr.trim());
        if (!matcher.matches()) {
            throw new RuntimeException(String.format("%s is not a valid ShardIdRange expression", expr));
        }

        return new ShardIdRange(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2)));
    }

    public boolean contains(int shardId) {
        return shardId <= this.end && shardId >= this.start;
    }

    @Override
    public String toString() {
        return "[" + start + ".." + end + "]";
    }
}
