package com.example.manu.moneybin;

import android.Manifest;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.io.InputStream;

import static com.example.manu.moneybin.QRCodeScanner.blueToothCreation;

public class RecyclingWaste extends AppCompatActivity {

    SurfaceView caamerPreview;
    private static final String TAG = "Recycling_Waste";
    TextView points;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    final int REQUEST_CAMERA_PERMISSION = 1001;
    int i = 0;
    int total_points_scored = 0;
    LottieAnimationView sendData;
    String codes[];
    ThreadConnected myThreadConnected;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycling_waste);



        Log.d(TAG,"Socket in Recycling = "+ blueToothCreation.getSocket().isConnected());



        points = findViewById(R.id.points);
        myThreadConnected = new ThreadConnected(blueToothCreation.getSocket(),points);
        myThreadConnected.start();
        sendData = findViewById(R.id.sendPoints);

        lottieAnimationView = findViewById(R.id.sendPoints);
        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"THe value i = "+(i-1)
                );
                Intent intent = new Intent(RecyclingWaste.this,Point_Activity.class);
                blueToothCreation.sendData("102");
                intent.putExtra("POINTS",total_points_scored);
                startActivity(intent);
                RecyclingWaste.this.finish();
            }
        });codes = new String[50];

        final View bar = findViewById(R.id.bar);
        final Animation animation = AnimationUtils.loadAnimation(RecyclingWaste.this, R.anim.barcode_ani);
        bar.setVisibility(View.VISIBLE);
        bar.startAnimation(animation);


        caamerPreview = findViewById(R.id.barCamerPerview);
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.CODE_39 | Barcode.CODE_128 | Barcode.CODE_93 | Barcode.EAN_13 |Barcode.EAN_8 | Barcode.CODABAR)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        caamerPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RecyclingWaste.this,
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
                    codes[i] = barcodes.valueAt(0).displayValue;
                    Log.d(TAG,"code "+i+"  "+codes[i] );
                    if(i == 0) {
                        Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(100);
                        i++;
                        blueToothCreation.sendData("100");
                        Log.d(TAG, "sendng  msg =" + "100");
                        codes[i] = barcodes.valueAt(0).displayValue;
                    }
                    else
                    {

                        if(!codes[i].equals(codes[i-1])) {
                            i++;
                            Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(100);
                            blueToothCreation.sendData("101");
                            Log.d(TAG, "sendng  msg =" + "101");
                        }
                    }

                }
            }

        });
    }






    //Thread to recieve the data from the Arudino
    private class ThreadConnected extends Thread {
        private BluetoothSocket connectedBluetoothSocket;
        private InputStream connectedInputStream = null;
        private TextView textStatus;
        byte[] buffer = new byte[1024];
        int bytes = 0, points = 0;
        String message = "";

        public ThreadConnected(BluetoothSocket socket, TextView view) {
            connectedBluetoothSocket = socket;
            textStatus = view;

            try {
                connectedInputStream = socket.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (connectedInputStream.available() > 0) {
                        try {
                            bytes = connectedInputStream.read(buffer);
                            message = message + new String(buffer, 0, bytes);
                            ;
                            final String finalMessage = message;
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Log.d(TAG, "reccieved msg =" + finalMessage);
                                    if (finalMessage.equals("Hello")) {
                                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        v.vibrate(100);
                                        points++;
                                        textStatus.setText("Points : " + points);
                                        total_points_scored = points;
                                    }
                                    message = "";
                                }
                            });

                        } catch (IOException e) {
                            Log.d(TAG, "Error While reacieving data" + e.toString());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
        @Override
    protected void onDestroy() {
        super.onDestroy();
        myThreadConnected.interrupt();
        try {
            blueToothCreation.getSocket().close();
        } catch (IOException e) {

        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        // blueToothCreation.sendData("102");
    }
}
