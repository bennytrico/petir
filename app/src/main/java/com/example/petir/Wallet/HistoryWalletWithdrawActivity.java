package com.example.petir.Wallet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.petir.MainActivity;
import com.example.petir.R;
import com.example.petir.WalletConfirmations;
import com.example.petir.adapter.WalletAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryWalletWithdrawActivity extends AppCompatActivity {

    ArrayList<WalletConfirmations> walletHistoryArrayList = new ArrayList<>();
    WalletAdapter walletAdapter;
    ListView listViewWalletHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_wallet_withdraw);
        getSupportActionBar().setTitle("Riwayat dompet");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listViewWalletHistory = (ListView) findViewById(R.id.listHistoryWallet);

        DatabaseReference dbWalletHistory = FirebaseDatabase.getInstance().getReference("WalletConfirmationHistory")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbWalletHistory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                walletHistoryArrayList.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    WalletConfirmations walletHistory = data.getValue(WalletConfirmations.class);

                    walletHistoryArrayList.add(walletHistory);
                }
                walletAdapter = new WalletAdapter(
                        getApplicationContext(),
                        0,
                        walletHistoryArrayList
                );
                Collections.reverse(walletHistoryArrayList);
                listViewWalletHistory.setAdapter(walletAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, 0);
        return true;
    }
}
