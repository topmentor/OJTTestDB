package com.ithows.log;

import java.util.Date;

public class SOXLog {
    public static void info(Object... msgs) {
        System.out.println("Log INFO " + new Date().toString() + "   ");
        for (Object obj : msgs) {
            System.out.print(obj.toString() + " ");
        }
        System.out.println();
    }
    public static void debug(Object... msgs) {
        System.out.println("Debug Data " + new Date().toString() + "   ");
        for (Object obj : msgs) {
            System.out.print(obj.toString() + " ");
        }
        System.out.println();
    }

    public static void log(Exception e, Object... msgs) {
        System.out.print("LOG Time " + new Date().toString() );
        e.printStackTrace();
        System.out.print(" Method Name: ");
        System.out.println(e.getStackTrace()[0].getMethodName());
        for (Object obj : msgs) {
            System.out.print(obj.toString() + "  ");
        }
        System.out.println("LOG END ");
    }
}
