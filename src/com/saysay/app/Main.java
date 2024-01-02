package com.saysay.app;

import com.ithows.db.ResultMap;
import com.saysay.CommonDAO;
import com.saysay.CommonDoc;
import java.util.*;


//  java -cp OJTTestDB.jar com.saysay.app.Main

public class Main {


    public static void main(String[] args) {


        ArrayList<ResultMap> list = (ArrayList<ResultMap>)CommonDAO.getAllUser();

        for(ResultMap map : list){
            System.out.println("id = " + map.getInt("id"));
            System.out.println("userId = " + map.getString("userId"));
            System.out.println("password = " + map.getString("userPassword"));
            System.out.println("time = " + map.getString("registerTime"));
        }

        System.out.println("resource path = " + CommonDoc.resourcePath);
    }


}
