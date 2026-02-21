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
public class RowColumnElement extends RowElement{
    int colIndex = -1; 
    
    public RowColumnElement(int colIndex){
        this.colIndex = colIndex;
    }
    
    @Override
    public  Comparable getValue(LinkedDataRow row){
        return row.getValue(colIndex);
    }
}
