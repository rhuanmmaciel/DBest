/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.locktable.items;

/**
 *
 * @author Sergio
 */
public class Interval implements Comparable<Interval> {
    public final long min;  // min endpoint
    public final long max;  // max endpoint

    // precondition: min <= max
    public Interval(long min, long max) {
        if (min <= max) {
            this.min = min;
            this.max = max;
        }
        else throw new RuntimeException("Illegal interval");
    }

    @Override
    public boolean equals(Object obj){
    if (obj instanceof Interval){
        Interval i1 = (Interval)obj;
        if (min!=i1.min) return false;
        if (max!=i1.max) return false;
        return true;
    }
    return false;
    }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 79 * hash + (int) (this.min ^ (this.min >>> 32));
            hash = 79 * hash + (int) (this.max ^ (this.max >>> 32));
            return hash;
        }
    // does this interval intersect that one?
    public boolean intersects(Interval that) {
        if (that.max < this.min) return false;
        if (this.max < that.min) return false;
        return true;
    }

    public boolean contains(int x) {
        return (min <= x) && (x <= max);
    }

    public int compareTo(Interval that) {
        if      (this.min < that.min) return -1;
        else if (this.min > that.min) return +1;
        else if (this.max < that.max) return -1;
        else if (this.max > that.max) return +1;
        else                          return  0;
    }

    public String toString() {
        return "[" + min + ", " + max + "]";
    }

        
    }