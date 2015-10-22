package br.com.bsz.androiddevelopersblog.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by diegobezerrasouza on 24/04/15.
 */
public class BlogServiceAuthenticator extends Service {

    private BlogAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new BlogAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
