package com.github.warren_bank.broadcast_sms_sent;

import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

public class SmsService extends Service {
    // ============================
    // Service lifecycle management
    // ============================

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        onStart();

        if (Build.VERSION.SDK_INT >= 5) {
            Notification notification = new Notification.Builder(SmsService.this)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle("SMS_SENT Service is running")
                .setAutoCancel(false)
                .build();

            startForeground(0, notification);
        }

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
    // helper
    // ============================

    public static void start(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(new Intent(context, SmsService.class));
        }
        else {
            context.startService(new Intent(context, SmsService.class));
        }
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
