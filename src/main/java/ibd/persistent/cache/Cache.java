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
import ibd.persistent.PageHeader;
import ibd.persistent.PageSerialization;
import java.util.Collection;

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
public abstract class Cache<P extends Page> extends PageFile<P> {

    /**
     * Cache size in bytes.
     */
    protected int cacheSizeBytes;

    /**
     * The maximum number of objects in this cache.
     */
    protected int cacheSize;

    /**
     * The underlying file of this cache. If a page is dropped it is written
     * to the file.
     */
    protected PageFile<P> file;

    /**
     * Initializes this cache with the specified parameters.
     *
     * @param cacheSizeBytes the maximum number of bytes for this cache
     */
    public Cache(int cacheSizeBytes) {
        this.cacheSizeBytes = cacheSizeBytes;
    }

    protected abstract P getFromCache(int pageID);

    protected abstract void addToCache(int pageID, P page);

    protected abstract void deleteFromCache(int pageID);

    protected abstract void initCache();

    protected abstract void clearCache();

    protected abstract Collection<P> getCachePages();
    
    public void setPageFile(PageFile<P> file){
        this.file = file;
    }
    

    /**
     * Retrieves a page from the cache. The retrieved page becomes the MRU (most
     * recently used) page.
     *
     * @param pageID the id of the page to be returned
     * @return the page associated to the id or null if no value with this key
     * exists in the cache
     */
    @Override
    public synchronized P readPage(int pageID) {
        engine.info.Parameters.BLOCKS_ACCESSED++;
        P page = getFromCache(pageID);
        if (page == null) {
            page = file.readPage(pageID);
            addToCache(pageID, page);
        }
        return page;
    }

    @Override
    public synchronized void writePage(int pageID, P page) {
        page.setDirty(true);
        addToCache(pageID, page);

    }

    @Override
    public void deletePage(int pageID) {
        deleteFromCache(pageID);
        file.deletePage(pageID);
    }

    /**
     * Write page through to disk.
     *
     * @param page page
     */
    protected void expirePage(P page) {
        if (page.isDirty()) {
            file.writePage(page);
        }
    }

    @Override
    public int setPageID(P page) {
        int pageID = file.setPageID(page);
        return pageID;
    }

    @Override
    public int getNextPageID() {
        return file.getNextPageID();
    }

    @Override
    public void setNextPageID(int nextPageID) {
        file.setNextPageID(nextPageID);
    }

    @Override
    public int getPageSize() {
        return file.getPageSize();
    }

    @Override
    public boolean initialize(PageHeader header) {
        boolean created = file.initialize(header);
        // Compute the actual cache size.
        this.cacheSize = cacheSizeBytes / file.getHeader().getPageSize();
        //System.out.println("cache size:"+cacheSize);

        if (this.cacheSize <= 0) {
            //throw new AbortException("Invalid cache size: " + cacheSizeBytes + " / " + header.getPageSize() + " = " + cacheSize);
        }

        initCache();

        return created;
    }

    @Override
    public void reopen() throws Exception{
        //flush();
        file.reopen();
    }
    
    @Override
    public void close() {
        //flush();
        file.close();
    }

    @Override
    public void clear() {
        file.clear();
    }

    /**
     * Flushes this caches by writing any entry to the underlying file.
     */
    @Override
    public void flush() {
        Collection<P> pages = getCachePages();
        for (P object : pages) {
            expirePage(object);
        }
        clearCache();
        file.flush();
    }

    @Override
    public PageHeader getHeader() {
        return file.getHeader();
    }

    @Override
    public void setPageSerialization(PageSerialization pageSerialization) {
        file.setPageSerialization(pageSerialization);
    }

    @Override
    public void reset() {
        file.reset();
    }

    /**
     * Sets the maximum size of this cache.
     *
     * @param cacheSize the cache size to be set
     */
//  public void setCacheSize(int cacheSize) {
//    this.cacheSize = cacheSize;
//
//    long toDelete = map.size() - this.cacheSize;
//    if(toDelete <= 0) {
//      return;
//    }
//
//    List<Integer> keys = new ArrayList<>(map.keySet());
//    Collections.reverse(keys);
//
//    for(Integer id : keys) {
//      P page = map.remove(id);
//      file.writePage(page);
//    }
//  }
}
