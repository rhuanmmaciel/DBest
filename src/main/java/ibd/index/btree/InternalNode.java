/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Sergio
 */
/**
 * This class represents the internal nodes within the B+ tree that traffic all
 * search/insert/delete operations. An internal node only holds keys; it does
 * not hold dictionary pairs.
 */
public class InternalNode extends Node  {

    int maxDegree;
    int minDegree;
    int degree;
    
    BPlusTree tree;
    
    InternalNode leftSibling;
    InternalNode rightSibling;

    int leftSiblingID = -1;
    int rightSiblingID = -1;

    Key[] keys;
    Node[] childPointers;
    Integer[] childPointersIDs;

    public InternalNode(BPlusTree tree) {
        this.tree = tree;
    }

    /**
     * Constructor
     *
     * @param m: the max degree of the InternalNode
     * @param keys: the list of keys that InternalNode is initialized with
     * @param tree: the tree where this node belongs
     */
    public InternalNode(int m, Key[] keys, BPlusTree tree) {
        this.maxDegree = m;
        //this.minDegree = (int) Math.ceil(m / 2.0);
        setMinDegree();
        this.degree = 0;
        this.keys = keys;
        this.tree = tree;
        this.childPointers = new Node[this.maxDegree + 1];
        this.childPointersIDs = new Integer[this.maxDegree + 1];

    }

    /**
     * Constructor
     *
     * @param m: the max degree of the InternalNode
     * @param keys: the list of keys that InternalNode is initialized with
     * @param pointers: the list of pointers that InternalNode is initialized
     * @param tree: the tree where this node belongs
     * with
     */
    public InternalNode(int m, Key[] keys, Node[] pointers, BPlusTree tree) {
        this.maxDegree = m;
        //this.minDegree = (int) Math.ceil(m / 2.0);
        setMinDegree();
        this.degree = Utils.linearNullSearch(pointers);
        this.keys = keys;
        this.tree = tree;
        this.childPointers = pointers;
        this.childPointersIDs = new Integer[this.maxDegree + 1];
        for (int i = 0; i < pointers.length; i++) {
            Node node = pointers[i];
            if (node == null) {
                break;
            }
            childPointersIDs[i] = node.getPageID();
        }

    }

    public InternalNode(int m, Key[] keys, Integer[] pointers, BPlusTree tree) {
        this.maxDegree = m;
        //this.minDegree = (int) Math.ceil(m / 2.0);
        setMinDegree();
        this.degree = Utils.linearNullSearch(pointers);
        this.keys = keys;
        this.tree = tree;
        this.childPointers = new Node[pointers.length];
        this.childPointersIDs = pointers;


    }

    private void setMinDegree() {
        this.minDegree = (int) Math.ceil(maxDegree / 2.0);
    }

    /**
     * This method appends 'pointer' to the end of the childPointers instance
     * variable of the InternalNode object. The pointer can point to an
     * InternalNode object or a LeafNode object since the formal parameter
     * specifies a Node object.
     *
     * @param pointer: Node pointer that is to be appended to the childPointers
     * list
     */
    public void appendChildPointer(Node pointer) {
        this.childPointers[degree] = pointer;
        this.childPointersIDs[degree] = pointer.getPageID();
        pointer.setParentNode(this);
        pointer.setParentID(this.getPageID());
        
        this.degree++;
    }
    
    
    /**
     * This method appends all entries of an internal node into the end of this internal node
     * The entries refer to the childPointers and the key values.
     * @param in: The node from where entries are copied
     */
    public void appendAllEntries(InternalNode in){
    appendChildPointer(in.childPointers[0]);
            for (int i = 1; i < in.degree; i++) {
                    appendKey(in.keys[i-1]);
                    appendChildPointer(in.childPointers[i]);
            }
    }

    /**
     * Given a Node pointer, this method will return the index of where the
     * pointer lies within the childPointers instance variable. If the pointer
     * can't be found, the method returns -1.
     *
     * @param pointer: a Node pointer that may lie within the childPointers
     * instance variable
     * @return the index of 'pointer' within childPointers, or -1 if 'pointer'
     * can't be found
     */
    public int findIndexOfPointer(Node pointer) {
        for (int i = 0; i < childPointersIDs.length; i++) {
            if (childPointersIDs[i] == pointer.getPageID()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Given a pointer to a Node object and an integer index, this method
     * inserts the pointer at the specified index within the childPointers
     * instance variable. As a result of the insert, some pointers may be
     * shifted to the right of the index.
     *
     * @param pointer: the Node pointer to be inserted
     * @param index: the index at which the insert is to take place
     */
    public void insertChildPointer(Node pointer, int index) {
        for (int i = degree - 1; i >= index; i--) {
            childPointers[i + 1] = childPointers[i];
            childPointersIDs[i + 1] = childPointersIDs[i];
        }
        this.childPointers[index] = pointer;
        this.childPointersIDs[index] = pointer.getPageID();
        pointer.setParentNode(this);
        this.degree++;
    }

    /**
     * This simple method determines if the InternalNode is deficient or not. An
     * InternalNode is deficient when its current degree of children falls below
     * the allowed minimum.
     *
     * @return a boolean indicating whether the InternalNode is deficient or not
     */
    public boolean isDeficient() {
        return this.degree < this.minDegree;
    }

    /**
     * This simple method determines if the InternalNode is capable of lending
     * one of its dictionary pairs to a deficient node. An InternalNode can give
     * away a dictionary pair if its current degree is above the specified
     * minimum.
     *
     * @return a boolean indicating whether or not the InternalNode has enough
     * dictionary pairs in order to give one away.
     */
    public boolean isLendable() {
        return this.degree > this.minDegree;
    }

    /**
     * This simple method determines if the InternalNode is capable of being
     * merged with. An InternalNode can be merged with if it has the minimum
     * degree of children.
     *
     * @return a boolean indicating whether or not the InternalNode can be
     * merged with
     */
    public boolean isMergeable() {
        return this.degree == this.minDegree;
    }

    /**
     * This simple method determines if the InternalNode is considered overfull,
     * i.e. the InternalNode object's current degree is one more than the
     * specified maximum.
     *
     * @return a boolean indicating if the InternalNode is overfull
     */
    public boolean isOverfull() {
        return this.degree == maxDegree + 1;
    }

    
    
    /**
     * Given a pointer to a Node object, this method inserts the pointer to the
     * beginning of the childPointers instance variable.
     *
     * @param pointer: the Node object to be prepended within childPointers
     */
    public void prependChildPointer(Node pointer) {
        for (int i = degree - 1; i >= 0; i--) {
            childPointers[i + 1] = childPointers[i];
            childPointersIDs[i + 1] = childPointersIDs[i];
        }
        this.childPointers[0] = pointer;
        this.childPointersIDs[0] = pointer.getPageID();
        this.degree++;
        pointer.setParentNode(this);
        pointer.setParentID(this.getPageID());
    }
    
    
    /**
     * This method inserts all entries of an internal node to the
     * beginning of this node.
     *
     * @param in: the node from where the entries are copied
     */
    public void prependAllEntries(InternalNode in) {
    
        //only here the number of keys is the same as the number of points.
        //reason: a key was addded before this method is called
        int nkeys = degree;
        for (int i = nkeys-1; i >= 0; i--) {
            this.keys[i+in.degree-1] = this.keys[i];
        }
        for (int i = degree-1; i >= 0; i--) {
            this.childPointers[i+in.degree] = this.childPointers[i];
            this.childPointersIDs[i+in.degree] = this.childPointersIDs[i];
        }
        
        int i = 0;
        for (i = 0; i <in.degree - 1; i++) {
            this.setPointer(i, in.childPointers[i]);
            this.setKey(i, in.keys[i]);
        }
        this.setPointer(i, in.childPointers[i]);
        this.degree+=in.degree;
        
    }
    
    /**
     * This method inserts all entries of an internal node to the
     * beginning of this node. Does the same as prependAllEntries, but in a last efficient way.
     *
     * @param in: the node from where the entries are copied
     */
    public void prependAllEntries1(InternalNode in) {
        prependChildPointer(in.childPointers[in.degree-1]);
        for (int i = in.degree - 2; i >= 0; i--) {
            prependChildKey(in.keys[i]);
            prependChildPointer(in.childPointers[i]);
        }
    }

    
    /**
     * This method inserts a key into the beginning of this node. 
     *
     * @param key: the key to be inserted
     */
    public void prependChildKey(Key key) {
        for (int i = keys.length - 2; i >= 0; i--) {
            keys[i + 1] = keys[i];
        }
        this.keys[0] = key;
    }

    /**
     * This method sets keys[index] to null. This method is used within the
     * parent of a merging, deficient LeafNode.
     *
     * @param index: the location within keys to be set to null
     */
    public void removeKey(int index) {
        this.keys[index] = null;
    }
    
    
    /**
     * This method removes an entry (key + pointer) from an internal node. 
     * The entry refers to a pointer and the key preceeding the pointer.
     *
     * @param index: the index of the pointer
     */
    public void removeEntry(int index) {
        removeKey(index - 1);
        removePointer(index);
        shiftNullKeysToEnd(index-1);
        shiftNullNodesToEnd(index);
        //checkKeys();
    }
    
    /**
     * This method removes an entry (key + pointer) from an internal node. 
     * The entry refers to a pointer and the key succeeding the pointer.
     * 
     * @param index: the index of the pointer
     */
    public void removeEntry1(int index) {
        removePointer(index);
        removeKey(index);
        shiftNullKeysToEnd(index);
        shiftNullNodesToEnd(index);
        //checkKeys();
    }

    /**
     * This method removes the last entry (key + pointer) from an internal node. 
     * The entry refers to a pointer and the key precceeding the pointer.
     *
     * @param index: the index of the pointer
     */
    public void removeLastEntry(){
        removeEntry(degree-1);
    }

    
    /**
     * This method sets childPointers[index] to null and additionally decrements
     * the current degree of the InternalNode.
     *
     * @param index: the location within childPointers to be set to null
     */
    public void removePointer(int index) {
        this.childPointers[index] = null;
        this.childPointersIDs[index] = null;
        this.degree--;
    }

    
    

    /**
     * This method removes 'pointer' from the childPointers instance variable
     * and decrements the current degree of the InternalNode. The index where
     * the pointer node was assigned is set to null.
     *
     * @param pointer: the Node pointer to be removed from childPointers
     */
    public void removePointer(Integer pointerID) {
        for (int i = 0; i < degree; i++) {
            if (childPointersIDs[i].equals(pointerID)) {
                this.childPointers[i] = null;
                this.childPointersIDs[i] = null;
            }
        }
        this.degree--;
    }

    /**
     * This method sets a key into the key array.
     * Make sure the setting does not break the keys ordering
     *
     * @param index: the index of the array where the key is to be set
     * @param key: the key to be set
     */
    public void setKey(int index, Key key) {
        keys[index] = key;
    }

    /**
     * This method adds a key at the appropriate position of the key array
     *
     * @param key: the key to be added
     */
    public void addKey(Key key) {
        //setKey(degree - 1, key);
        //Arrays.sort(keys, 0, degree);
        insertAndShift(key);
    }
    
    private void insertAndShift(Key key) {
        int index = 0;
        // Find the correct position to insert the new element
        while (index < degree-1 && keys[index].compareTo(key) < 0) {
            index++;
        }

        // Shift elements to the right to make space for the new element
        for (int i = degree-1; i > index; i--) {
            this.keys[i] = keys[i - 1];
        }

        // Insert the new element at the correct position
        this.keys[index] = key;

        // Increment the number of pairs
        //numPairs++;
    }
    
    

    /**
     * This method sets a node pointer into the pointer array
     *
     * @param index: the index of the array where the node pointer is to be set
     * @param key: the node pointer to be set
     */
    public void setPointer(int index, Node node) {
        childPointers[index] = node;
        childPointersIDs[index] = node.getPageID();
        node.setParentNode(this);
    }
    
    /**
     * This method adds a key at the end of the key array.
     * Make sure that the key to be added is higher than all other keys.
     *
     * @param key: the key to be added
     */
    public void appendKey(Key key) {
        setKey(degree - 1, key);
    }

    /**
     * Returns the last filled position of the key array.
     */
    public Key getLastKey() {
        return keys[degree - 2];
    }
    
    /**
     * Returns the first position of the key array.
     */
    public Key getFirstKey() {
        return keys[0];
    }

    /**
     * This method modifies the InternalNode 'in' by removing all pointers
     * within the childPointers after the specified split. The method returns
     * the removed pointers in a list of their own to be used when constructing
     * a new InternalNode sibling.
     *
     * @param in: an InternalNode whose childPointers will be split
     * @param split: the index at which the split in the childPointers begins
     * @return a Node[] of the removed pointers
     */
    public Integer[] splitChildPointersID(int split) {

        //Node[] pointers = childPointers;
        Integer[] pointersID = childPointersIDs;
        Integer[] halfPointers = new Integer[maxDegree + 1];

        // Copy half of the values into halfPointers while updating original keys
        for (int i = split + 1; i < pointersID.length; i++) {
            //halfPointers[i - split - 1] = getNode(pointersID[i]);
            halfPointers[i - split - 1] = pointersID[i];
            removePointer(i);
        }

        // Change degree of original InternalNode in
        degree = Utils.linearNullSearch(childPointersIDs);

        return halfPointers;
    }

    /**
     * Returns the first node of the nodes array.
     */
    public Node getFirstPointer() {
        return childPointers[0];
    }
    
    /**
     * Returns the page id of first node of the nodes array.
     */
    public Integer getFirstPointerID() {
        return childPointersIDs[0];
    }

    /**
     * Returns the last filled position of the nodes array.
     */
    public Node getLastPointer() {
        return childPointers[degree - 1];
    }
    
    /**
     * Returns the page id of the node in the last filled position of the nodes array.
     */
    public Integer getLastPointerID() {
        return childPointersIDs[degree - 1];
    }

    public Node[] splitChildPointers(int split) {

        Node[] pointers = childPointers;
        Node[] halfPointers = new Node[maxDegree + 1];

        // Copy half of the values into halfPointers while updating original keys
        for (int i = split + 1; i < pointers.length; i++) {
            //halfPointers[i - split - 1] = getNode(pointersID[i]);
            halfPointers[i - split - 1] = pointers[i];
            removePointer(i);
        }

        // Change degree of original InternalNode in
        degree = Utils.linearNullSearch(childPointers);

        return halfPointers;
    }

    @Override
    public int getHeaderSizeInBytes(){
        return super.getHeaderSizeInBytes() + 5 * Integer.BYTES;
    }
    
    
    @Override
    public void writeExternal(DataOutput out) throws IOException {
        //5 header attributes
        out.writeInt(maxDegree);
        out.writeInt(degree);
        out.writeInt(getParentID());
        out.writeInt(leftSiblingID);
        out.writeInt(rightSiblingID);
        
        //keys
        for (int i = 0; i < degree - 1; i++) {
            keys[i].writeExternal(out);
            //out.writeObject(keys[i]);
        }
        
        //values
        for (int i = 0; i < degree; i++) {
            out.writeInt(childPointersIDs[i]);
        }
    }

    @Override
    public void readExternal(DataInput in) throws IOException {
        //5 header attributes
        this.maxDegree = in.readInt();
        this.degree = in.readInt();
        this.setParentID(in.readInt());
        this.leftSiblingID = in.readInt();
        this.rightSiblingID = in.readInt();

        this.setMinDegree();

        //keys
        keys = new Key[maxDegree];
        for (int i = 0; i < degree - 1; i++) {
            Key key = tree.createKey();
            key.readExternal(in);
            keys[i] = key;
            //keys[i] = (Key)in.readObject();
        }

        //values
        childPointers = new Node[maxDegree + 1];
        childPointersIDs = new Integer[maxDegree + 1];
        for (int i = 0; i < degree; i++) {
            childPointersIDs[i] = in.readInt();
        }
    }

    
    
    /*
    * Returns the maximum degree (number of entries) of an internal node considering the pageSize, the fixed header size and the key size.
    * An entry is composed by a pointer (an integer ID) and a key.
    * Space for an additional pointer should be reserved, as there is one more pointer than keys.
    */
    public int findOutOptimalDegree(long pageSize, long keySize) {
        return (int) Math.floor((pageSize - getHeaderSizeInBytes()- Integer.BYTES) / (keySize+Integer.BYTES));
    }

            
    
    @Override
    public String toString() {
        String aux = "";
        for (Key key : keys) {
            if (key != null) {
                aux += key.toString() + "--";
            }
        }
        return aux;
    }

    
    /*
    * shift a null node to the end of the array
    * useful when a node is deleted, to group all nodes at the first positions of the array
    */
    public void shiftNullNodesToEnd(int index) {
        int nonNullIndex = index;
        for (int currentIndex = index; currentIndex < childPointersIDs.length; currentIndex++) {
            if (childPointersIDs[currentIndex] != null) {
                // Swap non-null element to the front of the array
                Node temp = childPointers[currentIndex];
                childPointers[currentIndex] = childPointers[nonNullIndex];
                childPointers[nonNullIndex] = temp;
                
                Integer temp_ = childPointersIDs[currentIndex];
                childPointersIDs[currentIndex] = childPointersIDs[nonNullIndex];
                childPointersIDs[nonNullIndex] = temp_;
                
                nonNullIndex++;
            }
        }
    }

    /*
    * shift a null key to the end of the array
    * useful when a key is deleted, to group all nodes at the first positions of the array
    *
    */
    public void shiftNullKeysToEnd(int index) {
        int nonNullIndex = index;
        for (int currentIndex = index; currentIndex < keys.length; currentIndex++) {
            if (keys[currentIndex] != null) {
                // Swap non-null element to the front of the array
                Key temp = keys[currentIndex];
                keys[currentIndex] = keys[nonNullIndex];
                keys[nonNullIndex] = temp;
                nonNullIndex++;
            }
        }
    }

}
