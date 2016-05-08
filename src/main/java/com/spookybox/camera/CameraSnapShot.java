package com.spookybox.camera;

import java.util.ArrayList;
import java.util.List;

public class CameraSnapShot {
    public final List<KinectFrame> mRgbFrames = new ArrayList<>();
    public final List<KinectFrame> mDepthFrames = new ArrayList<>();

    public CameraSnapShot(List<KinectFrame> rgbFrames, List<KinectFrame> depthFrames){
        if(rgbFrames == null || depthFrames == null){
            throw new IllegalArgumentException("null buffer not allowed");
        }

        for (KinectFrame f : rgbFrames){
            mRgbFrames.add(f.clone());
        }

        for(KinectFrame f : depthFrames){
            mDepthFrames.add(f.clone());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CameraSnapShot that = (CameraSnapShot) o;

        for(KinectFrame f : mRgbFrames){
            if(!that.mRgbFrames.contains(f)){
                return false;
            }
        }

        for(KinectFrame f : mDepthFrames){
            if(!that.mDepthFrames.contains(f)){
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = mRgbFrames != null ? mRgbFrames.hashCode() : 0;
        result = 31 * result + (mDepthFrames != null ? mDepthFrames.hashCode() : 0);
        return result;
    }
}
