package ibd.table.util;

import ibd.table.prototype.metadata.Metadata;


public class Util {


    public static String typeOfColumn(Metadata meta){
        if(meta == null) return "null";
        if(meta.isBoolean())return "boolean";
        if(meta.isString())return "string";
        if(meta.isInt() && meta.getSize()==8)return "long";
        if(meta.isInt() && meta.getSize()==4)return "int";
        if(meta.isFloat() && meta.getSize()==8)return "double";
        if(meta.isFloat() && meta.getSize()==4)return "float";
        return "binary";
    }
}
