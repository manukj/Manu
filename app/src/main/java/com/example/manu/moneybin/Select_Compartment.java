package com.example.manu.moneybin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import static com.example.manu.moneybin.QRCodeScanner.blueToothCreation;

public class Select_Compartment extends AppCompatActivity {


    private static final String TAG = "Select_Compartment";
    CardView recycling,nonrecycling;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__compartment);

        Log.d(TAG,"Socket in BARCODE = "+ blueToothCreation.getSocket().isConnected());


        recycling = findViewById(R.id.recycling);
        nonrecycling = findViewById(R.id.norecycling);

        recycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                blueToothCreation.sendData("101");

                Intent intent = new Intent(Select_Compartment.this,RecyclingWaste.class);
                startActivity(intent);
                finish();
            }
        });

        nonrecycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blueToothCreation.sendData("102");
                Intent intent = new Intent(Select_Compartment.this,RecyclingWaste.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
