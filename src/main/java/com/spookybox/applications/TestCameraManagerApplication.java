package com.spookybox.applications;

import com.spookybox.camera.CameraManager;

import static com.spookybox.util.Utils.sleep;

public class TestCameraManagerApplication extends DefaultInstance {
    protected CameraManager mCameraManager;

    public TestCameraManagerApplication(){
        super();
        mCameraManager = new CameraManager(mKinect);
    }
    @Override
    public void run() {
        printTiltAngle();
        mTiltManager.moveAndWait(20);
        sleep(500);
        printTiltAngle();
        mTiltManager.moveAndWait(0);
        sleep(500);
        printTiltAngle();
        sleep(1000);
        startCameraCapture();
        sleep(10000);
        stopCameraCapture();
    }

    @Override
    public void haltCameraAndTilt(){
        mCameraManager.stop();
    }

    protected void stopCameraCapture() {
        mCameraManager.stop();
    }



    protected void startCameraCapture(){
        mCameraManager.startCapture((snapshot) -> {
        });
    }

}
