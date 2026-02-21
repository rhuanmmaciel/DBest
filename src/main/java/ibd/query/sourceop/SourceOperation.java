/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.sourceop;

import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import ibd.query.SingleSource;


/**
 * A source operation is a leaf node. Its provides direct access to a data source (a table).
 * The data source has as alias. The alias is used by other operations to identify rows that come from a specific data source
 * @author Sergio
 */
public abstract class SourceOperation extends Operation implements SingleSource{
    
    //the alias of the data source accessed by this source operation
    //String dataSourceAlias;
    
    /**
     *
     * @param dataSourceAlias the alias of the data source accessed by this source operation
     */
    public SourceOperation(String dataSourceAlias){
        this.alias = dataSourceAlias;
        
    }
    
//    @Override
//    public void setDataSourceAlias(String newName){
//        this.dataSourceAlias = newName;
//    }
//    
//    /**
//     * 
//     * @return the alias of the data source accessed by this source operation
//     */
//    @Override
//    public String getDataSourceAlias() {
//    return dataSourceAlias;
//    }
    
    /**
     * 
     * @return the name of the data source accessed by this source operation
     */
    public abstract String getDataSourceName();

    
    @Override
    public void setExposedDataSources() throws Exception {
        dataSources = new ReferedDataSource[1];
        dataSources[0] = new ReferedDataSource();
        dataSources[0].alias = alias;
    }
    
    @Override
    public void setConnectedDataSources() throws Exception {
        connectedDataSources = new ReferedDataSource[1];
        connectedDataSources[0] = new ReferedDataSource();
        connectedDataSources[0].alias = alias;
    }
    
    
    
    /**
     *
     * @return
     */
    @Override
     public String toString(){
         return "["+alias+"]";
     }
    
}
