/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query;

/**
 * A class that defines a the name of a column and its corresponding table
 * @author Sergio
 */
public class ColumnDescriptor {
    private String columnName;
    private String tableName;
    
     /*
    * the index of the tuple that contains the column to be compared.
    */
    //protected int realTupleIndex;
    
    /*
    * the index of the tuple that contains the column to be compared.
    */
    //protected int tupleIndex;
    
    /*
    * the index of the column
    */
    //protected int colIndex;
    
    ColumnLocation columnLocation;
    
    public ColumnDescriptor(String columnName) throws Exception{
        setInfo(columnName);
    }
    
    public ColumnDescriptor(String tableName, String columnName){
        this.tableName = tableName;
        this.columnName = columnName;
    }
    
    /**
     *
     * @return the name of the column placed at the left side of the comparison. 
     */
    public String getColumnName(){
        return columnName;
    }
    
    /**
     *
     * @return the name of the table placed at the left side of the comparison.
     */
    public String getTableName(){
        return tableName;
    }
    
    private void setInfo(String column) throws Exception{
    if (column.length()==0) throw new Exception("Column name was not provided");
    int index = column.indexOf('.');
    if (index==-1) {
        columnName = column;
        return;
    }
    if (index==0) throw new Exception("Table name was not provided");
    if (index==column.length()-1) throw new Exception("Column name was not provided");
    
    tableName = column.substring(0, index);
    columnName = column.substring(index+1, column.length());
    
    }
//    
//    /**
//     * @return the tupleIndex
//     */
//    public int getRealTupleIndex() {
//        return realTupleIndex;
//    }
//    
//    /**
//     * Sets the tuple index. 
//     * This information needs to be set before the filter is performed by the match function
//     * @param realTupleIndex
//     */
//    public void setRealTupleIndex(int realTupleIndex) {
//        this.realTupleIndex = realTupleIndex;
//    }
//    
//    /**
//     * @return the tupleIndex
//     */
//    public int getTupleIndex() {
//        return tupleIndex;
//    }
//    
//    /**
//     * Sets the tuple index. 
//     * This information needs to be set before the filter is performed by the match function
//     * @param tupleIndex
//     */
//    public void setTupleIndex(int tupleIndex) {
//        this.tupleIndex = tupleIndex;
//    }
//    
//    public int getColumnIndex() {
//        return colIndex;
//    }
//    
//    /**
//     * Sets the column index. 
//     * This information needs to be set before the filter is performed by the match function
//     * @param colIndex
//     */
//    public void setColumnIndex(int colIndex) {
//        this.colIndex = colIndex;
//    }
    
    public ColumnLocation getColumnLocation(){
        return columnLocation;
    }
    
    public void setColumnLocation(ColumnLocation cl){
        this.columnLocation = cl;
    }
   
    @Override
    public String toString(){
        if (tableName == null)
            return columnName;
        return tableName+"."+columnName;
    }
}
