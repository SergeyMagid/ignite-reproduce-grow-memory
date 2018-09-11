package ua.sam.ignite.loader.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@Slf4j
public class IgniteConfig {

    @Value("${loader.cassandra.contact-points}")
    private String contactPoints;
    @Value("${loader.cassandra.keyspace-name:'contacts_dump'}")
    private String keyspace;
    @Value("${loader.cassandra.table-name:'contacts_partitioned'}")
    private String table;

    @Value("${ignite.client.mode:true}")
    private boolean clientMode;
    @Value("${ignite.nodes:127.0.0.1:47500..47509}")
    private String igniteNodes;

    @Bean
    public Ignite ignite() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName("springDataNode");
        cfg.setWorkDirectory("/tmp/ignite");

        cfg.setClientMode(clientMode);
        cfg.setPeerClassLoadingEnabled(false);
        cfg.setMetricsLogFrequency(0);


        DataRegionConfiguration defStorage = new DataRegionConfiguration();
        defStorage.setMetricsEnabled(true);
        cfg.setDataStorageConfiguration(new DataStorageConfiguration());
        cfg.getDataStorageConfiguration()
                .setDefaultDataRegionConfiguration(defStorage);

        {
            TcpDiscoverySpi discoSpi = new TcpDiscoverySpi();
            TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
            ipFinder.setAddresses(Arrays.asList(igniteNodes.split("\\s*,\\s*")));
            discoSpi.setIpFinder(ipFinder);
            cfg.setDiscoverySpi(discoSpi);
            System.out.println("===========> initialized");
        }

        return Ignition.start(cfg);
    }
}
