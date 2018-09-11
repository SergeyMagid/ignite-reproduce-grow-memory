package ua.sam.ignite.shared;

import org.apache.ignite.DataRegionMetrics;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;

import java.util.Collection;

public class MetricInfo implements IgniteCallable<Integer> {

    @IgniteInstanceResource
    Ignite ignite;

    private IgniteLogger log;


    @Override
    public Integer call() throws Exception {
        log = ignite.log();
        showMetric();
        return 2;
    }

    private void showMetric() {
        final String nodeId = ignite.cluster().localNode().id().toString();

        printLog("Show metrics inside ignite " + nodeId, log);
        Collection<DataRegionMetrics> regionsMetrics = ignite.dataRegionMetrics();

        ignite.cacheNames().forEach(s -> {
            printLog("Size : " + ignite.cache(s).size(CachePeekMode.ALL) + " of cache " + s, log);
        });

        for (DataRegionMetrics metrics : regionsMetrics) {
            if ("sysMemPlc".equals(metrics.getName())) {
                continue;
            }
            printLog("#############################################", log);
            printLog(">>> Memory Region Name: " + metrics.getName(), log);
            printLog("AllocationRate: " + metrics.getAllocationRate(), log);
            printLog("PagesFillFactor: " + metrics.getPagesFillFactor(), log);
            printLog("PhysicalMemoryPages: " + metrics.getPhysicalMemoryPages(), log);
            printLog("OffHeapSize: " + metrics.getOffHeapSize(), log);
            printLog("CheckpointBufferSize: " + metrics.getCheckpointBufferSize(), log);
            printLog("CheckpointBufferPages: " + metrics.getCheckpointBufferPages(), log);
            printLog("OffheapUsedSize: " + metrics.getOffheapUsedSize(), log);
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

}
