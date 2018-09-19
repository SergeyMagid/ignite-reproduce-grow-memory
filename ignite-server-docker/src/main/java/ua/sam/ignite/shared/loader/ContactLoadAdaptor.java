package ua.sam.ignite.shared.loader;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.resources.IgniteInstanceResource;
import ua.sam.ignite.shared.data.ContactDto;
import ua.sam.ignite.shared.data.GroupInfo;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ContactLoadAdaptor extends CacheStoreAdapter<Integer, ContactDto> {

    @IgniteInstanceResource
    protected Ignite ignite;

    private static Map<Integer, Integer> fieldsDropdown;

    static {

        fieldsDropdown = new HashMap<>();
        fieldsDropdown.put(40040,45564);
        fieldsDropdown.put(40041,45567);
        fieldsDropdown.put(40042,45569);
        fieldsDropdown.put(40043,45571);
        fieldsDropdown.put(40044,45573);
        fieldsDropdown.put(40046,45577);
        fieldsDropdown.put(40666,46169);
        fieldsDropdown.put(40896,46373);
        fieldsDropdown.put(62619,66969);
        fieldsDropdown.put(62620,66971);
        fieldsDropdown.put(62621,66973);
        fieldsDropdown.put(73665,77579);

    }


    @Override
    public void loadCache(IgniteBiInClosure<Integer, ContactDto> clo, Object... args) {
        loadCache( ignite.getOrCreateCache(configureCache("contactsEx.2")) );
    }

    private void loadCache(IgniteCache<Integer, ContactDto> cache) {

        try {
            File file = new File("/opt/ignite/apache-ignite-fabric/libs/5.csv");
            CsvReader csvReader = new CsvReader();

            try (CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8)) {
                CsvRow row;
                while ((row = csvParser.nextRow()) != null) {
                    Integer contactId = Integer.valueOf(row.getField(0));
                    String staticGroups = row.getField(1);

                    ContactDto c = new ContactDto();
                    c.setContactId(contactId);
                    c.setStaticFields(convert(staticGroups));
                    c.setFirstName(row.getField(2));
                    c.setFieldsDropdown(fieldsDropdown);

                    cache.put(contactId, c);
                }
            }


            System.out.println("\n\n>>> END LOAD CACHE <<<<\n\n");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Map<Integer, BinaryObject> convert(String field) {
        if ("{}".equals(field)) {
            return new HashMap<>();
        }
        return Arrays.stream(field.replace("\"","").replace("{", "").replace("}", "").split(","))
                .map(s -> s.split(":"))
                .collect(Collectors.toMap(
                        d -> Integer.valueOf(d[0].trim()),
                        d -> {
                            BinaryObjectBuilder builder = ignite.binary().builder(GroupInfo.class.getName());
                            builder.setField(GroupInfo.FIELD_UPDATE_TIME, new Date(Long.valueOf(d[1])) );
                            builder.setField(GroupInfo.FIELD_DELETED, false );
                            return builder.build();
                        }));
    }


    private CacheConfiguration<Integer, ContactDto> configureCache(String cacheName){
        CacheConfiguration cacheConfig = new CacheConfiguration(cacheName);
        cacheConfig.setCacheMode(CacheMode.PARTITIONED);
        cacheConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC);

        cacheConfig.setDataRegionName(cacheName);

        cacheConfig.setIndexedTypes(Integer.class, ContactDto.class);

        return cacheConfig;
    }


    ///////////////////////////
    @Override
    public ContactDto load(Integer key) throws CacheLoaderException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void write(Cache.Entry<? extends Integer, ? extends ContactDto> entry) throws CacheWriterException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        throw new RuntimeException("Not implemented");
    }
}
