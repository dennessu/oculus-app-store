/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.job.trigger;

import java.util.List;

/**
 * Created by fzhang on 2015/2/6.
 */
public class JobTriggerRequest {

    private String jobName;
    private List<String> jobArguments;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public List<String> getJobArguments() {
        return jobArguments;
    }

    public void setJobArguments(List<String> jobArguments) {
        this.jobArguments = jobArguments;
    }
}
