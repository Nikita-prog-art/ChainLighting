package com.example.chainlightning;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSION_BLUETOOTH_CONNECT=1;
    MainActivity self;
    protected void makeConnect(int buttonId, Class<?> activity){
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(self, activity);
            startActivity(intent);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            Log.i("THEME", "Was create");
            recreate();
        }
        Log.i("THEME", String.valueOf(AppCompatDelegate.getDefaultNightMode()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        self = this;
        makeConnect(R.id.CreateProgramMenuButton, CreateActivity.class);
        makeConnect(R.id.InstructionMenuButton, InstructionActivity.class);
        makeConnect(R.id.CreditsMenuButton, CreditsActivity.class);
        BluetoothPermissionManager bluetoothPermissionManager = new BluetoothPermissionManager(this);
        bluetoothPermissionManager.requestPermission();
        if (bluetoothPermissionManager.isGranted())
            makeConnect(R.id.SelectProgramMenuButton, SelectActivity.class);
        else{
            Log.i("BluetoothPermissionManager", "bad-granted");
            Button btn = findViewById(R.id.SelectProgramMenuButton);
            btn.setBackgroundColor(Color.GRAY);
            btn.setTextColor(Color.BLACK);
        }
    }
}