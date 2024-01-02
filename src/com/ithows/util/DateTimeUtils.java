package com.ithows.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author ksyuser
 */
public class DateTimeUtils {

    /**
     * Hours per day.
     */
    public static final int HOURS_PER_DAY = 24;
    /**
     * Minutes per hour.
     */
    public static final int MINUTES_PER_HOUR = 60;
    /**
     * Minutes per day.
     */
    public static final int MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;
    /**
     * Seconds per minute.
     */
    public static final int SECONDS_PER_MINUTE = 60;
    /**
     * Seconds per 10minute.
     */
    public static final int SECONDS_PER_10MINUTES = 600;
    /**
     * Seconds per hour.
     */
    public static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    /**
     * Seconds per day.
     */
    public static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;


    /**
     *      날짜 관련 주요 함수
     *         Calendar cal = Calendar.getInstance();
     *         int year = cal.get(Calendar.YEAR);
     *         int mon = cal.get(Calendar.MONTH);
     *         int day = cal.get(Calendar.DAY_OF_MONTH);
     *         int hour = cal.get(Calendar.HOUR_OF_DAY);
     *         int min = cal.get(Calendar.MINUTE);
     *         int sec = cal.get(Calendar.SECOND);
     *
     */


    /**
     * 시간 재기 기능
     *  Date start_time = new Date(System.currentTimeMillis());
     *  System.out.println("total time = " + DateTimeUtils.getTimeDifferenceNow(start_time));
     */




    /**
     * dateformat
     * yyyy/MM/dd/HH:mm:ss
     * YYYY-MM-dd HH-mm-ss
     * yyyy-MM-dd HH:mm:ss
     * yyyyMMdd_HHmmss
     */

    public static final String FORMAT_DATETIME_SLASHDATETIME = "yyyy/MM/dd/HH:mm:ss";
    public static final String FORMAT_DATETIME_SLASHDATEONLY = "yyyy/MM/dd";
    public static final String FORMAT_DATETIME_COLONTIMEONLY = "HH:mm:ss";
    public static final String FORMAT_DATETIME_DASHECOLONDATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATETIME_NOSPACECOLONDATETIME = "yyyyMMdd HH:mm:ss";
    public static final String FORMAT_DATETIME_DASHEDATEONLY = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME_DASHETIMEONLY = "HH-mm-ss";
    public static final String FORMAT_DATETIME_NOSPACEDATETIME = "yyyyMMdd_HHmmss";
    public static final String FORMAT_DATETIME_NOSPACEDATEONLY = "yyyyMMdd";
    public static final String FORMAT_DATETIME_NOSPACETIMEONLY = "HHmmss";



    public static String getStringFromDateObj(Date date, String inputFormat){
        DateFormat df = new SimpleDateFormat(inputFormat);
        return df.format(date);
    }

    public static String getStringFromDateTime(Date date){
        return getStringFromDateObj(date, FORMAT_DATETIME_SLASHDATETIME) ; // "yyyy/MM/dd/HH:mm:ss"
    }

    public static String getStringFromDate(Date date){
        return getStringFromDateObj(date, FORMAT_DATETIME_SLASHDATEONLY );  // "yyyy/MM/dd"
    }


    /**
     * 시간객체와 시간문자열간 변환 함수
     * @return
     */



    public static String convertTimestampToDate(long timestamp) {
        if(timestamp < 10000000000L){
            timestamp *= 1000L;
        }
        Date date = new java.util.Date(timestamp);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }


    public static String convertUnixTimeToDate(long timestamp) {
        if(timestamp < 10000000000L){
            timestamp *= 1000L;
        }
        Date date = new java.util.Date(timestamp);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static long convertDateToUnixTime(Date dTime) {
        long epoch = dTime.getTime();
        return epoch;
    }

    public static long convertDateToUnixTime2(Date dTime) {
        // Date dt = sdf.parse(timestamp);
        long epoch = dTime.getTime();
        return (long)(epoch/1000);
    }



    public static long convertTimeStringToUnixTime(String timeStr, String inputFormat){
        Date date ;
        long epoch = -1;
        try {
            DateFormat df = new SimpleDateFormat(inputFormat);
            date = df.parse(timeStr);
            epoch = date.getTime();

        } catch (Exception e) {
            System.out.println("Exception :" + e);
        }
        return epoch ;
    }




    public static String convertDateTimeString(String timeStr, String orgFormat, String tgtFormat){
        Date date ;
        String result = "";

        if(orgFormat.equals("") || tgtFormat.equals("")){
            return result;
        }

        try {
            DateFormat df = new SimpleDateFormat(orgFormat);
            date = df.parse(timeStr);

            DateFormat df2 = new SimpleDateFormat(tgtFormat);
            result = df2.format(date);

        } catch (Exception e) {
            System.out.println("Exception :" + e);
        }

        return result;
    }

    public static String getDateTimeToString(Date date, String dFormat){
        if(dFormat.equals("")){
            dFormat = "yyyy/MM/dd/HH:mm:ss";
        }

        DateFormat df2 = new SimpleDateFormat(dFormat);
        return df2.format(date);
    }

    public static String getDateToString(Date date){
        DateFormat df2 = new SimpleDateFormat("yyyy/MM/dd");
        return df2.format(date);
    }

    public static String getDateStringFromString(String timeStr, String format){

        if(format.equals("")){
            format = "yyyy/MM/dd";
        }

        Date date = null;
        try {
            DateFormat df = new SimpleDateFormat(format);
            date = df.parse(timeStr);

        } catch (Exception e) {
            System.out.println("Exception :" + e);
            return null;
        }

        return getDateToString(date);
    }

    public static String getTimeToString(Date date){
        DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
        return df2.format(date);
    }

    public static Date getStringToDate(String timeStr, String inputFormat){   // yyyy-MM-dd HH:mm:ss
        Date date ;
        try {
            DateFormat df = new SimpleDateFormat(inputFormat);
            date = df.parse(timeStr);


        } catch (Exception e) {
            System.out.println("Exception :" + e);
            return null;
        }
        return date;
    }



    /**
     * 특정 시간에서 초단위로 더하거나 뺌
     * @param date : 기준 시간
     * @param sec : 더하는 시간(초). 음수값이면 빼는 효과
     * @return
     */

    public static Date getAfterSecondTime(Date date, int sec) {

        Date later = null;
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.SECOND, sec);
        later = cal.getTime();

        return later;
    }

    // "yyyy-MM-dd HH:mm:ss" 포맷 기준
    public static String getAfterSecondTimeToString(String date, String format, int sec) {

        String eTime = "";
        if(format.equals("")){
            format = FORMAT_DATETIME_DASHECOLONDATETIME;
        }

        eTime = getDateTimeToString(getAfterSecondTime(date, format, sec) , format);

        return eTime;
    }

    public static Date getAfterSecondTime(String str_date, String inputFormat, int sec) {

        Date later = null;
        Date date = null;

        try {
            DateFormat df = new SimpleDateFormat(inputFormat);
            date = df.parse(str_date);
            later = getAfterSecondTime(date, sec);

        } catch (Exception e) {
            System.out.println("Exception :" + e);
            return null;
        }
        return later;
    }

    /**
     * 특정 시간 정보만 받아 옴
     * @param dateStr
     * @param inputFormat
     * @param valueOption  : Calendar.SECOND  캘린더 상수 사용
     * @return
     */
    public static int getOnlyTimeValue(String dateStr, String inputFormat, int valueOption){

        DateFormat df = new SimpleDateFormat(inputFormat);
        Date date;
        try {
            date = df.parse(dateStr);

        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }

        return getOnlyTimeValue(date, valueOption);
    }

    public static int getOnlyTimeValue(Date date, int valueOption){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getOnlyTimeValue(calendar, valueOption);
    }

    public static int getOnlyTimeValue(Calendar cal, int valueOption){
        return cal.get(valueOption);
    }




    /**
     * 현재 시간 얻기
     * @return
     *
     * 현재 시간 구하기 방법 들
     * Calendar calendar = Calendar.getInstance(); // gets current instance of the calendar
     * Date date = new Date(); // this object contains the current date value
     * System.currentTimeMillis();   Date date = new Date(System.currentTimeMillis());
     * LocalDate date = LocalDate.now(); // gets the current date
     * LocalTime time = LocalTime.now(); // gets the current time
     * LocalDateTime dateTime = LocalDateTime.now(); // gets the current date and time    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
     */

    public static String getDateTimeFull() {
        return getTimeDateNow("YYYY-MM-dd HH-mm-ss");
    }

    public static String getDateTime() {
        return getTimeDateNow("(MM-dd HH:mm:ss)");
    }

    public static String getOnlyTimeNow(){
        return getTimeDateNow("HH:mm:ss");
    }

    public static String getTimeDateNow(){
        return getTimeDateNow("yyyy-MM-dd HH:mm:ss");
    }

    public static String getTimeDateNow2(){
        return getTimeDateNow("yyyyMMdd_HHmmss");
    }

    public static String getDateNow(){
        return getTimeDateNow("yyyyMMdd");
    }

    public static String getTimeDateNow(String format){

        if(format.equals("")){
            format = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        String timeValue = sdf.format(date);

        return timeValue;
    }


    /**
     * 현재의 Calendar에서 마지막날 구하기
     * @param cal
     * @return
     */
    public static String getMaximumDate(Calendar cal){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Calendar max = Calendar.getInstance(Locale.KOREA);
        max.setTime(cal.getTime());
        max.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return dateFormat.format(max.getTime());
    }


    public final static int INTERVAL_SECOND = 0;
    public final static int INTERVAL_MINUTE = 1;
    public final static int INTERVAL_HOUR = 2;

    /**
     * 특정 시간부터 자정까지의 시간
     * @param dateStr : ""이면 현재 부터
     * @param option
     * @return
     */
    public static int getIntervalToMidnight(String dateStr, int option){

        LocalTime now = null;

        if(dateStr.equals("")){
            now = LocalTime.now(ZoneId.systemDefault()) ;// LocalTime = 14:42:43.062

        }else{
            DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");
            now = LocalTime.parse(dateStr, dt);
        }

        if(option == INTERVAL_MINUTE){
            return MINUTES_PER_DAY - (now.toSecondOfDay() / SECONDS_PER_MINUTE );
        }else if(option == INTERVAL_HOUR){
            return HOURS_PER_DAY - (now.toSecondOfDay() / SECONDS_PER_HOUR );// Int = 52963
        }

        return SECONDS_PER_DAY - now.toSecondOfDay();// Int = 52963
    }

    // 정각 까지의 시간 (분, 초)
    public static int getTimeToZero(Date date, int valueOption){

        Calendar oCalendar = Calendar.getInstance(Locale.KOREA);
        oCalendar.setTime(date);
        int minutes = oCalendar.get(Calendar.MINUTE);
        int seconds = oCalendar.get(Calendar.SECOND);

        if(valueOption == Calendar.SECOND){
            return 3600 - (minutes * 60 + seconds);
        }else if(valueOption == Calendar.MINUTE){
            return 60 - minutes ;
        }
        return 0;
    }

    public static long getTimeDifferenceNow(long t1){
        return t1 - System.currentTimeMillis();
    }

    public static String getTimeDifferenceNow(Date startTime){
        Date nowTime = new Date(System.currentTimeMillis());
        return getTimeDiff(nowTime, startTime);
    }

    public static String getTimeDifferenceNow(String startTime, String format){
        SimpleDateFormat dt = new SimpleDateFormat(format);
        Date d1Date = null;

        try {
            d1Date = dt.parse(startTime);

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        Date nowTime = new Date(System.currentTimeMillis());
        return getTimeDiff(nowTime, d1Date);
    }

    public static float getTimeDifference(String laterTime, String beforeTime, int option) {
        return getTimeDifference(laterTime, beforeTime, "yyyy-MM-dd kk:mm:ss", option);
    }


    public static float getTimeDifference(String laterTime, String beforeTime, String format, int option) {
        SimpleDateFormat dt = new SimpleDateFormat(format);
        Date d1Date = null;
        Date d2Date = null;

        try {
            d1Date = dt.parse(beforeTime);
            d2Date = dt.parse(laterTime);

        }catch (Exception e){
            e.printStackTrace();
            return 0.0f;
        }
        return getTimeDiff(d2Date, d1Date, option);
    }

    public static float getTimeDiff(Date laterTime, Date beforeTime, int option) {  // option은 Calendar.HOUR 상수로 지정
        float result = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(beforeTime);
        long t1 = cal.getTimeInMillis();

        cal.setTime(laterTime);

        float diff = cal.getTimeInMillis() - t1;

        final int ONE_DAY = 1000 * 60 * 60 * 24;
        final int ONE_HOUR = ONE_DAY / 24;
        final int ONE_MINUTE = ONE_HOUR / 60;
        final int ONE_SECOND = 1000;
        final int ONE_CENTISECOND = 10000;


        if (option == Calendar.HOUR ) {
            result = diff / ONE_HOUR;
        } else if(option == Calendar.MINUTE ) {
            result = diff / ONE_MINUTE;
        }  else if(option == Calendar.SECOND ) {
            result = diff / ONE_SECOND;
        }  else if(option == Calendar.DATE ) {
            result = diff / ONE_DAY;
        }  else if(option == 20 ) {
            result = diff / ONE_CENTISECOND;

        }else{
            result = diff;
        }

        return result;
    }



    public static String getTimeDiff(Date later, Date before) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(before);

        long t1 = cal.getTimeInMillis();
        cal.setTime(later);

        long diff = cal.getTimeInMillis() - t1;

        String result = getTimeString(diff);

        return result;
    }

    // 만 나이 계산
    public static int getAge(String birthDate, String format) {
        LocalDate now = LocalDate.now();
        LocalDate parsedBirthDate = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern(format));

        int americanAge = now.minusYears(parsedBirthDate.getYear()).getYear(); // (1)

        // (2)
        // 생일이 지났는지 여부를 판단하기 위해 (1)을 입력받은 생년월일의 연도에 더한다.
        // 연도가 같아짐으로 생년월일만 판단할 수 있다!
        if (parsedBirthDate.plusYears(americanAge).isAfter(now)) {
            americanAge = americanAge -1;
        }

        return americanAge;
    }

    public static int getAge(String birthDate) {
        return getAge(birthDate, "yyyy-MM-dd");
    }



    ////////////////////////////////////////////////////
    // 중간 간격에 들어가는 시간 구하기

    // 시작날짜와 끝날짜간 날짜들에 대한 배열을 리턴
    public static ArrayList<String> makeIntervalDays(String sTime,  String eTime , String format) {   // "yyyy/MM/dd"

        if(format.equals("")){
            format = "yyyy/MM/dd";
        }

        ArrayList<String> dateArr = new ArrayList<>();

        if(sTime.equals(eTime)){
            dateArr.add(sTime);
            return dateArr;
        }

        int interval = (int)DateTimeUtils.getTimeDifference(eTime, sTime , format, Calendar.DATE);

        dateArr.add(sTime);
        for(int i=1 ; i < interval ;i++){
            String dateStr = DateTimeUtils.getStringFromDateObj( DateTimeUtils.getAfterSecondTime(DateTimeUtils.getStringToDate(sTime, format), DateTimeUtils.SECONDS_PER_DAY * (i) ), format );
            dateArr.add(dateStr);
        }
        dateArr.add(eTime);

        return dateArr;
    }

    // 시작날짜와 끝날짜간 날짜들에 대한 배열을 리턴
    public static ArrayList<String> makeIntervalMinutes(String sTime,  String eTime , String format) {   // "yyyy/MM/dd/HH:mm:ss"

        if(format.equals("")){
            format = "yyyy/MM/dd/HH:mm:ss";
        }

        ArrayList<String> dateArr = new ArrayList<>();

        if(sTime.equals(eTime)){
            dateArr.add(sTime);
            return dateArr;
        }

        int interval = (int)DateTimeUtils.getTimeDifference(eTime, sTime , format, Calendar.MINUTE);

        dateArr.add(sTime);
        for(int i=1 ; i < interval ;i++){
            String dateStr = DateTimeUtils.getStringFromDateObj( DateTimeUtils.getAfterSecondTime(DateTimeUtils.getStringToDate(sTime, format), DateTimeUtils.SECONDS_PER_MINUTE * (i) ), format );
            dateArr.add(dateStr);
        }
        dateArr.add(eTime);

        return dateArr;
    }

    // 시작날짜와 끝날짜간 날짜들에 대한 배열을 리턴
    public static ArrayList<String> makeInterval2Hours(String sTime,  String eTime , String format) {   // "yyyy/MM/dd/HH:mm:ss"

        if(format.equals("")){
            format = "yyyy/MM/dd/HH:mm:ss";
        }

        ArrayList<String> dateArr = new ArrayList<>();

        if(sTime.equals(eTime)){
            dateArr.add(sTime);
            return dateArr;
        }

        int interval = (int) Math.round(DateTimeUtils.getTimeDifference(eTime, sTime , format, Calendar.HOUR) / 2.0) ;

        dateArr.add(sTime);
        for(int i=1 ; i < interval ;i++){
            String dateStr = DateTimeUtils.getStringFromDateObj( DateTimeUtils.getAfterSecondTime(DateTimeUtils.getStringToDate(sTime, format), DateTimeUtils.SECONDS_PER_HOUR * (i * 2) ), format );
            dateArr.add(dateStr);
        }
        dateArr.add(eTime);

        return dateArr;
    }

    // 시작날짜와 끝날짜간 날짜들에 대한 배열을 리턴
    public static ArrayList<String> makeIntervalHour(String sTime,  String eTime , String format) {   // "yyyy/MM/dd/HH:mm:ss"

        if(format.equals("")){
            format = "yyyy/MM/dd/HH:mm:ss";
        }

        ArrayList<String> dateArr = new ArrayList<>();

        if(sTime.equals(eTime)){
            dateArr.add(sTime);
            return dateArr;
        }

        int interval = (int) DateTimeUtils.getTimeDifference(eTime, sTime , format, Calendar.HOUR) ;

        dateArr.add(sTime);
        for(int i=1 ; i < interval ;i++){
            String dateStr = DateTimeUtils.getStringFromDateObj( DateTimeUtils.getAfterSecondTime(DateTimeUtils.getStringToDate(sTime, format), DateTimeUtils.SECONDS_PER_HOUR * (i) ), format );
            dateArr.add(dateStr);
        }
        dateArr.add(eTime);

        return dateArr;
    }




    /**
     * 밀리세컨드를 시간으로 변환
     * @param milliseconds
     * @return
     */
    public static String getTimeString(long milliseconds) {
        long days = milliseconds / (long)86400000;
        long remainder = milliseconds % (long)86400000;
        long hours = remainder / (long)3600000;
        remainder %= (long)3600000;
        long minutes = remainder / (long)'\uea60';
        remainder %= (long)'\uea60';
        long seconds = remainder / (long)1000;
        long millisec = milliseconds % 1000;

        return String.valueOf(String.valueOf(
                (new StringBuffer(String.valueOf(String.valueOf(days)))).append("d ").append(hours).append("h ").append(minutes).append("m ").append(seconds).append(".").append(millisec).append("s")));
    }




    // 이 함수를 이용하면 시간, 분, 초를 정각화 해서 처리가 가능
    public static Date getFormatDate(Date timeObj, String inputFormat){
        Date date ;
        String timeStr = "";

        try {
            DateFormat df2 = new SimpleDateFormat(inputFormat);
            timeStr = df2.format(timeObj);
            date = df2.parse(timeStr);

        } catch (Exception e) {
            System.out.println("Exception :" + e);
            return null;
        }
        return date;
    }


    public static void main(String[] args) {
        String startTime = DateTimeUtils.getDateToString( DateTimeUtils.getAfterSecondTime(new Date(), DateTimeUtils.SECONDS_PER_DAY * -1) ) + " 00:00:00";
        String endTime = DateTimeUtils.getDateToString(new Date()) + " 00:00:00";

//        System.out.println("1 : " + startTime + " ~ " + endTime );
//        System.out.println("2 : " + getDateToString(getAfterSecondTime("2020-01-01 23:59:59",  "yyyy-MM-dd kk:mm:ss", SECONDS_PER_DAY )) );
//        System.out.println("3 : " + getIntervalToMidnight("", INTERVAL_MINUTE) );
//        System.out.println("4 : " + getIntervalToMidnight("", INTERVAL_SECOND) );
//        System.out.println("5 : " + getTimeDateNow());
//        System.out.println("5 : " + getTimeToZero(new Date(), Calendar.SECOND) );

        System.out.println("" + DateTimeUtils.getStringToDate("2019/11/26/02:58:29:581", "yyyy/MM/dd/HH:mm:ss" ));  //

//
//        int intervalSec = -10;
//        String timeNow = DateTimeUtils.getTimeDateNow();
//        String eTime = DateTimeUtils.convertDateTimeString(timeNow, DateTimeUtils.FORMAT_DATETIME_DASHECOLONDATETIME , DateTimeUtils.FORMAT_DATETIME_SLASHDATETIME);
//        String sTime = DateTimeUtils.getAfterSecondTimeToString(eTime, DateTimeUtils.FORMAT_DATETIME_SLASHDATETIME , intervalSec) ;
//
//        System.out.println("sTime = " + sTime);
//        System.out.println("eTime = " + eTime);


        // 생성시간을 계산해서 당일이면 Edge에 요청한 데이터로 생성
        // 과거이면 DB에 생성된 snap데이터로 생성
        String timeNow = DateTimeUtils.getTimeDateNow();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // 날짜 문자열을 Date 객체로 파싱
            Date parsedDate1 = sdf.parse("2023-06-06 12:00:00");
            Date parsedDate2 = sdf.parse(timeNow);

            // 년, 월, 일이 같은지 비교
            boolean isSame = parsedDate1.compareTo(parsedDate2) == 0;

            // 결과 출력
            if (isSame) {
                System.out.println("두 날짜는 년, 월, 일이 같습니다.");
            } else {
                System.out.println("두 날짜는 년, 월, 일이 다릅니다.");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}