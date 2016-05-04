package com.spookybox.camera;

import org.junit.Before;
import org.junit.Test;
import org.openkinect.freenect.FrameMode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SerializationTest {

    private KinectFrame mTestSubject;

    @Before
    public void setUp(){
        mTestSubject = new KinectFrame(true, buildFrameMode(), buildByteBuffer(), buildTimestamp());

    }

    @Test
    public void testIntToByteList(){
        int testInt = -1353;
        List<Byte> serialized = Serialization.intToByteList(testInt);
        int result = Serialization.byteListToInt(serialized);
        assertEquals(testInt, result);
    }


    @Test
    public void testShortToByteList(){
        short testShort = -1028;
        List<Byte> serialized = Serialization.shortToByteList(testShort);
        int result = Serialization.byteListToShort(serialized);
        assertEquals(testShort, result);
    }

    @Test
    public void testBooleanToByteList(){
        boolean testBoolean = false;
        List<Byte> serialized = Serialization.booleanToByteList(testBoolean);
        boolean result = Serialization.byteListToBoolean(serialized);
        assertEquals(testBoolean, result);
    }

    @Test
    public void testFrameModeToByteList() throws IOException {
        FrameMode testFrameMode = buildFrameMode();
        List<Byte> serialized = Serialization.frameModeToByteList(testFrameMode);
        FrameMode result = Serialization.byteListToFrameMode(serialized);
        assertEquals(testFrameMode, result);
    }

    @Test
    public void testByteBufferToByteList() {
        ByteBuffer testBuffer = buildByteBuffer();
        List<Byte> serialized = Serialization.byteBufferToByteList(testBuffer);
        ByteBuffer result = Serialization.byteListToByteBuffer(serialized);
        assertEquals(testBuffer, result);
    }

    @Test
    public void testKinectFrameToByteList(){
        List<Byte> serialized = Serialization.kinectFrameToByteList(mTestSubject);
        KinectFrame result = Serialization.byteListToKinectFrame(serialized);
        assertEquals(mTestSubject, result);
    }

    private int buildTimestamp() {
        return -1;
    }

    private ByteBuffer buildByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.put((byte) 0);
        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.put((byte) 3);
        buffer.put((byte) 4);
        return buffer;
    }

    private FrameMode buildFrameMode() {
        FrameMode frameMode = new FrameMode();
        frameMode.reserved = 1;
        frameMode.resolution = 2;
        frameMode.format = 3;
        frameMode.bytes = 4;
        frameMode.width = 5;
        frameMode.height = 6;
        frameMode.dataBitsPerPixel = 7;
        frameMode.paddingBitsPerPixel = 8;
        frameMode.framerate = 9;
        frameMode.valid = 10;
        return frameMode;
    }
}
