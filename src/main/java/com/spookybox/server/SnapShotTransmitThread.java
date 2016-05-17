package com.spookybox.server;

import com.spookybox.camera.CameraManager;
import com.spookybox.camera.CameraSnapShot;
import com.spookybox.camera.KinectFrame;
import com.spookybox.graphics.ByteBufferToImage;
import com.spookybox.util.Pair;
import com.spookybox.util.SelectiveReceiver;

import java.awt.image.BufferedImage;
import java.util.function.Function;

public class SnapShotTransmitThread extends Thread {
    private final TransmitController mController;
    private final boolean readyToStart;
    private final int mThreadNumber;
    private final TcpServer mRgbServer;
    private final CameraManager mCameraManager;
    private final TcpServer mDepthServer;
    private TransmitDataAsImageThread<Pair<KinectFrame, KinectFrame>> mRgbTransmitThread;
    private TransmitDataAsImageThread<Pair<KinectFrame, KinectFrame>> mDepthTransmitThread;
    private boolean mRgbTransitThreadStarted = false;
    private boolean mDepthTransitThreadStarted = false;

    public SnapShotTransmitThread(final int threadNumber,
                                  final TransmitController controller,
                                  final CameraManager cameraManager,
                                  final TcpServer rgbServer,
                                  final TcpServer depthServer){
        super("SnapShotTransmitThread:"+threadNumber);
        mThreadNumber = threadNumber;
        mCameraManager = cameraManager;
        mController = controller;
        mRgbServer = rgbServer;
        mDepthServer = depthServer;
        mRgbTransitThreadStarted = false;
        mDepthTransitThreadStarted = false;
        readyToStart = controller != null && cameraManager != null && rgbServer!= null;
    }

    private SelectiveReceiver<CameraSnapShot> getRgbReceiver(){
        return new SelectiveReceiver<>(
                snapShot -> {
                    startRgbTransmitThread();
                    mRgbTransmitThread.queueData(
                            new Pair<>(
                                    snapShot.mRgbFrames.get(0),
                                    snapShot.mRgbFrames.get(1)
                            ));
                },
                snapShot -> mRgbServer.isConnected() && snapShot.mRgbFrames.size() > 1
        );
    }

    private SelectiveReceiver<CameraSnapShot> getDepthReceiver(){
        return new SelectiveReceiver<>(
                snapShot -> {
                    startDepthTransmitThread();
                    mDepthTransmitThread.queueData(
                            new Pair<>(
                                    snapShot.mDepthFrames.get(0),
                                    snapShot.mDepthFrames.get(1)
                            ));
                },
                snapShot -> mRgbServer.isConnected() && snapShot.mDepthFrames.size() > 1
        );
    }

    @Override
    public void run(){
        System.out.println(getName() + " Started");
        mRgbTransmitThread = new TransmitDataAsImageThread(mRgbServer, mController, mThreadNumber * 100, getKinectFrameConverter());
        mDepthTransmitThread = new TransmitDataAsImageThread(mDepthServer, mController, mThreadNumber * 100, getKinectFrameConverter());

        mCameraManager.registerSnapshotReceiver(getRgbReceiver());

        mCameraManager.registerSnapshotReceiver(getDepthReceiver());
    }

    private void startRgbTransmitThread() {
        if(!mRgbTransitThreadStarted){
            mRgbTransmitThread.start();
            mRgbTransitThreadStarted = true;
        }
    }

    private void startDepthTransmitThread() {
        if(!mDepthTransitThreadStarted){
            mDepthTransmitThread.start();
            mDepthTransitThreadStarted = true;
        }
    }

    private Function<Pair<KinectFrame, KinectFrame>, BufferedImage> getKinectFrameConverter() {
        return dualKinectFrame -> ByteBufferToImage.convertRgbToImage(dualKinectFrame.mFirst, dualKinectFrame.mSecond);
    }

    public boolean readyToStart() {
        return readyToStart;
    }

    public TcpServer getServer(){
        return mRgbServer;
    }
}
