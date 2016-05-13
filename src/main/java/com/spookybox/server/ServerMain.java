package com.spookybox.server;

import com.spookybox.camera.CameraManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {
    private final int firstPort = 4445;
    private int controllerInstances = 0;
    private ArrayList<TcpServer> servers = new ArrayList<>();

    private  TrasmitController trasmitController;
    private final CameraManager mCameraManager;

    public ServerMain(CameraManager cameraManager){
        mCameraManager = cameraManager;
        mCameraManager.setOnStartListener(() -> {
            try {
                start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private  ConnectedListener listener = new ConnectedListener() {
        @Override
        public void connectionInitiated(int socketIndex) {
            launchTransmitController();
        }
    };


    public void start() throws IOException {
        int numberOfFeeds = mCameraManager.getAttachedKinects();
        for(int index=0; index< numberOfFeeds; index++){
            TcpServer server = new TcpServer(firstPort+index, listener);
            server.start();
            servers.add(server);

        }
        launchTransmitController();
    }

    private  void launchTransmitController(){
        trasmitController = new TrasmitController("TransmitController"+controllerInstances, servers, mCameraManager);
        controllerInstances += 1;
        trasmitController.start();
    }
}
