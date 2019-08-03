package com.example.petir.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.petir.R;
import com.example.petir.WalletConfirmations;
import com.example.petir.helper.FormatNumber;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class WalletAdapter extends ArrayAdapter<WalletConfirmations> {
    private Context mContext;
    private List<WalletConfirmations> orderList = new ArrayList<>();
    public String status;
    FormatNumber formatNumber = new FormatNumber();

    public WalletAdapter(@NonNull Context context, int resource, @NonNull List<WalletConfirmations> objects) {
        super(context, resource, objects);

        mContext = context;
        orderList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_view_wallet_history,parent,false);

        WalletConfirmations currentWallet = orderList.get(position);
        TextView statusWallet = (TextView) listItem.findViewById(R.id.statusWalletConfirmation);
        switch (currentWallet.getStatus()) {
            case "requested":
                status = "Menunggu";
                break;
            case "accepted":
                status = "Diterima";
                statusWallet.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                break;
            case "declined":
                status = "Ditolak";
                statusWallet.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                break;
        }
        statusWallet.setText(status);

        TextView dateWallet = (TextView) listItem.findViewById(R.id.dateWalletConfirmation);
        dateWallet.setText(currentWallet.getCreated_at());

        TextView amountWallet = (TextView) listItem.findViewById(R.id.amountWalletConfirmation);
        amountWallet.setText(formatNumber.formatNumber(currentWallet.getAmount()));

        TextView nameWallet = (TextView) listItem.findViewById(R.id.nameWalletConfirmation);
        nameWallet.setText(currentWallet.getName());

        TextView bankAccountNameWallet = (TextView) listItem.findViewById(R.id.bankAccountNameWalletConfirmation);
        bankAccountNameWallet.setText(currentWallet.getBank_account_name());

        TextView bankAccountNumberWallet = (TextView) listItem.findViewById(R.id.bankAccountNumberWalletConfirmation);
        bankAccountNumberWallet.setText(currentWallet.getBank_account_number());

        return listItem;
    }
}
