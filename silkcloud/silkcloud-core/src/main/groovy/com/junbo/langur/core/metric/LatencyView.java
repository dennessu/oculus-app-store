package com.junbo.langur.core.metric;

/**
 * Created by x on 1/7/15.
 */
public class LatencyView {
    private long min;
    private long max;
    private double average;
    private double fiftyPercentile;
    private double eightyPercentile;
    private double ninetyFivePercentile;
    private long count;

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getFiftyPercentile() {
        return fiftyPercentile;
    }

    public void setFiftyPercentile(double fiftyPercentile) {
        this.fiftyPercentile = fiftyPercentile;
    }

    public double getEightyPercentile() {
        return eightyPercentile;
    }

    public void setEightyPercentile(double eightyPercentile) {
        this.eightyPercentile = eightyPercentile;
    }

    public double getNinetyFivePercentile() {
        return ninetyFivePercentile;
    }

    public void setNinetyFivePercentile(double ninetyFivePercentile) {
        this.ninetyFivePercentile = ninetyFivePercentile;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
