package com.spookybox.camera;

import org.openkinect.freenect.FrameMode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KinectFrame implements Cloneable {

    private final FrameMode mMode;
    private final ByteBuffer mBuffer;

    private final int mTimestamp;
    private final boolean mIsDepthFrame;

    public KinectFrame(
            final boolean isDepthFrame,
            final FrameMode mode,
            final ByteBuffer frame,
            final int timestamp){

        mIsDepthFrame = isDepthFrame;
        mMode = clone(mode);
        mBuffer = clone(frame);

        mTimestamp = timestamp;
    }

    private FrameMode clone(FrameMode mode) {
        FrameMode clone = new FrameMode();
        clone.reserved = mode.reserved;
        clone.resolution = mode.resolution;
        clone.format = mode.format;
        clone.bytes = mode.bytes;
        clone.width = mode.width;
        clone.height = mode.height;
        clone.dataBitsPerPixel = mode.dataBitsPerPixel;
        clone.paddingBitsPerPixel = mode.paddingBitsPerPixel;
        clone.framerate = mode.framerate;
        clone.valid = mode.valid;
        return clone;
    }

    private ByteBuffer clone(ByteBuffer frame) {
        ByteBuffer clone = ByteBuffer.allocate(frame.capacity());
        frame.flip();
        clone.put(frame);
        return clone;
    }

    public FrameMode getMode() {
        return mMode;
    }

    public ByteBuffer getBuffer() {
        return mBuffer;
    }

    public int getTimestamp() {
        return mTimestamp;
    }

    public boolean isDepthFrame() {
        return mIsDepthFrame;
    }

    private ByteBuffer cloneFrame() {
        ByteBuffer clone = ByteBuffer.allocate(mBuffer.limit());
        mBuffer.rewind();
        clone.put(mBuffer);
        return clone;
    }

    @Override
    public KinectFrame clone(){
        FrameMode mode = clone(mMode);
        ByteBuffer frame = cloneFrame();
        return new KinectFrame(mIsDepthFrame,mode,frame,mTimestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KinectFrame that = (KinectFrame) o;

        if (mTimestamp != that.mTimestamp) return false;
        if (mIsDepthFrame != that.mIsDepthFrame) return false;
        if (mMode != null ? !mMode.equals(that.mMode) : that.mMode != null) return false;
        return mBuffer != null ? mBuffer.equals(that.mBuffer) : that.mBuffer == null;

    }

    @Override
    public int hashCode() {
        int result = mMode != null ? mMode.hashCode() : 0;
        result = 31 * result + (mBuffer != null ? mBuffer.hashCode() : 0);
        result = 31 * result + mTimestamp;
        result = 31 * result + (mIsDepthFrame ? 1 : 0);
        return result;
    }
}
