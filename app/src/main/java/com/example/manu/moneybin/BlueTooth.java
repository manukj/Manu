package com.example.manu.moneybin;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;
//include of

public class BlueTooth extends Activity implements Serializable{
    private   BluetoothAdapter mbluetoothAdapter;
    private   BluetoothSocket mbtSocket = null;
    private   DataOutputStream moutStream = null;
    private   DataInputStream minputStream = null;
    private   static final String TAG = "BlueTooth";

    private  static final int REQUEST_ENABLE_BT = 1;
    private  static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private  static String maddress = "98:D3:31:F5:74:9A";

    public BlueTooth(BluetoothAdapter bluetoothAdapter) {
        mbluetoothAdapter = bluetoothAdapter;
    }

    //Checking for the Bluetooth Adaptor
    public void checkBTState() throws Exception {
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if(mbluetoothAdapter==null) {
            Log.d(TAG,"Fatal Error Bluetooth Not supported. Aborting.");
        } else {
            if (mbluetoothAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth is enabled...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(mbluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }
        }
    }

    //Creating a Socket to establish a connection
    public void createSocket() throws Exception{

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = mbluetoothAdapter.getRemoteDevice(maddress);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            mbtSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.d(TAG,"Fatal Error In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        mbluetoothAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting to Remote...");
        try {
            mbtSocket.connect();
            Log.d(TAG, "...Connection established and data link opened...");
        } catch (IOException e) {

            Log.d(TAG,"Fatal Error In onResume() and unable to close socket during connection failure" + e.getMessage() + ".");
        }
        initializeIOStream();
    }

    //Initializing the Input and Output Stream
    private void initializeIOStream() {
        try
        {
            minputStream = new DataInputStream(mbtSocket.getInputStream());
            moutStream = new DataOutputStream(mbtSocket.getOutputStream());
        }catch (IOException e)
        {

        }
    }

    //Sending data to Arudino
    public void sendData(String message) {
        Log.d("BlueTooth", "...dsajda data: " + message + "...");
        byte[]  msgBuffer = message.getBytes();
        Log.d("BlueTooth", "...Sending data: " + message + "...");
        try {
            moutStream.write(msgBuffer);
        }catch (IOException e)
        {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
            Log.d(TAG,msg);
        }
    }

    public BluetoothSocket getSocket() {return mbtSocket;}



}
