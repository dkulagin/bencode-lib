package com.dk.bencode.coder;

import com.dk.bencode.coder.BDeserializer;
import com.dk.bencode.exception.BDeserializationException;
import com.dk.bencode.types.*;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Denis Kulagin
 */
public class BDeserializerTest {
    private BDeserializer deserializer = new BDeserializer();

    @Test
    public void testByteString() throws BDeserializationException, IOException {
        String byteString = "4:spam";

        BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(byteString.getBytes()));

        BByteString bs = (BByteString) deserializer.read(is);

        assertEquals("spam", new String(bs.getValue()));
    }

    @Test(expected = BDeserializationException.class)
    public void testByteString_shouldThrowException_whenNotEnoughCharacters() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("4:spa");
        deserializer.read(is);
    }

    @Test
    public void testByteString_shouldNotThrowException_whenEmptyString() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("0:");

        BByteString bs = (BByteString) deserializer.read(is);

        assertEquals(0, bs.getValue().length);
    }

    @Test(expected = BDeserializationException.class)
    public void testByteString_shouldThrowException_whenNegativeLength() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("-1:");
        deserializer.read(is);
    }

    @Test(expected = BDeserializationException.class)
    public void testByteString_shouldThrowException_whenBrokenLength() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("123o5:");
        deserializer.read(is);
    }

    @Test
    public void testIntegerPositive() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("i1024e");

        BInteger bs = (BInteger) deserializer.read(is);

        assertEquals(1024L, bs.getValue());
    }

    @Test
    public void testIntegerNegative() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("i-512e");

        BInteger bs = (BInteger) deserializer.read(is);

        assertEquals(-512L, bs.getValue());
    }

    @Test
    public void testIntegerZero() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("i0e");

        BInteger bs = (BInteger) deserializer.read(is);

        assertEquals(0L, bs.getValue());
    }

    @Test
    public void testIntegerMaxValue() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("i" + Long.MAX_VALUE + "e");

        BInteger bs = (BInteger) deserializer.read(is);

        assertEquals(Long.MAX_VALUE, bs.getValue());
    }

    @Test
    public void testIntegerMinValue() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("i" + Long.MIN_VALUE + "e");

        BInteger bs = (BInteger) deserializer.read(is);

        assertEquals(Long.MIN_VALUE, bs.getValue());
    }

    @Test(expected = BDeserializationException.class)
    public void testInteger_shouldThrowException_whenLeadingZeros() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("i01e");
        deserializer.read(is);
    }

    @Test(expected = BDeserializationException.class)
    public void testInteger_shouldThrowException_whenNegativeZero() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("i-0e");
        deserializer.read(is);
    }

    @Test(expected = BDeserializationException.class)
    public void testInteger_shouldThrowException_whenNoSuffix() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("i1024");
        deserializer.read(is);
    }

    @Test(expected = BDeserializationException.class)
    public void testInteger_shouldThrowException_whenBrokenInteger() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("i1o24e");
        deserializer.read(is);
    }

    @Test
    public void testList() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("li-512e4:spame");

        BList bs = (BList) deserializer.read(is);

        BElement e1 = new BInteger(-512L);
        BElement e2 = new BByteString("spam");

        List<BElement> expectedList = Arrays.asList(e1, e2);

        assertEquals(expectedList, bs.getValue());
    }

    @Test
    public void testEmptyList() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("le");

        BList list = (BList)deserializer.read(is);

        assertEquals(0, list.getValue().size());
    }

    @Test(expected = BDeserializationException.class)
    public void testList_shouldThrowException_whenNoSuffix() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("li-512e4:spam");
        deserializer.read(is);
    }

    @Test
    public void testDictionary() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("d4:key1i-512e4:key24:spame");

        BDictionary bs = (BDictionary) deserializer.read(is);

        TreeMap<BByteString, BElement> expectedMap = new TreeMap<>();

        expectedMap.put(new BByteString("key2"), new BByteString("spam"));
        expectedMap.put(new BByteString("key1"), new BInteger(-512L));

        assertEquals(expectedMap, bs.getValue());
    }

    @Test
    public void testEmptyDictionary() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("de");

        BDictionary list = (BDictionary)deserializer.read(is);

        assertEquals(0, list.getValue().keySet().size());
    }

    @Test(expected = BDeserializationException.class)
    public void testDictionary_shouldThrowException_whenNoSuffix() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("d4:key1i-512e4:key24:spam");
        deserializer.read(is);
    }

    @Test(expected = BDeserializationException.class)
    public void testDictionary_shouldThrowException_whenNotLexOrder() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("d4:key2i-512e4:key14:spam");
        deserializer.read(is);
    }

    @Test(expected = BDeserializationException.class)
    public void testDictionary_shouldThrowException_whenKeyIsNotByteString() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("di512ei-512ee");
        deserializer.read(is);
    }

    @Test
    public void testDeserializeNestedStructure() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("d3:mapd4:key1i-512e4:key24:spamee");

        BDictionary bs = (BDictionary) deserializer.read(is);

        TreeMap<BByteString, BElement> innerMap = new TreeMap<>();

        innerMap.put(new BByteString("key1"), new BInteger(-512L));
        innerMap.put(new BByteString("key2"), new BByteString("spam"));

        TreeMap<BByteString, BElement> outerMap = new TreeMap<>();

        outerMap.put(new BByteString("map"), new BDictionary(innerMap));

        assertEquals(outerMap, bs.getValue());
    }

    @Test(expected = BDeserializationException.class)
    public void testDeserializer_shouldThrowException_whenWrongPrefix() throws BDeserializationException, IOException {
        BufferedInputStream is = getInputStream("p");
        deserializer.read(is);
    }

    private BufferedInputStream getInputStream(String string) {
        return new BufferedInputStream(new ByteArrayInputStream(string.getBytes()));
    }
}
