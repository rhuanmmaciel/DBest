package ibd.persistent;

import java.util.Arrays;

public class IntegerArray implements NumberArrayAdapter<Integer, IntegerArray> {
  private static final int MAX_ARRAY_SIZE = 2147483642;
  
  private static final int LAST_GROW_SIZE = 1431655761;
  
  public int[] data;
  
  public int size;
  
  public IntegerArray() {
    this(11);
  }
  
  public IntegerArray(int initialsize) {
    if (initialsize < 0) {
      initialsize = 11;
    } else if (initialsize > 2147483642) {
      initialsize = 2147483642;
    } 
    this.data = new int[initialsize];
    this.size = 0;
  }
  
  public IntegerArray(IntegerArray existing) {
    this.data = Arrays.copyOf(existing.data, existing.size);
    this.size = existing.size;
  }
  
  public void clear() {
    this.size = 0;
  }
  
  public void add(int attribute) {
    if (this.data.length == this.size)
      grow(); 
    this.data[this.size++] = attribute;
  }
  
  private void grow() throws OutOfMemoryError {
    if (this.data.length == 2147483642)
      throw new OutOfMemoryError("Array size has reached the Java maximum."); 
    int newsize = (this.size >= 1431655761) ? 2147483642 : (this.size + (this.size >> 1) + 1);
    this.data = Arrays.copyOf(this.data, newsize);
  }
  
  public int get(int pos) {
    if (pos < 0 || pos > this.size)
      throw new ArrayIndexOutOfBoundsException(pos); 
    return this.data[pos];
  }
  
  public void set(int pos, int value) {
    if (pos < 0 || pos > this.size)
      throw new ArrayIndexOutOfBoundsException(pos); 
    if (pos == this.size) {
      add(value);
      return;
    } 
    this.data[pos] = value;
  }
  
  public void swap(int p1, int p2) {
    if (p1 < 0 || p1 > this.size)
      throw new ArrayIndexOutOfBoundsException(p1); 
    if (p2 < 0 || p2 > this.size)
      throw new ArrayIndexOutOfBoundsException(p2); 
    int tmp = this.data[p1];
    this.data[p1] = this.data[p2];
    this.data[p2] = tmp;
  }
  
  public void remove(int start, int len) {
    int end = start + len;
    if (end > this.size)
      throw new ArrayIndexOutOfBoundsException(end); 
    System.arraycopy(this.data, end, this.data, start, this.size - end);
    this.size -= len;
  }
  
  public void insert(int pos, int val) {
    if (this.size == this.data.length) {
      if (this.data.length == 2147483642)
        throw new OutOfMemoryError("Array size has reached the Java maximum."); 
      int newsize = (this.size >= 1431655761) ? 2147483642 : (this.size + (this.size >> 1) + 1);
      int[] oldd = this.data;
      this.data = new int[newsize];
      System.arraycopy(oldd, 0, this.data, 0, pos);
      System.arraycopy(oldd, pos, this.data, pos + 1, this.size - pos);
    } else {
      System.arraycopy(this.data, pos, this.data, pos + 1, this.size - pos);
    } 
    this.data[pos] = val;
    this.size++;
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  public void sort() {
    Arrays.sort(this.data, 0, this.size);
  }
  
  public int size(IntegerArray array) {
    return array.size;
  }
  
  public Integer get(IntegerArray array, int off) throws IndexOutOfBoundsException {
    return Integer.valueOf(array.data[off]);
  }
  
  public double getDouble(IntegerArray array, int off) throws IndexOutOfBoundsException {
    return array.data[off];
  }
  
  public float getFloat(IntegerArray array, int off) throws IndexOutOfBoundsException {
    return array.data[off];
  }
  
  public int getInteger(IntegerArray array, int off) throws IndexOutOfBoundsException {
    return array.data[off];
  }
  
  public short getShort(IntegerArray array, int off) throws IndexOutOfBoundsException {
    return (short)array.data[off];
  }
  
  public long getLong(IntegerArray array, int off) throws IndexOutOfBoundsException {
    return array.data[off];
  }
  
  public byte getByte(IntegerArray array, int off) throws IndexOutOfBoundsException {
    return (byte)array.data[off];
  }
  
  public int[] toArray() {
    return Arrays.copyOf(this.data, this.size);
  }
}
