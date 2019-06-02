package com.example.petir;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CurrentUser {
    private static Montir montir = new Montir();

    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static String currentUserID = mAuth.getCurrentUser().getUid();
    public static String currentUserBankAccountName, currentUserBankAccountNumber, currentUserBank,
                        currentUserEmail, currentUserName;
    public static Integer currentUserWallet;
    public static void getCurrentMontirData () {
        DatabaseReference dbMontirs = FirebaseDatabase.getInstance().getReference("Montirs");
        dbMontirs.orderByChild(FirebaseAuth.getInstance().getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                montir = dataSnapshot.getValue(Montir.class);
                currentUserBankAccountName = montir.getBank_account_name();
                currentUserBankAccountNumber = montir.getBank_account_number();
                currentUserBank = montir.getBank();
                currentUserEmail = montir.getEmail();
                currentUserName = montir.getName();
                currentUserWallet = montir.getWallet();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}