/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.lookup;

import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;

/**
 *
 * @author ferna
 */
public class LiteralElement extends Element{
    protected Comparable value;

    public LiteralElement(Comparable value){
        this.value = value;
    }
    
    @Override
    public Comparable getValue(Tuple tuple) {
        return value;
    }
    
    @Override
    public Comparable getValueFromRow(LinkedDataRow row){
        return value;
    }
    
    public void setValue(Comparable value) {
        this.value = value;
    }
    
    @Override
    public String toString(){
        return value.toString();
    }
}
