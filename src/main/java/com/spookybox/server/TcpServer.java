package com.spookybox.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TcpServer {
    private final int mPortNumber;
    private final ServerSocket mSocket;
    private int clientsConnected = -1;
    private final ConnectedListener mListener;
    private List<Socket> clientSockets = new ArrayList<>();
    private List<BufferedOutputStream> outputStreams = new ArrayList<>();
    private List<BufferedReader> inputStreams = new ArrayList<>();

    private final Thread serverThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while(true) {
                    System.out.println("Waiting for client");
                    Socket clientSocket = mSocket.accept();
                    System.out.println("Client connected");
                    clientsConnected += 1;
                    clientSockets.add(clientsConnected, clientSocket);
                    BufferedOutputStream outputStream = new BufferedOutputStream(clientSocket.getOutputStream(), 25344);
                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    outputStreams.add(clientsConnected, outputStream);
                    inputStreams.add(clientsConnected, inputReader);
                    mListener.connectionInitiated(clientsConnected);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    public TcpServer(int port,ConnectedListener listener) throws IOException {
        mPortNumber = port;
        mSocket = new ServerSocket(mPortNumber);
        mListener = listener;
    }

    public boolean isConnected(){
        if(clientSockets.size() > 0){
            return true;
        }
        return false;
    }

    public void start(){
        serverThread.start();
    }

    public void transmit(int[] buffer,int offset,int bytesToWrite){
        if(isConnected()){
            try {
                for(int clientIndex = 0; clientIndex < outputStreams.size(); clientIndex++){
                    BufferedOutputStream client = outputStreams.get(clientIndex);
                    for(int index = offset; index < offset+bytesToWrite; index++) {
                        client.write(buffer[index]);
                    }
                }
                //System.out.println("Wrote "+bytesToWrite);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public BufferedOutputStream getClient(int socketIndex) {
        return outputStreams.get(socketIndex);
    }
}
