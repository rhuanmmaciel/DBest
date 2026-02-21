/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.persistent.cache;

import ibd.persistent.Page;
import ibd.persistent.PageFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 *
 * @author Sergio
 */
public class MidPointCache2<P extends Page> extends Cache<P> {

    protected Hashtable<Integer, P> blocksBuffer = new Hashtable();
    LinkedList<P> newBlocksList = new LinkedList();
    LinkedList<P> oldBlocksList = new LinkedList();

    /**
     * Initializes this cache with the specified parameters.
     *
     * @param cacheSizeBytes the maximum number of bytes for this cache
     */
    public MidPointCache2(int cacheSizeBytes) {
        super(cacheSizeBytes);
    }

    @Override
    protected P getFromCache(int pageID) {
        P block = blocksBuffer.get(pageID);
        if (block != null) {
            // We remove from both lists since we don't know where it is
            newBlocksList.remove(block);
            oldBlocksList.remove(block);

            // If it was already on buffer and we are accessing
            // it again, it should go to the HEAD of the new list
            newBlocksList.addFirst(block);
            // If NEW was full and some block was promoted
            // then the statement below is true
            if (newBlocksList.size() > cacheSize / 2) { // Let's say that New and Old list have both same size
                P lastBlockFromNew = newBlocksList.removeLast();
                oldBlocksList.addFirst(lastBlockFromNew);
                // There is no need to check if the OLD list is full because if it was on buffer
                // we are working inside the boundaries. That is, we are just moving things around.
            }
        }
        return block;
    }

    @Override
    protected void addToCache(int pageID, P page) {

        //if buffer is full, needs to flush a block
        if (oldBlocksList.size() == cacheSize / 2) {
            P b = oldBlocksList.removeLast(); // Remove from List
            expirePage(b); // Save on disk
            blocksBuffer.remove(b.getPageID()); // Remove from Buffer
//            System.out.printf("Removing from buffer"+ b.block_id);
        }

//        System.out.println("needed to load block "+block_id);
        // If we reach this point means that the block
        // was not in buffer, so we must insert it in the HEAD
        // of the old list.
        blocksBuffer.put(pageID, page);
        oldBlocksList.addFirst(page);

    }

    @Override
    protected void deleteFromCache(int pageID) {
        P block = blocksBuffer.get(pageID);
        if (block != null) {
            oldBlocksList.remove(pageID);
            newBlocksList.remove(block);
        }

    }

    /**
     * Clears this cache.
     */
    @Override
    public void clearCache() {
        blocksBuffer.clear();
        oldBlocksList.clear();
        newBlocksList.clear();
    }

    protected void initCache() {

    }

    protected Collection<P> getCachePages() {

        Collection<P> result = new ArrayList(newBlocksList);
        result.addAll(oldBlocksList);
        return result;
    }
}
