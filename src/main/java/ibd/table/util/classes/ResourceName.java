package ibd.table.util.classes;

public class ResourceName {

    private String source,column;

    public ResourceName(String source,String column){
        this.source = source;
        this.column = column;
    }

    public String getSource() {
        return source;
    }

    public String getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return source+"."+column;
    }
}
