package com.junbo.apphost.core.health

import com.codahale.metrics.Meter
import com.junbo.langur.core.metric.LatencyView
import com.junbo.langur.core.metric.MetricHelper
import groovy.transform.CompileStatic

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

/**
 * Created by x.
 */
@Path("/metric")
@Produces(["application/json"])
@CompileStatic
class MetricEndpoint {
    @GET
    static Map<String, Object> getAll() {
        Map<String, Object> result = new HashMap<>()
        result.put("recentLatency", getLastFiveMinutesLatencyView())
        result.put("overallLatency", getAllLatencyView())
        result.put("throughput", getMeter())
        return result
    }

    @GET
    @Path("/latency/recent")
    static Map<String, LatencyView> getLastFiveMinutesLatencyView() {
        return MetricHelper.getView("recent")
    }

    @GET
    @Path("/latency/overall")
    static Map<String, LatencyView> getAllLatencyView() {
        return MetricHelper.getView("overall")
    }

    @GET
    @Path("/throughput")
    static Map<String, Meter> getMeter() {
        return MetricHelper.getMeter()
    }
}
