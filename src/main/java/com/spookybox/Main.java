package com.spookybox;

import com.spookybox.applications.ApplicationInstance;
import com.spookybox.applications.RecordFramesApplication;
import com.spookybox.applications.TestCameraManagerApplication;

public class Main {

    private static ApplicationInstance instance =
            new RecordFramesApplication();
             //new TestCameraManagerApplication();

    public static void main(String[] args){
        System.out.println("-- Application Start --");
        instance.run();
        instance.haltCameraAndTilt();
        instance.shutdownKinect();
        System.out.println("-- Application End --");
    }

}
