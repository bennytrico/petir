package com.example.petir;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfilePage extends AppCompatActivity {

    TextView nameProfile;
    TextView emailProfile;
    EditText oldPassword;
    EditText newPassword;
    EditText reNewPassword;
    Button submitChangePassword;

    private String formOldPassword;
    private String formNewPassword;
    private String formReNewPassword;
    private Boolean flag = false;

    Montir montir = new Montir();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        getUserData();
        initVariable();
        submitChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ProfilePage.this);
                dialog.setContentView(R.layout.dialog_confirmation);
                dialog.setTitle("Info");

                Button agree = (Button) dialog.findViewById(R.id.agreeTopUp);
                Button disAgree = (Button) dialog.findViewById(R.id.disagreeTopUp);

                disAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        validateForm();
                    }
                });
                dialog.show();
            }
        });
    }
    public void validateForm() {
        formOldPassword = oldPassword.getText().toString().trim();
        formNewPassword = newPassword.getText().toString().trim();
        formReNewPassword = reNewPassword.getText().toString().trim();
        flag = false;
        if (TextUtils.isEmpty(formOldPassword)) {
            flag = true;
            Toast.makeText(this,"Password lama harus diisi", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(formNewPassword)) {
            flag = true;
            Toast.makeText(this,"Password baru harus diisi", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(formReNewPassword)) {
            flag = true;
            Toast.makeText(this,"Password ulang baru harus diisi", Toast.LENGTH_SHORT).show();
        } else if (!formNewPassword.equals(formReNewPassword)) {
            flag = true;
            Toast.makeText(this,"Password baru harus sama dengan password ulang baru",Toast.LENGTH_SHORT).show();
        }

        if (!flag) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.updatePassword(formNewPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference dbMontir = FirebaseDatabase.getInstance().getReference("Montirs").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Map<String, Object> update = new HashMap<String, Object>();
                                update.put("password",formNewPassword);
                                dbMontir.updateChildren(update);
                                finish();
                            } else {
                                Toast.makeText(ProfilePage.this, "Gagal ganti password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public void initVariable() {
        oldPassword = (EditText) findViewById(R.id.oldPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        reNewPassword = (EditText) findViewById(R.id.reNewPassword);
        nameProfile = (TextView) findViewById(R.id.nameProfile);
        emailProfile = (TextView) findViewById(R.id.emailProfile);
        submitChangePassword = (Button) findViewById(R.id.submitChangePassword);
    }
    public void getUserData () {
        DatabaseReference dbMontir = FirebaseDatabase.getInstance().getReference("Montirs").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbMontir.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                montir = dataSnapshot.getValue(Montir.class);
                nameProfile.setText(montir.getName());
                emailProfile.setText(montir.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
