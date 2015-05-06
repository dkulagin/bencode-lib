package com.dk.bencode.types;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Denis Kulagin
 */
public class BDictionary implements BElement {
    Map<BByteString, BElement> value;

    public BDictionary(TreeMap<BByteString, BElement> value) {
        if (null == value) {
            throw new IllegalArgumentException("value == null");
        }

        this.value = value;
    }

    public Map<BByteString, BElement> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BDictionary that = (BDictionary) o;

        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BDictionary{" +
                "value=" + value +
                '}';
    }
}
