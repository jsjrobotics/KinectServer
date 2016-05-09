package com.spookybox.camera;

import org.junit.Before;
import org.junit.Test;
import org.openkinect.freenect.FrameMode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testCameraSnapShotToByteList(){
        List<KinectFrame> rgbFrames = new ArrayList<>();
        List<KinectFrame> depthFrames = new ArrayList<>();
        rgbFrames.add(mTestSubject);
        depthFrames.add(getAlternateKinectFrame());

        CameraSnapShot snapShot = new CameraSnapShot(rgbFrames, depthFrames);
        List<Byte> serialized = Serialization.cameraSnapShotToByteList(snapShot);
        CameraSnapShot result = Serialization.byteListToCameraSnapShot(serialized);
        assertEquals(snapShot, result);

    }

    @Test
    public void testExtractKinectFrame(){
        List<Byte> serialized = Serialization.kinectFrameToByteList(mTestSubject);
        List<KinectFrame> result = new ArrayList<>();
        int bytesRead = Serialization.extractKinectFrame(serialized, 0, result, 1);
        assertTrue("sizes differ", 1 == result.size());
        assertTrue("Did not read all expected bytes" , serialized.size() == bytesRead);
        assertTrue("Frame not found", result.contains(mTestSubject));

        KinectFrame secondTestSubject = getAlternateKinectFrame();
        serialized.addAll(Serialization.kinectFrameToByteList(secondTestSubject));
        result.clear();
        bytesRead = Serialization.extractKinectFrame(serialized, 0, result, 2);
        assertTrue("sizes differ", 2 == result.size());
        assertTrue("Did not read all expected bytes" , serialized.size() == bytesRead);
        assertTrue("First Frame not found", result.contains(mTestSubject));
        assertTrue("Second Frame not found", result.contains(secondTestSubject));

    }

    private KinectFrame getAlternateKinectFrame() {
        return new KinectFrame(true, buildFrameMode(), buildAlternateByteBuffer(), buildTimestamp());
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

    private ByteBuffer buildAlternateByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(6);
        buffer.put((byte) 5);
        buffer.put((byte) 6);
        buffer.put((byte) 7);
        buffer.put((byte) 8);
        buffer.put((byte) 9);
        buffer.put((byte) 10);
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
