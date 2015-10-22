package br.com.bsz.androiddevelopersblog.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by diegobezerrasouza on 21/04/15.
 */
public class BlogContract {

    public static final String CONTENT_AUTHORITY = "br.com.bsz.androiddevelopersblog";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_AUTHOR = "author";
    public static final String PATH_ARTICLE = "article";

    public static final class AuthorEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_AUTHOR).build();

        public static final String CONTENT_TYPE = "br.com.bsz.androiddevelopersblog.dir/" + CONTENT_AUTHORITY + "/" + PATH_AUTHOR;
        public static final String CONTENT_ITEM_TYPE = "br.com.bsz.androiddevelopersblog.item/" + CONTENT_AUTHORITY + "/" + PATH_AUTHOR;

        public static final String TABLE_NAME = "author";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_GOOGLE_PLAY_URI = "google_play_uri";

        public static Uri buildAuthorUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getAuthorNameFromUri(Uri uri) {
            return uri.getPathSegments().get(0);
        }

        public static Uri buildAuthorWithName(String authorName) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_NAME, authorName)
                    .build();
        }
    }

    public static final class ArticleEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTICLE).build();

        public static final String CONTENT_TYPE = "br.com.bsz.androiddevelopersblog.dir/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;
        public static final String CONTENT_ITEM_TYPE = "br.com.bsz.androiddevelopersblog.item/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;

        public static final String TABLE_NAME = "article";
        public static final String COLUMN_BLOG_ID = "blog_id";
        public static final String COLUMN_PUBLISHED = "published";
        public static final String COLUMN_UPDATED = "updated";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SELF_LINK = "self_link";
        public static final String COLUMN_SELF_LINK_ALTERNATE = "self_link_alternate";
        public static final String COLUMN_MEDIA_THUMB = "media_thumb";
        public static final String COLUMN_AUTHOR_KEY = "author_key";

        public static final String DATE_FORMAT = "yyyyMMdd";

        public static String getDbDateString(Date date) {
            return new SimpleDateFormat(DATE_FORMAT).format(date);
        }

        public static Uri buildArticleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildArticleNewestUri() {
            return CONTENT_URI.buildUpon().appendPath("newest").build();
        }

        public static String getArticleBlogIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildArticleWithBlogId(String blogId) {
            return CONTENT_URI.buildUpon().appendPath("blogid")
                    .appendQueryParameter(COLUMN_BLOG_ID, blogId)
                    .build();
        }
    }
}
