package com.spookybox.util;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Utils {
    private static final long JOIN_TIMEOUT = 2000;

    public static long getUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public static void joinThread(Optional<Thread> thread) {
        thread.ifPresent(Utils::joinThread);
    }

    private static void joinThread(Thread thread){
        try {
            thread.join(JOIN_TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("Interrupted waiting for thread to join: "+thread);

        }
    }
}
