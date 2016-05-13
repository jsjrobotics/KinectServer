package com.spookybox.applications;

import com.spookybox.graphics.ByteBufferToImage;
import com.spookybox.graphics.DisplayCanvas;
import com.spookybox.camera.KinectFrame;
import com.spookybox.server.ServerMain;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class DisplayCamera extends DefaultInstance {
    private DisplayCanvas mCanvas;
    private ServerMain mServer;

    @Override
    public void run() {
        mCanvas = DisplayCanvas.initWindow();
        mCameraManager.registerSnapshotReceiver(
                snapShot -> {
                    KinectFrame kinectFrame = snapShot.mRgbFrames.get(0);
                    KinectFrame kinectFrame1 = snapShot.mRgbFrames.get(1);
                    BufferedImage image = ByteBufferToImage.convertToImage2(kinectFrame, kinectFrame1);
                    mCanvas.setImage(image);
                    mCanvas.repaint();
                },
                snapShot -> snapShot.mRgbFrames.size() > 1
        );
        mServer = new ServerMain(mCameraManager);
        mCameraManager.startCapture();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
