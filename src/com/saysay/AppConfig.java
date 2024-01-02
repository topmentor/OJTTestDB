package com.saysay;

import com.ithows.db.SqliteDao;
import com.ithows.log.SOXLog;

import java.util.Iterator;
import java.util.Set;

public class AppConfig {

    public static AppProperties properties = null;

    static {
        try {
            String OS = System.getProperty("os.name").toLowerCase();
            String configpath = SqliteDao.class.getResource("/resource").toString();

            if (OS.indexOf("win") >= 0) {
                configpath = configpath + "/configapp.xml";   //진짜 파일의 경로를 얻어낸다. -- @@ 윈도우즈 적용
            }

            configpath = configpath.replaceAll("file:/", ""); //경로를 얻어내며 생긴file:/을 제거한다

            SOXLog.info("configpath = " , configpath);

            properties = new AppProperties(configpath);  //XML파일 로딩시켜준다.
        } catch (Exception e) {
            SOXLog.log(e, "로딩 실패 : ");
        }

    }

    //APP_CONFIG 전용 메서드
    public static AppProperties getAppConfig() {
        return properties; //자신을 돌려주는 메서드
    }

    public static String getConf(String key) {//키를 받고 그에 해당하는 값을 얻어냄 myconf에서
        if (properties != null) {
            return properties.getString(key);
        } else {
            return null;
        }
    }

    public static int getConfInt(String key) {//키를 받고 그에 해당하는 값을 얻어냄 myconf에서
        String str = AppConfig.getConf(key);
        int result = -1;
        if (str != null) {
            try {
                result = Integer.parseInt(str);
            } catch (Exception e) {
                SOXLog.log(e, "파싱 실패 : ");
            }
            return result;
        } else {
            return -1;
        }
    }

    public static void deleteConf(String key) {//키를 받고 해당 값을 삭제 하고 save
        properties.remove(key);
        properties.saveProperties();
    }

    public static void setConf(String key, String name) {//키와 벨류값을 저장하고 세이브
        properties.put(key, name);//메모리로만 저장
        properties.saveProperties();
    }

    public void init() {
        setAppConfigToServletContext();
    }

    private static void setAppConfigToServletContext() {
        if (properties != null) {
            Set states = properties.keySet(); // get set-view of keys
            Iterator<String> itr = states.iterator();
            while (itr.hasNext()) {
                String key = itr.next();
                String value = properties.getString(key);
            }
        }
    }
}
