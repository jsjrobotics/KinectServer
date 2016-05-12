package com.spookybox.util;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

    public static long getUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    public static byte[] toByteArray(List<Byte> input) {
        byte[] bytes = new byte[input.size()];
        for(int index = 0; index< input.size(); index++){
            bytes[index] = input.get(index);
        }
        return bytes;
    }

    public static List<Byte> toByteList(byte[] serialized) {
        ArrayList<Byte> result = new ArrayList<>();
        for(int index = 0; index < serialized.length; index++){
            result.add(serialized[index]);
        }
        return result;
    }


}
