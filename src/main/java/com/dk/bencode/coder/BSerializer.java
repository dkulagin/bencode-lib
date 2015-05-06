package com.dk.bencode.coder;

import com.dk.bencode.types.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author Denis Kulagin
 */
class BSerializer {
    public void writeByteString(BufferedOutputStream os, BByteString element) throws IOException {
        writeString(os, Integer.toString(element.getValue().length));
        os.write(':');
        os.write(element.getValue());
    }

    public void writeInteger(BufferedOutputStream os, BInteger element) throws IOException {
        os.write('i');
        writeString(os, Long.toString(element.getValue()));
        os.write('e');
    }

    public void writeList(BufferedOutputStream os, BList element) throws IOException {
        os.write('l');
        for (BElement e : element.getValue()) {
            write(os, e);
        }
        os.write('e');
    }

    public void writeDictionary(BufferedOutputStream os, BDictionary element) throws IOException {
        os.write('d');
        for (Map.Entry<BByteString, BElement> e : element.getValue().entrySet()) {
            writeByteString(os, e.getKey());
            write(os, e.getValue());
        }
        os.write('e');
    }

    public void write(BufferedOutputStream os, BElement element) throws IOException {
        if (element instanceof BByteString) {
            writeByteString(os, (BByteString) element);
        } else if (element instanceof BInteger) {
            writeInteger(os, (BInteger) element);
        } else if (element instanceof BList) {
            writeList(os, (BList) element);
        } else if (element instanceof BDictionary) {
            writeDictionary(os, (BDictionary) element);
        }
    }

    private void writeString(BufferedOutputStream os, String string) throws IOException {
        os.write(string.getBytes());
    }
}
