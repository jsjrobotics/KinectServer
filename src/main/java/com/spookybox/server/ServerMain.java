package com.spookybox.server;

import com.spookybox.camera.CameraManager;

import java.io.IOException;
import java.util.ArrayList;

public class ServerMain {
    private static final int FIRST_PORT = 4445;
    private int controllerInstances = 0;
    private ArrayList<TcpServer> servers = new ArrayList<>();

    private TransmitController transmitController;
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

    public void start() throws IOException {
        int numberOfFeeds = 2;
        for(int index=0; index< numberOfFeeds; index++){
            String serverName;
            if(index == 0){
                serverName = "RgbServer";
            } else {
                serverName = "DepthServer";
            }
            TcpServer server = new TcpServer(serverName, FIRST_PORT +index, () -> launchTransmitController());
            server.start();
            servers.add(server);
        }
    }

    private  void launchTransmitController(){
        transmitController = new TransmitController("TransmitController"+controllerInstances, servers, mCameraManager);
        controllerInstances += 1;
        transmitController.start();
    }
}
