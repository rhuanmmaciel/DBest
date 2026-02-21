/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.table.prototype;

/**
 *
 * @author ferna
 */
public class UnlinkedBData extends BData{
    byte[] data;
    
    public UnlinkedBData(byte[] data){
        super();
        this.data = data;
    }
    
    public UnlinkedBData(byte[] data, int offset, int length){
        super(offset, length);
        this.data = data;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
