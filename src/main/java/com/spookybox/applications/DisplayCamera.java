package com.spookybox.applications;

import com.spookybox.camera.CameraSnapShot;
import com.spookybox.graphics.ByteBufferToImage;
import com.spookybox.graphics.DisplayCanvas;
import com.spookybox.camera.KinectFrame;
import com.spookybox.server.ServerMain;
import com.spookybox.util.SelectiveReceiver;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class DisplayCamera extends DefaultInstance {
    private DisplayCanvas mRgbCanvas;
    private ServerMain mServer;
    private DisplayCanvas mDepthCanvas;

    private SelectiveReceiver<CameraSnapShot> getRgbFramesReceiver(){
        return new SelectiveReceiver<>(
                snapShot -> {
                    KinectFrame kinectFrame = snapShot.mRgbFrames.get(0);
                    KinectFrame kinectFrame1 = snapShot.mRgbFrames.get(1);
                    BufferedImage image = ByteBufferToImage.convertRgbToImage(kinectFrame, kinectFrame1);
                    mRgbCanvas.setImage(image);
                    mRgbCanvas.repaint();
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
                    mDepthCanvas.setImage(image);
                    mDepthCanvas.repaint();
                },
                snapShot -> snapShot.mDepthFrames.size() > 1
        );
    }

    @Override
    public void run() {
        DisplayCanvas[] canvases = DisplayCanvas.initWindow();
        mRgbCanvas = canvases[0];
        mDepthCanvas = canvases[1];
        mCameraManager.registerSnapshotReceiver(getDepthFramesReceiver());
        mCameraManager.registerSnapshotReceiver(getRgbFramesReceiver());
        mServer = new ServerMain(mCameraManager);
        mCameraManager.startCapture();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
