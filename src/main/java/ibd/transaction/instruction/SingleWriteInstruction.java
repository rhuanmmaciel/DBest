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
public class SingleWriteInstruction extends Instruction{
    
    
    private String content;
    
    public SingleWriteInstruction(Table table, int pk, String content) throws Exception{
        super(table, pk);
        this.content = content;
        mode = Instruction.WRITE;
    }
    
    public SingleWriteInstruction(Table table, String pk, String content) throws Exception{
        this(table, SimulatedIterations.getValue(pk), content);
    }
    
    
    
    @Override
    public List<DataRow> process() throws Exception{
        BasicDataRow rowData = new BasicDataRow();
        rowData.setInt("id", pk);
        rowData.setString("nome", content);
        //if (transaction.getLogger()!=null)
            //transaction.getLogger().transactionWrite(transaction,getTable(), pk, rec.toString(), content);
        
        endProcessing = true;
        List l = new ArrayList();
        
        l.add(getTable().addRecord(rowData, true));
        return l;
        
    }
    
    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    
}
