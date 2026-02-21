/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree;

import ibd.persistent.AbstractExternalizablePage;

/**
 * This class represents a general node within the B+ tree and serves as a
 * superclass of InternalNode and LeafNode.
 */
public abstract class Node extends AbstractExternalizablePage {

    private InternalNode parent;
    private int parentID = -1;
    
    public void setParentNode(InternalNode n){
        parent = n;
        if (n==null)
            parentID = -1;
        else parentID = n.getPageID();
    }
    
    public InternalNode getParentNode(){
        return parent;
    }
    
    public int getParentID(){
        return parentID;
    }
    
    public void setParentID(int id){
        parentID = id;
    }
    
    
}
