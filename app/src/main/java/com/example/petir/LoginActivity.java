package com.example.petir;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private String email;
    private String password;
    private Boolean flag;

    EditText emailUser;
    EditText passwordUser;
    Button login;

    FirebaseAuth mAuth;
    RelativeLayout loginLayout;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailUser = (EditText) findViewById(R.id.loginEmail);
        passwordUser = (EditText) findViewById(R.id.loginPassword);
        login = (Button) findViewById(R.id.loginButton);
        loginLayout = (RelativeLayout) findViewById(R.id.layoutLogin);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        flag = true;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatorDataLogin();
            }
        });

    }
    public void validatorDataLogin () {
        email = emailUser.getText().toString().trim();
        password = passwordUser.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Email harus diisi", Toast.LENGTH_SHORT).show();
            flag = false;
        } else if (TextUtils.isEmpty(password)) {
            flag = false;
        Toast.makeText(getApplicationContext(), "Password harus diisi", Toast.LENGTH_SHORT).show();
        }else if (password.length() < 6) {
            flag = false;
            Toast.makeText(getApplicationContext(), "Password harus lebih dari 6", Toast.LENGTH_SHORT).show();
        } else {
            flag = true;
        }

        if (flag) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(loginLayout.getWindowToken(), 0);

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.setMessage("Sedang proses . .");
                        progressDialog.show();
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Montirs");
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean flag = false;
                                for (DataSnapshot data : dataSnapshot.getChildren()){
                                    if (data.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        flag = true;
                                    }
                                }
                                if (!flag) {
                                    progressDialog.dismiss();
                                    mAuth.getInstance().signOut();
                                    Toast.makeText(getApplicationContext(),"Anda bukan montir",Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                    Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(startIntent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
