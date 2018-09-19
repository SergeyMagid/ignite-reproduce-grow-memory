package ua.sam.ignite.loader;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.sam.ignite.shared.MetricInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LoaderApplicationTests {

    @Autowired
    Ignite ignite;

    @Rule
    public TestName name = new TestName();

    @Before
    public void beforeTest() {
        log.info("\n Run test " + name.getMethodName() + " \n ");
        ignite.cacheNames().forEach(s -> {
            ignite.cache(s).clear();
        });

        runMetricMonitoring();
    }

    @After
    public void afterTest() {
        log.info("\n END test " + name.getMethodName() + " \n ");
    }

    @Test
    public void testStreamAndUpdateContactWithoutBinary() {

        for (int z = 0; z < 500_000; z++) {
            ignite.cache("c_observer").loadCache(null);
            sleepUninterruptibly(25, TimeUnit.SECONDS);
        }
    }

    private void runMetricMonitoring() {
        Executors.newSingleThreadExecutor().execute(() -> {
            for (int i = 0; i < 1000; i++) {
                ignite.compute().broadcast(new MetricInfo()).forEach(integer -> {
                });
                sleepUninterruptibly(10, TimeUnit.SECONDS);
            }
        });
    }

}
