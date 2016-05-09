package com.spookybox.camera;

import org.openkinect.freenect.FrameMode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Serialization {

    public static final int INT_BYTE_LENGTH = 4;
    public static final int SHORT_BYTE_LENGTH = 2;
    public static final int BOOLEAN_BYTE_LENGTH = 1;

    public static List<Byte> shortToByteList(short value) {
        byte[] array = ByteBuffer.allocate(2).putShort(value).array();
        ArrayList<Byte> result = new ArrayList<>(2);
        for(byte b : array){
            result.add(b);
        }
        return result;
    }

    public static short byteListToShort(List<Byte> in) {
        if(in.size() != SHORT_BYTE_LENGTH){
            throw new IllegalArgumentException("in must have length "+ SHORT_BYTE_LENGTH);
        }
        byte[] array = new byte[in.size()];
        for(int index = 0; index < in.size(); index++){
            array[index] = in.get(index);
        }
        return ByteBuffer.wrap(array).getShort() ;
    }

    public static List<Byte> intToByteList(int value) {
        byte[] array = ByteBuffer.allocate(INT_BYTE_LENGTH).putInt(value).array();
        ArrayList<Byte> result = new ArrayList<>(INT_BYTE_LENGTH);
        for(byte b : array){
            result.add(b);
        }
        return result;
    }

    public static int byteListToInt(List<Byte> in) {
        if(in.size() != INT_BYTE_LENGTH){
            throw new IllegalArgumentException("in must have length "+INT_BYTE_LENGTH);
        }
        byte[] array = new byte[in.size()];
        for(int index = 0; index < in.size(); index++){
            array[index] = in.get(index);
        }
        return ByteBuffer.wrap(array).getInt() ;
    }

    public static List<Byte> booleanToByteList(boolean value) {
        byte byteValue = 0;
        if(value){
            byteValue = 1;
        }
        return Arrays.asList(byteValue);
    }

    public static boolean byteListToBoolean(List<Byte> in){
        if(in.size() != BOOLEAN_BYTE_LENGTH){
            throw new IllegalArgumentException("in must have length "+ BOOLEAN_BYTE_LENGTH);
        }
        if(in.get(0) == (byte) 0){
            return false;
        }
        return true;
    }

    public static List<Byte> frameModeToByteList(FrameMode mode) {
        List<Byte> reserved = intToByteList(mode.reserved);
        List<Byte> resolution = intToByteList(mode.resolution);
        List<Byte> format = intToByteList(mode.format);
        List<Byte> bytes = intToByteList(mode.bytes);
        List<Byte> width = shortToByteList(mode.width);
        List<Byte> height = shortToByteList(mode.height);



        ArrayList<Byte> resultList = new ArrayList<>();
        resultList.addAll(reserved);
        resultList.addAll(resolution);
        resultList.addAll(format);
        resultList.addAll(bytes);
        resultList.addAll(width);
        resultList.addAll(height);
        resultList.add(mode.dataBitsPerPixel);
        resultList.add(mode.paddingBitsPerPixel);
        resultList.add(mode.framerate);
        resultList.add(mode.valid);
        return resultList;
    }

    public static FrameMode byteListToFrameMode(List<Byte> in){
        FrameMode frameMode = new FrameMode();
        int index = 0;
        List<Byte> reservedList = in.subList(index, index + INT_BYTE_LENGTH);
        index += INT_BYTE_LENGTH;
        List<Byte> resolutionList = in.subList(index, index + INT_BYTE_LENGTH);
        index += INT_BYTE_LENGTH;
        List<Byte> formatList = in.subList(index, index + INT_BYTE_LENGTH);
        index += INT_BYTE_LENGTH;
        List<Byte> bytesList = in.subList(index, index + INT_BYTE_LENGTH);
        index += INT_BYTE_LENGTH;
        List<Byte> widthList = in.subList(index, index + SHORT_BYTE_LENGTH);
        index += SHORT_BYTE_LENGTH;
        List<Byte> heightList = in.subList(index, index + SHORT_BYTE_LENGTH);
        index += SHORT_BYTE_LENGTH;

        frameMode.dataBitsPerPixel = in.get(index);
        index += 1;
        frameMode.paddingBitsPerPixel = in.get(index);
        index += 1;
        frameMode.framerate = in.get(index);
        index += 1;
        frameMode.valid = in.get(index);
        index += 1;

        frameMode.reserved = byteListToInt(reservedList);
        frameMode.resolution = byteListToInt(resolutionList);
        frameMode.format = byteListToInt(formatList);
        frameMode.bytes = byteListToInt(bytesList);
        frameMode.width = byteListToShort(widthList);
        frameMode.height = byteListToShort(heightList);

        return frameMode;
    }



    public static List<Byte> byteBufferToByteList(ByteBuffer buffer) {
        byte[] array = buffer.array();
        int size = array.length;
        List<Byte> sizeList = intToByteList(array.length);

        ArrayList<Byte> resultList = new ArrayList<>();
        resultList.addAll(sizeList);
        for(int index =0; index < size; index++){
            resultList.add(array[index]);
        }
        return resultList;
    }

    public static ByteBuffer byteListToByteBuffer(List<Byte> in){
        ByteBuffer result = ByteBuffer.allocate(in.size());
        for(byte b : in){
            result.put(b);
        }
        return result;
    }

    public static int extractBufferSize(final List<Byte> in, final int start){
        List<Byte> sizeList = in.subList(start, start + INT_BYTE_LENGTH);
        return byteListToInt(sizeList);
    }

    public static List<Byte> kinectFrameToByteList(KinectFrame f) {
        List<Byte> isDepthFrame = booleanToByteList(f.isDepthFrame());
        List<Byte> mode = frameModeToByteList(f.getMode());
        List<Byte> timeStamp = intToByteList(f.getTimestamp());
        List<Byte> buffer = byteBufferToByteList(f.getBuffer());

        ArrayList<Byte> resultList = new ArrayList<>();
        resultList.addAll(isDepthFrame);
        resultList.addAll(mode);
        resultList.addAll(timeStamp);
        resultList.addAll(buffer);

        return resultList;
    }

    public static KinectFrame byteListToKinectFrame(List<Byte> in){
        int index = 0;

        List<Byte> isDepthFrameList = in.subList(index, index + BOOLEAN_BYTE_LENGTH);
        index += BOOLEAN_BYTE_LENGTH;

        List<Byte> modeList = in.subList(index, index + FrameMode.FRAME_MODE_BYTE_LENGTH);
        index += FrameMode.FRAME_MODE_BYTE_LENGTH;

        List<Byte> timeStampList = in.subList(index, index + INT_BYTE_LENGTH);
        index += INT_BYTE_LENGTH;

        List<Byte> bufferList = extractBufferList(in, index);
        boolean isDepthFrame = byteListToBoolean(isDepthFrameList);
        FrameMode frameMode = byteListToFrameMode(modeList);
        int timeStamp = byteListToInt(timeStampList);
        ByteBuffer byteBuffer = byteListToByteBuffer(bufferList);
        return new KinectFrame(isDepthFrame, frameMode, byteBuffer, timeStamp);
    }


    public static List<Byte> extractBufferList(final List<Byte> in, final int start) {
        int index = start;
        int size = extractBufferSize(in, start);
        if(size < 0){
            throw new IllegalArgumentException("size < 0");
        }
        index += INT_BYTE_LENGTH;

        List<Byte> bufferData = in.subList(index, index + size);
        if(size != bufferData.size()){
            throw new IllegalArgumentException("size not expected value: Expected "+size+" received "+bufferData.size());
        }
        return bufferData;
    }

    public static List<Byte> cameraSnapShotToByteList(CameraSnapShot snapShot) {
        int depthFrames = snapShot.mDepthFrames.size();
        int rgbFrames = snapShot.mRgbFrames.size();
        List<Byte> numDepthFrames = Serialization.intToByteList(depthFrames);
        List<Byte> numRgbFrames = Serialization.intToByteList(rgbFrames);
        List<Byte> kinectFrames = new ArrayList<>();
        for(KinectFrame f : snapShot.mDepthFrames){
            kinectFrames.addAll(Serialization.kinectFrameToByteList(f));
        }

        for(KinectFrame f : snapShot.mRgbFrames){
            kinectFrames.addAll(Serialization.kinectFrameToByteList(f));
        }
        ArrayList<Byte> resultList = new ArrayList<>();
        resultList.addAll(numDepthFrames);
        resultList.addAll(numRgbFrames);
        resultList.addAll(kinectFrames);
        return resultList;
    }

    public static CameraSnapShot byteListToCameraSnapShot(List<Byte> bytes) {
        int index = 0;
        List<Byte> numDepthFramesList = bytes.subList(0, index+INT_BYTE_LENGTH);
        index += INT_BYTE_LENGTH;
        List<Byte> numRgbFramesList = bytes.subList(index, index+INT_BYTE_LENGTH);
        index += INT_BYTE_LENGTH;

        int numDepthFrames = byteListToInt(numDepthFramesList);
        int numRgbFrames = byteListToInt(numRgbFramesList);
        List<KinectFrame> depthFrames = new ArrayList<>();
        List<KinectFrame> rgbFrames = new ArrayList<>();
        extractKinectFrames(bytes, index, numDepthFrames, numRgbFrames, depthFrames, rgbFrames);
        return new CameraSnapShot(rgbFrames, depthFrames);
    }

    public static void extractKinectFrames(List<Byte> bytes,
                                            int depthFramesStart,
                                            int numDepthFrames,
                                            int numRgbFrames,
                                            List<KinectFrame> depthFramesResult,
                                            List<KinectFrame> rgbFramesResult) {
        if(depthFramesResult == null || rgbFramesResult == null){
            throw new IllegalArgumentException("Result lists must not be null");
        }
        depthFramesResult = new ArrayList<>();
        rgbFramesResult = new ArrayList<>();
        int read = extractKinectFrame(bytes, depthFramesStart, depthFramesResult, numDepthFrames);
        int rgbFramesStart = depthFramesStart + read;
        extractKinectFrame(bytes, rgbFramesStart, rgbFramesResult, numRgbFrames);
    }

    public static int extractKinectFrame(List<Byte> bytes,
                                          int framesStart,
                                          final List<KinectFrame> result,
                                          int framesToRead) {
        if(result == null || !result.isEmpty()){
            throw new IllegalArgumentException("Result must not be null");
        }
        int framesRead = 0;
        int bytesRead = 0;
        int index = framesStart;
        while(index < bytes.size() && framesRead != framesToRead){
            int startIndex = index;
            List<Byte> isDepthFrameList = bytes.subList(index, index + BOOLEAN_BYTE_LENGTH);
            boolean isDepthFrame = byteListToBoolean(isDepthFrameList);
            index += BOOLEAN_BYTE_LENGTH;

            List<Byte> modeList = bytes.subList(index, index + FrameMode.FRAME_MODE_BYTE_LENGTH);
            FrameMode mode = byteListToFrameMode(modeList);
            index += FrameMode.FRAME_MODE_BYTE_LENGTH;

            List<Byte> timeStampList = bytes.subList(index, index + INT_BYTE_LENGTH);
            int timeStamp = byteListToInt(timeStampList);
            index += INT_BYTE_LENGTH;

            List<Byte> bufferList = extractBufferList(bytes, index);
            index += INT_BYTE_LENGTH;
            ByteBuffer frame = byteListToByteBuffer(bufferList);
            index += bufferList.size();

            KinectFrame kinectFrame = new KinectFrame(isDepthFrame, mode, frame, timeStamp);
            framesRead += 1;
            result.add(kinectFrame);
            bytesRead += index - startIndex;

        }
        return bytesRead;
    }
}
