/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import com.junbo.configuration.topo.DataCenters;

import java.util.UUID;

/**
 * UUIDUtils.
 */
public class UUIDUtils {
    public static UUID randomUUIDwithDC() {
        UUID uuid = UUID.randomUUID();
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        leastSigBits = (leastSigBits & ~0xf) | (DataCenters.instance().currentDataCenterId() & 0xf);
        return new UUID(mostSigBits, leastSigBits);
    }

    public static int getDCFromUUID(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            long leastSigBits = uuid.getLeastSignificantBits();
            return (int) (leastSigBits & 0xf);
        } catch (Exception ex) {
            return DataCenters.instance().currentDataCenterId();
        }
    }
}
