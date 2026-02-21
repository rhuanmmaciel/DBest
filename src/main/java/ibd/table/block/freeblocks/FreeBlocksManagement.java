/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.block.freeblocks;

import ibd.persistent.Page;
import ibd.persistent.PageFile;
import ibd.persistent.PageSerialization;
import ibd.persistent.PersistentPageFile;
import ibd.table.block.Block;
import ibd.table.block.TableHeader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import ibd.table.prototype.DataRow;

/**
 *
 * @author Sergio
 */
public class FreeBlocksManagement {

    protected PageFile<FreeBlocksPage> freeBlocksFile;
    protected TreeSet<FreeBlock> blocks = new TreeSet<FreeBlock>();
    public Hashtable<Integer, FreeBlock> blocks_ = new Hashtable();

    public FreeBlocksManagement(String folder, String name, int pageSize, boolean recreate) throws IOException {
        this.freeBlocksFile = new PersistentPageFile(pageSize, Paths.get(folder + File.separator + name), recreate);
        this.freeBlocksFile.setPageSerialization(new PageSerialization() {
            @Override
            public void writePage(DataOutputStream oos, Page page) throws IOException {
                ((FreeBlocksPage) page).writeExternal(oos);
            }

            @Override
            public Page readPage(DataInputStream ois) throws IOException {
                FreeBlocksPage block = new FreeBlocksPage(pageSize);
                block.readExternal(ois);
                return block;
            }
        });

    }

    protected TableHeader createHeader(int pageSize, int firstBlock, int firstHeapBlock, int lastHeapBlock) {
        return new TableHeader(pageSize, firstBlock, firstHeapBlock, lastHeapBlock);
    }

    public void load() {
        freeBlocksFile.initialize(createHeader(freeBlocksFile.getPageSize(), -1, -1, -1));

        clear();
        if (((TableHeader) freeBlocksFile.getHeader()).getFirstBlock() > -1) {
            loadPage(0);
        }
    }

    private void loadPage(int pageID) {
        if (pageID < 0) {
            return;
        }
        FreeBlocksPage block = freeBlocksFile.readPage(pageID);

        
        List<FreeBlock> list2 = block.freeBlocks;
        for (FreeBlock fb : list2) {
            blocks.add(fb);
            blocks_.put(fb.getBlockId(), fb);
        }

        loadPage(block.nextBlockID);

    }

    public void flush() {
        freeBlocksFile.reset();
        FreeBlocksPage block = new FreeBlocksPage(freeBlocksFile.getPageSize());
        freeBlocksFile.writePage(block);

        ((TableHeader) freeBlocksFile.getHeader()).setFirstBlock(block.getPageID());
        
        Iterator<FreeBlock> i1 = blocks.iterator();
        while (i1.hasNext()) {
            FreeBlock fb = i1.next();
            if (!block.addFreeBlock(fb)) {
                FreeBlocksPage newBlock = new FreeBlocksPage(freeBlocksFile.getPageSize());
                freeBlocksFile.writePage(newBlock);

                block.nextBlockID = newBlock.getPageID();
                freeBlocksFile.writePage(block);
                block = newBlock;
                block.addFreeBlock(fb);
            }
        }

        freeBlocksFile.writePage(block);
        freeBlocksFile.flush();
    }

    public void clear() {
        blocks.clear();
        blocks_.clear();
    }


    public int getFirstFittingBlock(DataRow rec) {
        FreeBlock fb = new FreeBlock(-1);
        fb.setLen(rec.len());
        FreeBlock result = blocks.higher(fb);
        
        if (result != null) {
            return result.getBlockId();
        }
        return -1;
    }

//    private void removeBlock(Block2 block){
//            FreeBlock fb = blocks_.get(block.getPageID());
//            if (fb==null) return;
//            blocks_.remove(fb.getBlockId());
//            blocks.remove(fb);
//            if (index.isEmpty() && blocks.isEmpty())
//            ((TableHeader)indexFile.getHeader()).setHasData(false);
//            }
    public void updateBlock(Block block) {
        FreeBlock fb = blocks_.get(block.getPageID());
        if (fb == null) {
            fb = new FreeBlock(block.getPageID());
            fb.setLen(block.getPageSize() - block.getUsedSpace() - block.getHeaderSizeInBytes());
            blocks_.put(fb.getBlockId(), fb);
        }
        blocks.remove(fb);
        fb.setLen(block.getPageSize() - block.getUsedSpace()  - block.getHeaderSizeInBytes());
        blocks.add(fb);

    }

    public int getFreeBlocksCount() {
        return blocks.size();
    }

}
