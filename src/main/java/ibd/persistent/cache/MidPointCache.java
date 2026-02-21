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
public class MidPointCache<P extends Page> extends Cache<P> {


    protected Hashtable<Integer, P> newBlocksBuffer = new Hashtable();
    protected Hashtable<Integer, P> oldBlocksBuffer = new Hashtable();
    
    LinkedList<P> newList = new LinkedList();
    LinkedList<P> oldList = new LinkedList();


  /**
   * Initializes this cache with the specified parameters.
   * 
   * @param cacheSizeBytes the maximum number of bytes for this cache
   */
  public MidPointCache(int cacheSizeBytes) {
    super(cacheSizeBytes);
  }

  
    @Override
  protected P getFromCache(int pageID){
    P block = newBlocksBuffer.get(pageID);
        if (block!=null) {
            newList.remove(block);
            newList.addFirst(block);
            return block;
        }
        
        block = oldBlocksBuffer.get(pageID);
        if (block!=null) {
            oldBlocksBuffer.remove(pageID);
            oldList.remove(block);
            
            newBlocksBuffer.put(pageID, block);
            newList.addFirst(block);
            
            if (newList.size()>cacheSize/2){
                
                P last = newList.removeLast();
                newBlocksBuffer.remove(last.getPageID());
                
                oldList.addFirst(last);
                oldBlocksBuffer.put(last.getPageID(), last);
                
            }
            return block;
        }
        
        
        //System.out.println(oldList.size() + newList.size());
        return block;
  }
  
    @Override
  protected void addToCache(int pageID, P page){
            
        if (oldList.size()==cacheSize/2){
            P last = oldList.removeLast();
            oldBlocksBuffer.remove(last.getPageID());
            expirePage(last);
        }
        
        oldList.addFirst(page);
        oldBlocksBuffer.put(page.getPageID(), page);
        
  }
  
    @Override
  protected void deleteFromCache(int pageID) {
    P block = newBlocksBuffer.get(pageID);
    if (block!=null){
        newBlocksBuffer.remove(pageID);
        newList.remove(block);
    }
    block = oldBlocksBuffer.get(pageID);
    if (block!=null){
        oldBlocksBuffer.remove(pageID);
        oldList.remove(block);
    }
  }
  
/**
   * Clears this cache.
   */
  @Override
  public void clearCache() {
    newBlocksBuffer.clear();
    oldBlocksBuffer.clear();
    newList.clear();
    oldList.clear();
  }
  
  protected void initCache(){
  
  }
  
  protected Collection<P> getCachePages(){
      
      Collection<P> result = new ArrayList(newList);
      result.addAll(oldList);
      return result;
  }
}