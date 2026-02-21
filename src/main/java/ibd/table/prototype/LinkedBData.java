/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.prototype;

/**
 *
 * @author ferna
 */
public class LinkedBData extends BData{
    LinkedDataRow row;
    
    public LinkedBData(LinkedDataRow row, int offset, int length){
        super(offset, length);
        this.row = row;
    }

    @Override
    public byte[] getData() {
        return row.data;
    }
}
