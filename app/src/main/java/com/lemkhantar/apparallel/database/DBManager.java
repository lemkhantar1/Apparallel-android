package com.lemkhantar.apparallel.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lemkhantar.apparallel.entity.Tache;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper
{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "apparallel";

    private static final String TABLE_TACHE = "tache";
    private static final String TACHE_ID = "_id";
    private static final String TACHE_TITRE = "_titre";
    private static final String TACHE_DUREE = "_duree";
    private static final String TACHE_TEMPSECOULE = "_tempsecoule";


    private static final String TABLE_TACHE_ACTIVE = "tacheactive";
    private static final String TACHE_ACTIVE_ID = "_id";

    private static String DB_PATH = "/data/data/com.lemkhantar.apparallel/databases/";
    private static String DB_FILE_NAME = "apparallel.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;


    //////////////--FONCTIONS DU DB-MANAGER--//////////////

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        try {
            createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if(!dbExist)
        {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try
        {
            String myPath = DB_PATH + DB_FILE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }
        catch(Exception e){}
        if(checkDB != null){checkDB.close();}

        return checkDB != null ? true : false;
    }
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_FILE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_FILE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }
    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_FILE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        myDataBase.enableWriteAheadLogging();

    }
    @Override public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }
    @Override public void onCreate(SQLiteDatabase db) {}
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    //////////////--FIN FONCTIONS DU DB-MANAGER--//////////////


    /////////////-- FONCTION TACHE--//////////////
    public Tache getTacheById(int idTache) {
        Cursor cursor = myDataBase.query(TABLE_TACHE, new String[]{TACHE_ID,
                        TACHE_TITRE, TACHE_DUREE, TACHE_TEMPSECOULE}, TACHE_ID + "=?",
                new String[]{String.valueOf(idTache)}, null, null, null, null);
        if (cursor.getCount()>0)
        {
            cursor.moveToFirst();



            Tache tache = new Tache(
                    cursor.getInt(cursor.getColumnIndex(TACHE_ID)),
                    cursor.getString(cursor.getColumnIndex(TACHE_TITRE)),
                    cursor.getInt(cursor.getColumnIndex(TACHE_DUREE)),
                    cursor.getInt(cursor.getColumnIndex(TACHE_TEMPSECOULE))
            );
            return tache;
        }
        else
            return null;
    }
    public void ajouterTache(Tache tache) {
        ContentValues values = new ContentValues();
        values.put(TACHE_TITRE, tache.get_titre());
        values.put(TACHE_DUREE, tache.get_duree());
        values.put(TACHE_TEMPSECOULE, tache.get_tempsecoule());
        myDataBase.insert(TABLE_TACHE, null, values);
    }
    public ArrayList<Tache> getAllTache() {
        ArrayList<Tache> taches = new ArrayList<Tache>();
        String selectQuery = "SELECT * FROM tache ";

        Cursor cursor = myDataBase.rawQuery(selectQuery, null);

        if(cursor.getCount()>0)
        {
            if (cursor.moveToFirst()) {
                do {


                    Tache tache = new Tache();
                    tache.set_id(cursor.getInt(cursor.getColumnIndex(TACHE_ID)));
                    tache.set_titre(cursor.getString(cursor.getColumnIndex(TACHE_TITRE)));
                    tache.set_duree(cursor.getInt(cursor.getColumnIndex(TACHE_DUREE)));
                    tache.set_tempsecoule(cursor.getInt(cursor.getColumnIndex(TACHE_TEMPSECOULE)));
                    taches.add(tache);
                } while (cursor.moveToNext());
            }
        }
        return taches;
    }
    public void deleteTache(int idTache) {
        String strSQL = "DELETE FROM "+TABLE_TACHE+" WHERE "+TACHE_ID+" = "+ idTache;
        myDataBase.execSQL(strSQL);

    }
    public void incrementerTache(int idTache) {
        String strSQL = "update "+TABLE_TACHE+" set "+TACHE_TEMPSECOULE+" = "+(getTacheById(idTache).get_tempsecoule()+5)+" where "+TACHE_ID+" = "+idTache;
        myDataBase.execSQL(strSQL);
    }
    public void reinitialiserTache(int idTache) {
            String strSQL = "update "+TABLE_TACHE+" set "+TACHE_TEMPSECOULE+" = 0 where "+TACHE_ID+" = "+idTache;
            myDataBase.execSQL(strSQL);
    }
    public void updateTacheActive(int idTacheActive) {
        String strSQL = "update "+TABLE_TACHE_ACTIVE+" set "+TACHE_ACTIVE_ID+" = "+idTacheActive;
        myDataBase.execSQL(strSQL);
    }
    public int getTacheActive() {
        Cursor cursor = myDataBase.query(TABLE_TACHE_ACTIVE, new String[]{TACHE_ACTIVE_ID,}, null,null, null, null, null, null);
        if (cursor.getCount()>0)
        {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(TACHE_ACTIVE_ID));
        }
        else
            return -1;
    }
    /////////////-- FIN FONCTION TACHE--////////////

}
