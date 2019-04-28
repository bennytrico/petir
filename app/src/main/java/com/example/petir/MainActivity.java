package com.example.petir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button test;
    FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = (Button) findViewById(R.id.test);

        mAuth = FirebaseAuth.getInstance();
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

    }
    @Override
    protected void onStart() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getApplicationContext(),"Harus login terlebih dahulu",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
        super.onStart();
    }
}

