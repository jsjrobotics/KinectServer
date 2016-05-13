package com.spookybox.server;

import com.spookybox.camera.CameraManager;

import java.util.ArrayList;
import java.util.List;

public class TrasmitController extends Thread {

    private final List<CameraThread> cameraThreads = new ArrayList<>();
    private final CameraManager mCameraManager;

    public TrasmitController(String name, ArrayList<TcpServer> serverList, CameraManager cameraManager){
        super(name);
        mCameraManager = cameraManager;
        int max = serverList.size();
        if(max < cameraManager.getAttachedKinects()){
            max = cameraManager.getAttachedKinects();
        }
        for(int index = 0; index < max; index ++){
            CameraThread camera = new CameraThread(1,this,mCameraManager,serverList.get(index));
            cameraThreads.add(camera);
        }
    }

    @Override
    public void run() {
        for(int index =0; index < cameraThreads.size(); index++){
            CameraThread camera = cameraThreads.get(index);
            if(camera.readyToStart()){
                camera.start();
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
