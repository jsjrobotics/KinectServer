package com.spookybox.applications;


import com.spookybox.camera.CameraManager;
import com.spookybox.camera.CameraSnapShot;
import com.spookybox.camera.KinectFrame;
import com.spookybox.camera.Serialization;
import com.spookybox.util.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

public class RecordFramesApplication extends DefaultInstance{
    private static final long TWENTY_SECONDS = 20000;
    private static final String OUT_FILE = "kinect_run.out";
    protected final CameraManager mCameraManager;
    private final FileOutputStream mFileOutputStream;
    private LinkedBlockingQueue<CameraSnapShot> snapShots = new LinkedBlockingQueue<>();
    private Thread mSavingThread;
    private boolean savedSnapshot = false;

    public RecordFramesApplication() {
        super();
        mCameraManager = new CameraManager(mKinect);
        try {
            mFileOutputStream = new FileOutputStream(OUT_FILE);
            System.out.println("Writing output to ->" + OUT_FILE);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            throw new IllegalAccessError("Couldn't open file output stream: "+e);
        }
    }

    @Override
    public void run() {
        mSavingThread = buildPersistanceThread();
        mSavingThread.start();
        mCameraManager.startCapture((snapshot) -> {
            try {
                snapShots.put(snapshot);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.err.println("Failed to add received snapshot: "+e);
            }
        });
        while(!savedSnapshot){
            Utils.sleep(1000);
        }
        stop();
        readSavedInput();
    }

    private void stop() {
        mCameraManager.stop();
        Utils.joinThread(Optional.of(mSavingThread));
        try {
            mFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error closing streams: "+e);
        }
    }

    private Thread buildPersistanceThread() {
        return new Thread(() -> {
            while(!savedSnapshot) {
                try {
                    CameraSnapShot snapShot = snapShots.take();
                    if(snapShot.mDepthFrames.size() > 0 && snapShot.mRgbFrames.size() > 0){
                        saveSnapshot(snapShot);
                        savedSnapshot = true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveSnapshot(CameraSnapShot snapShot) {
        try {
            List<Byte> serialized = Serialization.cameraSnapShotToByteList(snapShot);
            byte[] bytes = Utils.toByteArray(serialized);
            mFileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOError(new Throwable("Failed to save snapshot: "+e));
        }
    }

    @Override
    public void haltCameraAndTilt() {
        mCameraManager.stop();
    }

    private void readSavedInput(){
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(OUT_FILE);
            System.out.println("Reading input from ->" + OUT_FILE);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to create input streams");
            return;
        }
        List<Byte> read = new ArrayList<>();
        try {
            int nextByte = -1;
            while((nextByte = fileInputStream.read()) != -1){
                read.add((byte) nextByte);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        CameraSnapShot snapShot = Serialization.byteListToCameraSnapShot(read);

    }
}
