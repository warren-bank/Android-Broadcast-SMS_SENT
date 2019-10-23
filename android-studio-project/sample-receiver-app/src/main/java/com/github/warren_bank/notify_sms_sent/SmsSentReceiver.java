package com.github.warren_bank.notify_sms_sent;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SmsSentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        final Bundle extras = intent.getExtras();

        if (action.equals("android.provider.Telephony.SMS_SENT")) {
            String phone   = extras.getString("phone",   "[undefined]");
            String message = extras.getString("message", "[undefined]");

            Notification notification = new Notification.Builder(context)
                .setSmallIcon(android.R.drawable.sym_call_outgoing)
                .setContentTitle("SMS_SENT")
                .setContentText("to: " + phone + "\n\n" + message)
                .setAutoCancel(true)
                .build();

            NotificationManager notification_manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notification_manager.notify(0, notification);
        }
    }
}
