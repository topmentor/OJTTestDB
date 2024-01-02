package com.saysay;

import com.ithows.log.SOXLog;

import java.io.FileWriter;
import java.io.PrintWriter;

public class CommonDoc {


    private static PrintWriter logFile = null;

    public static String resourcePath = "";

    public static void colf(String str) // Console Out with Line Feed
    {
        System.out.println(str);
        logFile.println(str);

    }


    static {
        initConfig();

        try {
            logFile = new PrintWriter(new FileWriter("process.log", true));
        } catch (Exception e) {
            SOXLog.log(e, "logFile Error");
        }

    }

    public static void initConfig(){
        if( !AppConfig.getConf("resource_dir").equals("")){
            resourcePath = AppConfig.getConf("resource_dir");
        }

    }

}
