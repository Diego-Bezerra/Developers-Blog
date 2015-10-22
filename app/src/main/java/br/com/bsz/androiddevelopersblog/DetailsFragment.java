package br.com.bsz.androiddevelopersblog;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.atom.Content;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.atom.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

/**
 * Created by diegobezerrasouza on 23/04/15.
 */
public class DetailsFragment extends Fragment {

    public static final String URL_CONTENT_PARAM = "url_content_param";
    public static final String URL_PARAM = "url_param";
    public static final String URL_ALTERNATE_PARAM = "url_alternate_param";
    private static final String ADB_SHARE_HASHTAG = " #AndroidDevelopersBlogApp";

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private String mHtmlContent;
    private String mUrl;
    private String mUrlAlternate;
    private ShareActionProvider mShareActionProvider;

    public static DetailsFragment newInstance(String url, String urlAlternate) {
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(URL_CONTENT_PARAM, url);
        bundle.putString(URL_ALTERNATE_PARAM, urlAlternate);
        detailsFragment.setArguments(bundle);
        return detailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_fragment, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mWebView = (WebView) view.findViewById(R.id.webView);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider.setShareIntent(createShareForecastIntent());
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mUrlAlternate + ADB_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHtmlContent == null) {
            if (Utility.isOnline(getActivity())) {
                Bundle bundle = getArguments();
                if (bundle != null) {
                    mUrl = bundle.getString(URL_CONTENT_PARAM);
                    mUrlAlternate = bundle.getString(URL_ALTERNATE_PARAM);
                    new BlogContentAsync().execute(mUrl);
                } else {
                    getActivity().finish();
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.not_online), Toast.LENGTH_LONG).show();
            }
        } else {
            loadHtmlOnWebView(mHtmlContent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(URL_CONTENT_PARAM, mHtmlContent);
        outState.putString(URL_PARAM, mUrl);
        outState.putString(URL_ALTERNATE_PARAM, mUrlAlternate);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mHtmlContent = savedInstanceState.getString(URL_CONTENT_PARAM);
            mUrl = savedInstanceState.getString(URL_PARAM);
        }
        super.onViewStateRestored(savedInstanceState);
    }

    private String changeYoutubeIframeToLink(String html) {
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByTag("iframe");
        if (elements != null) {
            for (Element element : elements) {
                String src = element.attr("src");
                Element link = new Element(Tag.valueOf("a"), src).append("Play video").attr("href", src);
                element.replaceWith(link);
            }
        }

        return doc.outerHtml();
    }

    private void loadHtmlOnWebView(String content) {
        mWebView.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "UTF-8", null);
        mProgressBar.setVisibility(View.GONE);
    }

    private class BlogContentAsync extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            Entry entry = RssAtomFeedRetriever.getEntry(params[0]);
            String content = null;
            if (entry.getContents() != null && entry.getContents().size() > 0) {
                Content contentEntry = (Content) entry.getContents().get(0);
                content = contentEntry.getValue();
            }

            return changeYoutubeIframeToLink(content);
        }

        @Override
        protected void onPostExecute(String content) {
            content = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" /> " + content;
            mHtmlContent = content;
            loadHtmlOnWebView(content);
        }
    }

}
