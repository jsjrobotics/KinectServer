package com.spookybox.server;

import com.spookybox.camera.CameraManager;
import com.spookybox.camera.KinectFrame;
import com.spookybox.graphics.ByteBufferToImage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CameraThread extends Thread {
    private final TrasmitController controller;
    private final boolean readyToStart;
    private final int threadNumber;
    private final TcpServer server;
    private final CameraManager cameraManager;
    private int[] transmitBuffer;

    public CameraThread(int threadNumber, TrasmitController controller, CameraManager cameraManager, TcpServer server){
        super("CameraThread:"+threadNumber);
        this.threadNumber = threadNumber;
        this.cameraManager = cameraManager;
        this.controller = controller;
        this.server = server;
        readyToStart = controller != null && cameraManager != null && server!= null;
    }

    @Override
    public void run(){
        System.out.println(getName() + " Started");
        cameraManager.registerSnapshotReceiver(
                snapShot -> {
                    Runnable r = () -> {
                        if(!server.isConnected()){
                            return;
                        }
                        KinectFrame kinectFrame = snapShot.mRgbFrames.get(0);
                        KinectFrame kinectFrame1 = snapShot.mRgbFrames.get(1);
                        BufferedImage image = ByteBufferToImage.convertToImage2(kinectFrame, kinectFrame1);
                        int width = image.getWidth();
                        int height = image.getHeight();
                        int minBufferSize = width * height;
                        if( transmitBuffer == null || transmitBuffer.length < minBufferSize){
                            transmitBuffer = new int[minBufferSize];
                        }
                        image.getRGB(0, 0, image.getWidth(), image.getHeight(), transmitBuffer, 0, image.getWidth());
                        controller.transmit(server,threadNumber, transmitBuffer,0,width*height);
                    };
                    Thread t = new Thread(r);
                    t.start();
                },
                snapShot -> server.isConnected());
    }

    public boolean readyToStart() {
        return readyToStart;
    }

    public TcpServer getServer(){
        return server;
    }
}
