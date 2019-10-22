package com.github.warren_bank.broadcast_sms_sent;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

class SmsObserver extends ContentObserver {
    private ContentResolver resolver;
    private Context context;
    private int lastSmsId;

    public SmsObserver(Handler handler) {
        super(handler);
    }

    public void onStart(ContentResolver r, Context c) {
        resolver  = r;
        context   = c;
        lastSmsId = Preferences.getLastSmsId(context);

        if (lastSmsId < 0) {
            // ===============================
            // first run:
            //  * intialize 'lastSmsId' to highest value in DB, so the entire history of all sent SMS text messages isn't replayed
            //  * persist value in Preferences
            // ===============================
            lastSmsId = getLastSmsId();
            Preferences.setLastSmsId(context, lastSmsId);
        }
    }

    public void onStop() {
        Preferences.setLastSmsId(context, lastSmsId);
        resolver = null;
    }

    private int getLastSmsId() {
        // =================
        // schema reference:
        //     https://web.archive.org/web/20120423191752/http://www.androidjavadoc.com/m5-rc15/constant-values.html#android.provider.Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT
        //     https://web.archive.org/web/20101114141808/http://www.androidjavadoc.com/m5-rc15/android/provider/Telephony.TextBasedSmsColumns.html
        // =================

        Cursor cur = null;
        int id     = 0;

        try {
            Uri uri                = Uri.parse("content://sms");
            String[] projection    = new String[]{"_id"};
            String selection       = "type = 2";
            String[] selectionArgs = null;
            String sortOrder       = "_id DESC LIMIT 1";

            cur = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
            if ((cur == null) || (cur.getCount() == 0) || !cur.moveToFirst())
                throw new Exception();

            final int id_index = cur.getColumnIndex("_id");
            if (id_index < 0)
                throw new Exception();

            id = cur.getInt(id_index);
        }
        catch(Exception e) {
            id = 0;
        }
        finally {
            if (cur != null) {
                try {
                    cur.close();
                }
                catch(Exception e) {}
            }
            return id;
        }
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        onChange(selfChange);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        // =================
        // schema reference:
        //     https://web.archive.org/web/20120423191752/http://www.androidjavadoc.com/m5-rc15/constant-values.html#android.provider.Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT
        //     https://web.archive.org/web/20101114141808/http://www.androidjavadoc.com/m5-rc15/android/provider/Telephony.TextBasedSmsColumns.html
        // =================

        Cursor cur = null;

        try {
            Uri uri                = Uri.parse("content://sms");
            String[] projection    = new String[]{"_id", "address", "body"};
            String selection       = "type = 2 AND _id > " + lastSmsId;
            String[] selectionArgs = null;
            String sortOrder       = "_id ASC";

            cur = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
            if ((cur == null) || (cur.getCount() == 0) || !cur.moveToFirst())
                throw new Exception();

            final int id_index      = cur.getColumnIndex("_id");
            final int address_index = cur.getColumnIndex("address");
            final int message_index = cur.getColumnIndex("body");

            if ((id_index < 0) || (address_index < 0) || (message_index < 0))
                throw new Exception();

            int id;
            String address, message;
            do {
                id      = cur.getInt(id_index);
                address = cur.getString(address_index);
                message = cur.getString(message_index);

                lastSmsId = id;

                // broadcast Intent
                Intent intent = new Intent();
                intent.setAction("android.provider.Telephony.SMS_SENT");
                intent.putExtra("phone",   address);
                intent.putExtra("message", message);
                context.sendBroadcast(intent);
            }
            while(cur.moveToNext());

            Preferences.setLastSmsId(context, lastSmsId);
        }
        catch(Exception e) {}
        finally {
            if (cur != null) {
                try {
                    cur.close();
                }
                catch(Exception e) {}
            }
        }
    }
}
