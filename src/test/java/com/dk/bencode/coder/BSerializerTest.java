package com.dk.bencode.coder;

import com.dk.bencode.coder.BSerializer;
import com.dk.bencode.types.*;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

/**
 * @author Denis Kulagin
 */
public class BSerializerTest {
    private BSerializer serializer = new BSerializer();

    private ByteArrayOutputStream byteArrayStream;
    private BufferedOutputStream os;

    @Before
    public void setUp() {
        byteArrayStream = new ByteArrayOutputStream();
        os = new BufferedOutputStream(byteArrayStream);
    }

    @Test
    public void testWriteByteString() throws IOException {
        serializer.write(os, new BByteString("spam"));

        assertStreamContents("4:spam");
    }

    @Test
    public void testWriteIntegerPositive() throws IOException {
        serializer.write(os, new BInteger(1024L));

        assertStreamContents("i1024e");
    }

    @Test
    public void testWriteIntegerNegative() throws IOException {
        serializer.write(os, new BInteger(-1024L));

        assertStreamContents("i-1024e");
    }

    @Test
    public void testWriteList() throws IOException {
        BElement e1 = new BInteger(-512L);
        BElement e2 = new BByteString("spam");

        serializer.write(os, new BList(Arrays.asList(e1, e2)));

        assertStreamContents("li-512e4:spame");
    }

    @Test
    public void testWriteDictionary() throws IOException {
        TreeMap<BByteString, BElement> map = new TreeMap<>();

        map.put(new BByteString("key2"), new BByteString("spam"));
        map.put(new BByteString("key1"), new BInteger(-512L));

        serializer.write(os, new BDictionary(map));

        assertStreamContents("d4:key1i-512e4:key24:spame");
    }

    @Test
    public void testWriteNestedStructure() throws IOException {
        TreeMap<BByteString, BElement> map = new TreeMap<>();

        map.put(new BByteString("key1"), new BInteger(-512L));
        map.put(new BByteString("key2"), new BByteString("spam"));

        TreeMap<BByteString, BElement> outerMap = new TreeMap<>();

        outerMap.put(new BByteString("map"), new BDictionary(map));

        serializer.write(os, new BDictionary(outerMap));

        assertStreamContents("d3:mapd4:key1i-512e4:key24:spamee");
    }

    private void assertStreamContents(String expected) throws IOException {
        os.flush();
        assertEquals(expected, new String(byteArrayStream.toByteArray(), Charset.forName("UTF-8")));
    }
}
