package com.spookybox.applications;

import static com.spookybox.util.ThreadUtils.sleep;

public class TestCameraManagerApplication extends DefaultInstance {

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

    protected void stopCameraCapture() {
        mCameraManager.stop();
    }



    protected void startCameraCapture(){
        mCameraManager.startCapture();
    }

}
