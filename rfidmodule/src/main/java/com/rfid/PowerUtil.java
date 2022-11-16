package com.rfid;

import java.io.File;
import java.io.FileWriter;

public class PowerUtil {

    private static String s1 = "/proc/gpiocontrol/set_id";
    public static String s2 = "/proc/gpiocontrol/set_uhf";

    public static void power(String id) {
        try {

//            FileWriter RaidPower=new FileWriter (new File (s1));
//            RaidPower.write (id);
//            RaidPower.close ();

            FileWriter localFileWriterOn = new FileWriter(new File(s2));
            localFileWriterOn.write(id);
            localFileWriterOn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
