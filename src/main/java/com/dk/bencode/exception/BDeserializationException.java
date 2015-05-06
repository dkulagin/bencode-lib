package com.dk.bencode.exception;

import java.io.IOException;

/**
 * @author Denis Kulagin
 */
public class BDeserializationException extends Exception {
    public BDeserializationException(Exception e) {
        super(e);
    }

    public BDeserializationException(String message) {
        super(message);
    }
}
