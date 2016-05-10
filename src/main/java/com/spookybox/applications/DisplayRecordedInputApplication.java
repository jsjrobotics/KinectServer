package com.spookybox.applications;

import com.spookybox.camera.CameraSnapShot;
import com.spookybox.util.Utils;

import java.io.IOException;
import java.util.List;

public class DisplayRecordedInputApplication extends DefaultInstance {
    private static final String IN_FILE = "kinect_run.out";
    private CameraSnapShot mSnapShot;

    @Override
    public void run() {
        mSnapShot = readSavedInput();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void haltCameraAndTilt() {}

    private CameraSnapShot readSavedInput(){
        List<Byte> read = Utils.readInputFile(IN_FILE);
        CameraSnapShot snapShot = CameraSnapShot.byteListToCameraSnapShot(read);
        System.out.println("Read snapshot with "+snapShot.mDepthFrames.size() + "depth, "+snapShot.mRgbFrames.size()+ " rgb frames");
        return snapShot;
    }
}
