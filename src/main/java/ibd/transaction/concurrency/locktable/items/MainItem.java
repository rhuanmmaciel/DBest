/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.locktable.items;

import ibd.transaction.concurrency.Item;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Sergio
 */
public class MainItem {

    public static int steps = 0;
    
    public void BuildIntervals(ItemCollection list, int lower, int higher, boolean shuffle) {

        ArrayList<Interval> list_ = new ArrayList();
        
        for (int i = lower; i <= higher; i++) {
            for (int j = i; j <= higher; j++) {
                list_.add(new Interval(i,j));
            }
        }
        System.out.println("generated "+list_.size()+ " items");
        if (shuffle)
            Collections.shuffle(list_);
        for (Interval interval : list_) {
            list.addItem(interval.min, interval.max);
        }
        
        
    }
    
    public void BuildIntervals(ItemCollection list, int size, boolean shuffle) {

        ArrayList<Interval> list_ = new ArrayList();
        for (int i = 1; i <= size; i++) {
                list_.add(new Interval(i,i));
        }
        System.out.println("generated "+list_.size()+ " items");
        if (shuffle)
            Collections.shuffle(list_);
        for (Interval interval : list_) {
            list.addItem(interval.min, interval.max);
        }
    }
    
    public void BuildIntervals(ItemCollection list,  int size, boolean lower, boolean shuffle) {

        ArrayList<Interval> list_ = new ArrayList();
        for (int i = 1; i <= size; i++) {
            if (lower)
                list_.add(new Interval(1,i));
            else list_.add(new Interval(i,size));
        }
        System.out.println("generated "+list_.size()+ " items");
        if (shuffle)
            Collections.shuffle(list_);
        for (Interval interval : list_) {
            list.addItem(interval.min, interval.max);
        }

    }
    
    public ItemCollection getItemCollection()
    {
        return new IntervalTree();
    }

    public void print(Iterable<Item> items){
    int count = 1;
        System.out.println("Returned:");
        for (Item item : items) {
            System.out.println(count+"->"+ item.getLower() + "," + item.getHigher());
            count++;
        }
        System.out.println("steps needed:"+MainItem.steps);
        MainItem.steps = 0;
    }
    
    public void search(ItemCollection list, int lower, int higher){
     Item item = list.getItem(lower, higher);
        if (item!=null) System.out.println("found item  "+item.getLower()+","+item.getHigher());
        else System.out.println("not found item  ");
        
    }
    
    
    
    public static void main(String[] args) {
        MainItem m = new MainItem();
         Iterable<Item> items = null;
         MainItem.steps = 0;
        boolean shuffle = true;
        ItemCollection list = null;
        
        System.out.println("\n***TEST 1*********");
        list = m.getItemCollection();
        m.BuildIntervals(list, 1, 10,shuffle);
        
        System.out.println("\n->GET_OVERLAP");
        System.out.println("should return: 10-1, 10-2, 10-3, 10-4, ...");
        items = list.getOverlappedItems(10, 10);
        m.print(items);
        
        System.out.println("\n->GET_ITEM");
        System.out.println("should find: 10-10");
        m.search(list, 10, 10);
        
        System.out.println("\n->GET_ITEM");
        System.out.println("should miss: 10-100");
        m.search(list, 10, 100);
        
        System.out.println("\n***TEST 2*********");
        list = m.getItemCollection();
        m.BuildIntervals(list, 100, shuffle);
        
        System.out.println("\n->GET_OVERLAP");
        System.out.println("should find: 10-10,11-11,12-12,13-13,14-14,15-15");
        items = list.getOverlappedItems(10, 15);
        m.print(items);
        
        System.out.println("\n->GET_ITEM");
        System.out.println("should find: 10-10");
        m.search(list, 10, 10);
        
        System.out.println("\n->GET_ITEM");
        System.out.println("should miss: 10-100");
        m.search(list, 10, 100);
        
        System.out.println("\n***TEST 3*********");
        list = m.getItemCollection();
        m.BuildIntervals(list, 100, true,shuffle);
        
        System.out.println("->GET_OVERLAP");
        System.out.println("should find: all");
        items = list.getOverlappedItems(1, 1);
        m.print(items);
        
        System.out.println("\n->GET_OVERLAP");
        System.out.println("should find: 1-100");
        items = list.getOverlappedItems(100, 100);
        m.print(items);
        
        System.out.println("\n->GET_ITEM");
        System.out.println("should find: 1-10");
        m.search(list, 1, 10);
        
        System.out.println("\n->GET_ITEM");
        System.out.println("should miss: 10-1");
        m.search(list, 10, 1);
        
        
        
        System.out.println("\n***TEST 4*********");
        list = m.getItemCollection();
        m.BuildIntervals(list,100, false,shuffle);
        
        System.out.println("\n->GET_OVERLAP");
        System.out.println("should find: 1-100,2-100");
        items = list.getOverlappedItems(1, 2);
        m.print(items);
        
        System.out.println("\n->GET_ITEM");
        System.out.println("should find: 10-100");
        m.search(list, 10, 100);
        
        System.out.println("\n->GET_ITEM");
        System.out.println("should not find: 10-99");
        m.search(list, 10, 99);
        
    }

}
