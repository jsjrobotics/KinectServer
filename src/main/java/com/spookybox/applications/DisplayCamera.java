package com.spookybox.applications;

import com.spookybox.graphics.ByteBufferToImage;
import com.spookybox.graphics.DisplayCanvas;
import com.spookybox.camera.KinectFrame;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class DisplayCamera extends DefaultInstance {
    private DisplayCanvas mCanvas;


    @Override
    public void run() {
        mCanvas = DisplayCanvas.initWindow();
        mCameraManager.startCapture((snapShot -> {
            KinectFrame kinectFrame = snapShot.mRgbFrames.get(0);
            KinectFrame kinectFrame1 = snapShot.mRgbFrames.get(1);
            BufferedImage image = ByteBufferToImage.convertToImage2(kinectFrame, kinectFrame1);
            mCanvas.setImage(image);
            mCanvas.repaint();
        }));
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
