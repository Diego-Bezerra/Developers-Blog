package br.com.bsz.androiddevelopersblog.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by diegobezerrasouza on 24/04/15.
 */
public class BlogSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static BlogSyncAdapter sBlogSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sBlogSyncAdapter == null) {
                sBlogSyncAdapter = new BlogSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sBlogSyncAdapter.getSyncAdapterBinder();
    }
}
