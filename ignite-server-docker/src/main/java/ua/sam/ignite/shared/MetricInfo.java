package ua.sam.ignite.shared;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.ignite.DataRegionMetrics;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;

import java.util.Collection;

import static java.lang.String.format;

public class MetricInfo implements IgniteCallable<Integer> {

    private StatsDClient statsDClient;

    @IgniteInstanceResource
    Ignite ignite;

    private IgniteLogger log;

    public MetricInfo() {
    }

    @Override
    public Integer call() throws Exception {
        log = ignite.log();
        statsDClient = statsDClient(null);
        showMetric();
        return 2;
    }

    private void showMetric() {
        final String nodeId = "";

        printLog("Show metrics inside ignite " + nodeId, log);
        Collection<DataRegionMetrics> regionsMetrics = ignite.dataRegionMetrics();

        ignite.cacheNames().forEach(s -> {
            printLog("Size : " + ignite.cache(s).size(CachePeekMode.ALL) + " of cache " + s, log);
        });

        for (DataRegionMetrics metrics : regionsMetrics) {
            if ("sysMemPlc".equals(metrics.getName()) || "Default_Region".equals(metrics.getName())) {
                continue;
            }
            String cacheName = metrics.getName();

            printLog("#############################################", log);
            printLog(">>> Memory Region Name: " + metrics.getName(), log);
            printLog("AllocationRate: " + metrics.getAllocationRate(), log);
            statsDClient.recordGaugeValue(format("ignite.cache.%s.%s.allocationRate", cacheName, nodeId), metrics.getAllocationRate());
            printLog("PagesFillFactor: " + metrics.getPagesFillFactor(), log);
            statsDClient.recordGaugeValue(format("ignite.cache.%s.%s.pagesFillFactor", cacheName, nodeId), metrics.getPagesFillFactor());
            printLog("PhysicalMemoryPages: " + metrics.getPhysicalMemoryPages(), log);
            printLog("OffHeapSize: " + metrics.getOffHeapSize(), log);
            printLog("OffheapUsedSize: " + metrics.getOffheapUsedSize(), log);
            statsDClient.recordGaugeValue(format("ignite.cache.%s.%s.offHeapUsedSize", cacheName, nodeId), metrics.getOffheapUsedSize());
            printLog("#############################################", log);
        }
    }

    private static void printLog(String msg, IgniteLogger logger) {
        if (logger == null) {
            System.out.println(msg);
        } else {
            logger.info(msg);
        }
    }


    public static StatsDClient statsDClient(IgniteLogger logger) {
        String name = "reproducer";
        String host = "localhost";
        int port = 8125;
        boolean disableStatistic = false;

        StatsDClient statsd;
        try {
            printLog(format("StatsD config. host [%s], name : [%s], disables [%s]", host, name, disableStatistic), logger);
            if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(host)) {
                statsd = new NonBlockingStatsDClient(name, host, port);
            } else {
                statsd = new NoOpStatsDClient();
            }
        } catch (Exception ex) {
            printLog("Can not init statsD client " + ex.getMessage(), logger);
            statsd = new NoOpStatsDClient();
        }
        return statsd;
    }

}
