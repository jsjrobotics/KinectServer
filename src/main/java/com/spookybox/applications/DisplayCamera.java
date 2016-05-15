package com.spookybox.applications;

import com.spookybox.camera.CameraSnapShot;
import com.spookybox.graphics.ByteBufferToImage;
import com.spookybox.graphics.DisplayCanvas;
import com.spookybox.camera.KinectFrame;
import com.spookybox.server.ServerMain;
import com.spookybox.util.SelectiveReceiver;
import javafx.scene.Camera;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class DisplayCamera extends DefaultInstance {
    private DisplayCanvas mCanvas;
    private ServerMain mServer;

    private SelectiveReceiver<CameraSnapShot> getRgbFramesReceiver(){
        return new SelectiveReceiver<>(
                snapShot -> {
                    KinectFrame kinectFrame = snapShot.mRgbFrames.get(0);
                    KinectFrame kinectFrame1 = snapShot.mRgbFrames.get(1);
                    BufferedImage image = ByteBufferToImage.convertToImage3(kinectFrame, kinectFrame1);
                    mCanvas.setImage(image);
                    mCanvas.repaint();
                },
                snapShot -> snapShot.mRgbFrames.size() > 1
        );
    }

    private SelectiveReceiver<CameraSnapShot> getDepthFramesReceiver(){
        return new SelectiveReceiver<>(
                snapShot -> {
                    KinectFrame kinectFrame = snapShot.mDepthFrames.get(0);
                    KinectFrame kinectFrame1 = snapShot.mDepthFrames.get(1);
                    BufferedImage image = ByteBufferToImage.convertToImage3(kinectFrame, kinectFrame1);
                    mCanvas.setImage(image);
                    mCanvas.repaint();
                },
                snapShot -> snapShot.mDepthFrames.size() > 1
        );
    }

    @Override
    public void run() {
        mCanvas = DisplayCanvas.initWindow();
        mCameraManager.registerSnapshotReceiver(getDepthFramesReceiver());
        mServer = new ServerMain(mCameraManager);
        mCameraManager.startCapture();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
