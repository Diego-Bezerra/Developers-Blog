package br.com.bsz.androiddevelopersblog;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import br.com.bsz.androiddevelopersblog.data.BlogContract;
import br.com.bsz.androiddevelopersblog.sync.BlogSyncAdapter;
import settings.SettingsActivity;

/**
 * Created by diegobezerrasouza on 20/04/15.
 */
public class BlogContentFragment extends Fragment implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, BlogSyncAdapter.AfterLoadArticlesCallBack {

    public static final int BLOG_CONTENT_LOADER = 1;
    public static final String REFRESH_PARAM = "is_refresh_param";

    private static final String[] author_projection = new String[]{
            BlogContract.AuthorEntry._ID,
            BlogContract.AuthorEntry.COLUMN_NAME
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

    private BlogCursorAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.blog_content_fragment, container);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        ListView mListContent = (ListView) view.findViewById(R.id.listContent);
        mListContent.setOnItemClickListener(this);
        mAdapter = new BlogCursorAdapter(getActivity());
        mListContent.setAdapter(mAdapter);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().initLoader(BLOG_CONTENT_LOADER, null, this);
        BlogSyncAdapter.setAfterLoadArticlesCallBack(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateBlogNews() {
        mProgressBar.setVisibility(View.VISIBLE);
        //BlogSyncAdapter.syncImmediately(getActivity());
        getLoaderManager().restartLoader(BLOG_CONTENT_LOADER, null, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = mAdapter.getCursor();
        String selfLink = cursor.getString(cursor.getColumnIndex(BlogContract.ArticleEntry.COLUMN_SELF_LINK));
        String selfLinkAlternate = cursor.getString(cursor.getColumnIndex(BlogContract.ArticleEntry.COLUMN_SELF_LINK_ALTERNATE));
        if (!getResources().getBoolean(R.bool.isTablet)) {
            String title = cursor.getString(cursor.getColumnIndex(BlogContract.ArticleEntry.COLUMN_TITLE));
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra(DetailsFragment.URL_CONTENT_PARAM, selfLink);
            intent.putExtra(DetailsFragment.URL_ALTERNATE_PARAM, selfLinkAlternate);
            intent.putExtra(DetailsActivity.TITLE_PARAM, title);
            startActivity(intent);
        } else {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DetailsFragment.newInstance(selfLink, selfLinkAlternate)).commit();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                BlogContract.ArticleEntry.buildArticleNewestUri(),
                article_projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (data.getCount() > 0) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void afterLoadArticlesCallBack(final Context context) {
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getLoaderManager().restartLoader(BLOG_CONTENT_LOADER, null, BlogContentFragment.this);
            }
        });
    }
}
