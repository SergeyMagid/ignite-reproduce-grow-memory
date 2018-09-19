package ua.sam.ignite.loader.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.sam.ignite.shared.loader.ContactLoadFactory;

import java.util.Arrays;

@Configuration
@Slf4j
public class IgniteConfig {


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

        DataStorageConfiguration dsCfg = new DataStorageConfiguration();
        dsCfg.setDefaultDataRegionConfiguration(defStorage);
        dsCfg.setDataRegionConfigurations(createDataReg(1), createDataReg(2), createDataReg(5592));
        cfg.setDataStorageConfiguration(dsCfg);


        {
            CacheConfiguration cacheConfig = new CacheConfiguration("c_observer");
            cacheConfig.setCacheMode(CacheMode.PARTITIONED);
            cacheConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC);

            cacheConfig.setCacheStoreFactory(new ContactLoadFactory());

            cfg.setCacheConfiguration(cacheConfig);
        }

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

    @NotNull
    private DataRegionConfiguration createDataReg(int orgId) {
        DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
        dataRegionConfiguration.setMetricsEnabled(true);
        dataRegionConfiguration.setName("contactsEx."+orgId);
        return dataRegionConfiguration;
    }
}
