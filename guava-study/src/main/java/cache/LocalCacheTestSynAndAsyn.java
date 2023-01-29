/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package cache;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * @author yueyi
 * @version : LocalCacheTestSynAndAsyn.java, v 0.1 2022年03月11日 5:40 下午 yueyi Exp $
 */
public class LocalCacheTestSynAndAsyn {

    AsyncLoadingCache<Integer, String> asyncLoadingCache;

    public LocalCacheTestSynAndAsyn() {

    }

    private String computeValue(Integer key) {
        return "value_" + key;
    }
}