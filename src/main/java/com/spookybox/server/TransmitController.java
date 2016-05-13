package com.spookybox.server;

import com.spookybox.camera.CameraManager;

import java.util.ArrayList;
import java.util.List;

public class TransmitController extends Thread {

    private final List<SnapShotTransmitThread> cameraThreads = new ArrayList<>();
    private final CameraManager mCameraManager;

    public TransmitController(String name, ArrayList<TcpServer> serverList, CameraManager cameraManager){
        super(name);
        mCameraManager = cameraManager;
        if(serverList.size() != 2 && serverList.size() != 3){
            String errorMessage ="ServerList size must be 1 -> 3.\nlist[0] = rgbServer\nlist[1] = depthServer\n(optional) list[2] = otherServer";
            throw new IllegalArgumentException(errorMessage);
        }
        TcpServer rgbServer = serverList.get(0);
        TcpServer depthServer = serverList.get(1);
        SnapShotTransmitThread camera = new SnapShotTransmitThread(1,this,mCameraManager, rgbServer, depthServer);
        cameraThreads.add(camera);
    }

    @Override
    public void run() {
        for(int index =0; index < cameraThreads.size(); index++){
            SnapShotTransmitThread cameraThread = cameraThreads.get(index);
            if(cameraThread.readyToStart()){
                cameraThread.start();
            }
        }

    }

    public void transmit(TcpServer server,int threadNumber, int[] buffer, int offset, int transmitLength) {
        int[] data = prependCameraInformation(threadNumber,buffer,offset,transmitLength);
        server.transmit(data,0,data.length);
    }

    public int[] prependCameraInformation(int threadNumber, int[] buffer, int offset, int transmitLength) {
        int[] result = new int[transmitLength];
        for(int index = 0; index < transmitLength; index++){
            result[index] = buffer[offset+index];
        }
        return result;
    }
}
