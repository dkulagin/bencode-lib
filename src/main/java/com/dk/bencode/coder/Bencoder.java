package com.dk.bencode.coder;

import com.dk.bencode.exception.BDeserializationException;
import com.dk.bencode.types.BElement;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * @author Denis Kulagin
 *         <p>
 *         Allows to read/write Bencode.
 */
public class Bencoder {
    private final BSerializer serializer;
    private final BDeserializer deserializer;

    public Bencoder() {
        serializer = new BSerializer();
        deserializer = new BDeserializer();
    }

    /**
     * Reads BElement from the stream.
     *
     * @param is input stream
     * @return read element
     * @throws BDeserializationException if decoding exception
     * @throws IOException               if input stream throws an exception
     */
    public BElement read(BufferedInputStream is) throws BDeserializationException, IOException {
        return deserializer.read(is);
    }

    /**
     * Writes BElement to the stream.
     *
     * @param os      output stream
     * @param element element to write, must not be null
     * @throws IOException if output stream throws an exception
     */
    public void write(BufferedOutputStream os, BElement element) throws IOException {
        if (null == element) {
            throw new IllegalArgumentException("element == null");
        }

        serializer.write(os, element);
    }
}
