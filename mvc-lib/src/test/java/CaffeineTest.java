import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class CaffeineTest {
    @Test
    public void ShouDongCaffeine() {
        Cache<String, DataObject> cache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();

        String key = "A";
        DataObject dataObject = new DataObject(key);
//        if (dataObject == null) {
//            cache.put(key, dataObject);
//        }
        if (dataObject == null) {
            dataObject = cache
                    .get(key, k -> DataObject.get("Data for A"));
        }
        System.out.println("dataObject = " + dataObject);
    }

    @Test
    public void SyncCaffeine() {
        LoadingCache<String, DataObject> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(k -> DataObject.get("Data for " + k));

        String key = "A";

        DataObject dataObject = cache.get(key);

        Map<String, DataObject> dataObjectMap
                = cache.getAll(Arrays.asList("A", "B", "C"));

        System.out.println("dataObjectMap = " + dataObjectMap);

        assertEquals(3, dataObjectMap.size());

        assertNotNull(dataObject);
        assertEquals("Data for " + key, dataObject.getData());
    }
}
