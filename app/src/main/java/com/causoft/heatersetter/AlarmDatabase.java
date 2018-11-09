package com.causoft.heatersetter;

import android.provider.BaseColumns;

public final class AlarmDatabase {

    public static final class CreateDB implements BaseColumns {
        public static final String USERID = "userid";
        public static final String TIME = "time";
        public static final String REPEAT = "repeat";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +USERID+" text not null , "
                +TIME+" text not null , "
                +REPEAT+" integer not null );";
    }
}