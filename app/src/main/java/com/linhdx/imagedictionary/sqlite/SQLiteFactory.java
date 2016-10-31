package com.linhdx.imagedictionary.sqlite;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shine on 31/10/2016.
 */

public class SQLiteFactory {
    private static Map<String, BaseSQLiteHelper> map = new HashMap<>();
    public static BaseSQLiteHelper getSQLiteHelper(Context context, String dbname) {
        SQLiteHelper sqLiteHelper = (SQLiteHelper) map.get(dbname);
        if(sqLiteHelper == null) {
            sqLiteHelper = new SQLiteHelper(context);
            map.put(dbname, sqLiteHelper);
        }
        return sqLiteHelper;
    }
}
