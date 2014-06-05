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
public class Oculus48Id {
    private Oculus48Id() {
    }

    public static final long OCULUS48_MAXIMUM = 0xFFFFFFFFFFFFL;
    public static final long OCULUS48_MASK_BITS = 0x3FFFFFFFFL;
    public static final int OCULUS48_SHUFFLE_OFFSET = 14;
    public static final int OCULUS48_DECIMAL_LENGTH = 12;
    public static final int OCULUS48_ID_RADIX = 16;
    public static final String OCULUS48_ID_DEFAULT_FILL_FIELD = "0";
    private static DualHashBidiMap oculus48ShuffleMap;

    private static final Long MAGIC_NUMBER = 0x6B54FFB0BC9FL;

    static
    {
       // shuffle mapping is:
       // A: 14 10 12 27 0  1  15 13 8  2  17 11 31 18 7  16 33 19 4  32 26 29 23 21 22 5  24 20 30 6  25 9  3  28
       // B: 0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33

       // oculus 48bits Id hash map, 34 bits mapping
       oculus48ShuffleMap = new DualHashBidiMap();
       oculus48ShuffleMap.put(14, 0);
       oculus48ShuffleMap.put(10, 1);
       oculus48ShuffleMap.put(12, 2);
       oculus48ShuffleMap.put(27, 3);
       oculus48ShuffleMap.put(0, 4);
       oculus48ShuffleMap.put(1, 5);
       oculus48ShuffleMap.put(15, 6);
       oculus48ShuffleMap.put(13, 7);
       oculus48ShuffleMap.put(8, 8);
       oculus48ShuffleMap.put(2, 9);
       oculus48ShuffleMap.put(17, 10);
       oculus48ShuffleMap.put(11, 11);
       oculus48ShuffleMap.put(31, 12);
       oculus48ShuffleMap.put(18, 13);
       oculus48ShuffleMap.put(7, 14);
       oculus48ShuffleMap.put(16, 15);
       oculus48ShuffleMap.put(33, 16);
       oculus48ShuffleMap.put(19, 17);
       oculus48ShuffleMap.put(4, 18);
       oculus48ShuffleMap.put(32, 19);
       oculus48ShuffleMap.put(26, 20);
       oculus48ShuffleMap.put(29, 21);
       oculus48ShuffleMap.put(23, 22);
       oculus48ShuffleMap.put(21, 23);
       oculus48ShuffleMap.put(22, 24);
       oculus48ShuffleMap.put(5, 25);
       oculus48ShuffleMap.put(24, 26);
       oculus48ShuffleMap.put(20, 27);
       oculus48ShuffleMap.put(30, 28);
       oculus48ShuffleMap.put(6, 29);
       oculus48ShuffleMap.put(25, 30);
       oculus48ShuffleMap.put(9, 31);
       oculus48ShuffleMap.put(3, 32);
       oculus48ShuffleMap.put(28, 33);
    }

   public static Long shuffle(Long id) {
       Long shufflePart = (id >> OCULUS48_SHUFFLE_OFFSET) & OCULUS48_MASK_BITS;
       Long nonShufflePart = (id) & (~(OCULUS48_MASK_BITS << OCULUS48_SHUFFLE_OFFSET));
       Long shuffledValue = 0L;
       Iterator it = oculus48ShuffleMap.entrySet().iterator();
       while (it.hasNext()) {
           Map.Entry pair = (Map.Entry)it.next();
           shuffledValue += (shufflePart & (0x1L << (int)pair.getKey())) == 0 ? 0 : (0x1L << (int)pair.getValue());
       }
       Long value = nonShufflePart + (shuffledValue << OCULUS48_SHUFFLE_OFFSET);
       return value ^ MAGIC_NUMBER;
   }

   public static Long unShuffle(Long value) {
       Long id = value ^ MAGIC_NUMBER;
       Long shuffledPart = (id >> OCULUS48_SHUFFLE_OFFSET) & OCULUS48_MASK_BITS;
       Long nonShuffledPart = (id) & (~(OCULUS48_MASK_BITS << OCULUS48_SHUFFLE_OFFSET));
       Long unShuffleValue = 0L;
       Iterator it = oculus48ShuffleMap.entrySet().iterator();
       while (it.hasNext()) {
           Map.Entry pair = (Map.Entry)it.next();
           unShuffleValue += (shuffledPart & (0x1L << (int)pair.getValue())) == 0 ? 0 : (0x1L << (int)pair.getKey());
       }

       return nonShuffledPart + (unShuffleValue << OCULUS48_SHUFFLE_OFFSET);
   }

   public static String format(Long id) {
       String idStr = Long.toHexString(id).toLowerCase();
       String displayStr = "";
       for(int i=1; i<=OCULUS48_DECIMAL_LENGTH; i++) {
           if(i <= idStr.length()) {
               displayStr = idStr.substring(idStr.length()-i , idStr.length()-i+1) + displayStr;
           }
           else {
               displayStr = OCULUS48_ID_DEFAULT_FILL_FIELD + displayStr;
           }
       }

       return displayStr;
   }

   public static Long deFormat(String id) {
       return Long.parseLong(id, OCULUS48_ID_RADIX);
   }

    public static void validateRawValue(long value) {
        if(value > OCULUS48_MAXIMUM) {
            throw new RuntimeException("Invalid Oculus48Id value " + value);
        }
    }

    public static void validateEncodedValue(String value) {
        try {
            deFormat(value);
        }
        catch (Exception e) {
            throw new RuntimeException("Invalid Oculus48Id formatted value " + value);
        }
    }
}
