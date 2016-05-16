package com.spookybox.graphics;

import com.spookybox.camera.KinectFrame;
import com.spookybox.camera.Serialization;
import com.spookybox.util.SerializationUtils;

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
        int[] array = new int[input.size()/3];
        for(int index = 0; index < array.length; index++){
            int base = index*3;
            array[index] = input.get(base) << 24| input.get(base+1) << 16 | input.get(base+2) << 8;
        }

        int width = kinectFrame.getMode().width;
        int height = kinectFrame.getMode().height;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0,0,width,height,array,0,width);
        return image;
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
            array[index] = input.get(base) << 8| input.get(base+1);
        }
        int width = kinectFrame.getMode().width;
        int height = kinectFrame.getMode().height;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0,0,width,height,array,0,width);
        return image;
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
}
