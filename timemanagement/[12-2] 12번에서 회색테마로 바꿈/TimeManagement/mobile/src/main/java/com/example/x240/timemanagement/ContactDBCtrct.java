package com.example.x240.timemanagement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by X240 on 2017-09-04.
 */

public class ContactDBCtrct {
    public static final String TBL_CONTACT = "TodoDatabase";
    public static final String COL_DATE = "DATE";                       //"yyyy-MM-DD"
    public static final String COL_TAGNAME = "TAGNAME";
    //public static final String COL_TAGCOLOR = "TAGCOLOR";
    public static final String COL_TODO = "TODO";
    public static final String COL_TODOCHECK = "TODOCHECK";
    public static final String COL_EXPSTARTTIME = "EXPSTARTTIME";       //"HH:MI"
    public static final String COL_EXPENDTIME = "EXPENDTIME";
    public static final String COL_ACTSTARTTIME = "ACTSTARTTIME";
    public static final String COL_ACTENDTIME = "ACTENDTIME";

    //String sql = "create table if not exists " + tableName + "(_id integer PRIMARY KEY autoincrement, date text, tagName text, tagColor text, todo text, todoCheck text, expectedStartTime text, expectedEndTime text, actualStartTime text, actualEndTime text);";
    // CREATE TABLE IF NOT EXISTS CONTACT_T (NO INTEGER NOT NULL, NAME TEXT, PHONE TEXT, OVER20 INTEGER)
    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_CONTACT + " " +
            "(" +
            COL_DATE + " TEXT" + ", " +                     //DATE는 todo리스트를 하는 날짜, 즉 3일의 일로 되어있는것은 시간상으로는 4일 새벽에 하는 일이여도 DATE가 3일로 되어있음
            COL_TAGNAME + " TEXT" + ", " +
            COL_TODO + " TEXT" + ", " +
            COL_TODOCHECK + " INTEGER" + ", " +             //0 : notstart, 1 : doing, 2 : done,      4: etc일을 한 경우 달성도가 없음. 이 숫자는 추후 etc일을 제외시킬때 사용, 5 : 진행 중인 일에 대해 새로 추가된 데이터들
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

    //Tag-Color Map
    public static Map<String, String> tagMap = new HashMap<String, String>() {
        {
            put("Study", "#4682B4");            //blue
            put("Self Development", "#ADD8E6");      //skyblue
            put("Exercise", "#FF4500");         //orange
            put("Leisure", "#9ACD32");          //yellowgreen
            put("Breathe", "#D2691E");          //chocolate
            put("Napping", "#A9A9A9");         //gray
            put("NotRecord", "#FFFFFF");        //white
            put("Sleeping", "#A9A9A9");         //gray
        }
    };

    /*
    private ContactDBCtrct() {
        tagMap.put("Study", "#4682B4");            //blue
        tagMap.put("Self Development", "#ADD8E6");      //skyblue
        tagMap.put("Exercise", "#FF4500");         //orange
        tagMap.put("Leisure", "#9ACD32");          //yellowgreen
        tagMap.put("Breathe", "#D2691E");          //chocolate
        tagMap.put("Napping", "#A9A9A9");         //gray
        tagMap.put("NotRecord", "#FFFFFF");        //white
        tagMap.put("Sleeping", "#A9A9A9");         //gray
        //System.out.println(tagMap.get("dog"));
    }
    */

}
