package com.example.petir.Wallet;

import android.app.Dialog;
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
import com.example.petir.helper.FormatNumber;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.petir.CurrentUser.currentUserBankAccountName;
import static com.example.petir.CurrentUser.currentUserBankAccountNumber;
import static com.example.petir.CurrentUser.currentUserEmail;
import static com.example.petir.CurrentUser.currentUserID;
import static com.example.petir.CurrentUser.currentUserName;
import static com.example.petir.CurrentUser.currentUserWallet;
import static com.example.petir.CurrentUser.getCurrentMontirData;

public class Wallet extends AppCompatActivity {

    Boolean flagValidationEditBankAccount;
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
    String bankAccountName;
    String bankAccountNumber;
    String bankChange;

    private String walletConfirmationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_wallet_page);

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

        dbMontir = FirebaseDatabase.getInstance().getReference("Montirs").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbMontir.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Montir m = dataSnapshot.getValue(Montir.class);
                bank = m.getBank();
                bankName = m.getBank_account_name();
                nomorRekening = m.getBank_account_number();
                wallet = m.getWallet();

                FormatNumber formatNumber = new FormatNumber();
                currentWalletWalletPage.setText(formatNumber.formatNumber(wallet));
                currentBankAccountName.setText(bankName);
                currentBankAccountNumber.setText(nomorRekening);
                currentBankName.setText(bank);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



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
                if (flagValidationEditBankAccount) {
                    Log.e("asdasd","asdasd");
                    final Dialog dialog = new Dialog(Wallet.this);
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
                            Map<String, Object> update = new HashMap<String, Object>();
                            update.put("bank",bankChange);
                            update.put("bank_account_name",bankAccountName);
                            update.put("bank_account_number",bankAccountNumber);
                            dbMontir.updateChildren(update);
                            Toast.makeText(Wallet.this,"edit berhasil",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    dialog.show();
                }
            }
        });
        submitRequestWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentMontirData();

                if (validationRequestWithdrawal()) {
                    Log.e("asdasd","asdasd");
                    final Dialog dialog = new Dialog(Wallet.this);
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
                            DatabaseReference dbWalletConfirmations = FirebaseDatabase.getInstance().getReference("WalletConfirmations");
                            final DatabaseReference dbWalletHistory = FirebaseDatabase.getInstance().getReference("WalletConfirmationHistory")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


                            String status = "requested";
                            Date createdAt = new Date();
                            SimpleDateFormat formater = new SimpleDateFormat("dd MMMM yyyy");
                            final WalletConfirmations walletConfirmations = new WalletConfirmations(
                                    currentUserBankAccountName,
                                    currentUserBankAccountNumber,
                                    currentUserEmail,
                                    currentUserName,
                                    status,
                                    currentUserID,
                                    amount,
                                    formater.format(createdAt)
                            );
                            walletConfirmationId = dbWalletConfirmations.push().getKey();
                            dbWalletConfirmations.child(walletConfirmationId).setValue(walletConfirmations);

                            Map<String, Object> update = new HashMap<String, Object>();
                            Integer calculatedAmount = currentUserWallet - amount;
                            update.put("wallet", calculatedAmount);
                            dbMontir.updateChildren(update);
                            Toast.makeText(Wallet.this,"Permintaan anda berhasil",Toast.LENGTH_SHORT).show();

                            dbWalletHistory.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        dbWalletHistory.child(walletConfirmationId).setValue(walletConfirmations);
                                    } else {
                                        DatabaseReference dbWalletHistoryRegisterUserId = FirebaseDatabase.getInstance()
                                                .getReference("WalletConfirmationHistory");
                                        dbWalletHistoryRegisterUserId.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(walletConfirmationId).setValue(walletConfirmations);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            finish();
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    public void validationEditBankAccount () {
        bankAccountName = userBankAccountName.getText().toString().trim();
        bankAccountNumber = userBankAccountNumber.getText().toString().trim();
        bankChange = userBank.getText().toString().trim();

        if (TextUtils.isEmpty(bankAccountName)) {
            Toast.makeText(this,"Nama akun bank harus di isi", Toast.LENGTH_SHORT).show();
            flagValidationEditBankAccount = false;
        } else if (TextUtils.isEmpty(bankAccountNumber)) {
            Toast.makeText(this,"Nomor akun bank harus di isi",Toast.LENGTH_SHORT).show();
            flagValidationEditBankAccount = false;
        } else if (TextUtils.isEmpty(bankChange)) {
            Toast.makeText(this,"Nama bank harus di isi",Toast.LENGTH_SHORT).show();
            flagValidationEditBankAccount = false;
        }
    }
    public Boolean validationRequestWithdrawal () {
        Boolean flagValidationRequestWithdrawal;

        try {
            amount = Integer.parseInt(amountRequest.getText().toString());
        } catch (NumberFormatException e) {
            e.fillInStackTrace();
        }
        if (amount == null) {
            flagValidationRequestWithdrawal = false;
            Toast.makeText(this, "Jumlah harus di isi",Toast.LENGTH_SHORT).show();
        } else if (amount > currentUserWallet) {
            Toast.makeText(this,"Melebihi dari saldo",Toast.LENGTH_SHORT).show();
            flagValidationRequestWithdrawal = false;
        } else {
            flagValidationRequestWithdrawal = true;
        }
        return flagValidationRequestWithdrawal;
    }
}
