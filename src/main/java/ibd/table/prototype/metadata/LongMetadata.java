package ibd.table.prototype.metadata;

public class LongMetadata extends Metadata{

    public static final LongMetadata generic = new LongMetadata();

    public LongMetadata(boolean primaryKey) {
        super((short) 8, primaryKey? Metadata.PRIMARY_KEY:Metadata.NONE);
    }
    public LongMetadata(){
        this(false);
    }
}
