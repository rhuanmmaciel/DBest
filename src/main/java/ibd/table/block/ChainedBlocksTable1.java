/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.block;

import ibd.persistent.PageFile;
import ibd.persistent.PersistentPageFile;
import ibd.persistent.cache.Cache;
import ibd.persistent.cache.LRUCache;
import ibd.query.lookup.LookupFilter;
import ibd.table.btree.AllRowsIterator;
import ibd.table.btree.FilteredRowsIterator;
import ibd.table.Table;
import ibd.table.block.freeblocks.FreeBlocksManagement;
import ibd.table.block.index.BlockIndex;
import ibd.table.block.management.BlockPageSerialization;
import ibd.table.prototype.BasicDataRow;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.Header;
import ibd.table.prototype.LinkedDataRow;

public class ChainedBlocksTable1 extends Table {

    String folder;
    String name;
    int pageSize;

    protected FreeBlocksManagement freeBlocksFile;

    public boolean loaded = false;

    protected PageFile<Block> dataFile;
    protected Cache<Block> cache = null;

    protected BlockIndex blockIndex = null;

    public Prototype rowPrototype = null;

    public ChainedBlocksTable1(Header header, String folder, String name) throws Exception {

        super(header);
        this.folder = folder;
        this.name = name;

        
        loaded = false;

    }


    protected TableHeader createHeader(int pageSize, int firstBlock, int firstHeapBlock, int lastHeapBlock) {
        return new TableHeader(pageSize, firstBlock, firstHeapBlock, lastHeapBlock);
    }

    @Override
    public void create(Prototype rowPrototype, int pageSize) throws Exception {
        if (loaded) {
            return;
        }
        
        //this does not work if the file already exists, and the prototype is null
        rowPrototype.validateColumns();
        
        freeBlocksFile = new FreeBlocksManagement(folder, name + ".fbf", pageSize, true);
        blockIndex = new BlockIndex(folder, name, pageSize, true, rowPrototype);
        PageFile<Block> file = new PersistentPageFile(pageSize, Paths.get(folder + File.separator + name + ".dat"), true);
        open(file);
    }
    
    @Override
    public void open() throws Exception {
        if (loaded) {
            return;
        }
        freeBlocksFile = new FreeBlocksManagement(folder, name + ".fbf", pageSize, false);
        blockIndex = new BlockIndex(folder, name, pageSize, false, rowPrototype);

        PageFile<Block> file = new PersistentPageFile(pageSize, Paths.get(folder + File.separator + name + ".dat"), false);
        open(file);
    }
    
    private void open(PageFile<Block> file) throws Exception {

        file.setPageSerialization(new BlockPageSerialization(pageSize, rowPrototype));
        
        cache = new LRUCache(5000000);
        //cache = new MidPointCache<Block>(25000);
        cache.setPageFile(file);
        
        if (cache != null) {
            dataFile = cache;
        }

        dataFile.initialize(createHeader(dataFile.getPageSize(), -1, -1, -1));

        freeBlocksFile.load();
        blockIndex.open();
        
        loaded = true;

    }

    @Override
    public LinkedDataRow getRecord(BasicDataRow dataRow) throws Exception {

        //dataRow.setMetadata(rowPrototype);
        /*
        IndexRecord index_rec = index.getEntry(primaryKey);
        if (index_rec == null) {
            return null;
        }

        //find the block that contains the record
        Block block = getBlock(index_rec.getBlockId());
        
        //now locate the record within the block
        return (BlockRecord) block.getRecord(index_rec.getPrimaryKey());
         */

        Integer blockID = blockIndex.searchBlockID(dataRow);
        if (blockID == null) {
            return null;
        }

        Block block = getBlock(blockID);

        //now locate the record within the block
        LinkedDataRow linkedDataRow = (LinkedDataRow)block.getRecord(dataRow);

        //dataRow.setMetadata(rowPrototype);
        return linkedDataRow;
    }

    @Override
    public List<LinkedDataRow> getRecords(String col, Comparable comp, int comparisonType) throws Exception {

        //return chainedBlocks.getRecords("id", primaryKey, comparisonType);
        return null;
    }

    /**
     * Adds a record to the table.The record is added at the first fitting block
     *
     * @param dataRow
     * @param unique
     * @return
     * @throws Exception
     */
    @Override
    public LinkedDataRow addRecord(BasicDataRow dataRow, boolean unique) throws Exception {
        //dataRow.setMetadata(rowPrototype);
        Integer blockID = blockIndex.searchBlockID(dataRow);
        if (blockID != null) {
            throw new Exception("ID already exists");
        }

        LinkedDataRow linkedDataRow = addRecord1(dataRow);
        return linkedDataRow;

    }

    protected LinkedDataRow addRecord1(BasicDataRow dataRow) throws Exception {

        //finds first block with enough space for the record
        int blockID = getFreeBlockID(dataRow);

        Block b = getBlock(blockID);
        if (b == null) {
            b = addBlock();
        }

        return addRecord(b, dataRow);
    }

    protected LinkedDataRow addRecord(Block block, BasicDataRow dataRow) throws Exception {

        LinkedDataRow linkedDataRow = (LinkedDataRow)block.addRecord(dataRow);

        if (dataRow == null) {
            return null;
        }

        dataFile.writePage(block);
        freeBlocksFile.updateBlock(block);

        blockIndex.insert(dataRow, block.getPageID());

        return linkedDataRow;

    }

    @Override
    public LinkedDataRow updateRecord(BasicDataRow dataRow) throws Exception {
        //dataRow.setMetadata(rowPrototype);
        DataRow pkRow = rowPrototype.createPKRow(dataRow);
        Integer blockID = blockIndex.searchBlockID(pkRow);

        //IndexRecord index_rec = index.getEntry(primaryKey);
        //if (index_rec == null) 
        if (blockID == null) {
            return null;
        }

        //Block block = getBlock(index_rec.getBlockId());
        Block block = getBlock(blockID);
        LinkedDataRow foundDataRow = (LinkedDataRow) block.getRecord(pkRow);

        foundDataRow = block.updateRecord(foundDataRow, dataRow);
        if (foundDataRow == null) {
            return null;
        }

        dataFile.writePage(block);
        freeBlocksFile.updateBlock(block);

        blockIndex.update(foundDataRow, block.getPageID());

        return foundDataRow;

    }

    @Override
    public LinkedDataRow removeRecord(BasicDataRow dataRow) throws Exception {
        //dataRow.setMetadata(rowPrototype);
        Integer blockID = blockIndex.searchBlockID(dataRow);

        //IndexRecord index_rec = index.getEntry(primaryKey);
        //if (index_rec == null) 
        if (blockID == null) {
            return null;
        }

        //Block block = getBlock(index_rec.getBlockId());
        Block block = getBlock(blockID);
        LinkedDataRow foundDataRow = (LinkedDataRow) block.getRecord(dataRow);

        return removeRecord(block, foundDataRow);
    }

    protected LinkedDataRow removeRecord(Block block, DataRow dataRow) throws Exception {

        LinkedDataRow linkedDataRow = (LinkedDataRow)block.removeRecord(dataRow);

        if (linkedDataRow == null) {
            return null;
        }

        freeBlocksFile.updateBlock(block);
        dataFile.writePage(block);

        blockIndex.delete(linkedDataRow);

        return linkedDataRow;

    }

    @Override
    public void flushDB() throws Exception {

        freeBlocksFile.flush();
        dataFile.flush();
        blockIndex.flush();
    }

    @Override
    public int getRecordsAmount() throws Exception {

        return 0;
        //return tree.getRecordsAmount();
    }

    @Override
    public List<LinkedDataRow> getAllRecords() throws Exception {
        List<LinkedDataRow> list = new ArrayList();

        Block currentBlock_ = getBlock(getFirstBlock());
        Iterator<LinkedDataRow> iterator = currentBlock_.iterator();

        while (iterator.hasNext() || currentBlock_.next_block_id != -1) {
            if (iterator.hasNext()) {
                LinkedDataRow record = iterator.next();
                list.add(record);
            } else {
                currentBlock_ = getBlock(currentBlock_.next_block_id);
                iterator = currentBlock_.iterator();
            }
        }
        return list;
    }

    @Override
    public void printStats() throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    protected Integer getFreeBlockID(DataRow record) {
        return freeBlocksFile.getFirstFittingBlock(record);
    }

    protected Block getBlock(int block_id) throws Exception {
        if (block_id < 0) {
            return null;
        }
        Block n = dataFile.readPage(block_id);
        return n;
    }

    protected int getLargestBlock() throws Exception {
        return dataFile.getNextPageID() - 1;
    }

    /**
     * Adds a new block after the current tail.The new block is to become the
     * tail of the chained list.
     * @return 
     * @throws java.lang.Exception 
     */
    protected Block addBlock() throws Exception {
        Block b = null;
        int fb = getFirstBlock();
        if (fb == -1) {
            //the list is empty, meaning no block is yet allocated
            b = createFirstBlock();
        } else {
            //all blocks are full, so a new one should be added at the end
            Block currentTail = getBlock(getLargestBlock());
            b = addBlock(currentTail);
        }
        return b;
    }

    /**
     * Adds a new block after the current tail.The new block is to become the
     * tail of the chained list.
     *
     * @param currentTail
     * @return 
     */
    protected Block addBlock(Block currentTail) {
        Block newBlock = new Block(dataFile.getPageSize(), rowPrototype);
        newBlock.prev_block_id = currentTail.getPageID();
        dataFile.writePage(newBlock);

        currentTail.next_block_id = newBlock.getPageID();
        dataFile.writePage(currentTail);

        return newBlock;
    }

    protected Block createFirstBlock() throws Exception {
        Block b = new Block(dataFile.getPageSize(), rowPrototype);
        dataFile.writePage(b);
        setFirstBlock(b.getPageID());
        return b;
    }

    protected int getFirstBlock() throws Exception {
        return ((TableHeader) dataFile.getHeader()).getFirstBlock();
    }

    protected void setFirstBlock(int blockId) throws Exception {
        ((TableHeader) dataFile.getHeader()).setFirstBlock(blockId);
    }

    protected int getFirstHeapBlock() throws Exception {
        return ((TableHeader) dataFile.getHeader()).getFirstHeapBlock();
    }

    protected void setFirstHeapBlock(int blockId) throws Exception {
        ((TableHeader) dataFile.getHeader()).setFirstHeapBlock(blockId);
    }

    protected int getLastHeapBlock() throws Exception {
        return ((TableHeader) dataFile.getHeader()).getLastHeapBlock();
    }

    protected void setLastHeapBlock(int blockId) throws Exception {
        ((TableHeader) dataFile.getHeader()).setLastHeapBlock(blockId);
    }

    @Override
    public Prototype getPrototype() {
        return rowPrototype;
    }

    @Override
    public List<LinkedDataRow> getRecords(BasicDataRow rowData) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public LinkedDataRow updateRecord(LinkedDataRow rowdata) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public LinkedDataRow getRecord(LinkedDataRow rowdata) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<LinkedDataRow> getRecords(LinkedDataRow rowData) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean contains(LinkedDataRow pkRow) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<LinkedDataRow> getFilteredRecords(LookupFilter filter) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public AllRowsIterator getAllRecordsIterator() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public FilteredRowsIterator getFilteredRecordsIterator(LookupFilter filter) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<LinkedDataRow> getRecords(LinkedDataRow pkRow, LookupFilter rowFilter) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getHeaderName() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Iterator getPKFilteredRecordsIterator(LinkedDataRow pkRow, LookupFilter rowFilter, int compType) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


}
