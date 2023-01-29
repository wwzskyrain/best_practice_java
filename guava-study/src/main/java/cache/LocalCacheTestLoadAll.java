package cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author erik.wang
 * @date 2020-06-30 18:49
 * 测试loadAll时，只会加载没有命中的keys.
 * 比如，keys={1,2,3},其中1是命中的，这是loadAll就只会加载2,3
 */
public class LocalCacheTestLoadAll {

    private static final Logger logger = LoggerFactory.getLogger(LocalCacheTestLoadAll.class);

    private LoadingCache<Long, String> cache;

    {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(1000, TimeUnit.SECONDS)
                .build(new CacheLoader<Long, String>() {
                    public String load(@Nonnull Long key) {
                        logger.info("load_key key={} value ={}", key, getValue(key));
                        return getValue(key);
                    }

                    public Map<Long, String> loadAll(@Nonnull Iterable<? extends Long> keys) throws Exception {
                        final Map<Long, String> result = new HashMap<>();
                        keys.forEach(key -> result.put(key, getValue(key)));
                        logger.info("load_key keys={} values ={}", keys, result);
                        return result;
                    }

                });
    }

    public List<String> getAll(List<Long> keys) {
        Map<Long, String> all = cache.getAll(keys);
        return new ArrayList<>(all.values());
    }

    private static String getValue(Long key) {
        return String.format("value_%d", key);
    }

    @Test
    public void test() {

        LocalCacheTestLoadAll localCache = new LocalCacheTestLoadAll();
        List<Long> keys1 = LongStream.range(0, 5).boxed().collect(Collectors.toList());
        List<String> values1 = localCache.getAll(keys1);

        logger.info("value1={}", values1);

        List<Long> keys2 = LongStream.range(0, 10).boxed().collect(Collectors.toList());
        List<String> values2 = localCache.getAll(keys2);
        logger.info("value2={}", values2);

    }

}
