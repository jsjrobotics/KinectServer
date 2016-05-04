package com.spookybox.applications;


import com.spookybox.camera.CameraManager;
import com.spookybox.camera.CameraSnapShot;
import com.spookybox.camera.KinectFrame;
import com.spookybox.util.Utils;

import java.io.*;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

public class RecordFramesApplication extends DefaultInstance{
    private static final long TWENTY_SECONDS = 20000;
    private static final String OUT_FILE = "kinect_run.out";
    protected final CameraManager mCameraManager;
    private final FileOutputStream mFileOutputStream;
    private final ObjectOutputStream mObjectOutputStream;
    private LinkedBlockingQueue<CameraSnapShot> snapShots = new LinkedBlockingQueue<>();
    private Thread mSavingThread;
    private boolean savedSnapshot = false;

    public RecordFramesApplication() {
        super();
        mCameraManager = new CameraManager(mKinect);
        try {
            mFileOutputStream = new FileOutputStream(OUT_FILE);
            System.out.println("Writing output to ->" + OUT_FILE);
            mObjectOutputStream = new ObjectOutputStream(mFileOutputStream);
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
            mObjectOutputStream.close();
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
                    if(snapShot.mDepthFrames.size() > 0){
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
            mObjectOutputStream.writeObject(snapShot.mDepthFrames.get(0));
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
        ObjectInputStream objectInputStream;
        try {
            fileInputStream = new FileInputStream(OUT_FILE);
            System.out.println("Reading input from ->" + OUT_FILE);
            objectInputStream = new ObjectInputStream(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to create input streams");
            return;
        }
        int objectsRead = 0;
        while(!readSnapshot(objectInputStream).equals(Optional.empty())){
            objectsRead += 1;
        }
        System.out.println("Read "+objectsRead+" objects");
    }

    private Optional<KinectFrame> readSnapshot(ObjectInputStream in){
        try {
            return Optional.of( (KinectFrame) in.readObject() );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
