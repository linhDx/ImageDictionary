package com.linhdx.imagedictionary.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shine on 31/10/2016.
 */

public class SQLiteHelper extends BaseSQLiteHelper {
    private SQLiteDatabase sqLiteDatabase;
    public SQLiteHelper(Context context) {
        super(context);
    }

    @Override
    public boolean openDatabase(String dbname) {
        File file = new File(context.getFilesDir()+"/"+dbname);
        if (!file.exists()) {
            //Try to get db from assets foder
            copyDataBase(dbname);
        }
        sqLiteDatabase = SQLiteDatabase.openDatabase(file.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        return sqLiteDatabase.isOpen();
    }

    @Override
    public Cursor queryAll(String tableName) {
        return sqLiteDatabase.query(tableName, null, null, null, null, null, null);
    }
    public Cursor queryAll(String tableName, String fieldOrder, String typeOrder) {
        return sqLiteDatabase.query(tableName, null, null, null, null, null, fieldOrder+" "+typeOrder);
    }
    @Override
    public Cursor queryRandom(String tableName, int amount) {
        return sqLiteDatabase.query(tableName, null, null, null, null, null, "RANDOM()", String.valueOf(amount));
    }

    @Override
    public void insert(String tableName, ContentValues values) {
        sqLiteDatabase.insert(tableName, null, values);
    }

    public List getLikeWord(String tableName, String where, String word, int limit) {
        Cursor c= sqLiteDatabase.rawQuery("SELECT "+where+" AS _id, "+where+" FROM "+tableName+" WHERE "+where+" LIKE \""+word+"%\" LIMIT ?", new String[]{ String.valueOf(limit)});
        List<String> list = new ArrayList<>();
        while (c.moveToNext()){
            list.add(c.getString(c.getColumnIndex(where)));
        }
        c.close();
        return list;
    }

    @Override
    public Cursor getOneRow(String tableName, String column, String arg) {
        return sqLiteDatabase.query(tableName, null, column+"=?", arg==null?null:new String[]{arg}, null, null, null);
    }

    @Override
    public SQLiteDatabase getSQLiteDatabase() {
        return sqLiteDatabase;
    }

    public void createHistoryTable(){
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS history(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "word TEXT NOT NULL UNIQUE, phonetic TEXT, summary TEXT, mean TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }
}
