package com.ithows.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author dreamct
 */
public interface JdbcTransactor {
    void doTransaction(Connection conn)  throws SQLException;
}