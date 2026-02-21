/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.block.management;

import ibd.persistent.Page;
import ibd.persistent.PageSerialization;
import ibd.table.block.Block;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ibd.table.prototype.Prototype;

/**
 *
 * @author ferna
 */
public class BlockPageSerialization implements PageSerialization{
    
    int pageSize;
    Prototype rowPrototype;
    
    public BlockPageSerialization(int pageSize, Prototype rowPrototype){
        this.pageSize = pageSize;
        this.rowPrototype = rowPrototype;
                
    }
    
    @Override
    public void writePage(DataOutputStream oos, Page page) throws IOException {
        if (page != null) {
            ((Block) page).writeExternal(oos);
        }
    }

    @Override
    public Page readPage(DataInputStream ois) throws IOException {
        Block block = new Block(pageSize, rowPrototype);
        block.readExternal(ois);
        return block;
    }
}
