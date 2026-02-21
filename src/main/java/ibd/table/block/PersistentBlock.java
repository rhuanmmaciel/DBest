/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.block;

import ibd.persistent.AbstractExternalizablePage;

/**
 *
 * @author Sergio
 */
public class PersistentBlock extends AbstractExternalizablePage{
    
    protected int pageSize;
    
    public PersistentBlock(int pageSize){
        this.pageSize = pageSize;
    }
    
    public int getPageSize(){
        return pageSize;
    }
    
    
}
