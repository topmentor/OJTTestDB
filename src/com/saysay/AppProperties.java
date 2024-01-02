package com.saysay;

import com.ithows.log.SOXLog;

import java.io.*;
import java.util.Date;
import java.util.Properties;

public class AppProperties extends Properties {

    private String fileName = null;

    public AppProperties(String fileName) throws IOException {
        this.fileName = fileName; //생성자에서 경로를 받아 객체를 생성
        this.loadProperties();    //생성자에서 로드 프로퍼티 실행
    }

    public String getFileName() {
        return this.fileName; //자신의 XML파일의 경로를 내주는 함수
    }

    public void loadProperties() throws IOException {
        try {
            File f = new File(this.fileName);
            if (f.exists()) {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));  //XML경로로 파일스트림을 연다
                this.loadFromXML(bis);  //XML파일의 프로퍼티를 로드함
                bis.close();            //inputstream 닫음
            } else {
                SOXLog.log(null, "로딩 실패 : " + f.toString());
            }
        } catch (IOException e) {
            SOXLog.log(e, "로딩 실패 : ");
            throw e;
        }
    }

    public String getString(String key) {        //키를 이용해 벨류값을 얻어내는 함수
        Object obj = this.get(key);
        if (obj != null) {
            return (String) obj;
        } else {
            return null;
        }
    }

    public void saveProperties() {
        saveProperties("Properties " + new Date().toString(), "utf-8");
    }

    private void saveProperties(String comment, String charset) {//XML파일에 내용을 저장한다
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(this.fileName));
            this.storeToXML(bos, comment, charset);
            bos.close();
        } catch (IOException e) {
            SOXLog.log(e, "저장 실패 : ");
        }
    }

}
