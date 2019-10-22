package com.github.warren_bank.broadcast_sms_sent;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

public class SmsService extends Service {
    // ============================
    // Service lifecycle management
    // ============================

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        onStart();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onStop();

        // restart
        Intent intent = new Intent(this, RestartReceiver.class);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // ============================
    // workers
    // ============================

    private ContentResolver resolver;
    private SmsObserver observer;

    private final void onStart() {
        resolver = getContentResolver();
        observer = new SmsObserver(null);

        observer.onStart(resolver, SmsService.this);
        resolver.registerContentObserver(Uri.parse("content://sms"), true, observer);
    }

    private final void onStop() {
        resolver.unregisterContentObserver(observer);
        observer.onStop();

        resolver = null;
        observer = null;
    }

}
