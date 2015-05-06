package com.dk.bencode.types;

import java.util.List;

/**
 * @author Denis Kulagin
 */
public class BList implements BElement {
    private List<BElement> value;

    public BList(List<BElement> value) {
        if (null == value) {
            throw new IllegalArgumentException("value == null");
        }

        this.value = value;
    }

    public List<BElement> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BList bList = (BList) o;

        return !(value != null ? !value.equals(bList.value) : bList.value != null);

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BList{" +
                "value=" + value +
                '}';
    }
}
