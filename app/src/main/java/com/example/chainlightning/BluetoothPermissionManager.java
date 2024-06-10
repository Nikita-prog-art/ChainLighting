package com.example.chainlightning;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BluetoothPermissionManager {
    AppCompatActivity activity;
    boolean hasBluetooth(){
        return (getSystemService(activity, BluetoothManager.class) != null);
    }
    boolean isGranted(){
        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED);
    }
    private void requestPermissionOnUiThread(){
        Log.i("AppBluetooth", "requestPermissionOnUiThread");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Запрос разрешений на блютуз")
                .setMessage("Если вы не дадите разрешение, то вы не сможете подключится к гирлянде")
                .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    Log.i("AppBluetooth", "agreed");
                    String[] permissions = new String[]{
                            Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT, /*Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION */
                    };
                    activity.requestPermissions(permissions, 0);

                    AlertDialog.Builder builder2 = new AlertDialog.Builder(activity);
                    builder2.setTitle("Ура!")
                            .setMessage("Для применения изменений будет произведён выход из приложения")
                            .setPositiveButton(android.R.string.ok, (dialog2, id2) -> activity.finish())
                            .create()
                            .show();
                });
        builder.create().show();
    }
    //возвращает isGranted
    boolean requestPermission(){
        if (!hasBluetooth())
            return false;
        if (isGranted())
            return true;
        activity.runOnUiThread(() -> requestPermissionOnUiThread());
        return isGranted();
    }
    BluetoothPermissionManager(AppCompatActivity activity){
        this.activity = activity;
    }
}
