/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.jobs.utils.csv;

import java.util.List;

/**
 * The CSVWriter class.
 */
public interface CSVWriter {

    void writeRecords(List<List<String>> records);

    void close();
}
