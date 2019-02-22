package com.wafwaf.wafwaf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wafwaf.wafwaf.Model.AV;
import com.wafwaf.wafwaf.Model.Account;
import com.wafwaf.wafwaf.Model.Attack;


import java.util.ArrayList;
import java.util.List;






 interface IDatabaseHandler {

     public boolean addAccount(String name, String apiKey);
     public boolean deleteAccount(String name);
     public List<Account> getAllAccount();
      String getApiKey(String name);

    public int getLastAttackTime(String accountName);
     public boolean addAttack(String IP, String country, long startTime, long endTime,  String resultTypes, String accountName);
    public List<Attack> getAllAttackByTime(long startTime);
    public List<Attack> getAllAccountAttack(String accountName);
    public boolean deleteAttack( String accountName);

    public int getLastAVTime(String accountName);
    public boolean addAV(long eventTime, String eventType, String fileName, String fileExt, String filePath, String suspicionType, String suspicionDescription, String AccountName);
    public List<AV> getAllAVByTime(long startTime);
    public List<AV> getAllAccountAV(String accountName);
    public boolean deleteAV(String accountName);

}

public class DatabaseHandler extends SQLiteOpenHelper implements IDatabaseHandler {


    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "wafDB";
    private static final String TABLE_ATTACK = "attack";
    private static final String TABLE_AV = "av";
    private static final String TABLE_ACCOUNT = "account";

    public DatabaseHandler(Context context){
        super(context, DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_ACCOUNT_TABLE = "CREATE TABLE "+ TABLE_ACCOUNT
                +"( Name TEXT UNIQUE NOT NULL, ApiKey TEXT UNIQUE NOT NULL)";
        db.execSQL(CREATE_ACCOUNT_TABLE);
        String CREATE_ATTACK_TABLE = "CREATE TABLE "+ TABLE_ATTACK
                +"( IpAddr TEXT NOT NULL, Country TEXT NOT NULL, StartTime INTEGER NOT NULL, EndTime INTEGER NOT NULL,  ResultTypes TEXT NOT NULL, Account TEXT NOT NULL)";
        db.execSQL(CREATE_ATTACK_TABLE);
        String CREATE_AV_TABLE = "CREATE TABLE "+ TABLE_AV
                +"( EventTime INTEGER NOT NULL, EventType TEXT NOT NULL, FileName TEXT NOT NULL, FileExt TEXT NOT NULL, FilePath TEXT NOT NULL, SuspicionType TEXT NOT NULL, SuspicionDescription TEXT NOT NULL, Account TEXT NOT NULL)";
        db.execSQL(CREATE_AV_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        onCreate(db);
    }

    @Override
    public boolean addAccount(String name, String apiKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name",name);
        values.put("ApiKey", apiKey);

        try{
            db.insertOrThrow(TABLE_ACCOUNT, null, values);
        }catch (Throwable e){
            e.printStackTrace();
            db.close();
            return false;
        }

        db.close();
        return true;
    }

    @Override
    public boolean deleteAccount(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_ACCOUNT, " Name = ?",new String[]{name});
        }catch (Throwable e){
            e.fillInStackTrace();
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    @Override
    public List<Account> getAllAccount() {
        List<Account> accountList = new ArrayList<>();
        //String query = "SELECT * FROM " + TABLE_ATTACK;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ACCOUNT;
        //Cursor cursor =  db.rawQuery( query , null);
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Account account = new com.wafwaf.wafwaf.Model.Account();
                    account.setName( cursor.getString(0));
                    account.setApiKey( cursor.getString(1));

                    accountList.add(account);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Throwable e){
            e.fillInStackTrace();
            db.close();
            return null;
        }
        db.close();
        return accountList;
    }


    @Override
    public String getApiKey(String nameAccount) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ApiKey FROM " + TABLE_ACCOUNT + " WHERE Name=?";

        try {
            Cursor cursor = db.rawQuery(query, new String[]{nameAccount});
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("ApiKey"));
            }
            cursor.close();
        }catch (Throwable e){
            e.fillInStackTrace();
            db.close();
            return null;
        }
        db.close();
        return null;
    }

    @Override
    public int getLastAttackTime(String accountName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ATTACK,new String[]{"MAX(EndTime)"},"Account =?",new String[]{accountName},null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        //Integer.parseInt(cursor.getString(0))
        String result = cursor.getString(0);
        if (result!=null){
            db.close();
            return Integer.parseInt(result);
        }

        db.close();
        return 0;
    }

    @Override
    public boolean addAttack(String IP, String country, long startTime, long endTime,  String resultTypes, String account) {
        /*IpAddr TEXT NOT NULL, Country TEXT NOT NULL, StartTime INTEGER NOT NULL, EndTime INTEGER NOT NULL, TypeTrace TEXT NOT NULL, ResultTypes TEXT NOT NULL, Account TEXT NOT NULL*/
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("IpAddr",IP);
        values.put("Country", country);
        values.put("StartTime", startTime);
        values.put("EndTime", endTime);
        values.put("ResultTypes", resultTypes);
        values.put("Account", account);
        try{
            db.insertOrThrow(TABLE_ATTACK, null, values);
        }catch (Throwable e){
            e.fillInStackTrace();
            db.close();
            return false;
        }

        db.close();
        return true;
    }

    @Override
    public List<Attack> getAllAttackByTime(long startTime) {
        List<Attack> attackList = new ArrayList<Attack>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_ATTACK, new String[]{"IpAddr", "Country", "StartTime", "EndTime",  "ResultTypes","Account"}, "  EndTime>=?", new String[]{String.valueOf(startTime)}, null, null, "EndTime DESC", null);

            if (cursor.moveToFirst()) {
                do {
                    Attack attack = new Attack();
                    attack.setIp( cursor.getString(0));
                    attack.setCountry( cursor.getString(1));
                    attack.setStartAttackTime(Long.parseLong(cursor.getString(2)));
                    attack.setEndAttackTime(Long.parseLong(cursor.getString(3)));
                    attack.setTypes(cursor.getString(4));
                    attack.setImgId(cursor.getString(4));
                    attack.setAccount(cursor.getString(5));
                    attack.setApiKey(getApiKey(attack.getAccount()));
                    attackList.add(attack);
                } while (cursor.moveToNext());
            }
        }catch (Throwable e){
            e.fillInStackTrace();
            db.close();
            return null;
        }
        db.close();
        return attackList;
    }

    @Override
    public List<Attack> getAllAccountAttack(String account) {
        List<Attack> attackList = new ArrayList<Attack>();
        //String query = "SELECT * FROM " + TABLE_ATTACK;

        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor =  db.rawQuery( query , null);
        try {
            Cursor cursor = db.query(TABLE_ATTACK, new String[]{"IpAddr", "Country", "StartTime", "EndTime",  "ResultTypes","Account"}, " Account = ?", new String[]{account}, null, null, "EndTime DESC", null);

            if (cursor.moveToFirst()) {
                do {
                    Attack attack = new Attack();
                    attack.setIp(cursor.getString(0));
                    attack.setCountry( cursor.getString(1));
                    attack.setStartAttackTime(Long.parseLong(cursor.getString(2)));
                    attack.setEndAttackTime(Long.parseLong(cursor.getString(3)));
                    attack.setTypes(cursor.getString(4));
                    attack.setImgId(cursor.getString(4));
                    attack.setAccount(cursor.getString(5));
                    attack.setApiKey(getApiKey(attack.getAccount()));
                    attackList.add(attack);
                } while (cursor.moveToNext());
            }
        }catch (Throwable e){
            e.fillInStackTrace();
            db.close();
            return null;
        }
        db.close();
        return attackList;
    }

    @Override
    public boolean deleteAttack( String account) {
            SQLiteDatabase db = this.getWritableDatabase();
            try {
                db.delete(TABLE_ATTACK, " Account = ?",new String[]{ account});
            }catch (Throwable e){
                e.fillInStackTrace();
                db.close();
                return false;
            }
            db.close();
            return true;
    }

    @Override
    public int getLastAVTime(String accountName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_AV,new String[]{"MAX(EventTime)"},"Account =?",new String[]{accountName},null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
        }String result = cursor.getString(0);
        if (result!=null){
            db.close();
            return Integer.parseInt(result);
        }

        db.close();
        return 0;
    }

    @Override
    public boolean addAV(long eventTime, String eventType, String fileName, String fileExt, String filePath, String suspicionType, String suspicionDescription, String account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("EventTime",eventTime);
        values.put("EventType", eventType);
        values.put("FileName", fileName);
        values.put("FileExt", fileExt);
        values.put("FilePath", filePath);
        values.put("SuspicionType", suspicionType);
        values.put("SuspicionDescription", suspicionDescription);
        values.put("Account", account);
        try {
            db.insertOrThrow(TABLE_AV, null, values);
        }catch (Throwable e){
            e.fillInStackTrace();
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    @Override
    public List<AV> getAllAVByTime(long startTime) {
        List<AV> avList = new ArrayList<AV>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_AV, new String[]{ "EventTime", "EventType", "FileName", "FileExt", "FilePath", "SuspicionType","SuspicionDescription", "Account"}, "EventTime>=?" , new String[]{ String.valueOf(startTime)},null,null,"EventTime DESC",null );

            if (cursor.moveToFirst()) {
                do {
                    AV av = new AV();
                    av.setEventTime( Long.parseLong(cursor.getString(0)));
                    av.setEventType( cursor.getString(1));
                    av.setFileName( cursor.getString(2));
                    av.setDescription(cursor.getString(5) + " " + cursor.getString(6));
                    av.setImgId(cursor.getString(1));
                    av.setAccount( cursor.getString(7));
                    avList.add(av);
                } while (cursor.moveToNext());
            }
        }catch (Throwable e){
            e.fillInStackTrace();
            db.close();
            return null;
        }
        db.close();
        return avList;
    }

    @Override
    public List<AV> getAllAccountAV(String account) {
        List<AV> avList = new ArrayList<AV>();
        //String query = "SELECT * FROM " + TABLE_AV;
/* EventTime INTEGER, EventType TEXT, FileName TEXT, FileExt TEXT, FilePath TEXT, SuspicionType TEXT, SuspicionDescription TEXT*/
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(TABLE_AV, new String[]{ "EventTime", "EventType", "FileName", "FileExt", "FilePath", "SuspicionType","SuspicionDescription", "Account"}, " Account=?",new String[]{ account},null,null,"EventTime DESC",null );

            if (cursor.moveToFirst()) {
                do {
                    AV av = new AV();
                    av.setEventTime(Long.parseLong(cursor.getString(0)));
                    av.setEventType( cursor.getString(1));
                    av.setFileName( cursor.getString(2));
                    av.setDescription( cursor.getString(5) + " " + cursor.getString(6));
                    av.setImgId(cursor.getString(1));
                    av.setAccount( cursor.getString(7));
                    avList.add(av);
                } while (cursor.moveToNext());
            }
        }catch (Throwable e){
            e.fillInStackTrace();
            db.close();
            return null;
        }
        db.close();
        return avList;
    }

    @Override
    public boolean deleteAV( String account) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_AV, " Account=?", new String[]{ account});
            db.close();
        }catch (Throwable e){
            e.fillInStackTrace();
            db.close();
            return false;
        }
        db.close();
        return true;
    }


}
