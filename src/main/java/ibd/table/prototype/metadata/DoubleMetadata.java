package ibd.table.prototype.metadata;

public class DoubleMetadata extends Metadata{
    public static final DoubleMetadata generic = new DoubleMetadata();
    public DoubleMetadata() {
        super((short) 8, Metadata.FLOATING_POINT);
    }
}
