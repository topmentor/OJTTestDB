package com.ithows.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ithows.db.ResultMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class UtilJSON
 *
 * @author Roi Kim <S.O.X Co. Ltd.>
 */
public class UtilJSON {
    public static String JSonBeautify(String jsonStr){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(jsonStr);

        String prettyJsonString = gson.toJson(je);

        return prettyJsonString;
    }


    public static String jsonToStringForSave(JSONObject obj){
        return obj.toString().replaceAll("\"", "\\\\\"");
    }

    public static String jsonToStringForSave(JSONArray obj){
        return obj.toString().replaceAll("\"", "\\\\\"");
    }


    public static String objectToJsonstring(Object obj){
        Gson gson = new Gson();
        String jsonString = gson.toJson(obj);
        return jsonString;
    }

    // JSONObject를 ResultMap으로 변환
    public static ResultMap jsonToMap(JSONObject jObj){
        ResultMap map = new ResultMap();

        Iterator itr = jObj.keys();
        while (itr.hasNext()){
            try {
                String key = itr.next().toString();

                if(jObj.get(key) instanceof JSONObject){
                    ResultMap subMap = jsonToMap((JSONObject)jObj.get(key));
                    map.put(key, subMap);
                }else if(jObj.get(key) instanceof JSONArray){
                    ArrayList<ResultMap> arr = convertJSONArrayToArrayList((JSONArray)jObj.get(key));
                    map.put(key, arr);
                }else{
                    map.put(key, jObj.get(key));
                }

            } catch (JSONException ex) {  }
        }

        return map;
    }

    // 객체를 JSONObject로 변환
    public static JSONObject objectToJsonObject(Object obj){
        JSONObject jObj = null;

        try {
            jObj = new JSONObject(objectToJsonstring(obj));
        } catch (JSONException ex) {
            Logger.getLogger(UtilJSON.class.getName()).log(Level.SEVERE, null, ex);
        }

        return jObj;
    }

    public static JSONObject mapToJSon(Map<String, Object> payload){
        JSONObject jObj = new JSONObject();

        try {
            for(String keyVal : payload.keySet())
            {
                jObj.put(keyVal,payload.get(keyVal));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;
    }

    public static JSONObject mapToJSon(ResultMap payload){
        JSONObject jObj = new JSONObject();

        try {
            for (Object key : payload.keySet()) {
                jObj.put((String) key,  payload.get(key));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;
    }




    public static boolean writeJsonToFile(String jsonStr, String jsonFilName) {
        try (FileWriter writer = new FileWriter(jsonFilName)) {
            jsonStr = UtilJSON.JSonBeautify(jsonStr) ;
            writer.append(jsonStr);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // JSon 구조의 최대 너비를 체크하는 함수
    public static int getWidth(Object Obj) {
        int max = 0;
        int width = 0;
        int depth = 0;
        JSONArray jArr = null;
        if(Obj instanceof JSONObject){
            jArr = new JSONArray();
            jArr.put((JSONObject)Obj);
        }else if(Obj instanceof JSONArray){
            jArr = (JSONArray)Obj;
        }

        depth = getDepth(jArr);

        for(int i=1; i<=depth ; i++ ){
            width = getWidthDepth(jArr, i);
            if (max <= width) {
                max = width;
            }
        }

        return max;
    }

    private static int getWidthArray(JSONArray jArr) {
        int max = jArr.length();

        try {
            int width = 0 ;
            for(int i=0; i< jArr.length(); i++){
                JSONObject jObj = jArr.getJSONObject(i);
                Iterator itr = jObj.keys();
                while (itr.hasNext()){
                    String key = itr.next().toString();
                    if(jObj.get(key) instanceof JSONArray) {
                        width += getWidthArray(jObj.getJSONArray(key));
                    }
                }
                if (max <= width) {
                    max = width;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return max;
    }

    // JSon 구조의 깊이를 체크하는 함수
    public static int getDepth(Object Obj) {
        int depth = 1;
        if(Obj instanceof JSONObject){
            return getDepthObject((JSONObject)Obj, depth);
        }else if(Obj instanceof JSONArray){
            return getDepthArray((JSONArray)Obj, depth);
        }
        return -1;
    }

    private static int getDepthObject(JSONObject jObj, int depth) {
        Iterator i = jObj.keys();
        int max = depth;
        try {
            while (i.hasNext()){
                String key = i.next().toString();
                int dep = depth + 1;
                if(jObj.get(key) instanceof JSONObject){
                    dep = getDepthObject(jObj.getJSONObject(key), dep);
                    if(max <= dep){
                        max = dep;
                    }
                }else if(jObj.get(key) instanceof JSONArray){
                    dep =  getDepthArray(jObj.getJSONArray(key), dep );
                    if(max <= dep){
                        max = dep;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return max;
    }

    // JSon배열 구조의 깊이를 체크하는 함수
    private static int getDepthArray(JSONArray jArr, int depth) {
        int max = depth;

        try {
            for(int i=0; i< jArr.length(); i++){
                JSONObject jObj = jArr.getJSONObject(i);
                Iterator itr = jObj.keys();
                while (itr.hasNext()){
                    String key = itr.next().toString();
                    int dep = depth + 1;
                    if(jObj.get(key) instanceof JSONObject){
                        dep = getDepthObject(jObj.getJSONObject(key), dep);
                        if(max <= dep){
                            max = dep;
                        }
                    }else if(jObj.get(key) instanceof JSONArray){
                        dep =  getDepthArray(jObj.getJSONArray(key), dep );
                        if(max <= dep){
                            max = dep;
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return max;
    }



    // 특정 depth의 총 요소의 수를 얻기
    // context 값은 2로 고정
    public static int getWidthDepth(JSONArray jArr, int depth) {

        if(depth == 1){
            return jArr.length();
        }

        if(depth > getDepth(jArr)){
            return 0;
        }

        ArrayList<JSONObject> nodeList = new ArrayList<JSONObject>();
        getNodesDepth(nodeList, jArr, depth, 1);
        int value = nodeList.size();

        return value;
    }


    private static void getNodesDepth(ArrayList<JSONObject> nodeList, JSONArray jArr, int depth, int context) {

        JSONArray jChildren = null;

        try {
            int count = jArr.length();
            for(int i=0; i< count; i++){
                JSONObject jObj = jArr.getJSONObject(i);

                if(depth > context){
                    try {
                        jChildren = jObj.getJSONArray("children");
                    }catch (JSONException ex){
                        jChildren = null;
                    }
                    // 요소 확인
                    if(jChildren != null && jChildren.length()>0) {
                        getNodesDepth(nodeList, jChildren, depth, (context + 1) );
                    }
                }else if(depth == context) {
                    nodeList.add(jObj);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ;
    }


    public static String convertJSONArrayToCSVString(JSONArray jsonArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (i != 0) {
                stringBuilder.append(",");
            }
            try {
                stringBuilder.append(jsonArray.getString(i));
            } catch (JSONException ex) {
                Logger.getLogger(UtilJSON.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return stringBuilder.toString();
    }

    // 단순 HashMap
    public static JSONObject convertHashMapToJSon(HashMap map){
        JSONObject jObj = new JSONObject();

        try {
            for (Object key : map.keySet()) {
                jObj.put((String) key,  map.get(key));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;
    }

    // 이중 HashMap
    public static JSONObject convertHashMapListToJSon(HashMap<String, HashMap> map){
        JSONObject jObj = new JSONObject();

        try {
            for(String innerKey : map.keySet()){
                HashMap innerMap = map.get(innerKey);
                JSONObject innerJObj = new JSONObject();

                for (Object key : innerMap.keySet()) {
                    innerJObj.put((String) key,  innerMap.get(key));
                }
                jObj.put(innerKey, innerJObj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;
    }

    public static JSONArray convertArrayListToJSONArray(ArrayList<ResultMap> list) {

        JSONArray jArr = new JSONArray();

        if(list.size() < 1){
            return jArr;
        }

        try{

            for(ResultMap element : list) {
                JSONObject jObj = new JSONObject();
                for (Object key : element.keySet()) {
                    jObj.put((String) key,  element.get(key));
                }
                jArr.put(jObj);
            }
        }catch(JSONException ex){
            System.out.println(ex.getLocalizedMessage());
        }

        return jArr;
    }

    // JSONArray를 ArrayList로 변환
    public static ArrayList convertJSONArrayToArrayList(JSONArray jArr) {

        ArrayList<Object> arraylist = new ArrayList<Object>();

        if(jArr.length() < 1){
            return arraylist;
        }

        try {
            int count = jArr.length();

            if(!(jArr.get(0) instanceof  JSONObject)){

            }

            for(int i=0; i< count; i++){

                if(jArr.get(i) instanceof  JSONObject){
                    JSONObject jObj = jArr.getJSONObject(i);
                    ResultMap map = jsonToMap(jObj);
                    arraylist.add(map);
                }else{
                    arraylist.add(jArr.get(i));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return arraylist;
    }


    public static void printArrayList(ArrayList<ResultMap> list, boolean arrange) {

        if(list.size() < 1){
            return ;
        }

        JSONArray jArr = convertArrayListToJSONArray(list);

        if(arrange){
            String str = JSonBeautify(jArr.toString());
            System.out.println(str);
        }else{
            System.out.println(jArr.toString());
        }

        return ;
    }


    public static void main(String[] args) {
//        String jsonStr = "{\"aa\" : [  {\"1\" : {\"2\" : {\"3\" : 3}}}, {\"4\" : {\"1\" : {\"2\" : {\"3\" : 4}}}}  ] }";
//        String jsonStr = "[  {\"1\" : {\"2\" : [{\"3\" : 3},{\"4\" : 3},{\"5\" : 3} ] }}, {\"4\" : {\"1\" : {\"2\" : {\"3\" : 4}}}}  ]";
        String jsonStr = "[{\n" +
                "    \"name\": \"flare\",\n" +
                "    \"children\": [\n" +
                "        {\n" +
                "            \"name\": \"flex\",\n" +
                "            \"children\": [\n" +
                "                {\"name\": \"FlareVis\", \"value\": 4116}\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"scale\",\n" +
                "            \"children\": [\n" +
                "                {\"name\": \"IScaleMap\", \"value\": 2105,  \"children\": [ {\"name\": \"Map1\", \"value\": 1316} , {\"name\": \"Map2\", \"value\": 1312}] },\n" +
                "                {\"name\": \"LinearScale\", \"value\": 1316},\n" +
                "                {\"name\": \"LogScale\", \"value\": 3151},\n" +
                "                {\"name\": \"OrdinalScale\", \"value\": 3770},\n" +
                "                {\"name\": \"QuantileScale\", \"value\": 2435},\n" +
                "                {\"name\": \"QuantitativeScale\", \"value\": 4839},\n" +
                "                {\"name\": \"RootScale\", \"value\": 1756},\n" +
                "                {\"name\": \"Scale\", \"value\": 4268, \"children\": [ {\"name\": \"Map1\", \"value\": 1316} , {\"name\": \"Map2\", \"value\": 1312}]},\n" +
                "                {\"name\": \"ScaleType\", \"value\": 1821},\n" +
                "                {\"name\": \"TimeScale\", \"value\": 5833}\n" +
                "           ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"display\",\n" +
                "            \"children\": [\n" +
                "                {\"name\": \"DirtySprite\", \"value\": 8833}\n" +
                "           ]\n" +
                "        }\n" +
                "    ]\n" +
                "}," +
                "{\"name\": \"IScaleMap\", \"value\": 2105,  \"children\": [ {\"name\": \"Map1\", \"value\": 1316} , {\"name\": \"Map2\", \"value\": 1312}] } \n" +
                "];";
//        int depth = 0;
//        try {
//            JSONObject jobj = new JSONObject(jsonStr);
//            depth = getDepth(jobj);
//        } catch (JSONException e) {
//            try {
//                JSONArray jobj = new JSONArray(jsonStr);
//                depth = getDepth(jobj);
//            } catch (JSONException jsonException) {
//                jsonException.printStackTrace();
//            }
//        }
//        System.out.println("depth = " + depth);

        int width = 0;
        try {
            //JSONObject jobj = new JSONObject(jsonStr);   //  @@  \"는 객체 생성자로 바로 파싱 됨
            JSONArray arr = new JSONArray(jsonStr);       //  @@  \"는 객체 생성자로 바로 파싱 됨

            System.out.println("arr = " + arr.toString().replaceAll("\"", "\\\\\""));

            //arr.put(jobj);
            int depth = getDepth(arr);
            System.out.println("depth= " + depth);
            width = getWidth(arr);
            System.out.println("max width= " + width);
            width = getWidthDepth(arr, 1);
            System.out.println("width 1 = " + width);
            width = getWidthDepth(arr, 2);
            System.out.println("width 2 = " + width);
            width = getWidthDepth(arr, 3);
            System.out.println("width 3 = " + width);
            width = getWidthDepth(arr, 4);
            System.out.println("width 4 = " + width);


//            HashMap<String, String> map = new HashMap<String, String>();
//            map.put("heel", "sdfdsfsd");
//            System.out.println("tojson = " + objectToJsonstring(map));

            JSONArray arr2 = new JSONArray("[1,2,3]");
            ArrayList<ResultMap> list = convertJSONArrayToArrayList(arr);
            for(Object obj : list){
                if(obj instanceof ResultMap){
                    System.out.println("tojson - " + objectToJsonstring((ResultMap)obj));
                }else{
                    System.out.println("tojson - " + obj);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
