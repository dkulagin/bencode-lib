package com.dk.bencode.types;

import java.util.Arrays;

/**
 * @author Denis Kulagin
 */
public class BByteString implements BElement, Comparable<BByteString> {
    private final byte[] value;

    public BByteString(byte[] value) {
        this.value = value;
    }

    public BByteString(String string) {
        this(string.getBytes());
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BByteString that = (BByteString) o;

        return Arrays.equals(value, that.value);

    }

    @Override
    public int hashCode() {
        return value != null ? Arrays.hashCode(value) : 0;
    }

    @Override
    public String toString() {
        return "BByteString{" +
                "value=" + Arrays.toString(value) +
                '}';
    }

    @Override
    public int compareTo(BByteString o) {
        if (this.value == o.value) {
            return 0;
        }

        for (int i = 0, j = 0; i < this.value.length && j < o.value.length; i++, j++) {
            int a = (this.value[i] & 0xff);
            int b = (o.value[j] & 0xff);
            if (a != b) {
                return a - b;
            }
        }

        return this.value.length - o.value.length;
    }
}
