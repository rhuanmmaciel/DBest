package ibd.table.prototype.metadata;

public class IntegerMetadata extends Metadata{
    public static final IntegerMetadata generic = new IntegerMetadata();
    public IntegerMetadata(boolean primaryKey) {
        super((short) 4, primaryKey ? Metadata.PRIMARY_KEY:Metadata.NONE);
    }
    public IntegerMetadata() {
        this(false);
    }

}
