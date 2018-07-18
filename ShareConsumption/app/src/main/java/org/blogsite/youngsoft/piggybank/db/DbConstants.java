/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blogsite.youngsoft.piggybank.db;

import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

/**
 *
 * @author klee
 */
public class DbConstants {

    public static final String DB_PATH = SmsUtils.getDevicePath() + "/PiggyBank/";
    public static final String DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String DB_NAME = DB_PATH + "PiggyBank";
    public static final String CONNECTION_URL = "jdbc:derby:" + DB_NAME + ";create=true";
    public static final String DB_TABLE = "sms";

    public static final String SQL_CREATE_Table = "create table " + DB_TABLE + " ( "
            + "id integer primary key autoincrement,"
            + "timestamp  BIGINT not null, "
            + "ss INTEGER not null, "
            + "mm INTEGER not null, "
            + "hh INTEGER not null, "
            + "dddd INTEGER not null, "
            + "mmmm INTEGER not null, "
            + "yyyy INTEGER not null, "
            + "amount INTEGER not null, "
            + "category  TEXT not null, "
            + "card  TEXT not null, "
            + "data TEXT not null, "
            + "primary key (ID) )";


    public static final String SQL_SELECT_ALL = "SELECT * FROM " + DB_TABLE + " ORDER BY timestamp DESC";
}
