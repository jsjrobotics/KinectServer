package com.spookybox.camera;

import com.spookybox.util.Utils;
import org.junit.Before;
import org.junit.Test;
import org.openkinect.freenect.FrameMode;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileWriteTest {
    private final String SAVE_FILE = "fileWriteTest.out";

    private KinectFrame mTestSubject;

    @Before
    public void setUp(){
        mTestSubject = new KinectFrame(true, buildFrameMode(), buildByteBuffer(), buildTimestamp());
    }

    @Test
    public void testWriteToFile() throws IOException, ClassNotFoundException {
        FileOutputStream outputStream = new FileOutputStream(SAVE_FILE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        byte[] buffer = Utils.toByteArray(Serialization.kinectFrameToByteList(mTestSubject));
        objectOutputStream.write(buffer);
        objectOutputStream.close();
        outputStream.close();

        FileInputStream inputStream = new FileInputStream(SAVE_FILE);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        byte[] readBuffer = new byte[Serialization.KINECT_FRAME_BYTE_LENGTH];
        objectInputStream.read(readBuffer);
        objectInputStream.close();
        inputStream.close();

        List<Byte> byteList = Utils.toByteList(readBuffer);
        KinectFrame returnedValue = Serialization.byteListToKinectFrame(byteList);
        assertEquals(mTestSubject, returnedValue);
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
