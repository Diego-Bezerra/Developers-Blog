package br.com.bsz.androiddevelopersblog.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntryImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndLinkImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndPersonImpl;

import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

import br.com.bsz.androiddevelopersblog.MainActivity;
import br.com.bsz.androiddevelopersblog.R;
import br.com.bsz.androiddevelopersblog.RssAtomFeedRetriever;
import br.com.bsz.androiddevelopersblog.Utility;
import br.com.bsz.androiddevelopersblog.data.BlogContract;

/**
 * Created by diegobezerrasouza on 24/04/15.
 */
public class BlogSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String[] author_projection = new String[]{
            BlogContract.AuthorEntry._ID,
            BlogContract.AuthorEntry.COLUMN_NAME,
            BlogContract.AuthorEntry.COLUMN_GOOGLE_PLAY_URI
    };

    private static final String[] article_projection = new String[]{
            BlogContract.ArticleEntry._ID,
            BlogContract.ArticleEntry.COLUMN_BLOG_ID,
            BlogContract.ArticleEntry.COLUMN_TITLE,
            BlogContract.ArticleEntry.COLUMN_PUBLISHED,
            BlogContract.ArticleEntry.COLUMN_UPDATED,
            BlogContract.ArticleEntry.COLUMN_SELF_LINK,
            BlogContract.ArticleEntry.COLUMN_SELF_LINK_ALTERNATE,
            BlogContract.ArticleEntry.COLUMN_MEDIA_THUMB
    };

    private static int MAX_AMOUNT = 100;
    public static final int SYNC_INTERVAL = 60 * 180;
    //public static final int SYNC_INTERVAL = 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final int NEWS_NOTIFICATION_ID = 3004;
    private static boolean isRefresh = false;

    private static AfterLoadArticlesCallBack mAfterLoadArticlesCallBack;

    public interface AfterLoadArticlesCallBack {
        void afterLoadArticlesCallBack(Context context);
    }

    public static void setAfterLoadArticlesCallBack(AfterLoadArticlesCallBack afterLoadArticlesCallBack) {
        mAfterLoadArticlesCallBack = afterLoadArticlesCallBack;
    }

    public BlogSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if (isRefresh) {
            Cursor cursor = getContext().getContentResolver().query(BlogContract.ArticleEntry.buildArticleNewestUri(),
                    article_projection,
                    null, null, null);
            if (cursor != null && cursor.getCount() == 0) {
                loadBlogData(MAX_AMOUNT);
                cursor.close();
            }
        } else {
            loadBlogData(5);
        }
        isRefresh = false;
    }

    private void notifyNews() {
        Resources resources = getContext().getResources();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setColor(getContext().getResources().getColor(R.color.transparent))
                        .setSmallIcon(R.drawable.ic_news)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                        .setContentTitle(resources.getString(R.string.notification_news));

        Intent resultIntent = new Intent(getContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NEWS_NOTIFICATION_ID, mBuilder.build());
    }

    private void loadBlogData(int amount) {
        boolean notify = false;

        try {
            if (Utility.isOnline(getContext())) {
                SyndFeed feed = RssAtomFeedRetriever.getMostRecentNews(getContext().getClassLoader(), String.valueOf(amount));
                List<SyndEntryImpl> entries = (List<SyndEntryImpl>) feed.getEntries();
                List<ContentValues> articlesValuesToBeInserted = new ArrayList<>();
                for (SyndEntryImpl entry : entries) {

                    String authorGooglePlayUri = null;
                    if (entry.getAuthors() != null && entry.getAuthors().size() > 0) {
                        authorGooglePlayUri = ((SyndPersonImpl) entry.getAuthors().get(0)).getUri();
                    }

                    Cursor cursorAuthor = getContext().getContentResolver().query(
                            BlogContract.AuthorEntry.buildAuthorWithName(entry.getAuthor()),
                            author_projection,
                            null,
                            null,
                            null);

                    int authorId;
                    if (cursorAuthor.getCount() == 0) {
                        ContentValues authorValues = new ContentValues();
                        authorValues.put(BlogContract.AuthorEntry.COLUMN_NAME, entry.getAuthor());
                        authorValues.put(BlogContract.AuthorEntry.COLUMN_GOOGLE_PLAY_URI, authorGooglePlayUri);
                        Uri authorInsertedUri = getContext().getContentResolver().insert(BlogContract.AuthorEntry.CONTENT_URI, authorValues);
                        authorId = Integer.valueOf(authorInsertedUri.getPathSegments().get(1));
                    } else {
                        cursorAuthor.moveToNext();
                        authorId = cursorAuthor.getInt(cursorAuthor.getColumnIndex(BlogContract.AuthorEntry.COLUMN_NAME));
                    }
                    cursorAuthor.close();

                    Cursor cursorArticle = getContext().getContentResolver().query(
                            BlogContract.ArticleEntry.buildArticleWithBlogId(entry.getUri()),
                            article_projection,
                            null,
                            null,
                            null);

                    if (cursorArticle.getCount() == 0) {
                        notify = true;
                        SyndLinkImpl selfLink = (SyndLinkImpl) entry.getLinks().get(RssAtomFeedRetriever.SELF_LINK_POSITION);
                        SyndLinkImpl selfLinkAlternate = (SyndLinkImpl) entry.getLinks().get(RssAtomFeedRetriever.SELF_LINK_ALTERNATE_POSITION);
                        String mediaUrl = "";
                        if (entry.getForeignMarkup() != null) {
                            List mediaList = (List) entry.getForeignMarkup();
                            if (mediaList.size() > 0) {
                                Element element = (Element) mediaList.get(0);
                                mediaUrl = element.getAttribute("url").getValue();
                            }
                        }

                        ContentValues articlesValues = new ContentValues();
                        articlesValues.put(BlogContract.ArticleEntry.COLUMN_BLOG_ID, entry.getUri());
                        articlesValues.put(BlogContract.ArticleEntry.COLUMN_TITLE, entry.getTitle());
                        articlesValues.put(BlogContract.ArticleEntry.COLUMN_PUBLISHED, entry.getPublishedDate().getTime());
                        articlesValues.put(BlogContract.ArticleEntry.COLUMN_UPDATED, entry.getUpdatedDate().getTime());
                        articlesValues.put(BlogContract.ArticleEntry.COLUMN_SELF_LINK, selfLink.getHref());
                        articlesValues.put(BlogContract.ArticleEntry.COLUMN_SELF_LINK_ALTERNATE, selfLinkAlternate.getHref());
                        articlesValues.put(BlogContract.ArticleEntry.COLUMN_MEDIA_THUMB, selfLinkAlternate.getHref());
                        articlesValues.put(BlogContract.ArticleEntry.COLUMN_MEDIA_THUMB, mediaUrl);
                        articlesValues.put(BlogContract.ArticleEntry.COLUMN_AUTHOR_KEY, authorId);
                        articlesValuesToBeInserted.add(articlesValues);
                    }
                    cursorArticle.close();
                }

                if (articlesValuesToBeInserted.size() > 0) {
                    ContentValues[] arrayOfValues = new ContentValues[articlesValuesToBeInserted.size()];
                    articlesValuesToBeInserted.toArray(arrayOfValues);
                    getContext().getContentResolver().bulkInsert(BlogContract.ArticleEntry.CONTENT_URI, arrayOfValues);

                    boolean notificationPref = Utility.getNotificationPreferences(getContext());
                    if (notificationPref && amount != MAX_AMOUNT && notify) {
                        notifyNews();
                    }
                }
                if (mAfterLoadArticlesCallBack != null) {
                    mAfterLoadArticlesCallBack.afterLoadArticlesCallBack(getContext());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEntryUriId(String uri) {
        int postIndex = uri.indexOf("post");
        if (postIndex != -1) {
            uri = uri.substring(postIndex + 5);
        }
        return uri;
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        isRefresh = true;
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        BlogSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
