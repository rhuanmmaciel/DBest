/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.lookup;

import ibd.table.prototype.LinkedDataRow;

/**
 *
 * @author ferna
 */
public class RowValueElement extends RowElement{
    Comparable value;
    
    public RowValueElement(Comparable value){
        this.value = value;
    }
    
    public  Comparable getValue(LinkedDataRow row){
        return value;
    }
}
