package com.spookybox.applications;

import com.spookybox.tilt.TiltManager;
import org.openkinect.freenect.Context;
import org.openkinect.freenect.Device;
import org.openkinect.freenect.Freenect;

public abstract class DefaultInstance implements ApplicationInstance {

    protected TiltManager mTiltManager;
    protected Context context;
    protected Device mKinect;

    public DefaultInstance(){
        context = Freenect.createContext();
        if(context == null){
            throw new IllegalStateException("context is null");
        }
        if (context.numDevices() > 0) {
            mKinect = context.openDevice(0);
        } else {
            throw new IllegalAccessError("No kinect detected.");
        }
        mTiltManager = new TiltManager(mKinect);
    }

    public final void shutdownKinect() {
        mKinect.close();
        context.shutdown();
    }

    protected void printTiltAngle() {
        System.out.println(" Current Tilt angle: " + mTiltManager.getTiltAngle());
    }
}
