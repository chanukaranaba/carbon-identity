/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.sso.saml.cache;

import org.wso2.carbon.identity.application.authentication.framework.store.SessionDataStore;
import org.wso2.carbon.identity.application.common.cache.BaseCache;
import org.wso2.carbon.identity.core.util.IdentityUtil;

public class SAMLSSOSessionIndexCache extends BaseCache<String, CacheEntry> {

    private static final String CACHE_NAME = "SAMLSSOSessionIndexCache";
    private static volatile SAMLSSOSessionIndexCache instance;
    private boolean useCache = true;

    private SAMLSSOSessionIndexCache(String cacheName, int timeout) {
        super(cacheName, timeout);
        useCache = !Boolean.parseBoolean(IdentityUtil.getProperty("JDBCPersistenceManager.SessionDataPersist.Only"));
    }

    public static SAMLSSOSessionIndexCache getInstance(int timeout) {
        if (instance == null) {
            synchronized (SAMLSSOSessionIndexCache.class) {
                if (instance == null) {
                    instance = new SAMLSSOSessionIndexCache(CACHE_NAME, timeout);
                }
            }
        }
        return instance;
    }

    public void addToCache(CacheKey key, CacheEntry entry) {
        if (useCache) {
            super.addToCache(((SAMLSSOSessionIndexCacheKey) key).getTokenId(), entry);
        }
        String keyValue = ((SAMLSSOSessionIndexCacheKey) key).getTokenId();
        SessionDataStore.getInstance().storeSessionData(keyValue, CACHE_NAME, entry);
    }

    public CacheEntry getValueFromCache(CacheKey key) {
        CacheEntry cacheEntry = null;
        if (useCache) {
            cacheEntry = super.getValueFromCache(((SAMLSSOSessionIndexCacheKey) key).getTokenId());
        }
        if (cacheEntry == null) {
            String keyValue = ((SAMLSSOSessionIndexCacheKey) key).getTokenId();
            cacheEntry = (SAMLSSOSessionIndexCacheEntry) SessionDataStore.getInstance().
                    getSessionData(keyValue, CACHE_NAME);
        }
        return cacheEntry;
    }

    public void clearCacheEntry(CacheKey key) {
        if (useCache) {
            super.clearCacheEntry(((SAMLSSOSessionIndexCacheKey) key).getTokenId());
        }
        String keyValue = ((SAMLSSOSessionIndexCacheKey) key).getTokenId();
        SessionDataStore.getInstance().clearSessionData(keyValue, CACHE_NAME);
    }
}
