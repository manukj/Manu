package com.example.manu.moneybin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class    Login extends AppCompatActivity {

    private static final String TAG = "Database";
    EditText name;
    EditText passwrd;

    String username;
    String passward;

    Button button;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference().child("test");

        //Email,Password
        name = findViewById(R.id.EmailID);
        passwrd = findViewById(R.id.PasswordID);

        button = findViewById(R.id.SignIN);

        username = String.valueOf(name.getText());
        passward = String.valueOf(passwrd.getText());
        SharedPreferences settings = getSharedPreferences("prefs", 0);
        boolean firstRun = settings.getBoolean("firstRun", false);


        if (firstRun == false)//if running for first time
        //Splash will load for first time
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstRun", true);
            editor.commit();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Login.this, QRCodeScanner.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Intent intent = new Intent(Login.this, QRCodeScanner.class);
            startActivity(intent);
            finish();
        }
    }

}
