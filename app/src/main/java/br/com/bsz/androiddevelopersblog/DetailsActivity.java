package br.com.bsz.androiddevelopersblog;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by diegobezerrasouza on 23/04/15.
 */
public class DetailsActivity extends ActionBarActivity {

    private static final String FRAG_NAME = "detailsFrag";
    public static final String TITLE_PARAM = "tilte_param";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String title = getIntent().getStringExtra(TITLE_PARAM);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            String url = getIntent().getStringExtra(DetailsFragment.URL_CONTENT_PARAM);
            String urlAlternate = getIntent().getStringExtra(DetailsFragment.URL_ALTERNATE_PARAM);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, DetailsFragment.newInstance(url, urlAlternate), FRAG_NAME).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
