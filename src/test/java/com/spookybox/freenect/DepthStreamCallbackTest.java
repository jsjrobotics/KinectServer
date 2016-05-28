package com.spookybox.freenect;

import org.junit.Before;

public class DepthStreamCallbackTest {
    private DepthStreamCallback mCallback;

    @Before
    public void setup(){
        mCallback = new DepthStreamCallback();
    }
}
