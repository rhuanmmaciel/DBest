/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.locktable.items;

import ibd.transaction.concurrency.Item;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Sergio
 */
    public class HashItem implements ItemCollection {

    
    private HashMap<Long, Item> items = new HashMap<>();
    
    @Override
    public Item addItem(long lower, long higher) {
        
        Item item = getItem(lower, higher);
        if (item==null){
            item = new Item(lower, higher);
            items.put(lower, item);
        }
        
        return item;
        
    }

    @Override
    public Item getItem(long lower, long higher) {
        return items.get(lower);
    }

    @Override
    public Iterable<Item> getAllItems() {
        return items.values();
    }

    @Override
    public Iterable<Item> getOverlappedItems(long lower, long higher) {
        ArrayList list = new ArrayList();
        Item item = items.get(lower);
        if (item!=null) list.add(item);
        return list;
    }
    
    
    
    
}
