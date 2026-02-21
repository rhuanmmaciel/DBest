/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.unaryop;

import ibd.query.ColumnDescriptor;

/**
 *
 * @author ferna
 */
public class ColumnRename {
    private ColumnDescriptor originalColumn;
    private String renamedColumn;
    
    public ColumnRename(ColumnDescriptor originalColumn, String renamedColumn){
        this.originalColumn = originalColumn;
        this.renamedColumn = renamedColumn;
    }
    
}
