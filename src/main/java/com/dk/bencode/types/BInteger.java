package com.dk.bencode.types;

/**
 * @author Denis Kulagin
 */
public class BInteger implements BElement {
    private final long value;

    public BInteger(Long value) {
        if (null == value) {
            throw new IllegalArgumentException("value == null");
        }

        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BInteger bInteger = (BInteger) o;

        return value == bInteger.value;

    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public String toString() {
        return "BInteger{" +
                "value=" + value +
                '}';
    }
}
