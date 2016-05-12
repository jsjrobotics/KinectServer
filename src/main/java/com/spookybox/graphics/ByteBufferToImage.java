package com.spookybox.graphics;

import com.spookybox.camera.KinectFrame;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/*
    https://openkinect.org/wiki/Protocol_Documentation#RGB_Camera
    for (int p = 0, bit_offset = 0; p < 640*480; p++, bit_offset += 11) {
       uint32_t pixel = 0; // value of pixel

       pixel   = *((uint32_t *)(data+(p*11/8)));
       pixel >>= (p*11 % 8);
       pixel  &= 0x7ff;

       uint8_t pix_low  = (pixel & 0x00ff) >> 0;
       uint8_t pix_high = (pixel & 0xff00) >> 8;

       pix_low  = reverse[pix_low];
       pix_high = reverse[pix_high];

       pixel = (pix_low << 8) | (pix_high);
       pixel >>= 5;

       // Image drops the 3 MSbs
       rgb[3*p+0] = 255-pixel;
       rgb[3*p+1] = 255-pixel;
       rgb[3*p+2] = 255-pixel;
     }
     */
public class ByteBufferToImage {
    public static BufferedImage convertToImage(KinectFrame kinectFrame, KinectFrame kinectFrame1) {
        int width = kinectFrame.getMode().getWidth();
        int height = kinectFrame.getMode().getHeight();
        int[] array1 = convertToIntArray(kinectFrame.getBuffer());
        int[] array2 = convertToIntArray(kinectFrame1.getBuffer());
        int[] array = new int[array1.length + array2.length];
        for(int index = 0; index < array1.length; index++){
            array[index] = array1[index];
        }
        for(int index = 0; index < array2.length; index++){
            array[array1.length + index] = array2[index];
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0,0,width,height,array,0,width);
        return image;
    }

    public static BufferedImage convertToImage2(KinectFrame kinectFrame, KinectFrame kinectFrame1) {
        int width = kinectFrame.getMode().getWidth();
        int height = kinectFrame.getMode().getHeight();
        int[] array1 = convertToIntArray(kinectFrame.getBuffer());
        int[] array2 = convertToIntArray(kinectFrame1.getBuffer());
        int[] array = new int[array1.length + array2.length];

        int array1Index = 0;
        int array2Index = 0;
        for(int index = 0; index < array.length; index++){
            if(index % 2 == 0){
                array[index] = array1[array1Index];
                array1Index += 1;
            }
            else {
                array[index] = array2[array2Index];
                array2Index += 1;
            }
        }

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
