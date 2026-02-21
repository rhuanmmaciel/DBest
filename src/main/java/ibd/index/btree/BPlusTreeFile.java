/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree;

import ibd.persistent.AbstractExternalizablePage;
import ibd.persistent.Page;
import ibd.persistent.PageFile;
import ibd.persistent.PageSerialization;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a B+ tree whose nodes are stored on a disk file.
 *
 * @author Sergio
 */
public abstract class BPlusTreeFile extends BPlusTree implements PageSerialization {

    protected int rootID = -1;
    protected int firstLeafID = -1;

    /**
     * Page codes.
     */
    private static final int EMPTY_PAGE = 0;
    private static final int INTERNAL_NODE = 1;
    private static final int LEAF_NODE = 2;

    /**
     * The file storing the entries of this index.
     */
    protected PageFile<Node> file = null;

    /**
     * True if this index is already initialized.
     */
    protected boolean init = false;

    /**
     * Constructor
     *
     * @param pagefile: the file storing the entries of this index.
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public BPlusTreeFile(PageFile pagefile) throws InstantiationException, IllegalAccessException {
        this.file = pagefile;
        pagefile.setPageSerialization(this);

    }

    /**
     * Creates a file header for this index structure which is an instance of
     * {@link TreeIndexHeader}. Subclasses may need to overwrite this method if
     * they need a more specialized header.
     *
     * @return a new header for this index structure
     */
    protected abstract TreeIndexHeader loadHeader();

    /**
     * extracts properties from the file header. This is the same header created
     * by the loadHeader method.
     *
     * @param header: the file header from where the properties are read from
     */
    protected abstract void readFromHeader(TreeIndexHeader header);

    /*
    * Initialize the database file.
    * 
     */
    public void open() {
        if (!init) {

            //creates a header containing all relevant information for a B+ tree
            TreeIndexHeader header = loadHeader();
            //uses the header to initialize the B+ tree file.
            //if the file is not to be overwritten, the existing header retrieved frmo the file is use instead of the header created in the previous step
            file.initialize(header);
            //reads header information in the form of global fields
            readFromHeader(header);

            int pageSize = file.getHeader().getPageSize();

            //parameter Class<Key> keyClass not used anymore
            //Key key = keyClass.newInstance();
            //Value value = valueClass.newInstance();
            //finds out the size of the key
            Key key = createKey();
            int keySize = key.getSizeInBytes();

            //finds out the size of the value
            Value value = createValue();
            int valueSize = value.getSizeInBytes();

            //use the key size and page size to determine the order of the internal nodes. 
            InternalNode in = new InternalNode(this);
            this.m = in.findOutOptimalDegree(pageSize, keySize);

            //use the key,value sizes and page size to determine the order of the leaf nodes. 
            LeafNode ln = new LeafNode(this);
            this.leafM = ln.findOutOptimalDegree(pageSize, keySize, valueSize);

            init = true;
        }
    }
    
    public void reopen() throws Exception{
        file.reopen();
    }
    
    /**
     * Returns the capacity of a directory node (= 1 + maximum number of entries
     * in a directory node).
     *
     * @return the capacity of a directory node
     */
    public int getDirCapacity() {
        return m;
        //return ((TreeIndexHeader) file.getHeader()).getDirCapacity();
    }

    /**
     * Returns the capacity of a leaf node (= 1 + maximum number of entries in a
     * leaf node).
     *
     * @return
     */
    public int getLeafCapacity() {
        return leafM;
        //return ((TreeIndexHeader) file.getHeader()).getLeafCapacity();
    }

    public int getRootID() {
        return ((TreeIndexHeader) file.getHeader()).getRootID();
    }

    private void setRootID(int id) {
        this.rootID = id;
        ((TreeIndexHeader) file.getHeader()).setRootID(id);
    }

    public int getFirstLeafID() {
        return ((TreeIndexHeader) file.getHeader()).getFirstLeafID();
    }

    private void setFirstLeafID(int id) {
        this.firstLeafID = id;
        ((TreeIndexHeader) file.getHeader()).setFirstLeafID(id);
    }

    /**
     * This method starts at the root of the B+ tree and traverses down the tree
     * via key comparisons to the corresponding leaf node that holds 'key'
     * within its dictionary.
     *
     * @param key: the unique key that lies within the dictionary of a LeafNode
     * object
     * @return the LeafNode object that contains the key within its dictionary
     */
    private LeafNode findLeafNode(Key key) {

        // Initialize keys and index variable
        InternalNode root = (InternalNode) getNode(getRootID());
        return findLeafNode(root, key);
    }

    private LeafNode findLeafNode(InternalNode node, Key key) {

        // Initialize keys and index variable
        Key[] keys = node.keys;
        //int i;

        // Find next node on path to appropriate leaf node
//        for (i = 0; i < node.degree - 1; i++) {
//            if (key.compareTo(keys[i]) < 0) {
//                break;
//            }
//        }
//        int i = node.degree - 1;
//        int left = 0;
//        int right = node.degree - 2;
//        while (left <= right) {
//            int mid = left + (right - left) / 2;
//            if (keys[mid].compareTo(key) > 0) {
//                i = mid;
//                right = mid - 1;
//            } else {
//                left = mid + 1;
//            }
//        }
        int left = 0, right = node.degree - 2; // Last key index is degree - 2
int result = -1; // Store the first occurrence of the key

while (left <= right) {
    int mid = left + (right - left) / 2;
    int cmp = key.compareTo(keys[mid]);

    if (cmp <= 0) {  
        // Key is equal or smaller, but we want the first occurrence
        if (cmp == 0) {
            result = mid; // Store potential first occurrence
        }
        right = mid - 1; // Keep searching to the left
    } else {
        left = mid + 1; // Move right
    }
}

// If found, return the first occurrence; otherwise, return the insertion index
int i = (result != -1) ? result : left;


        /* Return node if it is a LeafNode object,
		   otherwise repeat the search function a level down */
        Node childNode = getNode(node.childPointersIDs[i]);
        //LRUBufferManager.getNode(childNode);
        if (childNode instanceof LeafNode) {
            return (LeafNode) childNode;
        } else {
            return findLeafNode((InternalNode) childNode, key);
        }
    }

    /**
     * This is a simple method that returns the midpoint (or lower bound
     * depending on the context of the method invocation) of the max degree m of
     * the B+ tree.
     *
     * @return (int) midpoint/lower bound
     */
    private int getMidpoint(int m_) {
        return (int) Math.ceil((m_ + 1) / 2.0) - 1;
    }

    /**
     * Copy all entries of a source node into the start of a target node
     *
     * @param targetNode: the target node
     * @param sourceNode: the source node
     */
    public void prependAllEntries(InternalNode targetNode, InternalNode sourceNode) {

        //only here the number of keys is the same as the number of points.
        //reason: a key was addded before this method is called
        int nkeys = targetNode.degree;
        for (int i = nkeys - 1; i >= 0; i--) {
            targetNode.keys[i + sourceNode.degree - 1] = targetNode.keys[i];
        }
        for (int i = targetNode.degree - 1; i >= 0; i--) {
            targetNode.childPointers[i + sourceNode.degree] = targetNode.childPointers[i];
            targetNode.childPointersIDs[i + sourceNode.degree] = targetNode.childPointersIDs[i];
        }

        int i;
        for (i = 0; i < sourceNode.degree - 1; i++) {
            Node child = getNode(sourceNode.childPointersIDs[i]);
            targetNode.setPointer(i, child);
            writeNode(child);
            targetNode.setKey(i, sourceNode.keys[i]);
        }
        Node child = getNode(sourceNode.childPointersIDs[i]);
        targetNode.setPointer(i, child);
        writeNode(child);
        targetNode.degree += sourceNode.degree;

    }

    /**
     * Given a deficient InternalNode in, this method remedies the deficiency
     * through borrowing and merging.
     *
     * @param in: a deficient InternalNode
     */
    private void handleDeficiency(InternalNode in) {

        InternalNode sibling;

        InternalNode parent = (InternalNode) getNode(in.getParentID());

        InternalNode root = (InternalNode) getNode(getRootID());
        if (in.leftSiblingID > 0) {
            in.leftSibling = (InternalNode) getNode(in.leftSiblingID);
        }
        if (in.rightSiblingID > 0) {
            in.rightSibling = (InternalNode) getNode(in.rightSiblingID);
        }

        if (in.leftSibling != null && in.leftSibling.getParentID() == in.getParentID() && in.leftSibling.isLendable()) {
            sibling = in.leftSibling;
            System.out.println("BORROW FROM LEFT");

            // Copy key and pointer into deficient node
            int pointerIndex = parent.findIndexOfPointer(in);
            in.prependChildKey(parent.keys[pointerIndex - 1]);

            // Copy pointer from left sibling
            Node borrowedPointer = getNode(sibling.getLastPointerID());
            in.prependChildPointer(borrowedPointer);

            // update Key into parent
            parent.setKey(pointerIndex - 1, sibling.getLastKey());

            // Delete last entry from left sibling
            sibling.removeLastEntry();

            writeNode(in);
            writeNode(borrowedPointer);
            writeNode(parent);
            writeNode(sibling);

        } else if (in.rightSibling != null && in.rightSibling.getParentNode() == in.getParentNode() && in.rightSibling.isLendable()) {
            sibling = in.rightSibling;
            System.out.println("BORROW FROM RIGHT");

            // Copy key from parent into deficient node
            int pointerIndex = parent.findIndexOfPointer(in);
            in.appendKey(parent.keys[pointerIndex]);

            // Copy pointer from right sibling into deficient node
            Node pointer = getNode(sibling.getFirstPointerID());
            in.appendChildPointer(pointer);

            // update Key into parent
            parent.setKey(pointerIndex, sibling.getFirstKey());

            // Delete key and pointer from sibling
            sibling.removeEntry1(0);

            writeNode(in);
            writeNode(pointer);
            writeNode(parent);
            writeNode(sibling);

        } // can merge with left sibling
        else if (in.leftSibling != null && in.leftSibling.getParentNode() == in.getParentNode() && in.leftSibling.isMergeable()) {
            sibling = in.leftSibling;
            System.out.println("MERGE WITH LEFT");

            // Copy key to left sibling in parent to end of the left sibling's keys 
            int pointerIndex = parent.findIndexOfPointer(in);
            sibling.appendKey(parent.keys[pointerIndex - 1]);

            // Copy in's entries to the end of the left sibling 
            appendAllEntries(sibling, in);

            // Delete entry from parent to deficient node
            parent.removeEntry(pointerIndex);

            // the deficient node is no longer exists. Pointers need to be updated
            sibling.rightSiblingID = in.rightSiblingID;
            if (sibling.rightSiblingID != -1) {
                sibling.rightSibling = (InternalNode) getNode(sibling.rightSiblingID);
                sibling.rightSibling.leftSiblingID = sibling.getPageID();
                writeNode(sibling.rightSibling);
            }

            writeNode(parent);
            writeNode(sibling);

        } // can merge with right sibling
        else if (in.rightSibling != null && in.rightSibling.getParentNode() == in.getParentNode() && in.rightSibling.isMergeable()) {
            sibling = in.rightSibling;
            System.out.println("MERGE WITH RIGHT");

            // Copy key from the deficient node in parent to beginning of the right sibling's keys 
            int pointerIndex = parent.findIndexOfPointer(in);
            sibling.prependChildKey(parent.keys[pointerIndex]);

            // Copy all in's entries to the beggining of the right sibling
            prependAllEntries(sibling, in);
            //checkKeys(sibling.keys);

            // Delete entry from parent to deficient node
            parent.removeEntry1(pointerIndex);

            // the deficient node is no longer exists. Pointers need to be updated.
            sibling.leftSiblingID = in.leftSiblingID;

            if (sibling.leftSiblingID != -1) {
                sibling.leftSibling = (InternalNode) getNode(sibling.leftSiblingID);
                sibling.leftSibling.rightSiblingID = sibling.getPageID();
                writeNode(sibling.leftSibling);
            }

            writeNode(parent);
            writeNode(sibling);
            //checkSibling(sibling.leftSibling, sibling);
            //printTree();
        }

        //if the root node ended up with a single pointer, it can be replaced with the pointed node
        if (parent.getPageID() == root.getPageID() && parent.degree == 1) {
            Node n = getNode(parent.getFirstPointerID());
            root = (InternalNode) n;
            rootID = n.getPageID();
            setRootID(rootID);
            root.setParentNode(null);
            root.setParentID(-1);
            writeNode(root);

            return;
        }

        // Handle deficiency a level up if it exists
        if (parent != null && parent.isDeficient() && parent.getPageID() != root.getPageID()) {
            handleDeficiency(parent);
        }
    }

    /**
     * Copy all entries of a source node into the end of a target node
     *
     * @param target: the target node
     * @param source: the source node
     */
    private void appendAllEntries(InternalNode targetNode, InternalNode sourceNode) {
        Node child = getNode(sourceNode.childPointersIDs[0]);
        targetNode.appendChildPointer(child);
        writeNode(child);
        for (int i = 1; i < sourceNode.degree; i++) {
            targetNode.appendKey(sourceNode.keys[i - 1]);
            child = getNode(sourceNode.childPointersIDs[i]);
            targetNode.appendChildPointer(child);
            writeNode(child);
        }
    }

    /**
     * Determines if the B+ tree is empty or not.
     *
     * @return a boolean indicating if the B+ tree is empty or not
     */
    private boolean isEmpty() {
        return getFirstLeafID() == -1;
    }

    /**
     * When an insertion into the B+ tree causes an overfull node, this method
     * is called to remedy the issue, i.e. to split the overfull node. This
     * method calls the sub-methods of splitKeys() and splitChildPointers() in
     * order to split the overfull node.
     *
     * @param in: an overfull InternalNode that is to be split
     */
    private void splitInternalNode(InternalNode in) {

        // Acquire parent
        InternalNode parent = null;//in.parent;
        if (parent == null) {
            parent = (InternalNode) getNode(in.getParentID());
        }

        // Split keys and pointers in half and leave the first half in the original node
        int midpoint = getMidpoint(getDirCapacity());
        Key newParentKey = in.keys[midpoint];
        Key[] halfKeys = splitKeys(in.keys, midpoint);
        Integer[] halfPointers = in.splitChildPointersID(midpoint);

        // Change degree of original InternalNode in
        //in.degree = Utils.linearNullSearch(in.childPointers);
        // Create new sibling internal node and add half of keys and pointers
        InternalNode sibling = new InternalNode(getDirCapacity(), halfKeys, halfPointers, this);
        writeNode(sibling);
        for (Integer pointer : halfPointers) {
            if (pointer != null) {
                Node p = getNode(pointer);
                p.setParentNode(sibling);
                writeNode(p);
            }
        }

        // Make internal nodes siblings of one another
        sibling.rightSiblingID = in.rightSiblingID;

        if (sibling.rightSiblingID != -1) {
            sibling.rightSibling = (InternalNode) getNode(sibling.rightSiblingID);
            sibling.rightSibling.leftSibling = sibling;
            sibling.rightSibling.leftSiblingID = sibling.getPageID();
            writeNode(sibling.rightSibling);
        }
        in.rightSibling = sibling;
        in.rightSiblingID = sibling.getPageID();
        sibling.leftSibling = in;
        sibling.leftSiblingID = in.getPageID();

        if (parent == null) {

            // Create new root node and add midpoint key and pointers
            Key[] keys = new Key[getDirCapacity()];
            keys[0] = newParentKey;
            InternalNode newRoot = new InternalNode(getDirCapacity(), keys, this);
            newRoot.appendChildPointer(in);
            newRoot.appendChildPointer(sibling);
            writeNode(newRoot);
            in.setParentID(newRoot.getPageID());
            sibling.setParentID(newRoot.getPageID());
            setRootID(newRoot.getPageID());

        } else {

            // Add key to parent
            parent.addKey(newParentKey);

            // Set up pointer to new sibling
            int pointerIndex = parent.findIndexOfPointer(in) + 1;
            parent.insertChildPointer(sibling, pointerIndex);
            writeNode(parent);
        }
        writeNode(sibling);
        writeNode(in);
    }

    /**
     * This method modifies a list of keys by removing half of the keys and
     * returning them in a separate array. This method is used when splitting an
     * InternalNode object.
     *
     * @param keys: a list of keys
     * @param split: the index where the split is to occur
     * @return array of removed keys
     */
    private Key[] splitKeys(Key[] keys, int split) {

        Key[] halfKeys = new Key[getDirCapacity()];

        // Remove split-indexed value from keys
        keys[split] = null;

        // Copy half of the values into halfKeys while updating original keys
        for (int i = split + 1; i < keys.length; i++) {
            halfKeys[i - split - 1] = keys[i];
            keys[i] = null;
        }

        return halfKeys;
    }

    /*~~~~~~~~~~~~~~~~ API: DELETE, INSERT, SEARCH, UPDATE ~~~~~~~~~~~~~~~~*/
    @Override
    public Value delete(Key key) {

        if (isEmpty()) {

            /* Flow of execution goes here when B+ tree has no dictionary pairs */
            //System.err.println("Invalid Delete: The B+ tree is currently empty.");
            return null;

        } else {

            LeafNode firstLeaf = (LeafNode) getNode(getFirstLeafID());
            //if (getRootID()!=-1)
            InternalNode root = (InternalNode) getNode(getRootID());

            // Find leaf node that holds the dictionary key
            LeafNode ln = (root == null) ? firstLeaf : findLeafNode(key);

            // Get leaf node and attempt to find index of key to delete
            int dpIndex = Utils.binarySearch(ln.dictionary, ln.numPairs, key, this);

            if (dpIndex < 0) {

                /* Flow of execution goes here when key is absent in B+ tree */
                //System.err.println("Invalid Delete: Key unable to be found: " + key);
                return null;

            } else {

                //saves value to return later
                Value valueToDelete = ln.dictionary[dpIndex].value;

                // Successfully delete the dictionary pair
                ln.delete(dpIndex);

                // Check for deficiencies
                if (ln.isDeficient()) {

                    InternalNode parent = (InternalNode) getNode(ln.getParentID());

                    if (ln.leftSiblingID != -1) {
                        ln.leftSibling = (LeafNode) getNode(ln.leftSiblingID);
                    }

                    if (ln.rightSiblingID != -1) {
                        ln.rightSibling = (LeafNode) getNode(ln.rightSiblingID);
                    }

                    // Borrow: First, check the left sibling, then the right sibling
                    if (ln.leftSibling != null
                            && ln.leftSibling.getParentID() == ln.getParentID()
                            && ln.leftSibling.isLendable()) {

                        LeafNode sibling = ln.leftSibling;

                        //System.out.println("BORROW FROM LEFT 0");
                        //copy last dictonary pair from left sibling into the beggining of the deficient node 
                        DictionaryPair borrowedDP = sibling.getLastDictionaryPair();
                        ln.prependPair(borrowedDP);

                        //delete dictionary pair from left sibling 
                        sibling.deleteLastDictionaryPair();

                        // Update key in parent
                        int pointerIndex = parent.findIndexOfPointer(ln);
                        //parent.keys[pointerIndex - 1] = ln.dictionary[0].key;
                        parent.keys[pointerIndex - 1] = borrowedDP.key;

                        writeNode(ln);
                        writeNode(sibling);
                        writeNode(parent);

                        return valueToDelete;

                    } else if (ln.rightSibling != null
                            && ln.rightSibling.getParentID() == ln.getParentID()
                            && ln.rightSibling.isLendable()) {

                        LeafNode sibling = ln.rightSibling;
                        //System.out.println("BORROW FROM RIGHT 0");

                        //copy first dictonary pair from right sibling into the end of the deficient node 
                        DictionaryPair borrowedDP = sibling.getFirstDictionaryPair();
                        ln.appendPair(borrowedDP);

                        //delete dictionary pair from sibling
                        sibling.deleteFirstDictionaryPair();

                        // Update key in parent
                        int pointerIndex = parent.findIndexOfPointer(ln);

                        //parent.keys[pointerIndex] = sibling.dictionary[0].key;
                        parent.keys[pointerIndex] = sibling.getFirstDictionaryPair().key;

                        writeNode(ln);
                        writeNode(sibling);
                        writeNode(parent);

                        return valueToDelete;
                        //checkKeys(parent.keys);

                    } // Merge: First, check the left sibling, then the right sibling
                    else if (ln.leftSibling != null
                            && ln.leftSibling.getParentID() == ln.getParentID()
                            && ln.leftSibling.isMergeable()) {

                        LeafNode sibling = ln.leftSibling;

                        //System.out.println("MERGE WITH LEFT 0");
                        int pointerIndex = parent.findIndexOfPointer(ln);
                        // Remove entry of the deficient node from parent
                        parent.removeEntry(pointerIndex);
                        writeNode(parent);

                        //merging into left sibling
                        sibling.appendPairs(ln);

                        // Deficient node ln no longer exists. Need to update pointers
                        sibling.rightSiblingID = ln.rightSiblingID;
                        if (sibling.rightSiblingID != -1) {
                            sibling.rightSibling = (LeafNode) getNode(sibling.rightSiblingID);
                            sibling.rightSibling.leftSiblingID = sibling.getPageID();
                            writeNode(sibling.rightSibling);
                        }
                        writeNode(sibling);

                        //printTree();
                        deleteNode(ln);

                        //writeNode(ln);
                        // Check for deficiencies in parent
                        if (parent.isDeficient() && parent.getPageID() != root.getPageID()) {
                            handleDeficiency(parent);
                        }
                        return valueToDelete;

                    } else if (ln.rightSibling != null
                            && ln.rightSibling.getParentID() == ln.getParentID()
                            && ln.rightSibling.isMergeable()) {

                        LeafNode sibling = ln.rightSibling;

                        //System.out.println("MERGE WITH RIGHT 0");
                        int pointerIndex = parent.findIndexOfPointer(ln);
                        // Remove entry of the deficient node from parent
                        parent.removeEntry1(pointerIndex);

                        writeNode(parent);
                        //merging into right sibling
                        sibling.prependPairs(ln);

                        // Deficient node ln no longer exists. Need to update pointers
                        sibling.leftSiblingID = ln.leftSiblingID;
                        if (sibling.leftSiblingID == -1) {
                            //firstLeaf = sibling;
                            setFirstLeafID(sibling.getPageID());
                        } else {
                            sibling.leftSibling = (LeafNode) getNode(sibling.leftSiblingID);
                            sibling.leftSibling.rightSiblingID = sibling.getPageID();
                            writeNode(sibling.leftSibling);
                        }
                        writeNode(sibling);
                        deleteNode(ln);

                        //writeNode(ln);
                        // Check for deficiencies in parent
                        if (parent.isDeficient() && parent.getPageID() != root.getPageID()) {
                            handleDeficiency(parent);
                        }
                        return valueToDelete;

                        //printTree();
                    } else {
                        writeNode(ln);
                        return valueToDelete;
                    }

                } else if (root == null && firstLeaf.numPairs == 0) {

                    /* Flow of execution goes here when the deleted dictionary
					   pair was the only pair within the tree */
                    // Set first leaf as null to indicate B+ tree is empty
                    //this.firstLeaf = null;
                    setFirstLeafID(-1);
                    deleteNode(firstLeaf);

                } else {
                    writeNode(ln);

                }
                return valueToDelete;
            }
        }
    }

    @Override
    public boolean insert(Key key, Value value, boolean unique) {
        if (isEmpty()) {

            /* Flow of execution goes here only when first insert takes place */
            // Create leaf node as first node in B plus tree (root is null)
            LeafNode ln = new LeafNode(getLeafCapacity(), new DictionaryPair(key, value, this), this);

            // Set as first leaf node (can be used later for in-order leaf traversal)
            //this.firstLeaf = ln;
            writeNode(ln);
            setFirstLeafID(ln.getPageID());

        } else {

            // Find leaf node to insert into
            LeafNode firstLeaf = (LeafNode) getNode(getFirstLeafID());
            // Find leaf node to insert into
            LeafNode ln = (getRootID() == -1) ? firstLeaf
                    : findLeafNode(key);

            if (unique) {
                // Perform binary search to find index of key within dictionary
                DictionaryPair[] dps = ln.dictionary;
                int index = Utils.binarySearch(dps, ln.numPairs, key, this);

                //key is already indexed.
                if (index >= 0) {
                    return false;
                }
            }

            // Insert into leaf node fails if node becomes overfull
            if (!ln.insert(new DictionaryPair(key, value, this))) {

                // Sort all the dictionary pairs with the included pair to be inserted
                ln.sortDictionary(key, value);

                // Split the sorted pairs into two halves
                int midpoint = getMidpoint(getLeafCapacity());
                //midpoint = getLeafCapacity() - 2;
                DictionaryPair[] halfDict = ln.splitDictionary(midpoint);
                writeNode(ln);

                if (ln.getParentID() >= 0) {
                    ln.setParentNode((InternalNode) getNode(ln.getParentID()));
                }

                if (ln.getParentNode() == null) {

                    /* Flow of execution goes here when there is 1 node in tree */
                    // Create internal node to serve as parent, use dictionary midpoint key
                    Key[] parent_keys = new Key[getDirCapacity()];
                    parent_keys[0] = halfDict[0].key;
                    InternalNode parent = new InternalNode(getDirCapacity(), parent_keys, this);

                    parent.appendChildPointer(ln);
                    writeNode(parent);
                    ln.setParentID(parent.getPageID());
                    writeNode(ln);

                } else {

                    /* Flow of execution goes here when parent exists */
                    // Add new key to parent for proper indexing
                    Key newParentKey = halfDict[0].key;
                    ln.getParentNode().addKey(newParentKey);
                }

                // Create new LeafNode that holds the other half
                LeafNode newLeafNode = new LeafNode(getLeafCapacity(), halfDict, ln.getParentNode());
                writeNode(newLeafNode);

                if (ln.getParentNode() == null) {
                    ln.setParentNode((InternalNode) getNode(ln.getParentID()));
                }

                // Update child pointers of parent node
                int pointerIndex = ln.getParentNode().findIndexOfPointer(ln) + 1;
                ln.getParentNode().insertChildPointer(newLeafNode, pointerIndex);
                writeNode(ln.getParentNode());

                // Make leaf nodes siblings of one another
                newLeafNode.rightSiblingID = ln.rightSiblingID;
                if (newLeafNode.rightSiblingID != -1) {
                    newLeafNode.rightSibling = (LeafNode) getNode(newLeafNode.rightSiblingID);
                    newLeafNode.rightSibling.leftSibling = newLeafNode;
                    newLeafNode.rightSibling.leftSiblingID = newLeafNode.getPageID();
                    writeNode(newLeafNode.rightSibling);
                }
                ln.rightSibling = newLeafNode;
                ln.rightSiblingID = newLeafNode.getPageID();
                newLeafNode.leftSibling = ln;
                newLeafNode.leftSiblingID = ln.getPageID();

                writeNode(newLeafNode);
                writeNode(ln);

                if (getRootID() == -1) {

                    // Set the root of B+ tree to be the parent
                    setRootID(ln.getParentID());

                } else {

                    /* If parent is overfull, repeat the process up the tree,
			   		   until no deficiencies are found */
                    //printTree();
                    InternalNode in = ln.getParentNode();
                    while (in != null) {
                        if (in.isOverfull()) {
                            splitInternalNode(in);
                        } else {
                            break;
                        }
                        if (in.getParentID() != -1) {
                            in = (InternalNode) getNode(in.getParentID());
                        } else {
                            in = null;
                        }
                    }
                }
            } else {
                writeNode(ln);
            }
        }
        return true;
    }

    @Override
    public Value search(Key key) {

        // If B+ tree is completely empty, return null
        if (isEmpty()) {
            return null;
        }

        // Find leaf node that holds the dictionary key
        LeafNode ln = (getRootID() == -1) ? (LeafNode) getNode(getFirstLeafID())
                : findLeafNode(key);

        // Perform binary search to find index of key within dictionary
        DictionaryPair[] dps = ln.dictionary;
        int index = Utils.binarySearch(dps, ln.numPairs, key, this);

        // If index negative, the key doesn't exist in B+ tree
        if (index < 0) {
            return null;
        } else {
            return dps[index].value;
        }
    }

    @Override
    public ArrayList<Value> search(Key lowerBound, Key upperBound) {

        LeafNode firstLeaf = (LeafNode) getNode(getFirstLeafID());

        // Instantiate Double array to hold values
        ArrayList<Value> values = new ArrayList<>();

        // Iterate through the doubly linked list of leaves
        LeafNode currNode = firstLeaf;
        while (currNode != null) {
            // Iterate through the dictionary of each node
            DictionaryPair dps[] = currNode.dictionary;
            for (DictionaryPair dp : dps) {

                /* Stop searching the dictionary once a null value is encountered
				   as this indicates the end of non-null values */
                if (dp == null) {
                    break;
                }

                // Include value if its key fits within the provided range
                if (lowerBound.compareTo(dp.key) <= 0 && dp.key.compareTo(upperBound) <= 0) {
                    values.add(dp.value);
                }
            }

            /* Update the current node to be the right sibling,
			   leaf traversal is from left to right */
            currNode = currNode.rightSibling;

        }

        return values;
    }

    @Override
    public ArrayList<DictionaryPair> searchAll() {

        LeafNode firstLeaf = (LeafNode) getNode(getFirstLeafID());

        // Instantiate array to hold values
        ArrayList<DictionaryPair> values = new ArrayList<>();

        // Iterate through the doubly linked list of leaves
        LeafNode currNode = firstLeaf;
        while (currNode != null) {
            // Iterate through the dictionary of each node
            DictionaryPair dps[] = currNode.dictionary;
            for (DictionaryPair dp : dps) {

                /* Stop searching the dictionary once a null value is encountered
				   as this the indicates the end of non-null values */
                if (dp == null) {
                    break;
                }

                values.add(dp);
            }

            /* Update the current node to be the right sibling,
			   leaf traversal is from left to right */
            currNode = (LeafNode) getNode(currNode.rightSiblingID);

        }

        return values;
    }

    @Override
    public AllKeysIterator searchAllIterator() {
        return new AllKeysIterator(this);
    }

    public EqualKeyIterator equalKeyIterator(Key key) {
        return new EqualKeyIterator(this, key);
    }

    public LargerKeyIterator largerKeyIterator(Key key, boolean largerEqual) {
        return new LargerKeyIterator(this, key, largerEqual);
    }

    public SmallerKeyIterator smallerKeyIterator(Key key, boolean smallerEqual) {
        return new SmallerKeyIterator(this, key, smallerEqual);
    }

    public DifferentKeyIterator differentKeyIterator(Key key) {
        return new DifferentKeyIterator(this, key);
    }

    public LeafNode getFirstPage() {
        return (LeafNode) getNode(getFirstLeafID());
    }

    public LeafNode getFirstPage(Key key) {

        //LeafNode firstLeaf = (LeafNode) getNode(getFirstLeafID());
        //InternalNode root = (InternalNode) getNode(getRootID());
        // If B+ tree is completely empty, return null
        if (isEmpty()) {
            return null;
        }

        // Find leaf node that holds the dictionary key
        LeafNode ln = (getRootID() == -1) ? (LeafNode) getNode(getFirstLeafID()) : findLeafNode(key);
        return ln;
    }

    @Override
    public ArrayList<Value> partialSearch(Key key) {

        // Instantiate array to hold values
        ArrayList<Value> values = new ArrayList();

        // If B+ tree is completely empty, return null
        if (isEmpty()) {
            return values;
        }

        // Find leaf node that holds the dictionary key
        LeafNode ln = (getRootID() == -1) ? (LeafNode) getNode(getFirstLeafID()) : findLeafNode(key);

        // Perform binary search to find index of key within dictionary
        DictionaryPair[] dps = ln.dictionary;
        int index = Utils.binarySearch(dps, ln.numPairs, key, this);

        // in this case, a negative index may simply mean that the key has less levels than the indexed keys.
        // we need to access the largest value smaller than the key, and advance one position.
        if (index < 0) {
            index = ~index;
        } //the bynary search might not retrieve the first matching value
        //so we need to search back until the first matching position is found
        else {
            while (index > 0 && dps[index - 1].key.compareTo(key) == 0) {
                index--;
            }
        }

        //
        for (int i = index; i < dps.length; i++) {
            if (dps[i] != null) {
                if (key.partialMatch(dps[i].key)) {
                    values.add(dps[i].value);
                } else {
                    return values;
                }
            }
        }

        // Iterate through the doubly linked list of leaves
        //LeafNode currNode = ln.rightSibling;
        LeafNode currNode = (LeafNode) getNode(ln.rightSiblingID);
        while (currNode != null) {
            // Iterate through the dictionary of each node
            DictionaryPair dps_[] = currNode.dictionary;
            for (DictionaryPair dp : dps_) {

                /* Stop searching the dictionary once a null value is encountered
				   as this the indicates the end of non-null values */
                if (dp == null) {
                    break;
                }

                // Include value if its key fits within the provided range
                if (key.partialMatch(dp.key)) {
                    values.add(dp.value);
                } else {
                    return values;
                }
            }

            /* Update the current node to be the right sibling,
			   leaf traversal is from left to right */
            //currNode = currNode.rightSibling;
            currNode = (LeafNode) getNode(currNode.rightSiblingID);

        }

        return values;
    }

    @Override
    public boolean contains(Key key) {

        // If B+ tree is completely empty, return null
        if (isEmpty()) {
            return false;
        }

        // Find leaf node that holds the dictionary key
        LeafNode ln = (getRootID() == -1) ? (LeafNode) getNode(getFirstLeafID()) : findLeafNode(key);

        // Perform binary search to find index of key within dictionary
        DictionaryPair[] dps = ln.dictionary;
        int index = Utils.binarySearch(dps, ln.numPairs, key, this);

        // in this case, a negative index may simply mean that the key has less levels than the indexed keys.
        // we need to access the largest value smaller than the key, and advance one position.
        if (index < 0) {
            index = ~index;
        } //the bynary search might not retrieve the first matching value
        //so we need to search back until the first matching position is found
        else {
            while (index > 0 && dps[index - 1].key.compareTo(key) == 0) {
                index--;
            }
        }

        //
        for (int i = index; i < dps.length; i++) {
            if (dps[i] != null) {
                if (key.partialMatch(dps[i].key)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        // Iterate through the doubly linked list of leaves
        //LeafNode currNode = ln.rightSibling;
        LeafNode currNode = (LeafNode) getNode(ln.rightSiblingID);
        while (currNode != null) {
            // Iterate through the dictionary of each node
            DictionaryPair dps_[] = currNode.dictionary;
            for (DictionaryPair dp : dps_) {

                /* Stop searching the dictionary once a null value is encountered
				   as this the indicates the end of non-null values */
                if (dp == null) {
                    break;
                }

                // Include value if its key fits within the provided range
                if (key.partialMatch(dp.key)) {
                    return true;
                } else {
                    return false;
                }
            }

            /* Update the current node to be the right sibling,
			   leaf traversal is from left to right */
            //currNode = currNode.rightSibling;
            currNode = (LeafNode) getNode(currNode.rightSiblingID);

        }
        return false;
    }

    @Override
    public Value update(Key key, Value value) {

        // If B+ tree is completely empty, return null
        if (isEmpty()) {
            return null;
        }

        // Find leaf node that holds the dictionary key
        LeafNode ln = (getRootID() == -1) ? (LeafNode) getNode(getFirstLeafID()) : findLeafNode(key);

        // Perform binary search to find index of key within dictionary
        DictionaryPair[] dps = ln.dictionary;
        int index = Utils.binarySearch(dps, ln.numPairs, key, this);

        // If index negative, the key doesn't exist in B+ tree
        if (index < 0) {
            return null;
        } else {
            dps[index].value = value;
            writeNode(ln);
            return dps[index].value;
        }
    }

    /**
     * Closes the backing storage.
     */
    public void close() {
        file.close();
    }

    public void printNode(LeafNode node) {
        if (node == null) {
            return;
        }
        String parent = "";
        if (node.getParentNode() != null) {
            parent = node.getParentNode().toString();
        }
        System.out.println("LF:" + node.toString() + " => " + parent);
    }

    public void printNode(InternalNode node) {
        if (node == null) {
            return;
        }
        String parent = "";
        if (node.getParentNode() != null) {
            parent = node.getParentNode().toString();
        }
        System.out.println("IN:" + node.toString() + " => " + parent);
        for (int x = 0; x < node.childPointers.length; x++) {
            if (node.childPointers != null) {
                if (node.childPointers[x] instanceof InternalNode) {
                    printNode((InternalNode) node.childPointers[x]);
                } else {
                    printNode((LeafNode) node.childPointers[x]);
                }
            }
        }

    }

    /**
     * Returns the node with the specified id.
     *
     * @param nodeID the page id of the node to be returned
     * @return the node with the specified id
     */
    public Node getNode(int nodeID) {
        if (nodeID < 0) {
            return null;
        }
        Node n = file.readPage(nodeID);
        n.setPageID(nodeID);
        return n;
    }

    /**
     * Write a node to the backing storage.
     *
     * @param node Node to write
     */
    protected void writeNode(Node node) {
        file.writePage(node);
    }

    /**
     * Delete a node from the backing storage.
     *
     * @param node Node to delete
     */
    protected void deleteNode(Node node) {
        file.deletePage(node.getPageID());
    }

    /**
     * Puts an Internal/leaf Node page into a outputstream stream.
     *
     * @param oos the outputstream
     * @param page the page that contains the node
     * @throws java.io.IOException
     */
    @Override
    public void writePage(DataOutputStream oos, Page page) throws IOException {
        if (page == null) {
            oos.writeInt(EMPTY_PAGE);
        } else {
            if (page instanceof LeafNode) {
                oos.writeInt(LEAF_NODE);
            } else if (page instanceof InternalNode) {
                oos.writeInt(INTERNAL_NODE);
            }

            ((AbstractExternalizablePage) page).writeExternal(oos);
        }

    }

    /**
     * Reads from an inputstream an Internal/Leaf Node page.
     *
     * @param ois the inputstream return the page read from the inputstream
     * @return
     * @throws java.io.IOException
     */
    @Override
    public Page readPage(DataInputStream ois) throws IOException {
        final int type = ois.readInt();
        if (type == EMPTY_PAGE) {
            return null;
        }
//      else if(type != FILLED_PAGE) {
//        throw new IllegalArgumentException("Unknown type: " + type);
//      }
        try {
            AbstractExternalizablePage page = null;
            if (type == INTERNAL_NODE) {
                page = new InternalNode(this);
            } else if (type == LEAF_NODE) {
                page = new LeafNode(this);
            }
            page.readExternal(ois);
            return page;
        } catch (Exception e) {
            //e.printStackTrace();
            throw new IOException("Error instanciating an index page", e);

        }

    }

    /*
    Flushes the file contents to disk
     */
    public void flush() {
        file.flush();
    }

}
