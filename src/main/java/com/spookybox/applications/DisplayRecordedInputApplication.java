package com.spookybox.applications;

import com.spookybox.graphics.ByteBufferToImage;
import com.spookybox.graphics.DisplayCanvas;
import com.spookybox.camera.CameraSnapShot;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.spookybox.util.FileUtils.readInputFile;
import static com.spookybox.util.ThreadUtils.sleep;

public class DisplayRecordedInputApplication extends DefaultInstance {
    private static final String IN_FILE = "kinect_run.out";
    private Optional<CameraSnapShot> mSnapShot;
    private DisplayCanvas mCanvas;

    @Override
    public void run() {
        mCanvas = DisplayCanvas.initWindow();
        mSnapShot = readSavedInput();
        mSnapShot.ifPresent(snapShot -> {
            for(int index = 0; index < snapShot.mRgbFrames.size()-1; index += 2){
                BufferedImage image = ByteBufferToImage.convertToImage(snapShot.mRgbFrames.get(index), snapShot.mRgbFrames.get(index+1));
                mCanvas.setImage(image);
                mCanvas.repaint();
                sleep(500);
            }
        });
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean initCamera(){
        return false;
    }


    private Optional<CameraSnapShot> readSavedInput(){
        List<Byte> read = readInputFile(IN_FILE);
        if(read.isEmpty()){
            return Optional.empty();
        }
        CameraSnapShot snapShot = CameraSnapShot.byteListToCameraSnapShot(read);
        System.out.println("Read snapshot with "+snapShot.mDepthFrames.size() + "depth, "+snapShot.mRgbFrames.size()+ " rgb frames");
        return Optional.of(snapShot);
    }

}
