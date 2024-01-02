package com.ithows.db;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.ithows.log.SOXLog;
import com.ithows.util.UtilFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 *  DAO(Database Access Object)에 공통적으로 필요한 속성과 기능을 지원해주는 클래스
 * @author dreamct2
 */
public class JdbcDao {

    public static String JDBC_DRIVER_1 = "com.mysql.jdbc.Driver";
    public static String JDBC_DRIVER_2 = "org.mariadb.jdbc.Driver";
    public static String DB_SERVER_IP = "127.0.0.1";   // 연결 DB의 주소
    public static int DB_SERVER_PORT = 3306;               // 연결 DB의 포트
    public static String DB_URL = "jdbc:mysql://" + DB_SERVER_IP + ":" + DB_SERVER_PORT + "/_________?characterEncoding=utf8&amp;useSSL=false";  // db이름 지정

    public static String USERNAME = "username";  // 유저 이름
    public static String PASSWORD = "****";   // 비번

    public static int MIN_IDLE = 3;
    public static int MAX_ACTIVE = 5;
    public static int INITIAL_SIZE = 2;

    static {
        try {
            JdbcDao.loadProperties();
        } catch (Exception ex) {
            SOXLog.log(ex, "드라이브 로딩 에러");
        }
    }

    private static DataSource datasource = null;

    public static Properties loadProperties() {
        Properties p = new Properties();
        return p;
    }

    public synchronized static DataSource getDataSource() {
        if (datasource == null) {
            PoolConfiguration p = new PoolProperties();
            String OS = System.getProperty("os.name").toLowerCase();
            Properties config = JdbcDao.loadProperties();

//            p.setDriverClassName(JDBC_DRIVER_1);
            p.setDriverClassName(JDBC_DRIVER_2);
            p.setUrl(DB_URL);

            p.setUsername(USERNAME);
            p.setPassword(PASSWORD);
            p.setMinIdle(MIN_IDLE);
            p.setInitialSize(INITIAL_SIZE);
            p.setMaxActive(MAX_ACTIVE);

            p.setJmxEnabled(true);
            p.setTestWhileIdle(false);
            p.setTestOnBorrow(true);
            p.setValidationQuery("SELECT 1");
            p.setTestOnReturn(false);
            p.setValidationInterval(30000);
            p.setTimeBetweenEvictionRunsMillis(30000);
            p.setMaxWait(10000);
            p.setRemoveAbandonedTimeout(60);
            p.setMinEvictableIdleTimeMillis(30000);
            p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
            p.setLogAbandoned(true);
            p.setRemoveAbandoned(true);
            datasource = new DataSource();
            datasource.setPoolProperties(p);
        }
        return datasource;
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = JdbcDao.getDataSource().getConnection();
            conn.setAutoCommit(false);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() +  " : Connection 얻어내기 에러");
        }
        return conn;
    }

    private static void printPool() {
        if (datasource != null) {
            SOXLog.info(" active:" + datasource.getPool().getActive());
            SOXLog.info(" wait:" + datasource.getPool().getWaitCount());
            SOXLog.info(" idle:" + datasource.getPool().getIdle());
            SOXLog.info(" ");
        }
    }

    public static void endConnection(Connection conn) {
        try {
            conn.commit();
        } catch (SQLException e) {
            try {
                SOXLog.log(e, "endConnection 에러 - commit - error");
                conn.rollback();
            } catch (SQLException e2) {
                SOXLog.log(e2, "endConnection 에러 - rollback - error");
            }
        } finally {
            try {
                conn.close();
            } catch (SQLException e3) {
                SOXLog.log(e3,  "endConnection 에러 - close - error");
            }
        }
    }

    public static void transacUpdate(JdbcTransactor transactor) throws SQLException{
        Connection conn = JdbcDao.getConnection();
        try{
            if(conn!=null)
                transactor.doTransaction(conn);
        }catch(SQLException e){
            throw e;
        }finally{
            if(conn!=null)
                JdbcDao.endConnection(conn);
        }
    }



    public static boolean execute(String sp_query) throws SQLException {
        return JdbcDao.execute(null, sp_query);
    }

    /**
     * alter table DML 쿼리를 실행한다
     * @param sp_query 요청된 query
     * @return result 쿼리 성공 여부
     * @throws SQLException
     */
    public static boolean execute(Connection conn, String sp_query) throws SQLException {
        boolean result = false;
        if (sp_query == null) {
            throw new SQLException("SQL Query가 NULL입니다.");
        }
        /* --------2011.9.5 transaction 처리를 위해서 ------------------- */
        boolean isInnerConnection = false;//일단은 외부 Connection 이용
        if(conn==null){ //없으면 내부 Connection 이용
            isInnerConnection = true;
            conn = JdbcDao.getConnection();
        }
        /* -------------------------------------------------------------- */

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sp_query);
            result = pstmt.execute();
        } catch (SQLException ex) {
            System.out.println("Alter Query ");
            throw ex;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getLocalizedMessage());
                throw ex;
            } finally {
                if (conn != null) {
                    /* -------------------------------------------------------------- */
                    if(isInnerConnection==true){//내부에서 만든 Connection만 제거한다.
                        JdbcDao.endConnection(conn);
                    }/* -------------------------------------------------------------- */
                }
            }
        }
        return result;
    }


    public static int update(String sp_query) throws SQLException {
        return JdbcDao.update(null, sp_query, null);
    }
    public static int update(String sp_query, Object[] sp_Input) throws SQLException {
        return JdbcDao.update(null, sp_query, sp_Input);
    }
    /**
     * insert, update, delete 쿼리를 실행하고 변경된 레코드의 개수를 리턴한다.
     * @param sp_query 요청된 query
     * @param sp_Input IN 파리미터
     * @return result 변경된 레코드 개수
     * @throws SQLException
     */
    public static int update(Connection conn, String sp_query, Object[] sp_Input) throws SQLException {
        int result = -1;
        if (sp_query == null) {
            throw new SQLException("SQL Query가 NULL입니다.");
        }
        /* --------2011.9.5 transaction 처리를 위해서 ------------------- */
        boolean isInnerConnection = false;//일단은 외부 Connection 이용
        if(conn==null){ //없으면 내부 Connection 이용
            isInnerConnection = true;
            conn = JdbcDao.getConnection();
        }
        /* -------------------------------------------------------------- */

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sp_query);
            if (sp_Input != null) {
                for (int i = 0; i < sp_Input.length; i++) {
                    pstmt.setObject(i + 1, sp_Input[i]);
                }
            }
            result = pstmt.executeUpdate();
        } catch (SQLException ex) {
            SOXLog.log(ex,  "SQL Query Update");
            throw ex;
        } finally {
            try {
                if (pstmt != null) {
                    //conn.commit();
                    pstmt.close();
                }
            } catch (SQLException ex) {
                SOXLog.log(ex);
                throw ex;
            } finally {
                if (conn != null) {
                    /* -------------------------------------------------------------- */
                    if(isInnerConnection==true){//내부에서 만든 Connection만 제거한다.
                        JdbcDao.endConnection(conn);
                    }/* -------------------------------------------------------------- */
                }
            }
        }
        return result;
    }



    /**
     * insert, update, delete 쿼리를 실행하고 변경된 레코드의 개수를 리턴한다.
     * commit 처리를 안하는 버전
     **/

    public static int updateNoCommit(Connection conn, String sp_query, Object[] sp_Input) throws SQLException {
        int result = -1;
        if (sp_query == null) {
            throw new SQLException("SQL Query가 NULL입니다.");
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sp_query);
            if (sp_Input != null) {
                for (int i = 0; i < sp_Input.length; i++) {
                    pstmt.setObject(i + 1, sp_Input[i]);
                }
            }
            result = pstmt.executeUpdate();
        } catch (SQLException ex) {
            SOXLog.log(ex);
            throw ex;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {
                SOXLog.log(ex);
                throw ex;
            }
        }
        return result;
    }




    /**
     * 주어진 쿼리와 파라미터를 이용하여 PreparedStatement 객체를 생성한 후 List 타입으로 결과값을 리턴한다.
     * @param sp_query 요청된 query
     * @param sp_Input IN 파리미터
     * @return list list 객체
     * @throws SQLException
     */
    public static List queryForList(String sp_query, Object[] sp_Input) throws SQLException {
        if (sp_query == null) {
            throw new SQLException("SQL Query가 NULL입니다.");
        }
        List list = new ArrayList();
        Connection conn = JdbcDao.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sp_query);
            if (sp_Input != null) {
                for (int i = 0; i < sp_Input.length; i++) {
                    pstmt.setObject(i + 1, sp_Input[i]);
                }
            }
            boolean hasResults = pstmt.execute();
            rs = pstmt.getResultSet();
            while (rs.next()) {
                    list.add(rs.getObject(1));
            }
        } catch (SQLException e) {
            SOXLog.log(e,  "catch:" + sp_query);
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    //conn.commit();
                    pstmt.close();
                }
            } catch (SQLException ex) {
                SOXLog.log(ex, "catch :" + sp_query);
                throw ex;
            } finally {
                if (conn != null) {
                    JdbcDao.endConnection(conn);
                }
            }
        }
        return list;
    }

    public static ResultMap queryForMapObject(String sp_query, Object[] sp_Input) throws SQLException {
        List list = queryForMapList(sp_query, sp_Input);
        if (list.size() > 0) {
            return (ResultMap) list.get(0);
        } else {
            return null;
        }
    }

    public static List queryForMapList(String sp_query, Object[] sp_Input) throws SQLException {
        if (sp_query == null) {
            throw new SQLException("SQL Query가 NULL입니다.");
        }
        List list = new ArrayList();
        Connection conn = JdbcDao.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sp_query);
            if (sp_Input != null) {
                for (int i = 0; i < sp_Input.length; i++) {
                    pstmt.setObject(i + 1, sp_Input[i]);
                }
            }
            boolean hasResults = pstmt.execute();
            rs = pstmt.getResultSet();
            /*----------------------------------------------------------------*/
            ResultSetMetaData rsMetaData = rs.getMetaData();
            while (rs.next()) {
                ResultMap map = new ResultMap();
                for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                    map.put(rsMetaData.getColumnLabel(i), rs.getObject(i));
                }
                list.add(map);
            }
            /*----------------------------------------------------------------*/
        } catch (SQLException e) {
            SOXLog.log(e, "catch:" + sp_query);
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    //conn.commit();
                    pstmt.close();
                }
            } catch (SQLException ex) {
                SOXLog.log(ex, "catch :" + sp_query);
                throw ex;
            } finally {
                if (conn != null) {
                    JdbcDao.endConnection(conn);
                }
            }
        }
        return list;
    }



    // CSV로 File Export : 구분자는 '|'
    public static String queryForListCSVFile(String sp_query, Object[] sp_Input, String fileName) throws Exception {
        return queryForListCSVFile(sp_query, sp_Input, fileName, "");
    }

    public static String queryForListCSVFile(String sp_query, Object[] sp_Input, String fileName, String addString) throws Exception {
        if (sp_query == null) {
            throw new SQLException("SQL Query가 NULL입니다.");
        }
        Connection conn = JdbcDao.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sp_query);
            if (sp_Input != null) {
                for (int i = 0; i < sp_Input.length; i++) {
                    pstmt.setObject(i + 1, sp_Input[i]);
                }
            }
            boolean hasResults = pstmt.execute();
            rs = pstmt.getResultSet();

            OutputStreamWriter csvFile = new OutputStreamWriter(new FileOutputStream(new File(fileName), true));

            String rowString = "";

            /*----------------------------------------------------------------*/
            ResultSetMetaData rsMetaData = rs.getMetaData();
            rs.last();
            int rowcount = rs.getRow();
            rs.beforeFirst();

            System.out.println("rowCount = " + rowcount);

            // column 입력
            for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                rowString += rsMetaData.getColumnLabel(i) + "|";
            }

            // 사용자 추가 입력 데이터
            if(!addString.equals("")){
                rowString += "userData|";
            }
            if(!rowString.equals("")) {
                rowString = rowString.substring(0, rowString.length() - 1);
            }
            csvFile.write(rowString+"\n");

            // 데이터 입력
            int cnt = 0;
            while (rs.next()) {
                rowString = "";
                for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                    rowString += rs.getObject(i) + "|";
                }

                // 사용자 추가 입력 데이터
                if(!addString.equals("")){
                    rowString += addString + "|";
                }

                if(!rowString.equals("")) {
                    rowString = rowString.substring(0, rowString.length() - 1);
                }

                cnt++;
                if(cnt < rowcount ){
                    rowString += "\n";
                }
                csvFile.write(rowString);
            }

            /*----------------------------------------------------------------*/
            csvFile.write("\n");
            csvFile.flush();
            csvFile.close();




        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() + "\ncatch:" + sp_query);
            fileName = "";
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    //conn.commit();
                    pstmt.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getLocalizedMessage() + "\ncatch :" + sp_query);
                throw ex;
            } finally {
                if (conn != null) {
                    JdbcDao.endConnection(conn);
                }
            }
        }
        return fileName;
    }


    // JSON으로 File Export
    public static String queryForListJsonFile(String sp_query, Object[] sp_Input, String fileName) throws Exception {
        return queryForListJsonFile(sp_query,  sp_Input, fileName, "");
    }
    public static String queryForListJsonFile(String sp_query, Object[] sp_Input, String fileName, String addData) throws Exception {
        if (sp_query == null) {
            throw new SQLException("SQL Query가 NULL입니다.");
        }
        JSONArray list = new JSONArray();
        Connection conn = JdbcDao.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sp_query);
            if (sp_Input != null) {
                for (int i = 0; i < sp_Input.length; i++) {
                    pstmt.setObject(i + 1, sp_Input[i]);
                }
            }
            boolean hasResults = pstmt.execute();
            rs = pstmt.getResultSet();

            OutputStreamWriter csvFile = new OutputStreamWriter(new FileOutputStream(new File(fileName), false));
            rs.last();
            int rowcount = rs.getRow();
            rs.beforeFirst();

            System.out.println("rowCount = " + rowcount);

            String rowString = "";
            int cnt = 0;

            /*----------------------------------------------------------------*/
            ResultSetMetaData rsMetaData = rs.getMetaData();

            csvFile.write("[");

            JSONObject map = new JSONObject();
            while (rs.next()) {

                for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                    map.put(rsMetaData.getColumnLabel(i), rs.getObject(i));  // column과 row 꺼내기
                }

                // 사용자 추가 입력 데이터
                if(!addData.equals("")){
                    map.put("userData", addData);
                }

                rowString = map.toString();

                cnt++;
                if(cnt < rowcount ){
                    rowString += ",\n";
                }

                csvFile.write(rowString);
            }
            /*----------------------------------------------------------------*/
            csvFile.write("]");
            csvFile.flush();
            csvFile.close();


            String zipFileName = FilenameUtils.getFullPath(fileName) + FilenameUtils.getBaseName(fileName) + ".zip";
            if(UtilFile.zip(fileName, zipFileName)){
                File nfile = new File(fileName);
                if (nfile.exists()) {
                    nfile.delete();
                }
                fileName = zipFileName;
            }


        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() + "\ncatch:" + sp_query);
            fileName = "";
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    //conn.commit();
                    pstmt.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getLocalizedMessage() + "\ncatch :" + sp_query);
                throw ex;
            } finally {
                if (conn != null) {
                    JdbcDao.endConnection(conn);
                }
            }
        }
        return fileName;
    }


    // JSON Array로 Export
    public static JSONArray queryForJsonArray(String sp_query, Object[] sp_Input) throws Exception {
        if (sp_query == null) {
            throw new SQLException("SQL Query가 NULL입니다.");
        }
        JSONArray list = new JSONArray();
        Connection conn = JdbcDao.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sp_query);
            if (sp_Input != null) {
                for (int i = 0; i < sp_Input.length; i++) {
                    pstmt.setObject(i + 1, sp_Input[i]);
                }
            }
            boolean hasResults = pstmt.execute();
            rs = pstmt.getResultSet();
            /*----------------------------------------------------------------*/
            ResultSetMetaData rsMetaData = rs.getMetaData();
            while (rs.next()) {
                JSONObject map = new JSONObject();
                for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                    map.put(rsMetaData.getColumnLabel(i), rs.getObject(i));
                }
                list.put(map);
            }
            /*----------------------------------------------------------------*/
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() + "\ncatch:" + sp_query);
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    //conn.commit();
                    pstmt.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getLocalizedMessage() + "\ncatch :" + sp_query);
                throw ex;
            } finally {
                if (conn != null) {
                    JdbcDao.endConnection(conn);
                }
            }
        }
        return list;
    }



    /**
     * 주어진 쿼리를 이용하여 PreparedStatement 객체를 생성한 후 하나의 Object 타입 객체를 리턴하는 함수
     * @param sp_query 요청된 query
     * @return obj Object 객체
     * @throws SQLException
     */
    public static Object queryForObject(String sp_query, Object[] sp_Input) throws SQLException {
        List list = queryForList(sp_query, sp_Input);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 주어진 쿼리와 파라미터를 이용하여 PreparedStatement 객체를 생성한 후 하나의 Object 타입 객체를 리턴하는 함수
     * @param sp_query 요청된 query
     * @return obj Object 객체
     * @throws SQLException
     */
    public static Object queryForObject(String sp_query) throws SQLException {
        return queryForObject(sp_query, null);
    }

    /**
     * 주어진 쿼리를 이용하여 PreparedStatement 객체를 생성한 후 하나의 int형 결과값을 리턴하는 함수
     * @param sp_query - 요청된 query
     * @return resultInt - int형 결과값
     * @throws SQLException
     * @throws NumberFormatException
     */
    public static int queryForInt(String sp_query) throws SQLException, NumberFormatException {
        return queryForInt(sp_query, null);
    }

    /**
     * 주어진 쿼리와 파라미터값을 이용하여 PreparedStatement 객체를 생성한 후 하나의 int형 결과값을 리턴하는 함수
     * @param sp_query 요청된 query
     * @param sp_Input IN 파리미터
     * @return resultInt int형 결과값
     * @throws SQLException
     * @throws NumberFormatException
     */
    public static int queryForInt(String sp_query, Object[] sp_Input) throws SQLException, NumberFormatException {
        int resultInt = -1;
        Object obj = queryForObject(sp_query, sp_Input);
        if(obj!=null){
            try {
                resultInt = Integer.parseInt(obj.toString());
            } catch (NumberFormatException e) {
                SOXLog.log(e);
            }
        }
        return resultInt;
    }

    /**
     * 주어진 쿼리와 파라미터값을 이용하여 PreparedStatement 객체를 생성한 후 하나의 String형 결과값을 리턴하는 함수
     * @param sp_query 요청된 query
     * @return obj.toString() String 객체
     * @throws SQLException
     */
    public static String queryForString(String sp_query) throws SQLException {
        return queryForString(sp_query, null);
    }

    /**
     * 주어진 쿼리와 파라미터값을 이용하여 PreparedStatement 객체를 생성한 후 하나의 String형 결과값을 리턴하는 함수
     * @param sp_query
     * @param sp_Input
     * @return obj.toString() String 객체
     * @throws SQLException
     */
    public static String queryForString(String sp_query, Object[] sp_Input) throws SQLException {
        Object obj = queryForObject(sp_query, sp_Input);
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    /**
     * 주어진 쿼리와 파라미터를 이용하여 CallableStatement 객체를 생성한 후 OUT  파라미터와 변경된 레코드 갯수를 리턴한다.
     * @param sp_query 요청된 query
     * @param sp_Input IN 파리미터
     * @param sp_Out OUT 파라미터
     * @return reuslt 변경된 레코드 갯수
     * @throws SQLException
     */
    public static int updateStored(Connection conn, String sp_query, Object[] sp_Input, Object[] sp_Out) throws SQLException {
        if (sp_query == null) {
            throw new SQLException("SQL Query가 NULL입니다.");
        }
        int result = -1;
        /* --------2011.9.5 transaction 처리를 위해서 ------------------- */
        boolean isInnerConnection = false;//일단은 외부 Connection 이용
        if(conn==null){ //없으면 내부 Connection 이용
            isInnerConnection = true;
            conn = JdbcDao.getConnection();
        }
        /* -------------------------------------------------------------- */
        CallableStatement cstmt = null;
        try {
            cstmt = conn.prepareCall(sp_query);
            if (sp_Input != null) {
                for (int i = 0; i < sp_Input.length; i++) {
                    cstmt.setObject(i + 1, sp_Input[i]);
                }
            }
            System.out.println(sp_query);
//            if (sp_Out != null) {
//                int pos = sp_Input.length; // IN 파라미더 이후 OUT 파라미터를 받기 위해 pos 이동
//                for (int i = 0; i < sp_Out.length; i++) {
//                    if (sp_Out[i] instanceof String) {
//                        System.out.println("String Procedue");
//                        cstmt.registerOutParameter(pos + i + 1, Types.VARCHAR);
//                    } else if (sp_Out[i] instanceof Integer) {
//                        System.out.println("Integer Procedue");
//                        cstmt.registerOutParameter(pos + i + 1, Types.INTEGER);
//                    } else if (sp_Out[i] instanceof Date) {
//                        System.out.println("Date Procedue");
//                        cstmt.registerOutParameter(pos + i + 1, Types.DATE);
//                    } else{
//                        System.out.println("Nothing Procedue");
//                    }
//                }
//            }
            result = cstmt.executeUpdate();

            if (sp_Out != null) {
                int pos = sp_Input.length; // IN 파라미터 이후 OUT 파라미터를 받기 위해 pos 이동
                for (int i = 0; i < sp_Out.length; i++) {
                    sp_Out[i] = cstmt.getObject(pos + i + 1);
                }
            }

        } catch (SQLException ex) {
            SOXLog.log(ex);
            throw ex;
        } finally {
            try {
                if (cstmt != null) {
                    //conn.commit();
                    cstmt.close();
                }
            } catch (SQLException ex) {
                SOXLog.log(ex);
                throw ex;
            } finally {
                if (conn != null) {
                    /* -------------------------------------------------------------- */
                    if(isInnerConnection==true)//내부에서 만든 Connection만 제거한다.
                        JdbcDao.endConnection(conn);
                    /* -------------------------------------------------------------- */
                }
            }
        }
        return result;
    }

    /**
     * 주어진 쿼리와 파라미터를 이용하여 CallableStatement 객체를 생성한 후 변경된 레코드 갯수를 리턴한다.
     * @param sp_query 요청된 query
     * @param sp_Input IN 파리미터
     * @return result 변경된 레코드 갯수
     * @throws SQLException
     */
    public static int updateStored(String sp_query, Object[] sp_Input) throws SQLException {
        return updateStored(null, sp_query, sp_Input, null);
    }

    public static int updateStored(String sp_query, Object[] sp_Input, Object[] sp_Out) throws SQLException {
        return updateStored(null, sp_query, sp_Input, sp_Out);
    }

    /**
     * 주어진 쿼리와 파라미터를 이용하여 CallableStatement 객체를 생성한 후 List 타입 객체를 리턴한다.
     * @param sp_query 요청된 query
     * @param sp_Input IN 파리미터
     * @return list List 객체
     * @throws SQLException
     */
    public static List queryForListStored(String sp_query, Object[] sp_Input) throws SQLException {
        return JdbcDao.queryForListStoredOutParam(sp_query, sp_Input, null);
    }

    /**
     * 주어진 쿼리와 파라미터를 이용하여 CallableStatement 객체를 생성한 후 List 타입 객체를 리턴한다.
     * @param sp_query 요청된 query
     * @param sp_Input IN 파리미터
     * @return list List 객체
     * @throws SQLException
     */
    public static List queryForListStoredOutParam(String sp_query, Object[] sp_Input, Object[] sp_Out) throws SQLException {
        if (sp_query == null) {
            throw new SQLException("SQL Query가 NULL입니다.");
        }
        List list = new ArrayList();
        Connection conn = JdbcDao.getConnection();
        CallableStatement cstmt = null;
        ResultSet rs = null;
        try {
            cstmt = conn.prepareCall(sp_query);
            if (sp_Input != null) {
                for (int i = 0; i < sp_Input.length; i++) {
                    cstmt.setObject(i + 1, sp_Input[i]);
                }
            }
            if (sp_Out != null) {
                int pos = sp_Input.length; // IN 파라미더 이후 OUT 파라미터를 받기 위해 pos 이동
                for (int i = 0; i < sp_Out.length; i++) {
                    if (sp_Out[i] instanceof String) {
                        cstmt.registerOutParameter(pos + i + 1, Types.VARCHAR);
                    } else if (sp_Out[i] instanceof Integer) {
                        cstmt.registerOutParameter(pos + i + 1, Types.INTEGER);
                    } else if (sp_Out[i] instanceof Date) {
                        cstmt.registerOutParameter(pos + i + 1, Types.DATE);
                    }
                }
            }

            boolean hasResults = cstmt.execute();
            rs = cstmt.getResultSet();
//            while (rs.next()) {
//                list.add(mapper.mapRow(rs));
//            }
            if (sp_Out != null) {
                int pos = sp_Input.length; // IN 파라미터 이후 OUT 파라미터를 받기 위해 pos 이동
                for (int i = 0; i < sp_Out.length; i++) {
                    sp_Out[i] = cstmt.getObject(pos + i + 1);
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (cstmt != null) {
                    //conn.commit();
                    cstmt.close();
                }
            } catch (SQLException ex) {
                throw ex;
            } finally {
                if (conn != null) {
                    JdbcDao.endConnection(conn);
                }
            }
        }
        return list;
    }

    /**
     * 주어진 쿼리와 파라미터를 이용하여 CallableStatement 객체를 생성한 후 Object 타입 객체를 리턴한다.
     * @param sp_query 요청된 query
     * @param sp_Input IN 파리미터
     * @return list List 객체
     * @throws SQLException
     */
    public static Object queryForObjectStored(String sp_query, Object[] sp_Input) throws SQLException {
        List list = JdbcDao.queryForListStored(sp_query, sp_Input);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 주어진 쿼리를 CallableStatement 객체를 생성한 후 Object 타입 객체를 리턴한다.
     * @param sp_query 요청된 query
     * @return list List 객체
     * @throws SQLException
     */
    public static Object queryForObjectStored(String sp_query) throws SQLException {
        return queryForObjectStored(sp_query, null);
    }

    public static ResultMap queryForMapObjectStored(String sp_query, Object[] sp_Input, Object[] sp_Out) throws SQLException{
        List list = queryForMapListStored(sp_query, sp_Input, sp_Out);
        if(list.size()>0){
            return (ResultMap)list.get(0);
        }else{
            return null;
        }
    }
    public static List queryForMapListStored(String sp_query, Object[] sp_Input, Object[] sp_Out) throws SQLException{
        if (sp_query == null) {
            throw new SQLException("SQL Query가 NULL입니다.");
        }
        List list = new ArrayList();
        Connection conn = JdbcDao.getConnection();
        CallableStatement cstmt = null;
        ResultSet rs = null;
        try {
            cstmt = conn.prepareCall(sp_query);
            if (sp_Input != null) {
                for (int i = 0; i < sp_Input.length; i++) {
                    cstmt.setObject(i + 1, sp_Input[i]);
                }
            }
            if (sp_Out != null) {
                int pos = sp_Input.length; // IN 파라미더 이후 OUT 파라미터를 받기 위해 pos 이동
                for (int i = 0; i < sp_Out.length; i++) {
                    if (sp_Out[i] instanceof String) {
                        cstmt.registerOutParameter(pos + i + 1, Types.VARCHAR);
                    } else if (sp_Out[i] instanceof Integer) {
                        cstmt.registerOutParameter(pos + i + 1, Types.INTEGER);
                    } else if (sp_Out[i] instanceof Date) {
                        cstmt.registerOutParameter(pos + i + 1, Types.DATE);
                    }
                }
            }

            boolean hasResults = cstmt.execute();
            rs = cstmt.getResultSet();
            /*----------------------------------------------------------------*/
            ResultSetMetaData rsMetaData = rs.getMetaData();
            while (rs.next()) {
                ResultMap map = new ResultMap();
                for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                    map.put(rsMetaData.getColumnLabel(i), rs.getObject(i));
                }
                list.add(map);
            }
            /*----------------------------------------------------------------*/
            if (sp_Out != null) {
                int pos = sp_Input.length; // IN 파라미터 이후 OUT 파라미터를 받기 위해 pos 이동
                for (int i = 0; i < sp_Out.length; i++) {
                    sp_Out[i] = cstmt.getObject(pos + i + 1);
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (cstmt != null) {
                    //conn.commit();
                    cstmt.close();
                }
            } catch (SQLException ex) {
                throw ex;
            } finally {
                if (conn != null) {
                    JdbcDao.endConnection(conn);
                }
            }
        }
        return list;
    }
}
