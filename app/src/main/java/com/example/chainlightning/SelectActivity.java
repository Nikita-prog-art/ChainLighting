package com.example.chainlightning;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

//нужно использовать как synchronized(hasMakeConnectBluetoothLaunched)
class HasMakeConnectBluetoothLaunched{
    boolean has = false;
}

public class SelectActivity extends AppCompatActivity {

    SelectActivity self;
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice lights;
    TextView status;

    void setBluetoothStatus(String text) {
        runOnUiThread(() -> status.setText(text));
    }

    HasMakeConnectBluetoothLaunched hasMakeConnectBluetoothLaunched = new HasMakeConnectBluetoothLaunched();

    class MakeConnectBluetooth extends Thread {

        @Override
        public void run() {
            synchronized (hasMakeConnectBluetoothLaunched){ //избегаем создания двух тредов
                if (hasMakeConnectBluetoothLaunched.has == true)
                    return;
                hasMakeConnectBluetoothLaunched.has = true;
            }
            if (ActivityCompat.checkSelfPermission(self, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                //ничего не делаем, т.к BLUETOOTH_CONNECT всегда != PackageManager.PERMISSION_GRANTED
            }
            if (!bluetoothAdapter.isEnabled()) {
                setBluetoothStatus("Вы должны включить блютуз\n Выход из активности через 5 секунд");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> self.finish());
            }
            boolean connected = false;
            Set<BluetoothDevice> pairedDevices;
            while (!connected) {
                pairedDevices = bluetoothAdapter.getBondedDevices();
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("HC-05")) {
                        if (lights != null)
                            synchronized (lights) {
                                lights = device;
                            }
                        else
                            lights = device;
                        connected = true;
                        status.setText("НАЙДЕН");
                    }
                }
                if (!connected) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class RunConnection extends Thread {
        String program;
        RunConnection(String program){
            this.program = 'S' + program + 'E';
        }
        @Override
        public void run() {
            if (lights == null)
                return;
            Log.i("AppBluetooth", program);
            synchronized (lights) {
                if (ActivityCompat.checkSelfPermission(self, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    //ничего не делаем, т.к BLUETOOTH_CONNECT всегда != PackageManager.PERMISSION_GRANTED
                }
                ParcelUuid[] uuids = lights.getUuids();
                if (!(uuids.length > 0)){
                    setBluetoothStatus("HC-05 сохранён, но не подключён");
                    return;
                }
                setBluetoothStatus("НАЙДЕН");
                BluetoothSocket socket;
                try {
                    socket = lights.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                } catch (IOException e) {
                    Log.e("AppBluetooth","can't create socket from UUID", e);
                    return;
                }
                try { //https://developer.android.com/develop/connectivity/bluetooth/connect-bluetooth-devices#connect-client
                    // Connect to the remote device through the socket. This call blocks
                    // until it succeeds or throws an exception.
                    socket.connect();
                    OutputStream outputStream = null;
                    try {
                        outputStream = socket.getOutputStream();
                        try{
                            outputStream.write(program.getBytes(StandardCharsets.US_ASCII));
                        }
                        catch (IOException e){
                            Log.e("AppBluetooth", "can't write", e);
                        }
                    } catch (IOException e) {
                        Log.e("AppBluetooth", "Error occurred when creating output stream", e);
                    }

                } catch (IOException connectException) {
                    // Unable to connect; close the socket and return.
                    try {
                        socket.close();
                    } catch (IOException closeException) {
                        Log.e("AppBluetooth", "Could not close the client socket", closeException);
                    }
                    return;
                }

            }
        }

    }

    private void makeRunButton(int programId, int buttonId){
        findViewById(buttonId).setOnClickListener((v) -> {
            RunConnection runConnection = new RunConnection(((TextView)(findViewById(programId))).getText().toString());
            runConnection.start();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        status = findViewById(R.id.bluetoothStatus);
        self = this;
        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothManager == null) {
            status.setText("Ваше устройство не поддерживает блютуз");
        }
        MakeConnectBluetooth makeConnectBluetooth = new MakeConnectBluetooth();
        makeConnectBluetooth.start();
        Button backToMenu = findViewById(R.id.backToMenuButton);
        backToMenu.setOnClickListener((v) -> finish());
        makeRunButton(R.id.program1, R.id.run1);
        makeRunButton(R.id.program2, R.id.run2);
        makeRunButton(R.id.programCustom, R.id.runCustom);
    }
}
