/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.transaction;

import ibd.transaction.log.Logger;
import ibd.transaction.instruction.Instruction;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author pccli
 */
public class Transaction {
    
    private ArrayList<Instruction> instructions = new ArrayList<>();
    public boolean waitingLockRelease = false;
    private int currrentInstructionIndex = 0;
    private boolean commited = false;
    private int id;
    
    
    private Logger logger;
    
    
    public Iterable<Instruction> getInstrutions(){
        return instructions;
    }
    
    public int getId(){
    return id;
    }
    
    public void setId(int id){
    this.id = id;
    }
    
    public int getInstructionsSize(){
        return instructions.size();
    }
    
    public Instruction getInstruction(int index){
        return instructions.get(index);
    }
    public Instruction getCurrentInstruction(){
        return instructions.get(currrentInstructionIndex);
    }
    
    public int getCurrentInstructionIndex(){
        return currrentInstructionIndex;
    }
    
        
    public void addInstruction(Instruction i){
        instructions.add(i);
        i.setTransaction(this);
    }
    
    
    
    
    public boolean hasNext(){
        return currrentInstructionIndex<instructions.size();
    }

    public boolean waitingLockRelease(){
        return waitingLockRelease;
    }
    
    
    
    
    public void abort(){
        currrentInstructionIndex = 0;
        waitingLockRelease = false;
    }
    
    public void commit(){
        commited = true;
    }
    
    public boolean isCommited(){
    return commited;
    }
    
    public void advanceInstruction() throws Exception{
        //AbstractRecord r = instructions.get(currrentInstructionIndex).process();
        currrentInstructionIndex++;
        waitingLockRelease = false;
        //return r;
    }
    
    
    @Override
    public String toString(){
        return "T:" + id;
    }

    /**
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
    
    
    
    
}
