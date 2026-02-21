package ibd.table.prototype.metadata;

public class StringMetadata extends Metadata{
    public StringMetadata(int size) {
        super((short)((size>Short.MAX_VALUE)?(size >> 8 + 1):size), (short)(Metadata.STRING|Metadata.DINAMIC_COLUMN_SIZE | (size>Short.MAX_VALUE?Metadata.LSHIFT_8_SIZE_COLUMN:Metadata.NONE)));
    }

}
