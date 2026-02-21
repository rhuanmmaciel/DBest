package ibd.table.prototype.metadata;

public class FloatMetadata extends Metadata{
    public static final FloatMetadata generic = new FloatMetadata();
    public FloatMetadata() {
        super((short) 4, Metadata.FLOATING_POINT);
    }
}
