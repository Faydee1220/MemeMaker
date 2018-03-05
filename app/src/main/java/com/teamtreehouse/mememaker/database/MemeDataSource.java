package com.teamtreehouse.mememaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.teamtreehouse.mememaker.models.Meme;
import com.teamtreehouse.mememaker.models.MemeAnnotation;

import java.util.ArrayList;

public class MemeDataSource {

    private Context mContext;
    private MemeSQLiteHelper memeSQLiteHelper;

    public MemeDataSource(Context context) {
        mContext = context;
        memeSQLiteHelper = new MemeSQLiteHelper(context);
//        SQLiteDatabase database = memeSQLiteHelper.getReadableDatabase();
//        database.close();
    }

    private SQLiteDatabase open() {
        return memeSQLiteHelper.getWritableDatabase();
    }

    private void close(SQLiteDatabase database) {
        database.close();
    }

    public ArrayList<Meme> read() {
        ArrayList<Meme> memes = readMemes();
        addMemeAnnotations(memes);
        return memes;
    }

    public ArrayList<Meme> readMemes() {
        SQLiteDatabase database = open();

        // A cursor is a pointer to the result set from a database query.
        // It provides read access to the rows and columns.
        // Android uses cursor class as a return value for queries.
        // Using Cursor enables android to more efficiently manage rows and columns as needed with no need to load all data to memory

        // Once a cursor has been returned from a database query,
        // an app needs to iterate over the result set and read the column data from the cursor using the Cursor Class methods.

        // Internally, the cursor stores the rows of data returned by the query along with a position that points to the current row of data in the result set.
        Cursor cursor = database.query(MemeSQLiteHelper.MEMES_TABLE,
                new String[] {
                MemeSQLiteHelper.COLUMN_MEME_NAME,
                BaseColumns._ID,
                MemeSQLiteHelper.COLUMN_MEME_ASSET },
                null,
                null,
                null,
                null,
                null);
        ArrayList<Meme> memes = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Meme meme = new Meme(
                        getIntFromColumnName(cursor, BaseColumns._ID),
                        getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_MEME_ASSET),
                        getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_MEME_NAME),
                        null);
                memes.add(meme);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return memes;
    }

    public void addMemeAnnotations(ArrayList<Meme> memes) {
        SQLiteDatabase database = open();
        for (Meme meme : memes) {
            ArrayList<MemeAnnotation> annotations = new ArrayList<>();
            Cursor cursor = database.rawQuery(
                    "SELECT * FROM " +
                            MemeSQLiteHelper.ANNOTATIONS_TABLE +
                            " WHERE MEME_ID = " +
                            meme.getId(), null);
            if (cursor.moveToFirst()) {
                do {
                    MemeAnnotation annotation = new MemeAnnotation(
                            getIntFromColumnName(cursor, BaseColumns._ID),
                            getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR),
                            getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE),
                            getIntFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_X),
                            getIntFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_Y));
                    annotations.add(annotation);
                } while (cursor.moveToNext());
            }
            meme.setAnnotations(annotations);
            cursor.close();
        }
        close(database);
    }

    public void update(Meme meme) {
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues updateMemeValues = new ContentValues();
        updateMemeValues.put(MemeSQLiteHelper.COLUMN_MEME_NAME, meme.getName());
        database.update(MemeSQLiteHelper.MEMES_TABLE,
                updateMemeValues,
                String.format("%s=%d", BaseColumns._ID, meme.getId()),
                null);
        for (MemeAnnotation annotation : meme.getAnnotations()) {
            ContentValues updateAnnotations = new ContentValues();
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE, annotation.getTitle());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_X, annotation.getLocationX());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_Y, annotation.getLocationY());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, meme.getId());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR, annotation.getColor());

            if (annotation.hasBeenSaved()) {
                database.update(MemeSQLiteHelper.ANNOTATIONS_TABLE,
                        updateAnnotations,
                        String.format("%s=%d", BaseColumns._ID, annotation.getId()),
                        null);
            }
            else {
                database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE,
                        null,
                        updateAnnotations);
            }
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    private int getIntFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    public void create(Meme meme) {
        SQLiteDatabase database = open();
        database.beginTransaction();
        // Implementation details
        ContentValues memeValues = new ContentValues();
        memeValues.put(MemeSQLiteHelper.COLUMN_MEME_NAME, meme.getName());
        memeValues.put(MemeSQLiteHelper.COLUMN_MEME_ASSET, meme.getAssetLocation());
        long memeId = database.insert(MemeSQLiteHelper.MEMES_TABLE, null, memeValues);

        for (MemeAnnotation annotation : meme.getAnnotations()) {
            ContentValues annotationValues = new ContentValues();
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR, annotation.getColor());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE, annotation.getTitle());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_X, annotation.getLocationX());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_Y, annotation.getLocationY());
            annotationValues.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, memeId);
            database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE, null, annotationValues);
        }


        database.setTransactionSuccessful();
        database.endTransaction();

        close(database);
    }

}













