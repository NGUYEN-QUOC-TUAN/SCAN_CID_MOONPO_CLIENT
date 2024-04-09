package com.moonpo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.moonpo.activity.ScanQrActivity;

public class MainActivity extends AppCompatActivity {

    Button btnScanQr, btnDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        btnScanQr = findViewById(R.id.btn_scan_code);
        btnDisplay = findViewById(R.id.btn_display);

        btnScanQr.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScanQrActivity.class);
            startActivity(intent);
        });
    }
}