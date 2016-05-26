package com.spookybox.freenect;

import org.openkinect.freenect.FrameMode;

import java.nio.ByteBuffer;

public class DepthStreamCallback {
    static {
        System.loadLibrary("freenectDepth");
    }

    private native void depthCallback(FrameMode mode, ByteBuffer frame, int timestamp);

    public static void main(String[] args) {
        new DepthStreamCallback().depthCallback(null, null, 0);  // invoke the native method
    }
}
