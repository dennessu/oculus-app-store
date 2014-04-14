/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.shuffle;
        
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import java.util.Iterator;
import java.util.Map;
        
/**
  * Created by liangfu on 3/6/14.
  */
public class Oculus40Id {
    private Oculus40Id() {
    }

    public static final long OCULUS40_MAXIMUM = 0xFFFFFFFFFFL;
    public static final long OCULUS40_MASK_BITS = 0x3FFFFFFL;
    public static final int OCULUS40_SHUFFLE_OFFSET = 14;
    public static final String OCULUS40_ID_SEPARATOR = "-";
    public static final String OCULUS40_ID_DEFAULT_FILL_FIELD = "0";
    public static final int OCULUS40_DECIMAL_LENGTH = 12;
    private static DualHashBidiMap oculus40ShuffleMap;

    private static final Long MAGIC_NUMBER = 365067025567L;

    static
    {
        // shuffle mapping is:
        // A: 14 10 12 18 0  1  15 13 8  2  17 11 16 21 7  9  3  19 4  5 6  20 23 24 22 25
        // B: 0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25

        // oculus 40bits Id hash map, 26 bits mapping
        oculus40ShuffleMap = new DualHashBidiMap();
        oculus40ShuffleMap.put(14, 0);
        oculus40ShuffleMap.put(10, 1);
        oculus40ShuffleMap.put(12, 2);
        oculus40ShuffleMap.put(18, 3);
        oculus40ShuffleMap.put(0, 4);
        oculus40ShuffleMap.put(1, 5);
        oculus40ShuffleMap.put(15, 6);
        oculus40ShuffleMap.put(13, 7);
        oculus40ShuffleMap.put(8, 8);
        oculus40ShuffleMap.put(2, 9);
        oculus40ShuffleMap.put(17, 10);
        oculus40ShuffleMap.put(11, 11);
        oculus40ShuffleMap.put(16, 12);
        oculus40ShuffleMap.put(21, 13);
        oculus40ShuffleMap.put(7, 14);
        oculus40ShuffleMap.put(9, 15);
        oculus40ShuffleMap.put(3, 16);
        oculus40ShuffleMap.put(19, 17);
        oculus40ShuffleMap.put(4, 18);
        oculus40ShuffleMap.put(5, 19);
        oculus40ShuffleMap.put(6, 20);
        oculus40ShuffleMap.put(20, 21);
        oculus40ShuffleMap.put(23, 22);
        oculus40ShuffleMap.put(24, 23);
        oculus40ShuffleMap.put(22, 24);
        oculus40ShuffleMap.put(25, 25);
    }

    public static Long shuffle(Long id) {
        Long shufflePart = (id >> OCULUS40_SHUFFLE_OFFSET) & OCULUS40_MASK_BITS;
        Long nonShufflePart = (id) & (~(OCULUS40_MASK_BITS << OCULUS40_SHUFFLE_OFFSET));
        Long shuffledValue = 0L;
        Iterator it = oculus40ShuffleMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            shuffledValue += (shufflePart & (0x1L << (int)pair.getKey())) == 0 ? 0 : (0x1L << (int)pair.getValue());
        }

        Long value = nonShufflePart + (shuffledValue << OCULUS40_SHUFFLE_OFFSET);

        return value ^ MAGIC_NUMBER;
    }

    public static Long unShuffle(Long value) {
        Long id = value ^ MAGIC_NUMBER;
        Long shuffledPart = (id >> OCULUS40_SHUFFLE_OFFSET) & OCULUS40_MASK_BITS;
        Long nonShuffledPart = (id) & (~(OCULUS40_MASK_BITS << OCULUS40_SHUFFLE_OFFSET));
        Long unShuffleValue = 0L;
        Iterator it = oculus40ShuffleMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            unShuffleValue += (shuffledPart & (0x1L << (int)pair.getValue())) == 0 ? 0 : (0x1L << (int)pair.getKey());
        }

        return nonShuffledPart + (unShuffleValue << OCULUS40_SHUFFLE_OFFSET);
    }

    public static String format(Long id) {
        String idStr = id.toString();
        String displayStr = "";
        for(int i=1; i<=OCULUS40_DECIMAL_LENGTH; i++) {
            if(i <= idStr.length()) {
                displayStr = idStr.substring(idStr.length()-i , idStr.length()-i+1) + displayStr;
            }
            else {
                displayStr = OCULUS40_ID_DEFAULT_FILL_FIELD + displayStr;
            }
            if(i%4 == 0 && (i != OCULUS40_DECIMAL_LENGTH)) {
                displayStr = OCULUS40_ID_SEPARATOR + displayStr;
            }
        }

        return displayStr;
    }

    public static Long deFormat(String id) {
        String idStr = id.replaceAll(OCULUS40_ID_SEPARATOR, "");
        return Long.parseLong(idStr);
    }

    public static void validateRawValue(long value) {
        if(value > OCULUS40_MAXIMUM) {
            throw new RuntimeException("Invalid Oculus40Id value " + value);
        }
    }

    public static void validateEncodedValue(String value) {
        try {
            deFormat(value);
        }
        catch (Exception e) {
            throw new RuntimeException("Invalid Oculus40Id formatted value " + value);
        }
    }
}
