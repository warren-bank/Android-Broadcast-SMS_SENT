package com.github.warren_bank.broadcast_sms_sent;

import android.app.Activity;
import android.os.Bundle;

public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmsService.start(StartActivity.this);
        finish();
    }
}
