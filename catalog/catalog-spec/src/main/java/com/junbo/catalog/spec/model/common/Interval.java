package com.junbo.catalog.spec.model.common;

import java.util.Date;

/**
 * Created by baojing on 4/12/14.
 */
public class Interval {
    private Date start;
    private Date end;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return new Date(end.getTime());
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
