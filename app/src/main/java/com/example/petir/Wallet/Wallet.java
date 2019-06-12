package com.example.petir.Wallet;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petir.Montir;
import com.example.petir.R;
import com.example.petir.WalletConfirmations;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.petir.CurrentUser.currentUserBank;
import static com.example.petir.CurrentUser.currentUserBankAccountName;
import static com.example.petir.CurrentUser.currentUserBankAccountNumber;
import static com.example.petir.CurrentUser.currentUserEmail;
import static com.example.petir.CurrentUser.currentUserID;
import static com.example.petir.CurrentUser.currentUserName;
import static com.example.petir.CurrentUser.currentUserWallet;
import static com.example.petir.CurrentUser.getCurrentMontirData;

public class Wallet extends AppCompatActivity {

    Boolean flagValidationEditBankAccount;
    Boolean flagValidationRequestWithdrawal;
    Integer amount;

    TextView currentWalletWalletPage;
    TextView currentBankAccountName;
    TextView currentBankAccountNumber;
    TextView currentBankName;
    RelativeLayout buttonDropDownRequestWithdrawal;
    RelativeLayout buttonDropDownEditAccountBank;
    LinearLayout dropDownDetailRequestWithdrawal;
    LinearLayout dropDownDetailEditAccountBank;
    EditText amountRequest;
    EditText userBankAccountName;
    EditText userBank;
    EditText userBankAccountNumber;
    Button submitRequestWithdrawal;
    Button submitEditAccountBank;

    DatabaseReference dbMontir;

    private String bank;
    private String bankName;
    private String nomorRekening;
    private Integer wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_wallet_page);

        currentWalletWalletPage = (TextView) findViewById(R.id.currentWalletWalletPage);
        currentBankAccountName = (TextView) findViewById(R.id.currentBankAccountName);
        currentBankAccountNumber = (TextView) findViewById(R.id.currentBankAccountNumber);
        currentBankName = (TextView) findViewById(R.id.currentBankName);
        buttonDropDownRequestWithdrawal = (RelativeLayout) findViewById(R.id.dropDownDetailWithdrawalWallet);
        buttonDropDownEditAccountBank = (RelativeLayout) findViewById(R.id.dropDownDetailChangeBankAccount);
        dropDownDetailEditAccountBank = (LinearLayout) findViewById(R.id.dropDownDetailInputChangeBankAccount);
        dropDownDetailRequestWithdrawal = (LinearLayout) findViewById(R.id.dropDownDetail);
        amountRequest = (EditText) findViewById(R.id.amountRequest);
        userBankAccountName = (EditText) findViewById(R.id.bankAccountNameWalletPage);
        userBankAccountNumber = (EditText) findViewById(R.id.bankAccountNumberWalletPage);
        userBank = (EditText) findViewById(R.id.bankWalletPage);
        submitRequestWithdrawal = (Button) findViewById(R.id.submitRequest);
        submitEditAccountBank = (Button) findViewById(R.id.submitRequestChangeAkunBank);

        dbMontir = FirebaseDatabase.getInstance().getReference("Montirs").child(currentUserID);
        dbMontir.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Montir m = dataSnapshot.getValue(Montir.class);
                bank = m.getBank();
                bankName = m.getBank_account_name();
                nomorRekening = m.getBank_account_number();
                wallet = m.getWallet();


                currentBankAccountName.setText(bankName);
                currentBankAccountNumber.setText(nomorRekening);
                currentBankName.setText(bank);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        flagValidationEditBankAccount = true;
        flagValidationRequestWithdrawal = true;


        buttonDropDownRequestWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dropDownDetailRequestWithdrawal.getVisibility() == View.VISIBLE) {
                    dropDownDetailRequestWithdrawal.setVisibility(View.GONE);
                } else {
                    dropDownDetailRequestWithdrawal.setVisibility(View.VISIBLE);
                }
            }
        });
        buttonDropDownEditAccountBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dropDownDetailEditAccountBank.getVisibility() == View.VISIBLE) {
                    dropDownDetailEditAccountBank.setVisibility(View.GONE);
                } else {
                    dropDownDetailEditAccountBank.setVisibility(View.VISIBLE);
                }
            }
        });
        submitEditAccountBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationEditBankAccount();
            }
        });
        submitRequestWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentMontirData();
                validationRequestWithdrawal();
            }
        });
    }

    public void validationEditBankAccount () {
        String bankAccountName = userBankAccountName.getText().toString().trim();
        String bankAccountNumber = userBankAccountNumber.getText().toString().trim();
        String bank = userBank.getText().toString().trim();

        if (TextUtils.isEmpty(bankAccountName)) {
            Toast.makeText(this,"nama akun bank harus di isi", Toast.LENGTH_SHORT).show();
            flagValidationEditBankAccount = false;
        } else if (TextUtils.isEmpty(bankAccountNumber)) {
            Toast.makeText(this,"nomor akun bank harus di isi",Toast.LENGTH_SHORT).show();
            flagValidationEditBankAccount = false;
        } else if (TextUtils.isEmpty(bank)) {
            Toast.makeText(this,"nama bank harus di isi",Toast.LENGTH_SHORT).show();
            flagValidationEditBankAccount = false;
        }

        if (flagValidationEditBankAccount) {
            Map<String, Object> update = new HashMap<String, Object>();
            update.put("bank",bank);
            update.put("bank_account_name",bankAccountName);
            update.put("bank_account_number",bankAccountNumber);
            dbMontir.updateChildren(update);
            Toast.makeText(this,"edit berhasil",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void validationRequestWithdrawal () {
        try {
            amount = Integer.parseInt(amountRequest.getText().toString());
        } catch (NumberFormatException e) {
            e.fillInStackTrace();
        }
        if (amount == null) {
            flagValidationRequestWithdrawal = false;
            Toast.makeText(this, "amount harus di isi",Toast.LENGTH_SHORT).show();
        } else if (amount > currentUserWallet) {
            Toast.makeText(this,"melebihi dari saldo",Toast.LENGTH_SHORT).show();
            flagValidationRequestWithdrawal = false;
        }
        if (flagValidationRequestWithdrawal) {
            DatabaseReference dbWalletConfirmations = FirebaseDatabase.getInstance().getReference("WalletConfirmations");
            String status = "requested";
            WalletConfirmations walletConfirmations = new WalletConfirmations(
                    currentUserBankAccountName,
                    currentUserBankAccountNumber,
                    currentUserEmail,
                    currentUserName,
                    status,
                    currentUserID,
                    amount
            );
            dbWalletConfirmations.push().setValue(walletConfirmations);

            Map<String, Object> update = new HashMap<String, Object>();
            Integer calculatedAmount = currentUserWallet - amount;
            update.put("wallet", calculatedAmount);
            dbMontir.updateChildren(update);
            finish();
        }
    }
}
