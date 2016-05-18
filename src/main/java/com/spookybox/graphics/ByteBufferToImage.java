package com.spookybox.graphics;

import com.spookybox.camera.KinectFrame;
import com.spookybox.camera.Serialization;
import org.openkinect.freenect.FrameMode;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ByteBufferToImage {

    private static final int RGB_FRAME_SIZE =  307200;
    private static final int DEPTH_FRAME_SIZE = 422400;

    private static BufferedImage rgbArrayToImage(FrameMode mode, int[] rgbArray){
        int width = mode.width;
        int height = mode.height;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0,0,width,height,rgbArray,0,width);
        return image;
    }

    public static BufferedImage rgbFramesToImage(KinectFrame kinectFrame, KinectFrame kinectFrame1){
        ByteBuffer buffer1 = kinectFrame.clone().getBuffer();
        ByteBuffer buffer2 = kinectFrame1.clone().getBuffer();
        byte[] input = new byte[buffer1.capacity() + buffer2.capacity()];
        buffer1.get(input, 0, buffer1.capacity());
        buffer2.get(input, buffer1.capacity(), buffer2.capacity());
        int length = kinectFrame.getMode().height * kinectFrame.getMode().width;
        int[] rgbArray = new int[length];
        for(int index = 0; index < rgbArray.length; index++){
            int base = index*3;
            rgbArray[index] = input[base] <<  16 | input[base+1] << 8 | input[base+2];
        }
        return rgbArrayToImage(kinectFrame.getMode(), rgbArray);
    }

    private static int[] byteListToRgb(List<Byte> input){
        int[] array = new int[RGB_FRAME_SIZE];
        for(int index = 0; index < array.length; index++){
            int base = index*3;
            int red = input.get(base) << 16 & 0xFF0000;
            int green = input.get(base+1) << 8 & 0xFF00;
            int blue = input.get(base+2) & 0xFF;
            array[index] = red | green | blue;
        }
        return array;
    }

    public static BufferedImage convertDepthToImage(KinectFrame kinectFrame, KinectFrame kinectFrame1){
        List<Byte> kinectList1 = Serialization.byteBufferToByteList(kinectFrame.getBuffer());
        List<Byte> kinectList2 = Serialization.byteBufferToByteList(kinectFrame1.getBuffer());
        List<Byte> input = new ArrayList<>();
        for(Byte b : kinectList1){
            input.add(b);
        }
        for(Byte b : kinectList2){
            input.add(b);
        }
        int[] array = new int[DEPTH_FRAME_SIZE];
        for(int index = 0; index < array.length; index+=2){
            int base = index*2;
            int value = (input.get(base) << 8) | input.get(base+1);
            int divider = 21845;
            if(value < Short.MIN_VALUE +divider){
                array[index] = value << 16;
            } else if(value < Short.MIN_VALUE + divider*2){
                array[index] = value << 8;
            } else {
                array[index] = 0x88 + value;
            }
        }
        int width = kinectFrame.getMode().width;
        int height = kinectFrame.getMode().height;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0,0,width,height,array,0,width);
        return image;
    }

    public static BufferedImage convertDepthToImage2(KinectFrame kinectFrame, KinectFrame kinectFrame1) {
        List<Short> depthStream =  new ArrayList<>();
        ShortBuffer buffer = kinectFrame.getBuffer().asShortBuffer();
        for(int index = 0; index < 640*480; index++){
            depthStream.add(buffer.get(index));
        }
        List<Byte> rgbStream = depthToRgbStream(depthStream);
        int[] array = byteListToRgb(rgbStream);
        return rgbArrayToImage(kinectFrame.getMode(), array);
    }

    private static void debugFrame(KinectFrame kinectFrame){
        ByteBuffer buffer = kinectFrame.getBuffer();
        ByteBuffer clone = kinectFrame.clone().getBuffer();
        byte[] debugByteArray = new byte[buffer.capacity()];
        buffer.get(debugByteArray);

        ShortBuffer shortBuffer = clone.asShortBuffer();
        short[] debugShortArray = new short[shortBuffer.capacity()];
        shortBuffer.get(debugShortArray);


    }

    public static BufferedImage convertDepthToImage3(KinectFrame kinectFrame){
        debugFrame(kinectFrame);
        BitSet bits = BitSet.valueOf(kinectFrame.getBuffer());
        List<Short> values = new ArrayList<>();
        for(int index = 0; index < 640*480*11; index += 11){
            boolean bit1 = bits.get(index);
            boolean bit2 = bits.get(index + 1);
            boolean bit3 = bits.get(index + 2);
            boolean bit4 = bits.get(index + 3);
            boolean bit5 = bits.get(index + 4);
            boolean bit6 = bits.get(index + 5);
            boolean bit7 = bits.get(index + 6);
            boolean bit8 = bits.get(index + 7);
            boolean bit9 = bits.get(index + 8);
            boolean bit10 = bits.get(index + 9);
            boolean bit11= bits.get(index + 10);

            short value = leftShiftBit(bit1, 10);
            value |= leftShiftBit(bit2, 9);
            value |= leftShiftBit(bit3, 8);
            value |= leftShiftBit(bit4, 7);
            value |= leftShiftBit(bit5, 6);
            value |= leftShiftBit(bit6, 5);
            value |= leftShiftBit(bit7, 4);
            value |= leftShiftBit(bit8, 3);
            value |= leftShiftBit(bit9, 2);
            value |= leftShiftBit(bit10, 1);
            value |= leftShiftBit(bit11, 0);
            if(value < 0 || value >= 2048){
                System.out.println("Invalid value read");
                values.add((short) 0);
            }
            else {
                values.add(value);
            }
        }
        List<Byte> rgbStream = depthToRgbStream(values);
        int[] array = byteListToRgb(rgbStream);
        return rgbArrayToImage(kinectFrame.getMode(), array);

    }

    public static short leftShiftBit(boolean isSet, int leftShift) {
        if(!isSet){
            return 0;
        }
        return (short) (1 << leftShift);
    }

    /*  Following Functions From glview.c */
    private static int[] getGammaMatrix(){
        int[] matrix = new int[2048];
        for (int i=0; i<2048; i++) {
            double v = i/2048.0;
            v = Math.pow(v,3.0) * 6;
            matrix[i] = (byte) (int) (v*6*256);
        }
        return matrix;
    }

    private static List<Byte> depthToRgbStream(List<Short> input){
        int[] gammaMatrix = getGammaMatrix();
        List<Byte> rgbList = new ArrayList<>(640*480*3);
        for (int i=0; i<640*480; i++) {
            int value = input.get(i);
            int pval = gammaMatrix[value];
            int lb = pval & 0xff;
            switch (pval>>8) {
                case 0:
                    rgbList.add((byte) 255);
                    rgbList.add((byte) (255-lb));
                    rgbList.add((byte) (255-lb));
                    break;
                case 1:
                    rgbList.add((byte) 255);
                    rgbList.add((byte) lb);
                    rgbList.add((byte) 0);
                    break;
                case 2:
                    rgbList.add((byte) (255-lb));
                    rgbList.add((byte) 255);
                    rgbList.add((byte) 0);
                    break;
                case 3:
                    rgbList.add((byte) 0);
                    rgbList.add((byte) 255);
                    rgbList.add((byte) lb);
                    break;
                case 4:
                    rgbList.add((byte) 0);
                    rgbList.add((byte) (255-lb));
                    rgbList.add((byte) 255);
                    break;
                case 5:
                    rgbList.add((byte) 0);
                    rgbList.add((byte) 0);
                    rgbList.add((byte) (255-lb));
                    break;
                default:
                    rgbList.add((byte) 0);
                    rgbList.add((byte) 0);
                    rgbList.add((byte) 0);
                    break;
            }
        }
        return rgbList;
    }
    /* End Functions from glview.c  */
}
