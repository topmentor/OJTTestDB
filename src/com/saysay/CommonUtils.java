package com.saysay;


public class CommonUtils {

    public static boolean checkWindows(){
        String OS = System.getProperty("os.name").toLowerCase();

        if(OS.indexOf("win") >= 0){
            return true;
        }else{
            return false;
        }
    }

    public static void sleep(float sec){

        try {
            Thread.sleep((int)(sec * 1000));
        }catch (Exception e){}
    }



}
