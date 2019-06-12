package com.example.petir;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentUser {
    private static Montir montir = new Montir();

    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static String currentUserID = mAuth.getCurrentUser().getUid();
    public static String currentUserBankAccountName, currentUserBankAccountNumber, currentUserBank,
                        currentUserEmail, currentUserName;
    public static Integer currentUserWallet;
    public static void getCurrentMontirData () {
        DatabaseReference dbMontirs = FirebaseDatabase.getInstance().getReference("Montirs");
        dbMontirs.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                montir = dataSnapshot.getValue(Montir.class);
                currentUserBankAccountName = montir.getBank_account_name();
                currentUserBankAccountNumber = montir.getBank_account_number();
                currentUserBank = montir.getBank();
                currentUserEmail = montir.getEmail();
                currentUserName = montir.getName();
                currentUserWallet = montir.getWallet();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
