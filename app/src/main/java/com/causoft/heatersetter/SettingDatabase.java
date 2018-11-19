package com.causoft.heatersetter;

import android.provider.BaseColumns;

public final class SettingDatabase {

    public static final class CreateDB implements BaseColumns {
        public static final String USERID = "userid";
        public static final String IP = "ip";
        public static final String _TABLENAME0 = "settingtable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +USERID+" text not null);";
    }
}