package br.com.bsz.androiddevelopersblog.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;

import br.com.bsz.androiddevelopersblog.R;

public class BlogContentProvider extends ContentProvider {

    private BlogHelper blogHelper;

    //public static final SQLiteQueryBuilder articleJoiAuthor;
    public static final int AUTHOR = 100;
    public static final int AUTHOR_BY_NAME = 101;
    public static final int ARTICLE = 102;
    public static final int ARTICLE_NEWEST = 103;
    public static final int ARTICLE_WITH_PUBLISHED_DATE = 104;
    public static final int ARTICLE_WITH_BLOG_ID = 105;
    private static UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BlogContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, BlogContract.PATH_AUTHOR, AUTHOR);
        uriMatcher.addURI(authority, BlogContract.PATH_AUTHOR + "/*", AUTHOR_BY_NAME);
        uriMatcher.addURI(authority, BlogContract.PATH_ARTICLE, ARTICLE);
        uriMatcher.addURI(authority, BlogContract.PATH_ARTICLE + "/newest", ARTICLE_NEWEST);
        uriMatcher.addURI(authority, BlogContract.PATH_ARTICLE + "/blogid", ARTICLE_WITH_BLOG_ID);
        //uriMatcher.addURI(authority, BlogContract.PATH_ARTICLE + "*/*", ARTICLE_WITH_BLOG_ID);
        //uriMatcher.addURI(authority, BlogContract.PATH_ARTICLE + "/*", ARTICLE_WITH_PUBLISHED_DATE);


        return uriMatcher;
    }

    private static String authorByNameSelection;
    private static String articleByBlogIdSelection;

    static {
//        String tblAuthor = BlogContract.AuthorEntry.TABLE_NAME;
//        String tblArticle = BlogContract.ArticleEntry.TABLE_NAME;
//
//        // JOIN
//        articleJoiAuthor = new SQLiteQueryBuilder();
//        articleJoiAuthor.setTables(
//                tblAuthor + " INNER JOIN " + tblArticle + " ON " +
//                        tblAuthor + "." + BlogContract.AuthorEntry._ID + " = " +
//                        tblArticle + "." + BlogContract.ArticleEntry.COLUMN_AUTHOR_KEY);

        authorByNameSelection = BlogContract.AuthorEntry.COLUMN_NAME + " = ?";
        articleByBlogIdSelection = BlogContract.ArticleEntry.COLUMN_BLOG_ID + " = ?";
    }


    @Override
    public boolean onCreate() {
        blogHelper = new BlogHelper((getContext()));
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri insertedUri = null;
        final int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = blogHelper.getWritableDatabase();
        switch (uriType) {
            case AUTHOR: {
                long _id = db.insert(
                        BlogContract.AuthorEntry.TABLE_NAME, null, values);
                if (_id != -1) {
                    insertedUri =
                            BlogContract.AuthorEntry.buildAuthorUri(_id);
                } else {
                    throw new SQLException("Fail to insert author.");
                }
                break;
            }
            case ARTICLE:
                long _id = db.insert(
                        BlogContract.ArticleEntry.TABLE_NAME, null, values);
                if (_id != -1) {
                    insertedUri = BlogContract.ArticleEntry.buildArticleUri(_id);
                } else {
                    throw new SQLException("Fail to insert article.");
                }
                break;

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return insertedUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case ARTICLE: {
                int count = 0;
                SQLiteDatabase db = blogHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    for (int i = 0; i < values.length; i++) {
                        long _id = db.insert(BlogContract.ArticleEntry.TABLE_NAME,
                                null, values[i]);
                        if (_id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor = null;
        SQLiteDatabase database = blogHelper.getReadableDatabase();
        final int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case AUTHOR:
                cursor = database.query(
                        BlogContract.AuthorEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case AUTHOR_BY_NAME:
                cursor = getAuthorByName(uri, database, projection);
                break;
            case ARTICLE:
                cursor = database.query(
                        BlogContract.ArticleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ARTICLE_NEWEST:
                cursor = getNewestArticle(database, projection, selection, selectionArgs);
                break;
            case ARTICLE_WITH_PUBLISHED_DATE:
                break;
            case ARTICLE_WITH_BLOG_ID:
                cursor = getArticleByBlogId(uri, database, projection);
                break;
        }
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = blogHelper.getWritableDatabase();
        int affectedRows = 0;
        final int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case AUTHOR: {
                affectedRows = db.update(BlogContract.AuthorEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            }
            case ARTICLE: {
                affectedRows = db.update(BlogContract.ArticleEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }
        if (affectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = blogHelper.getWritableDatabase();
        int affectedRows = 0;
        final int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case AUTHOR: {
                affectedRows = db.delete(BlogContract.AuthorEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            case ARTICLE: {
                affectedRows = db.delete(BlogContract.ArticleEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }
        if (selection == null || affectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public String getType(Uri uri) {
        final int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case AUTHOR:
                return BlogContract.AuthorEntry.CONTENT_TYPE;
            case ARTICLE_NEWEST:
            case ARTICLE_WITH_PUBLISHED_DATE:
                return BlogContract.ArticleEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unkownn Uri: " + uri);
        }
    }

    private Cursor getNewestArticle(SQLiteDatabase database, String[] projection,
                                    String selection, String[] selectionArgs) {

        if (selection == null) selection = " ";
        selection += BlogContract.ArticleEntry.COLUMN_PUBLISHED + " <= ? ";
        String sortOrder = BlogContract.ArticleEntry.COLUMN_PUBLISHED + " DESC";
        if (selectionArgs != null) {
            selectionArgs[selectionArgs.length - 1] = String.valueOf(System.currentTimeMillis());
        } else {
            selectionArgs = new String[]{String.valueOf(System.currentTimeMillis())};
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String limitVal = preferences.getString(getContext().getResources().getString(R.string.amount_articles_pref_key), "25");

        return database.query(
                BlogContract.ArticleEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                limitVal);
    }

    private Cursor getAuthorByName(Uri uri, SQLiteDatabase database, String[] projection) {
        String authorName = BlogContract.AuthorEntry.getAuthorNameFromUri(uri);
        String[] selectionArgs = new String[]{authorName};

        return database.query(
                BlogContract.ArticleEntry.TABLE_NAME,
                projection,
                authorByNameSelection,
                selectionArgs,
                null,
                null,
                null);
    }

    private Cursor getArticleByBlogId(Uri uri, SQLiteDatabase database, String[] projection) {
        String blogId = uri.getQueryParameter(BlogContract.ArticleEntry.COLUMN_BLOG_ID);
        String[] selectionArgs = new String[]{blogId};

        return database.query(
                BlogContract.ArticleEntry.TABLE_NAME,
                projection,
                articleByBlogIdSelection,
                selectionArgs,
                null,
                null,
                null);
    }
}

