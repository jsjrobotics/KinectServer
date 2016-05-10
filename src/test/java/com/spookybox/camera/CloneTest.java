package com.spookybox.camera;

import org.junit.Before;
import org.junit.Test;
import org.openkinect.freenect.FrameMode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CloneTest {
    private KinectFrame mKinectFrame1;
    private KinectFrame mKinectFrame2;

    @Before
    public void setUp(){
        mKinectFrame1 = new KinectFrame(true, buildFrameMode(), buildByteBuffer(), buildTimestamp());
        mKinectFrame2 = new KinectFrame(false, buildAlternateFrameMode(), buildAlternateByteBuffer(), buildTimestamp());
    }

    @Test
    public void testCloneByteBuffer(){
        ByteBuffer duplicate = Cloning.clone(buildByteBuffer());
        assertTrue(Cloning.isEqual(duplicate, buildByteBuffer()));
    }

    @Test
    public void testCloneMode(){
        KinectFrame duplicate = mKinectFrame2.clone();
        assertEquals(duplicate.getMode(), mKinectFrame2.getMode());
    }

    @Test
    public void testCloneFrame(){
        KinectFrame duplicate = mKinectFrame2.clone();
        assertEquals(duplicate, mKinectFrame2);

        duplicate = mKinectFrame1.clone();
        assertEquals(duplicate, mKinectFrame1);
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

    private FrameMode buildAlternateFrameMode() {
        FrameMode frameMode = new FrameMode();
        frameMode.reserved = 11;
        frameMode.resolution = 12;
        frameMode.format = 13;
        frameMode.bytes = 14;
        frameMode.width = 15;
        frameMode.height = 16;
        frameMode.dataBitsPerPixel = 17;
        frameMode.paddingBitsPerPixel = 18;
        frameMode.framerate = 19;
        frameMode.valid = 20;
        return frameMode;
    }
}
