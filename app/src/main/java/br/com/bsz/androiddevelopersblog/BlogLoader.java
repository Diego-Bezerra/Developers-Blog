package br.com.bsz.androiddevelopersblog;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import br.com.bsz.androiddevelopersblog.data.BlogContract;

/**
 * Created by diegobezerrasouza on 21/04/15.
 */
public class BlogLoader extends CursorLoader {

    private boolean isRefresh;

    private final String[] author_projection = new String[]{
            BlogContract.AuthorEntry._ID,
            BlogContract.AuthorEntry.COLUMN_NAME
    };

    private final String[] article_projection = new String[]{
            BlogContract.ArticleEntry._ID,
            BlogContract.ArticleEntry.COLUMN_BLOG_ID,
            BlogContract.ArticleEntry.COLUMN_TITLE,
            BlogContract.ArticleEntry.COLUMN_PUBLISHED,
            BlogContract.ArticleEntry.COLUMN_UPDATED,
            BlogContract.ArticleEntry.COLUMN_SELF_LINK,
            BlogContract.ArticleEntry.COLUMN_SELF_LINK_ALTERNATE,
            BlogContract.ArticleEntry.COLUMN_MEDIA_THUMB
    };

    public BlogLoader(Context context) {
        super(context);

        setProjection(article_projection);
        setUri(BlogContract.ArticleEntry.buildArticleNewestUri());
    }

    public void setIsRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    @Override
    public Cursor loadInBackground() {
        Log.i("teste", "loadInBackground()");
        if (isRefresh || Utility.isFirstTimeApp(getContext())) {
            Log.i("teste", "loadBlogData()");
            loadBlogData();
        }
        return super.loadInBackground();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    private void loadBlogData() {
//        SyndFeed feed = RssAtomFeedRetriever.getMostRecentNews();
//        List<SyndEntryImpl> entries = (List<SyndEntryImpl>) feed.getEntries();
//        List<ContentValues> articlesValuesToBeInserted = new ArrayList<>();
//        for (SyndEntryImpl entry : entries) {
//
//            String authorGooglePlayUri = null;
//            if (entry.getAuthors() != null && entry.getAuthors().size() > 0) {
//                authorGooglePlayUri = ((SyndPersonImpl) entry.getAuthors().get(0)).getUri();
//            }
//
//            ContentValues authorValues = new ContentValues();
//            authorValues.put(BlogContract.AuthorEntry.COLUMN_NAME, entry.getAuthor());
//            authorValues.put(BlogContract.AuthorEntry.COLUMN_GOOGLE_PLAY_URI, authorGooglePlayUri);
//
//            Uri authorInsertedUri = getContext().getContentResolver().insert(BlogContract.AuthorEntry.CONTENT_URI, authorValues);
//            int authorId = Integer.valueOf(authorInsertedUri.getPathSegments().get(1));
//
//            SyndLinkImpl selfLink = (SyndLinkImpl) entry.getLinks().get(RssAtomFeedRetriever.SELF_LINK_POSITION);
//            SyndLinkImpl selfLinkAlternate = (SyndLinkImpl) entry.getLinks().get(RssAtomFeedRetriever.SELF_LINK_ALTERNATE_POSITION);
//            String mediaUrl = "";
//            if (entry.getForeignMarkup() != null) {
//                List mediaList = (List) entry.getForeignMarkup();
//                if (mediaList.size() > 0) {
//                    Element element = (Element) mediaList.get(0);
//                    mediaUrl = element.getAttribute("url").getValue();
//                }
//            }
//
//            ContentValues articlesValues = new ContentValues();
//            articlesValues.put(BlogContract.ArticleEntry.COLUMN_BLOG_ID, entry.getUri());
//            articlesValues.put(BlogContract.ArticleEntry.COLUMN_TITLE, entry.getTitle());
//            articlesValues.put(BlogContract.ArticleEntry.COLUMN_PUBLISHED, entry.getPublishedDate().getTime());
//            articlesValues.put(BlogContract.ArticleEntry.COLUMN_UPDATED, entry.getUpdatedDate().getTime());
//            articlesValues.put(BlogContract.ArticleEntry.COLUMN_SELF_LINK, selfLink.getHref());
//            articlesValues.put(BlogContract.ArticleEntry.COLUMN_SELF_LINK_ALTERNATE, selfLinkAlternate.getHref());
//            articlesValues.put(BlogContract.ArticleEntry.COLUMN_MEDIA_THUMB, selfLinkAlternate.getHref());
//            articlesValues.put(BlogContract.ArticleEntry.COLUMN_MEDIA_THUMB, mediaUrl);
//            articlesValues.put(BlogContract.ArticleEntry.COLUMN_AUTHOR_KEY, authorId);
//            articlesValuesToBeInserted.add(articlesValues);
//        }
//
//        ContentValues[] arrayOfValues = new ContentValues[articlesValuesToBeInserted.size()];
//        articlesValuesToBeInserted.toArray(arrayOfValues);
//        getContext().getContentResolver().bulkInsert(BlogContract.ArticleEntry.CONTENT_URI, arrayOfValues);
    }

//    private int insertAuthor(ContentValues values) {
//        String authorName = values.getAsString(BlogContract.AuthorEntry.COLUMN_NAME);
//        Cursor cursor = context.getContentResolver().query(
//                BlogContract.AuthorEntry.buildAuthorWithName(authorName),
//                author_projection,
//                null,
//                null,
//                null);
//
//        int id;
//        if (cursor.getCount() == 0) {
//            Uri authorInsertedUri = context.getContentResolver().insert(BlogContract.AuthorEntry.CONTENT_URI, values);
//            id = Integer.valueOf(authorInsertedUri.getPathSegments().get(1));
//        } else {
//            id = cursor.getInt(cursor.getColumnIndex(BlogContract.AuthorEntry._ID));
//        }
//        return id;
//    }

//    private int insertArticle(ContentValues values) {
//        String blogId = values.getAsString(BlogContract.ArticleEntry.COLUMN_BLOG_ID);
//        Cursor cursor = context.getContentResolver().query(
//                BlogContract.ArticleEntry.buildArticleWithBlogId(blogId),
//                article_projection,
//                null,
//                null,
//                null);
//
//        int id;
//        if (cursor.getCount() == 0) {
//            Uri uri = context.getContentResolver().insert(BlogContract.ArticleEntry.CONTENT_URI, values);
//            id = Integer.valueOf(uri.getPathSegments().get(1));
//        } else {
//            id = cursor.getInt(cursor.getColumnIndex(BlogContract.ArticleEntry._ID));
//        }
//        return id;
//    }
}
