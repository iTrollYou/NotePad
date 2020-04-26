package com.das.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "NotePad.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Columnas de la tabla 'Note'
    private static final String NOTE_TABLE_NAME = "Note";
    private static final String NOTE_KEY_ID = "id";
    private static final String NOTE_KEY_TITLE = "title";
    private static final String NOTE_KEY_CONTENT = "content";
    private static final String NOTE_KEY_DATE = "date";
    private static final String NOTE_KEY_TIME = "time";

    private SQLiteDatabase db = this.getWritableDatabase();

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Hacemos la sentencia sql de la creación de la tabla.
        String createNoteTable = "CREATE TABLE " + NOTE_TABLE_NAME + " (" +
                NOTE_KEY_ID + " INTEGER PRIMARY KEY," +
                NOTE_KEY_TITLE + " TEXT," +
                NOTE_KEY_CONTENT + " TEXT," +
                NOTE_KEY_DATE + " TEXT," +
                NOTE_KEY_TIME + " TEXT"
                + " )";

        db.execSQL(createNoteTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion)
            return;
        db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE_NAME);
        onCreate(db);
    }

    /**
     * @param note Clase Note
     * @return True, si la introducción se ha realizado correctamente.
     * False, si ha ocurrido algún fallo.
     */
    boolean addNote(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTE_KEY_TITLE, note.getTitle());
        contentValues.put(NOTE_KEY_CONTENT, note.getDescription());
        contentValues.put(NOTE_KEY_DATE, note.getDate());
        contentValues.put(NOTE_KEY_TIME, note.getTime());
        return db.insert(NOTE_TABLE_NAME, null, contentValues) != -1;

    }

    /**
     * @return Lista con la información de todas las notas de la BD.
     */
    public List<Note> getAllNotes() {
        List<Note> allNotes = new ArrayList<>();
        String query = "SELECT * FROM " + NOTE_TABLE_NAME + " ORDER BY " + NOTE_KEY_ID + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Note note = new Note();
            note.setId(Integer.parseInt(cursor.getString(0)));
            note.setTitle(cursor.getString(1));
            note.setDescription(cursor.getString(2));
            note.setDate(cursor.getString(3));
            note.setTime(cursor.getString(4));
            allNotes.add(note);
        }
        return allNotes;

    }

    /**
     * @param id Identificador de Note.
     * @return Clase Note con ese identificador.
     */
    public Note getNote(int id) {
        String[] query = new String[]{NOTE_KEY_ID, NOTE_KEY_TITLE, NOTE_KEY_CONTENT, NOTE_KEY_DATE,
                NOTE_KEY_TIME};
        Cursor cursor = db.query(NOTE_TABLE_NAME, query, NOTE_KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new Note(
                Integer.parseInt(Objects.requireNonNull(cursor).getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4));
    }

    /**
     * @param id Identificador de Note.
     */
    public void deleteNote(int id) {
        db.delete(NOTE_TABLE_NAME, NOTE_KEY_ID + "=?",
                new String[]{String.valueOf(id)});
    }


    /**
     * @param note Clase Note
     * @return True, si la actualizaciónSe se ha realizado correctamente.
     * False, si ha ocurrido algún fallo.
     */
    public boolean editNote(Note note) {

        ContentValues c = new ContentValues();
        c.put(NOTE_KEY_TITLE, note.getTitle());
        c.put(NOTE_KEY_CONTENT, note.getDescription());
        c.put(NOTE_KEY_DATE, note.getDate());
        c.put(NOTE_KEY_TIME, note.getTime());


        int dev = db.update(NOTE_TABLE_NAME, c, NOTE_KEY_ID + "=?",
                new String[]{String.valueOf(note.getId())});
        return dev == 1;
    }
}
