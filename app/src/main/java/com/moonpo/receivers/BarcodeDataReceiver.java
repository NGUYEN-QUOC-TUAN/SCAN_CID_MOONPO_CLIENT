package com.moonpo.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BarcodeDataReceiver extends BroadcastReceiver {
    public static final String ACTION_DECODE_DATA = "com.example.scan_qr_cccd_moonpo.ACTION_DECODE_DATA";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals("android.intent.ACTION_DECODE_DATA")) {
            String scannedData = intent.getStringExtra("barcode_string");
            Intent newDataIntent = new Intent(ACTION_DECODE_DATA);
            newDataIntent.putExtra("scanned_data", scannedData);
            context.sendBroadcast(newDataIntent);
        }
    }
}





