package com.spookybox.camera;

import com.spookybox.util.Functional;
import com.spookybox.util.Utils;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class CameraSnapShot implements Serializable{
    public final List<KinectFrame> mRgbFrames = new ArrayList<>();
    public final List<KinectFrame> mDepthFrames = new ArrayList<>();

    public CameraSnapShot(List<KinectFrame> rgbFrames, List<KinectFrame> depthFrames){
        if(rgbFrames == null || depthFrames == null){
            throw new IllegalArgumentException("null buffer not allowed");
        }

        for (KinectFrame f : rgbFrames){
            mRgbFrames.add(f.clone());
        }

        for(KinectFrame f : mDepthFrames){
            mDepthFrames.add(f.clone());
        }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        int rgbFrames = mRgbFrames.size();
        out.writeInt(rgbFrames);


        for(KinectFrame f : mRgbFrames){
            out.write(Utils.toByteArray(Serialization.kinectFrameToByteList(f)));
        }

        int depthFrames = mDepthFrames.size();
        out.writeInt(depthFrames);
        for(KinectFrame d : mDepthFrames){
            out.write(Utils.toByteArray(Serialization.kinectFrameToByteList(d)));
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        int rgbFrames = in.readInt();
        for(int index = 0; index < rgbFrames; index++){
            byte[] serialized = (byte[]) in.readObject();
            mRgbFrames.add(Serialization.byteListToKinectFrame(Utils.toByteList(serialized)));
        }

        int depthFrames = in.readInt();
        for(int index = 0; index < depthFrames; index++){
            byte[] serialized = (byte[]) in.readObject();
            mDepthFrames.add(Serialization.byteListToKinectFrame(Utils.toByteList(serialized)));
        }
    }
    private void readObjectNoData() throws ObjectStreamException {}
}
