package ibd.table.prototype.metadata;

public class BooleanMetadata extends Metadata{
    public static final BooleanMetadata generic = new BooleanMetadata();
    public BooleanMetadata() {
        super((short) 1, Metadata.BOOLEAN);
    }
}
