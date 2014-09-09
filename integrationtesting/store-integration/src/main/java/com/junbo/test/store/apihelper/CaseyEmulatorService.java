/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper;

import com.junbo.emulator.casey.spec.model.CaseyEmulatorData;

/**
 * The CaseyEmulatorService class.
 */
public interface CaseyEmulatorService {

    CaseyEmulatorData postEmulatorData(CaseyEmulatorData caseyEmulatorData) throws Exception;

}
