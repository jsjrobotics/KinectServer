package com.spookybox.camera;

import com.spookybox.util.Utils;
import org.openkinect.freenect.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static com.spookybox.util.Utils.sleep;

public class CameraManager {
    private final Device mKinect;
    private final ConcurrentLinkedQueue<KinectFrame> mRecentDepthFrames;
    private final ConcurrentLinkedQueue<KinectFrame> mRecentRgbFrames;
    private boolean isTerminating = true;
    private Optional<Thread> mDepthThread = Optional.empty();
    private Optional<Thread> mRgbThread = Optional.empty();
    private Optional<Thread> mConsumerThread = Optional.empty();


    public CameraManager(Device kinect){
        if(kinect == null){
            throw new IllegalArgumentException("Kinect is null");
        }
        mKinect = kinect;
        mRecentDepthFrames = new ConcurrentLinkedQueue<>();
        mRecentRgbFrames = new ConcurrentLinkedQueue<>();
        stop();
    }

    public void startCapture(final Consumer<CameraSnapShot> receiver){
        isTerminating = false;
        startDepthCapture();
        startRgbCapture();
        startConsumer(receiver);
    }

    private void startConsumer(final Consumer<CameraSnapShot> receiver) {
        mConsumerThread = Optional.of(new Thread(() -> {
            while(!isTerminating) {
                try {
                    Thread.sleep(500);
                    receiveFrames(receiver);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }));
        mConsumerThread.get().start();
    }

    private void receiveFrames(Consumer<CameraSnapShot> receiver) {
        if(mRecentDepthFrames.isEmpty() || mRecentRgbFrames.isEmpty()){
            return;
        }
        KinectFrame[] typeParameter = new KinectFrame[0];
        List<KinectFrame> depthFrames =  Arrays.asList(mRecentDepthFrames.toArray(typeParameter));
        List<KinectFrame> rgbFrames = Arrays.asList(mRecentRgbFrames.toArray(typeParameter));
        mRecentRgbFrames.clear();
        mRecentDepthFrames.clear();
        System.out.println("Depth ["+depthFrames.size() +"] - Video [" + rgbFrames.size() + "] @"+Utils.getUptime());
        receiver.accept(new CameraSnapShot(depthFrames, rgbFrames));
    }

    private void startRgbCapture() {
        Object awaitStart = new Object();
        mKinect.setVideoFormat(VideoFormat.RGB, Resolution.MEDIUM);
        mRgbThread = Optional.of(new Thread(() -> {
            VideoHandler receiver = (mode, frame, timestamp) -> {
                if(isTerminating || frame == null){
                    return;
                }
                mRecentRgbFrames.add(new KinectFrame(false, mode, frame, timestamp));
            };
            while(!(mKinect.startVideo(receiver) == 0)){
                System.out.println("Restarting depth");
                sleep(50);
            }
            notifyOnObject(awaitStart);
        }));
        mRgbThread.get().start();
        waitOnObject(awaitStart);
    }

    private void startDepthCapture() {
        Object awaitStart = new Object();
        mKinect.setDepthFormat(DepthFormat.D10BIT);
        mDepthThread = Optional.of(new Thread(() -> {
            DepthHandler receiver = (mode, frame, timestamp) -> {
                if(isTerminating || frame == null){
                    return;
                }
                mRecentDepthFrames.add(new KinectFrame(true, mode, frame, timestamp));
            };
            while(!(mKinect.startDepth(receiver) == 0)){
                System.out.println("Restarting depth");
                sleep(50);
            };
            notifyOnObject(awaitStart);
        }));
        mDepthThread.get().start();
        waitOnObject(awaitStart);
    }

    private void notifyOnObject(Object lock){
        synchronized (lock){
            lock.notify();
        }
    }

    private void waitOnObject(Object lock){
        synchronized (lock){
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void stop(){
        isTerminating = true;
        mKinect.stopVideo();
        mKinect.stopDepth();
        Utils.joinThread(mRgbThread);
        Utils.joinThread(mDepthThread);
        Utils.joinThread(mConsumerThread);
        mRgbThread = Optional.empty();
        mDepthThread = Optional.empty();
        mConsumerThread = Optional.empty();
    }

    public boolean isStopping() {
        return isTerminating;
    }
}
