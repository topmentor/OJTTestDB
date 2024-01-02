package com.saysay;

import com.ithows.db.JdbcDao;
import com.ithows.db.SqliteDao;

import java.sql.*;
import java.util.*;

public class CommonDAO {

    public static int countAllUser() {
        String query = "select count(*) from user ";

        try {
            return SqliteDao.queryForInt(query, new Object[]{});
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean updateUserPassword(String userId, String newPass)  {

        try {
            SqliteDao.update("UPDATE user SET userPassword = ?  WHERE  userId=? ; ", new Object[]{newPass, userId});
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean deleteUser(String userId)  {

        try {
            SqliteDao.update("DELETE FROM user WHERE userId=? ; ", new Object[]{userId});
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List getAllUser() {
        String query = "select * from user ";

        try {
            return SqliteDao.queryForMapList(query, new Object[]{});
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static int insertUser(String userId, String userPass)  {

        int res = 0;
        try {
            SqliteDao.update("INSERT INTO user (  " +
                    "					userId," +
                    "					userPassword," +
                    "					registerTime) " +
                    "				  VALUES (" +
                    "					?, " +
                    "					?," +
                    "					now()" +
                    "             ); ", new Object[]{userId, userPass});

        }catch (Exception ex){
            ex.printStackTrace();
            res = -1;
        }
        return res;
    }

}
