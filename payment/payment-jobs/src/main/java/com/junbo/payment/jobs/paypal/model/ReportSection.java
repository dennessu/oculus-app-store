/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.paypal.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ReportSection.
 */
public class ReportSection {
    private List<SectionBodyRow> rows = new ArrayList<SectionBodyRow>();
    public List<SectionBodyRow> getRows(){
        return rows;
    }
}
