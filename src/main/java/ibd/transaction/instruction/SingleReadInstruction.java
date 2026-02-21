/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.transaction.instruction;

import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import ibd.transaction.SimulatedIterations;
import java.util.ArrayList;
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class SingleReadInstruction extends Instruction{
    
    
    
public SingleReadInstruction(Table table, int pk) throws Exception{
        super(table, pk);
        mode = Instruction.READ;
    }
    
    public SingleReadInstruction(Table table, String pk) throws Exception{
        this(table, SimulatedIterations.getValue(pk));
    }
    
    
    
    @Override
    public List<DataRow> process() throws Exception{
        List l = new ArrayList();
        BasicDataRow rowData = new BasicDataRow();
        rowData.setInt("id", pk);
        l.add(getTable().getRecord(rowData));
        endProcessing = true;
        return l;
    }
        
        
    
    @Override
    public int getMode(){
    return Instruction.READ;
    }


    
}
