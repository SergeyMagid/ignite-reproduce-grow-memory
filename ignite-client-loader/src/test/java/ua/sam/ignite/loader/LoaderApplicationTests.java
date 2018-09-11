package ua.sam.ignite.loader;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
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
import ua.sam.ignite.shared.data.ContactWithBinary;
import ua.sam.ignite.shared.data.ContactWithoutBinary;
import ua.sam.ignite.shared.data.CustomInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.IntStream;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
//@ActiveProfiles("server-mode")
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

        prepareCache();
        runMetricMonitoring();

    }

    @After
    public void afterTest() {
        log.info("\n END test " + name.getMethodName() + " \n ");
    }

    @Test
    public void testStreamAndUpdateContactWithBinary() {

        //prepare base contacts custom fields
        HashMap<String, BinaryObject> baseCustomFields = new HashMap<>();
        IntStream.range(0, 20).forEach(value -> {
            baseCustomFields.put("customX" + value, ignite.binary().toBinary(createNewCIWithTime()));
        });

        for (int z = 0; z < 50_000; z++) {

            //Fill base contacts
            {
                IgniteCache<Integer, ContactWithBinary> cache = ignite.getOrCreateCache("contactsEx");
                IntStream.range(0, 100_000).forEach(counter -> {
                    Lock lock = cache.lock(counter);
                    lock.lock();
                    try {
                        ContactWithBinary c = new ContactWithBinary();
                        c.setCustomFields(baseCustomFields);
                        c.setId(counter);
                        cache.put(counter, c);
                    } finally {
                        lock.unlock();
                    }

                });
            }

            //////simulate events
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                {
                    IgniteCache<Integer, ContactWithBinary> cache = ignite.getOrCreateCache("contactsEx");
                    IntStream.range(0, 100_000).forEach(counter -> {

                        Lock lock = cache.lock(counter);
                        lock.lock();
                        try {

                            ContactWithBinary c = cache.get(counter);
                            Map<String, BinaryObject> customFields = c.getCustomFields();
                            customFields.put("customY",
                                    ignite.binary().toBinary(createNewCIWithTime())
                            );
                            cache.put(counter, c);
                            sleepUninterruptibly(1, TimeUnit.MILLISECONDS);

                        } finally {
                            lock.unlock();
                        }
                    });
                    }
                executorService.shutdown();
            });

        }

    }

    @Test
    public void testStreamAndUpdateContactWithoutBinary() {

        //prepare base contacts custom fields
        HashMap<String, CustomInfo> baseCustomFields = new HashMap<>();
        IntStream.range(0, 20).forEach(value -> {
            baseCustomFields.put("customX" + value, createNewCIWithTime());
        });

        for (int z = 0; z < 50_000; z++) {

            //Fill base contacts
            {
                IgniteCache<Integer, ContactWithoutBinary> cache = ignite.getOrCreateCache("contactsEx");
                IntStream.range(0, 100_000).forEach(counter -> {
                    Lock lock = cache.lock(counter);
                    lock.lock();
                    try {
                        ContactWithoutBinary c = new ContactWithoutBinary();
                        c.setCustomFields(baseCustomFields);
                        c.setId(counter);
                        cache.put(counter, c);
                    } finally {
                        lock.unlock();
                    }

                });
            }

            //////simulate events
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                {
                    IgniteCache<Integer, ContactWithoutBinary> cache = ignite.getOrCreateCache("contactsEx");
                    IntStream.range(0, 20_000).forEach(counter -> {

                        Lock lock = cache.lock(counter);
                        lock.lock();
                        try {
                            ContactWithoutBinary c = cache.get(counter);
                            Map<String, CustomInfo> m_customFields = c.getCustomFields();
                            CustomInfo value = new CustomInfo();
                            value.setUpdateTime(new Date());
                            m_customFields.put("customY", value);
                            cache.put(counter, c);
//                            sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
                        } finally {
                            lock.unlock();
                        }
                    });
                }
                executorService.shutdown();
            });

        }

    }

    private void prepareCache() {
        CacheConfiguration<Integer, ContactWithBinary> cfg = new CacheConfiguration<>("contactsEx");
        cfg.setCacheMode(CacheMode.PARTITIONED);
        cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        cfg.setStoreKeepBinary(true);
        ignite.getOrCreateCache(cfg);
    }


    private CustomInfo createNewCIWithTime() {
        CustomInfo customInfo = new CustomInfo();
        customInfo.setUpdateTime(new Date());
        return customInfo;
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
