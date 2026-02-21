package ibd.table.prototype;

import ibd.table.util.UtilConversor;



public abstract class BData {
    private int offset;
    private int length;

    public BData(){
        //this.data = new byte[0];
    }
    public BData(int offset, int length){
        this.offset = offset;
        this.length = length;
    }

//    public void setData(byte[] data) {
//        this.data = data;
//    }
    /*
    public void setInt(int data) {
        this.setData(UtilConversor.intToByteArray(data));
    }
    public void setLong(long data) {
        this.setData(UtilConversor.longToByteArray(data));
    }
    public void setString(String data) {
        this.setData(UtilConversor.stringToByteArray(data));
    }
    public void setFloat(float data) {
        this.setData(UtilConversor.floatToByteArray(data));
    }
    public void setDouble(double data) {
        this.setData(UtilConversor.doubleToByteArray(data));
    }
    public void setBoolean(boolean data) {
        this.setData(new byte[]{(byte) (data ? 1 : 0)});
    }

*/
        public abstract byte[] getData();
    
    public Integer getInt() {
        //byte[] data = this.data;
        byte[] data = this.getData();
        if(data==null)return null;
        return UtilConversor.byteArrayToInt(data, offset);
    }
    
    public Long getLong() {
       byte[] data = this.getData();
        if(data==null)return null;
        return UtilConversor.byteArrayToLong(data, offset);
    }
    public Float getFloat() {
        byte[] data = this.getData();
        if(data==null)return null;
        return UtilConversor.byteArrayToFloat(data, offset);
    }
    public Double getDouble() {
        byte[] data = this.getData();
        if(data==null)return null;
        return UtilConversor.byteArrayToDouble(data, offset);
    }
    public String getString() {
        byte[] data = this.getData();
        if(data==null)return null;
        
        return UtilConversor.byteArrayToString(data, offset, length);
        //return UtilConversor.byteArrayToString(data);
    }
    public Boolean getBoolean() {
        byte[] data = this.getData();
        if(data==null)return null;
        return data[0]!=0;
    }

    public int length(){
        return this.getData().length;
    }

}
