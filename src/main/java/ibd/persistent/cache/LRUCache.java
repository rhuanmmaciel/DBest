/*
 * This file is part of ELKI:
 * Environment for Developing KDD-Applications Supported by Index-Structures
 *
 * Copyright (C) 2022
 * ELKI Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ibd.persistent.cache;

import ibd.persistent.Page;
import ibd.persistent.PageFile;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An LRU cache, based on <code>LinkedHashMap</code>.<br>
 * This cache has a fixed maximum number of objects (<code>cacheSize</code>). If
 * the cache is full and another object is added, the LRU (least recently used)
 * object is dropped.
 *
 * @author Elke Achtert
 * @since 0.1
 *
 * @assoc - - - PageFile
 *
 * @param <P> Page type
 */
public class LRUCache<P extends Page> extends Cache<P> {

    /**
     * The map holding the objects of this cache.
     */
    private LinkedHashMap<Integer, P> map;

    /**
     * Initializes this cache with the specified parameters.
     *
     * @param cacheSizeBytes the maximum number of bytes for this cache
     */
    public LRUCache(int cacheSizeBytes) {
        super(cacheSizeBytes);
    }

    protected P getFromCache(int pageID) {
        return map.get(pageID);
    }

    protected void addToCache(int pageID, P page) {
        map.put(pageID, page);
    }

    protected void deleteFromCache(int pageID) {
        map.remove(pageID);
    }

    /**
     * Clears this cache.
     */
    @Override
    public void clearCache() {
        map.clear();
    }

    protected void initCache() {
        float hashTableLoadFactor = 0.75f;
        int hashTableCapacity = (int) Math.ceil(cacheSize / hashTableLoadFactor) + 1;

        this.map = new LinkedHashMap<Integer, P>(hashTableCapacity, hashTableLoadFactor, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, P> eldest) {
                if (size() > LRUCache.this.cacheSize) {
                    expirePage(eldest.getValue());
                    return true;
                }
                return false;
            }
        };
    }

    protected Collection<P> getCachePages() {
        return map.values();
    }

}
