package com.example.x240.timemanagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by One on 2017-09-04.
 * Author : Jiwon Park
 */

public class ContactDBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1 ;
    public static final String DBFILE_CONTACT = "TodoDatabase.db" ;

    public ContactDBHelper(Context context) {
        super(context, DBFILE_CONTACT, null, DB_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ContactDBCtrct.SQL_CREATE_TBL) ;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL(ContactDBCtrct.SQL_DROP_TBL) ;
        // onCreate(db) ;
        }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // onUpgrade(db, oldVersion, newVersion);
        }

}
