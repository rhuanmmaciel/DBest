package enums;

public enum OperationFlags {

    NO_ARGUMENT_NEEDED ((short) 1);

    private final short value;

    OperationFlags(short value) {
        this.value = value;
    }

    public short getValue() {
        return this.value;
    }
}