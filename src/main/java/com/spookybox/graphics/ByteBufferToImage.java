package com.spookybox.graphics;

import com.spookybox.camera.KinectFrame;
import com.spookybox.camera.Serialization;
import com.spookybox.util.SerializationUtils;
import org.openkinect.freenect.FrameMode;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ByteBufferToImage {

    public static BufferedImage convertRgbToImage(KinectFrame kinectFrame, KinectFrame kinectFrame1){
        List<Byte> kinectList1 = Serialization.byteBufferToByteList(kinectFrame.getBuffer());
        List<Byte> kinectList2 = Serialization.byteBufferToByteList(kinectFrame1.getBuffer());
        List<Byte> input = new ArrayList<>();
        for(Byte b : kinectList1){
            input.add(b);
        }
        for(Byte b : kinectList2){
            input.add(b);
        }

        int[] array = byteListToRgb(input);
        return rgbArrayToImage(kinectFrame.getMode(), array);
    }

    private static BufferedImage rgbArrayToImage(FrameMode mode, int[] rgbArray){
        int width = mode.width;
        int height = mode.height;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0,0,width,height,rgbArray,0,width);
        return image;
    }
    private static int[] byteListToRgb(List<Byte> input){
        int[] array = new int[input.size()/3];
        for(int index = 0; index < array.length; index++){
            int base = index*3;
            array[index] = input.get(base) << 16 | input.get(base+1) << 8 | input.get(base+2);
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
        int[] array = new int[422400];
        for(int index = 0; index < 422400; index+=2){
            int base = index*2;
            int value = input.get(base) << 8| input.get(base+1);
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
        List<Byte> kinectList1 = Serialization.byteBufferToByteList(kinectFrame.getBuffer());
        List<Byte> kinectList2 = Serialization.byteBufferToByteList(kinectFrame1.getBuffer());
        List<Byte> input = new ArrayList<>();
        for(Byte b : kinectList1){
            input.add(b);
        }
        for(Byte b : kinectList2){
            input.add(b);
        }
        List<Byte> rgb = depthToRgbStream(input);
        int[] array = byteListToRgb(rgb);
        return rgbArrayToImage(kinectFrame.getMode(), array);
    }

    public static int[] convertToIntArray(ByteBuffer buffer){
        ArrayList<Integer> result = new ArrayList<>();
        for(int index = 0; index < buffer.capacity(); index+= 4){
            int data = buffer.get(index) << 24 |
                    buffer.get(index+1) << 16 |
                    buffer.get(index+2) << 8 |
                    buffer.get(index+3);
            result.add(data);
        }
        int[] array = new int[result.size()];
        for(int index = 0; index < array.length; index++){
            array[index] = result.get(index);
        }
        return array;
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

    private static List<Byte> depthToRgbStream(List<Byte> input){
        int[] gammaMatrix = getGammaMatrix();
        List<Byte> rgbList = new ArrayList<>(640*480*3);
        for (int i=0; i<640*480; i++) {
            short value = (short) (input.get(i*2) | input.get(i*2+1));
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
