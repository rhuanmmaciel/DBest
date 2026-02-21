/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.locktable.items;

import ibd.transaction.concurrency.Item;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 *
 * @author Sergio
 */
public class IntervalTree implements ItemCollection{

    Hashtable<Interval, Item> table_ = new Hashtable();
    
    
    @Override
    public Item addItem(long lower, long higher) {
        Item item = getItem(lower, higher);
        if (item==null){
            item = new Item(lower, higher);
            Interval interval = new Interval(lower, higher);
            put(interval, item);
            table_.put(interval, item);
        }
        return item;
        
    }

    @Override
    public Item getItem(long lower, long higher) {
        Interval interval = new Interval(lower, higher);
        return table_.get(interval);
    }

    @Override
    public Iterable<Item> getAllItems() {
        Interval interval = new Interval(Long.MIN_VALUE, Long.MAX_VALUE);
        return searchAll(interval);
    }

    @Override
    public Iterable<Item> getOverlappedItems(long lower, long higher) {
        Interval interval = new Interval(lower, higher);
        return searchAll(interval);
        
    }

    private Node root;   // root of the BST

    
    
    
    // BST helper node data type
    private class Node {
        Interval interval;      // key
        Item value;              // associated data
        Node left, right;         // left and right subtrees
        int N;                    // size of subtree rooted at this node
        long max;                  // max endpoint in subtree rooted at this node

        Node(Interval interval, Item value) {
            this.interval = interval;
            this.value    = value;
            this.N        = 1;
            this.max      = interval.max;
        }
    }


   /***************************************************************************
    *  BST search
    ***************************************************************************/

    public boolean contains(Interval interval) {
        return (get(interval) != null);
    }

    // return value associated with the given key
    // if no such value, return null
    public Item get(Interval interval) {
        return get(root, interval);
    }

    private Item get(Node x, Interval interval) {
        if (x == null)                  return null;
        int cmp = interval.compareTo(x.interval);
        if      (cmp < 0) return get(x.left, interval);
        else if (cmp > 0) return get(x.right, interval);
        else              return x.value;
    }


   /***************************************************************************
    *  randomized insertion
    ***************************************************************************/
    public void put(Interval interval, Item value) {
        //if (contains(interval)) { System.out.println("duplicate"); remove(interval);  }
        //root = randomizedInsert(root, interval, value);
        
        if (contains(interval)) { return; }
        root = randomizedInsert(root, interval, value);
    }

    // make new node the root with uniform probability
    private Node randomizedInsert(Node x, Interval interval, Item value) {
        if (x == null) return new Node(interval, value);
        if (Math.random() * size(x) < 1.0) return rootInsert(x, interval, value);
        int cmp = interval.compareTo(x.interval);
        if (cmp < 0)  x.left  = randomizedInsert(x.left,  interval, value);
        else          x.right = randomizedInsert(x.right, interval, value);
        fix(x);
        return x;
    }

    private Node rootInsert(Node x, Interval interval, Item value) {
        if (x == null) return new Node(interval, value);
        int cmp = interval.compareTo(x.interval);
        if (cmp < 0) { x.left  = rootInsert(x.left,  interval, value); x = rotR(x); }
        else         { x.right = rootInsert(x.right, interval, value); x = rotL(x); }
        return x;
    }


   /***************************************************************************
    *  deletion
    ***************************************************************************/
    private Node joinLR(Node a, Node b) { 
        if (a == null) return b;
        if (b == null) return a;

        if (Math.random() * (size(a) + size(b)) < size(a))  {
            a.right = joinLR(a.right, b);
            fix(a);
            return a;
        }
        else {
            b.left = joinLR(a, b.left);
            fix(b);
            return b;
        }
    }

    // remove and return value associated with given interval;
    // if no such interval exists return null
    public Item remove(Interval interval) {
        Item value = get(interval);
        root = remove(root, interval);
        return value;
    }

    private Node remove(Node h, Interval interval) {
        if (h == null) return null;
        int cmp = interval.compareTo(h.interval);
        if      (cmp < 0) h.left  = remove(h.left,  interval);
        else if (cmp > 0) h.right = remove(h.right, interval);
        else              h = joinLR(h.left, h.right);
        fix(h);
        return h;
    }


   /***************************************************************************
    *  Interval searching
    ***************************************************************************/

    // return an interval in data structure that intersects the given inteval;
    // return null if no such interval exists
    // running time is proportional to log N
    public Interval search(Interval interval) {
        return search(root, interval);
    }

    // look in subtree rooted at x
    public Interval search(Node x, Interval interval) {
        while (x != null) {
            if (interval.intersects(x.interval)) return x.interval;
            else if (x.left == null)             x = x.right;
            else if (x.left.max < interval.min)  x = x.right;
            else                                 x = x.left;
        }
        return null;
    }


    // return *all* intervals in data structure that intersect the given interval
    // running time is proportional to R log N, where R is the number of intersections
    public Iterable<Item> searchAll(Interval interval) {
        LinkedList<Item> list = new LinkedList<Item>();
        searchAll(root, interval, list);
        return list;
    }

    // look in subtree rooted at x
    public boolean searchAll(Node x, Interval interval, LinkedList<Item> list) {
         boolean found1 = false;
         boolean found2 = false;
         boolean found3 = false;
         if (x == null)
            return false;
        if (interval.intersects(x.interval)) {
            list.add(x.value);
            found1 = true;
        }
        if (x.left != null && x.left.max >= interval.min)
            found2 = searchAll(x.left, interval, list);
        if (found2 || x.left == null || x.left.max < interval.min)
            found3 = searchAll(x.right, interval, list);
        return found1 || found2 || found3;
    }


   /***************************************************************************
    *  useful binary tree functions
    ***************************************************************************/

    // return number of nodes in subtree rooted at x
    public int size() { return size(root); }
    private int size(Node x) { 
        if (x == null) return 0;
        else           return x.N;
    }

    // height of tree (empty tree height = 0)
    public int height() { return height(root); }
    private int height(Node x) {
        if (x == null) return 0;
        return 1 + Math.max(height(x.left), height(x.right));
    }


   /***************************************************************************
    *  helper BST functions
    ***************************************************************************/

    // fix auxilliar information (subtree count and max fields)
    private void fix(Node x) {
        if (x == null) return;
        x.N = 1 + size(x.left) + size(x.right);
        x.max = max3(x.interval.max, max(x.left), max(x.right));
    }

    private long max(Node x) {
        if (x == null) return Integer.MIN_VALUE;
        return x.max;
    }

    // precondition: a is not null
    private long max3(long a, long b, long c) {
        return Math.max(a, Math.max(b, c));
    }

    // right rotate
    private Node rotR(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        fix(h);
        fix(x);
        return x;
    }

    // left rotate
    private Node rotL(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        fix(h);
        fix(x);
        return x;
    }


   /***************************************************************************
    *  Debugging functions that test the integrity of the tree
    ***************************************************************************/

    // check integrity of subtree count fields
    public boolean check() { return checkCount() && checkMax(); }

    // check integrity of count fields
    private boolean checkCount() { return checkCount(root); }
    private boolean checkCount(Node x) {
        if (x == null) return true;
        return checkCount(x.left) && checkCount(x.right) && (x.N == 1 + size(x.left) + size(x.right));
    }

    private boolean checkMax() { return checkMax(root); }
    private boolean checkMax(Node x) {
        if (x == null) return true;
        return x.max ==  max3(x.interval.max, max(x.left), max(x.right));
    }
    
}
