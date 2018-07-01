package com.moloxor.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NotesDB extends SQLiteOpenHelper {
    private static final String TAG = "NotesDB";

    private static final String DB_NAME = "db_notes";
    private static final int DB_VERSION = 1;

    private static final String NOTES_TABLE_NAME = "tbl_notes";

    private static final String COL_ID = "col_id";
    private static final String COL_TITLE = "col_title";
    private static final String COL_CONTENT = "col_content";

    private static final String SQL_COMMAND_CREATE_POST_TABLE = "CREATE TABLE IF NOT EXISTS " + NOTES_TABLE_NAME +
            "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TITLE + " TEXT, " + COL_CONTENT + " TEXT);";


    private Context context;


    public NotesDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(SQL_COMMAND_CREATE_POST_TABLE);
        } catch (SQLException e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addNote(Note note) {

        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, note.getTitle());
        cv.put(COL_CONTENT, note.getContent());

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long isInserted = sqLiteDatabase.insert(NOTES_TABLE_NAME, null, cv);
        sqLiteDatabase.close();
        Log.i(TAG, "addNote: " + isInserted);
    }

    public List<Note> getNotes() {

        List<Note> notes = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + NOTES_TABLE_NAME, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Note note = new Note();
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                notes.add(note);
                cursor.moveToNext();
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return notes;
    }

    public void updateNote(String lastTitle, String title, String content) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_CONTENT, content);
        sqLiteDatabase.update(NOTES_TABLE_NAME, cv, COL_TITLE + " = ? ", new String[]{lastTitle});
        sqLiteDatabase.close();
    }

    public void deleteNote(String title) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(NOTES_TABLE_NAME, COL_TITLE + " = ? ", new String[]{title});
        sqLiteDatabase.close();

    }

    public void deleteAll() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(NOTES_TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }

}
