package org.blogsite.youngsoft.piggybank.db;

import android.provider.BaseColumns;

// DataBase Table
public final class DataBases {

    public static final class CreateDB implements BaseColumns{
        public static final String timestamp = "timestamp";
        public static final String second = "second";
        public static final String minute = "minute";
        public static final String hour = "hour";
        public static final String day = "day";
        public static final String month = "month";
        public static final String year = "year";
        public static final String amount = "amount";
        public static final String category = "category";
        public static final String card = "card";
        public static final String data = "data";

        public static final String username = "username";
        public static final String useremail = "useremail";
        public static final String photourl = "photourl";
        public static final String settings = "settings";

        public static final String _DATA_TABLENAME = "smstable";
        public static final String _SETTINGS_TABLENAME = "smssetting";
        public static final String _DONATION_INFO = "donation_info";
        public static final String _DONATION_HISTORY = "donation_history";
        public static final String _CATEGORY_SERVICE = "category_service";

        public static final String _CREATE_DATATABLE = "CREATE TABLE " + _DATA_TABLENAME + "("
                + "_id INTEGER PRIMARY KEY autoincrement, "
                + "timestamp INTEGER NOT null,"
                + "second INTEGER not null, "
                + "minute INTEGER not null, "
                + "hour INTEGER not null, "
                + "day INTEGER not null, "
                + "month INTEGER not null, "
                + "year INTEGER not null, "
                + "amount INTEGER not null, "
                + "category TEXT not null, "
                + "card TEXT not null, "
                + "data VARCHAR(1024) not null);";

        public static final String _CREATE_SETTINGTABLE = "CREATE TABLE " + _SETTINGS_TABLENAME + "("
                + "_id INTEGER PRIMARY KEY autoincrement, "
                + "photourl TEXT not null, "
                + "username TEXT not null, "
                + "useremail TEXT not null, "
                + "settings VARCHAR(3072) not null);";

        public static final String _CREATE_DONATION_INFO = "CREATE TABLE " + _DONATION_INFO + "("
                + "_id INTEGER PRIMARY KEY autoincrement, "
                + "card TEXT not null, "
                + "category TEXT not null, "
                + "threshold INTEGER not null, "
                + "percent INTEGER not null, "
                + "thresholdoverall INTEGER not null);";

        public static final String _CREATE_DONATION_HISTORY = "CREATE TABLE " + _DONATION_HISTORY + " ( "
                + "_id INTEGER PRIMARY KEY autoincrement, "
                + "year INTEGER not null, "
                + "month INTEGER not null, "
                + "day INTEGER not null, "
                + "amount INTEGER not null, "
                + "donation VARCHAR ( 1024 ) not null, "
                + "account TEXT not null, "
                + "bankname TEXT not null, "
                + "home VARCHAR ( 1024 ) not null, "
                + "tel TEXT not null, "
                + "address VARCHAR ( 1024 ) not null, "
                + "category TEXT not null);";

        public static final String _CREATE_CATEGORY_SERVICE = "CREATE TABLE " + _CATEGORY_SERVICE + " ( "
                + "_id INTEGER PRIMARY KEY autoincrement, "
                + "timestamp INTEGER NOT NULL, "
                + "hash TEXT NOT NULL);";
    }
}

