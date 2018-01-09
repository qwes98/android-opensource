package com.example.x240.timemanagement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by One on 2017-09-04.
 * Author : Jiwon Park
 * This include constant and sqlite database.
 */

public class ContactDBCtrct {
    public static final String TBL_CONTACT = "TodoDatabase";
    public static final String COL_DATE = "DATE";                       //format : "yyyy-MM-DD"
    public static final String COL_TAGNAME = "TAGNAME";
    //public static final String COL_TAGCOLOR = "TAGCOLOR";
    public static final String COL_TODO = "TODO";
    public static final String COL_TODOCHECK = "TODOCHECK";
    public static final String COL_EXPSTARTTIME = "EXPSTARTTIME";       //format : "HH:MI"
    public static final String COL_EXPENDTIME = "EXPENDTIME";
    public static final String COL_ACTSTARTTIME = "ACTSTARTTIME";
    public static final String COL_ACTENDTIME = "ACTENDTIME";

    // String sql = "create table if not exists " + tableName + "(_id integer PRIMARY KEY autoincrement, date text, tagName text, tagColor text, todo text, todoCheck text, expectedStartTime text, expectedEndTime text, actualStartTime text, actualEndTime text);";
    // CREATE TABLE IF NOT EXISTS CONTACT_T (NO INTEGER NOT NULL, NAME TEXT, PHONE TEXT, OVER20 INTEGER)
    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_CONTACT + " " +
            "(" +
            COL_DATE + " TEXT" + ", " +                     //Date is separated by sleeping
            COL_TAGNAME + " TEXT" + ", " +
            COL_TODO + " TEXT" + ", " +
            COL_TODOCHECK + " INTEGER" + ", " +             //0 : notstart, 1 : doing, 2 : done, 4 : already exist work, 5 : extra data of todo-list, 6 : memo notdone 7 : memo done
            COL_EXPSTARTTIME + " TEXT" + ", " +
            COL_EXPENDTIME + " TEXT" + ", " +
            COL_ACTSTARTTIME + " TEXT" + ", " +
            COL_ACTENDTIME + " TEXT" +
            ")";

    // DROP TABLE IF EXISTS CONTACT_T
    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_CONTACT;

    // SELECT * FROM CONTACT_T \
    public static final String SQL_SELECT = "SELECT * FROM " + TBL_CONTACT;

    // INSERT OR REPLACE INTO CONTACT_T (NO, NAME, PHONE, OVER20) VALUES (x, x, x, x)
    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_CONTACT + " " +
            "(" + COL_DATE + ", " + COL_TAGNAME + ", " + COL_TODO + ", " + COL_TODOCHECK + ", " + COL_EXPSTARTTIME + ", " +
            COL_EXPENDTIME + ", " + COL_ACTSTARTTIME + ", " + COL_ACTENDTIME + ") VALUES ";

    // DELETE FROM CONTACT_T
    public static final String SQL_DELETE = "DELETE FROM " + TBL_CONTACT;

    // Change date based on this value
    public static final int dateChangeHour = 4;

    // Tag-Color Map
    public static Map<String, String> tagMap = new HashMap<String, String>() {
        {
            put("Study", "#9E96FF");            //pupple
            put("Self Development", "#ADD8E6");      //skyblue
            put("Exercise", "#EC776E");         //orange
            put("Leisure", "#9ACD32");          //yellowgreen
            put("Breathe", "#FAD16A");          //yellow
            put("Napping", "#45464B");         //gray
            put("NotRecord", "#FFFFFF");        //white
            put("Sleeping", "#45464B");         //gray
            put("Memo", "#4682B4");              //blue
        }
    };

    public static Map<String, String> tagMapForBackground = new HashMap<String, String>() {
        {
            put("Study", "#559E96FF");            //pupple
            put("Self Development", "#77ADD8E6");      //skyblue
            put("Exercise", "#55EC776E");         //orange
            put("Leisure", "#559ACD32");          //yellowgreen
            put("Breathe", "#55FAD16A");          //yellow
            put("Napping", "#DD45464B");         //gray
            put("NotRecord", "#FFFFFF");        //white
            put("Sleeping", "#DD45464B");         //gray
            put("Memo", "#554682B4");              //blue
        }
    };

}
