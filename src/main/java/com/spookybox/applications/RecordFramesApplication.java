package com.spookybox.applications;


import com.spookybox.camera.CameraSnapShot;
import com.spookybox.util.FileUtils;
import com.spookybox.util.SelectiveReceiver;
import com.spookybox.util.ThreadUtils;
import com.spookybox.util.SerializationUtils;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

public class RecordFramesApplication extends DefaultInstance{
    private static final String OUT_FILE = "kinect_run.out";
    private final FileOutputStream mFileOutputStream;
    private LinkedBlockingQueue<CameraSnapShot> snapShots = new LinkedBlockingQueue<>();
    private Thread mSavingThread;
    private boolean savedSnapshot = false;

    public RecordFramesApplication() {
        super();
        try {
            mFileOutputStream = new FileOutputStream(OUT_FILE);
            System.out.println("Writing output to ->" + OUT_FILE);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            throw new IllegalAccessError("Couldn't open file output stream: "+e);
        }
    }


    private SelectiveReceiver<CameraSnapShot> getSnapshotReceiver(){
        return new SelectiveReceiver<>(
                snapshot -> {
                    try {
                        if(savedSnapshot){
                            return;
                        }
                        snapShots.put(snapshot);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.err.println("Failed to add received snapshot: "+e);
                    }
                },
                snapShot -> true
        );
    }
    @Override
    public void run() {
        mSavingThread = buildPersistanceThread();
        mSavingThread.start();
        mCameraManager.registerSnapshotReceiver(getSnapshotReceiver());
        mCameraManager.startCapture();
        while(!savedSnapshot){
            ThreadUtils.sleep(1000);
        }
        stop();
        readSavedInput();
    }

    private void stop() {
        snapShots.clear();
        mCameraManager.stop();
        ThreadUtils.joinThread(Optional.of(mSavingThread));
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
                    if(snapShot.mDepthFrames.size() == 15 && snapShot.mRgbFrames.size() == 15){
                        savedSnapshot = true;
                        System.out.println("Saving snapshot");
                        saveSnapshot(snapShot);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveSnapshot(CameraSnapShot snapShot) {
        try {
            List<Byte> serialized = CameraSnapShot.cameraSnapShotToByteList(snapShot);
            byte[] bytes = SerializationUtils.toByteArray(serialized);
            System.out.println("Writing "+bytes.length);
            mFileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOError(new Throwable("Failed to save snapshot: "+e));
        }
    }

    private void readSavedInput(){
        List<Byte> read = FileUtils.readInputFile(OUT_FILE);
        CameraSnapShot snapShot = CameraSnapShot.byteListToCameraSnapShot(read);
        System.out.println("Read snapshot with "+snapShot.mDepthFrames.size() + "depth, "+snapShot.mRgbFrames.size()+ " rgb frames");

    }
}
