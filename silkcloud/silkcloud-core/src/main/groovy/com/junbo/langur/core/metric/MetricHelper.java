package com.junbo.langur.core.metric;

import com.codahale.metrics.*;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * UniformHistogram shows overall latency.
 * LastFiveMinutesHistogram shows last five minutes' latency.
 * Meter shows throughput rate.
 */
public class MetricHelper {
    private MetricHelper() {
    }

    public static final String UNIFORM_PREFIX = "u";
    public static final String LAST_FIVE_MINUTES_PREFIX = "l";
    public static final String RESPONSE_TYPE_ALL = "(ALL)";

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MetricHelper.class);
    private static final MetricRegistry METRICS = new MetricRegistry();

    public static void addMetric(String type, String responseType, Long time) {
        try {
            getOrAddUniformHistogram(MetricRegistry.name(type, responseType)).update(time);
            getOrAddUniformHistogram(MetricRegistry.name(type, RESPONSE_TYPE_ALL)).update(time);
            getOrAddLastFiveMinutesHistogram(MetricRegistry.name(type, responseType)).update(time);
            getOrAddLastFiveMinutesHistogram(MetricRegistry.name(type, RESPONSE_TYPE_ALL)).update(time);
            getOrAddMeter(MetricRegistry.name(type, responseType)).mark();
            getOrAddMeter(MetricRegistry.name(type, RESPONSE_TYPE_ALL)).mark();
        } catch (Exception e) {
            logger.error("Error occurred during adding metric", e);
        }
    }

    public static Map<String, LatencyView> getView(String type) {
        Map<String, LatencyView> result = new TreeMap<>();
        for (Map.Entry<String, Histogram> entry : METRICS.getHistograms().entrySet()) {
            String name = entry.getKey();
            if (type.equals("recent") && name.startsWith(LAST_FIVE_MINUTES_PREFIX)) {
                result.put(name.substring(10), toLatencyView(entry.getValue()));
            }
            if (type.equals("overall") && name.startsWith(UNIFORM_PREFIX)) {
                result.put(name.substring(10), toLatencyView(entry.getValue()));
            }
        }
        return result;
    }

    public static Map<String, Meter> getMeter() {
        return METRICS.getMeters();
    }

    private static Histogram getOrAddUniformHistogram(String name) {
        name = UNIFORM_PREFIX + "Histogram" + name;
        try {
            return METRICS.register(name, new Histogram(new UniformReservoir()));
        } catch (IllegalArgumentException e) {
            return METRICS.histogram(name);
        }
    }

    private static Histogram getOrAddLastFiveMinutesHistogram(String name) {
        return METRICS.histogram(LAST_FIVE_MINUTES_PREFIX + "Histogram" + name);
    }

    private static Meter getOrAddMeter(String name) {
        return METRICS.meter(name);
    }

    private static LatencyView toLatencyView(Histogram histogram) {
        Snapshot snapshot = histogram.getSnapshot();
        LatencyView latencyView = new LatencyView();
        latencyView.setMax(snapshot.getMax());
        latencyView.setMin(snapshot.getMin());
        latencyView.setAverage(snapshot.getMean());
        latencyView.setFiftyPercentile(snapshot.getMedian());
        latencyView.setEightyPercentile(snapshot.getValue(0.8));
        latencyView.setNinetyFivePercentile(snapshot.get95thPercentile());
        latencyView.setCount((snapshot instanceof UniformSnapshot) ? histogram.getCount() : snapshot.size());
        return latencyView;
    }
}
