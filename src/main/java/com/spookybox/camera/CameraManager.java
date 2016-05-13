package com.spookybox.camera;

import com.spookybox.util.SelectiveReceiver;
import com.spookybox.util.ThreadUtils;
import com.spookybox.util.Utils;
import org.openkinect.freenect.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.spookybox.util.ThreadUtils.sleep;

public class CameraManager {
    private final Device mKinect;
    private final ConcurrentLinkedQueue<KinectFrame> mRecentDepthFrames;
    private final ConcurrentLinkedQueue<KinectFrame> mRecentRgbFrames;
    private boolean isTerminating = true;
    private Optional<Thread> mDepthThread = Optional.empty();
    private Optional<Thread> mRgbThread = Optional.empty();
    private Optional<Thread> mConsumerThread = Optional.empty();
    private List<SelectiveReceiver<CameraSnapShot>> receiverList = new ArrayList<>();
    private List<Runnable> mOnStartListeners = new ArrayList<>();


    public CameraManager(Device kinect){
        if(kinect == null){
            throw new IllegalArgumentException("Kinect is null");
        }
        mKinect = kinect;
        mRecentDepthFrames = new ConcurrentLinkedQueue<>();
        mRecentRgbFrames = new ConcurrentLinkedQueue<>();
        stop();
    }

    public void startCapture(){
        isTerminating = false;
        startDepthCapture();
        startRgbCapture();
        startConsumer();
        mOnStartListeners.forEach(Runnable::run);
        mOnStartListeners.clear();
    }

    public void registerSnapshotReceiver(Consumer<CameraSnapShot> receiver, Predicate<CameraSnapShot> predicate){
        receiverList.add(new SelectiveReceiver<>(receiver,predicate));
    }
    private void startConsumer() {
        mConsumerThread = Optional.of(new Thread(() -> {
            while(!isTerminating) {
                try {
                    Thread.sleep(500);
                    receiveFrames();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }));
        mConsumerThread.get().start();
    }

    private void receiveFrames() {
        if(mRecentDepthFrames.isEmpty() || mRecentRgbFrames.isEmpty()){
            return;
        }
        KinectFrame[] typeParameter = new KinectFrame[0];
        List<KinectFrame> depthFrames =  Arrays.asList(mRecentDepthFrames.toArray(typeParameter));
        List<KinectFrame> rgbFrames = Arrays.asList(mRecentRgbFrames.toArray(typeParameter));
        mRecentRgbFrames.clear();
        mRecentDepthFrames.clear();
        System.out.println("Depth ["+depthFrames.size() +"] - Video [" + rgbFrames.size() + "] @"+Utils.getUptime());
        CameraSnapShot snapShot = new CameraSnapShot(rgbFrames, depthFrames);
        for(SelectiveReceiver<CameraSnapShot> receiver : receiverList){
            if(receiver.mPredicate.test(snapShot)){
                receiver.mReceiver.accept(snapShot);
            }
        }
    }

    private void startRgbCapture() {
        Object awaitStart = new Object();
        mKinect.setVideoFormat(VideoFormat.RGB, Resolution.HIGH);
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
        ThreadUtils.joinThread(mRgbThread);
        ThreadUtils.joinThread(mDepthThread);
        ThreadUtils.joinThread(mConsumerThread);
        mRgbThread = Optional.empty();
        mDepthThread = Optional.empty();
        mConsumerThread = Optional.empty();
    }

    public boolean isStopping() {
        return isTerminating;
    }

    public int getAttachedKinects() {
        return mKinect != null ? 1 : 0;
    }

    public void setOnStartListener(Runnable r) {
        if(isTerminating == true){
            mOnStartListeners.add(r);
        }
        else{
            r.run();
        }
    }
}
