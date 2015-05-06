package com.dk.bencode.coder;

import com.dk.bencode.exception.BDeserializationException;
import com.dk.bencode.types.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author Denis Kulagin
 */
class BDeserializer {
    public BElement read(BufferedInputStream is) throws BDeserializationException, IOException {
        try {
            char head = readChar(is);
            return read(is, head);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new BDeserializationException(e);
        }
    }

    private BElement read(BufferedInputStream is, char head) throws BDeserializationException, IOException {
        if (head >= '0' && head <= '9') {
            return readByteString(is, head);
        } else if ('i' == head) {
            return readInteger(is);
        } else if ('l' == head) {
            return readList(is);
        } else if ('d' == head) {
            return readDictionary(is);
        } else {
            throw new BDeserializationException("Unexpected header char " + head + " in the stream.");
        }
    }

    private char readChar(BufferedInputStream is) throws BDeserializationException, IOException {
        int intValue = is.read();

        if (-1 == intValue) {
            throw new BDeserializationException("Unexpected end of stream.");
        } else {
            return (char) intValue;
        }
    }

    private BByteString readByteString(BufferedInputStream is, char head) throws IOException, BDeserializationException {
        StringBuilder header = new StringBuilder();
        header.append(head);

        boolean isHeaderComplete = false;
        do {
            char c = readChar(is);
            if (':' == c) {
                isHeaderComplete = true;
            } else if (c >= '0' && c <= '9') {
                header.append(c);
            } else {
                throw new BDeserializationException("Unexpected char " + c + " in BByteString header.");
            }
        } while (!isHeaderComplete);

        int length = Integer.parseInt(header.toString());

        byte[] buf = new byte[length];

        int count = is.read(buf, 0, length);

        if (length != count) {
            throw new BDeserializationException("Not enough data in stream to decode BByteString. Expected: " + length + ", actual: " + count);
        }

        return new BByteString(buf);
    }

    private BInteger readInteger(BufferedInputStream is) throws IOException, BDeserializationException {
        StringBuilder content = new StringBuilder();

        boolean isContentComplete = false;
        do {
            char c = readChar(is);
            if ('e' == c) {
                isContentComplete = true;
            } else if (c >= '0' && c <= '9' || '-' == c) {
                content.append(c);
            } else {
                throw new BDeserializationException("Unexpected char " + c + " in BInteger content.");
            }
        } while (!isContentComplete);

        try {
            String string = content.toString();

            if (string.startsWith("0") && string.length() > 1) {
                throw new BDeserializationException("Exception while parsing BInteger. Leading zeros are not allowed.");
            }

            if (string.startsWith("-0")) {
                throw new BDeserializationException("Exception while parsing BInteger. Negative zero is not allowed.");
            }

            return new BInteger(Long.parseLong(string));
        } catch (NumberFormatException e) {
            throw new BDeserializationException(e);
        }
    }

    private BElement readList(BufferedInputStream is) throws IOException, BDeserializationException {
        List<BElement> value = new ArrayList<>();

        boolean isContentComplete = false;
        do {
            char c = readChar(is);
            if ('e' == c) {
                isContentComplete = true;
            } else {
                value.add(read(is, c));
            }
        } while (!isContentComplete);

        return new BList(value);
    }

    private BDictionary readDictionary(BufferedInputStream is) throws IOException, BDeserializationException {
        TreeMap<BByteString, BElement> map = new TreeMap<>();

        boolean isContentComplete = false;
        BByteString oldKey = null;
        do {
            char c = readChar(is);
            if ('e' == c) {
                isContentComplete = true;
            } else {
                BByteString key = (BByteString) read(is, c);

                if (oldKey != null && oldKey.compareTo(key) > 0) {
                    throw new BDeserializationException("Keys in dictionary must follow in lexicographical order.");
                }

                oldKey = key;

                BElement value = read(is);
                map.put(key, value);
            }
        } while (!isContentComplete);

        return new BDictionary(map);
    }
}
