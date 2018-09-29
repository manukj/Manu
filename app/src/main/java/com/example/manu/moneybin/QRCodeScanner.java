package com.example.manu.moneybin;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;


public class QRCodeScanner extends AppCompatActivity {

    SurfaceView caamerPreview;
    TextView readData;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    final int REQUEST_CAMERA_PERMISSION = 1001;
    public static BluetoothAdapter btAdapter;
    public static BlueTooth blueToothCreation;

    //a counter to Scan the Barcode only once
    int i = 0;

    LottieAnimationView lottieAnimationView ;

    // if permission granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) ) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(caamerPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //Create the instance of the BlueTooth class
        blueToothCreation = new BlueTooth(btAdapter);
        try {
            //We are checking weither the stat of the  i,e weither the adpater is enabled or not
            blueToothCreation.checkBTState();
        } catch (Exception e) {
            // show the error if the bluetooth adapter is not on
            Toast.makeText(QRCodeScanner.this, "TRUN ON BLUE TOOTH ", Toast.LENGTH_SHORT).show();
        }
    }

    private void initialiseDetectorsAndSources() {

        //getting the Surface view where the QR is read
        caamerPreview = findViewById(R.id.camerPerview);
        //creating a barcode detector
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        caamerPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QRCodeScanner.this,
                            new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION);

                    return;
                }

                try { cameraSource.start(caamerPreview.getHolder()); }
                catch(IOException e) { e.printStackTrace(); }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size() != 0 )
                {
                    i++;
                    String data = barcodes.valueAt(0).displayValue;

                    if(i == 1) {
                        Log.d("BlueTooth", "...dsajda data:...");
                        blueToothCreation.sendData(data);
                        Intent intent = new Intent(QRCodeScanner.this,Select_Compartment.class);
                        startActivity(intent);
                        QRCodeScanner.this.finish();
                    }
                }
            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //this is where the QRcode starts scanning the QRCODE
        initialiseDetectorsAndSources();
        try {
            blueToothCreation.createSocket();
        } catch (Exception e) {
            Toast.makeText(QRCodeScanner.this, "On the BLUETOOTH", Toast.LENGTH_SHORT).show();
        }
    }


}
