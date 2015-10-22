package br.com.bsz.androiddevelopersblog.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by diegobezerrasouza on 21/04/15.
 */
public class BlogHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "blogAndroidDevelopers.db";
    public static final int CURRENT_VERSION = 1;

    public BlogHelper(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_AUTHOR_TABLE = "CREATE TABLE " + BlogContract.AuthorEntry.TABLE_NAME + " (" +
                BlogContract.AuthorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BlogContract.AuthorEntry.COLUMN_NAME + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                BlogContract.AuthorEntry.COLUMN_GOOGLE_PLAY_URI + " TEXT);";

        final String SQL_CREATE_ARTICLE_TABLE = "CREATE TABLE " + BlogContract.ArticleEntry.TABLE_NAME + " (" +
                BlogContract.ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BlogContract.ArticleEntry.COLUMN_BLOG_ID + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                BlogContract.ArticleEntry.COLUMN_AUTHOR_KEY + " INTEGER NOT NULL, " +
                BlogContract.ArticleEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                BlogContract.ArticleEntry.COLUMN_PUBLISHED + " REAL NOT NULL, " +
                BlogContract.ArticleEntry.COLUMN_UPDATED + " REAL NOT NULL, " +
                BlogContract.ArticleEntry.COLUMN_SELF_LINK + " TEXT NOT NULL, " +
                BlogContract.ArticleEntry.COLUMN_SELF_LINK_ALTERNATE + " TEXT NOT NULL, " +
                BlogContract.ArticleEntry.COLUMN_MEDIA_THUMB + " TEXT , " +

                " FOREIGN KEY (" + BlogContract.ArticleEntry.COLUMN_AUTHOR_KEY + ") REFERENCES " +
                BlogContract.AuthorEntry.TABLE_NAME + " (" + BlogContract.AuthorEntry._ID + "))";

        db.execSQL(SQL_CREATE_AUTHOR_TABLE);
        db.execSQL(SQL_CREATE_ARTICLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
