package br.com.bsz.androiddevelopersblog;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import br.com.bsz.androiddevelopersblog.sync.BlogSyncAdapter;


public class MainActivity extends ActionBarActivity {

    private String mAmountDisplayedArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        mAmountDisplayedArticles = Utility.getAmountDisplayArticlesPreference(this);

        BlogSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String amount = Utility.getAmountDisplayArticlesPreference(this);
        if (!amount.equals(mAmountDisplayedArticles)) {
            mAmountDisplayedArticles = amount;
            BlogContentFragment blogContentFragment = (BlogContentFragment) getSupportFragmentManager().findFragmentById(R.id.blogContentFragment);
            blogContentFragment.updateBlogNews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
