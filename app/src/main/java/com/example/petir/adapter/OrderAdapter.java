package com.example.petir.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.petir.Order;
import com.example.petir.Order;
import com.example.petir.R;
import com.example.petir.helper.FormatNumber;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends ArrayAdapter<Order> {
    private Context mContext;
    FormatNumber formatNumber = new FormatNumber();
    private List<Order> orderList = new ArrayList<>();
    public OrderAdapter(@NonNull Context context, int resource, @NonNull List<Order> objects) {
        super(context, resource, objects);
        mContext = context;
        orderList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_view_order,parent,false);

        Order currentOrder = orderList.get(position);

        TextView typeOrder = (TextView)listItem.findViewById(R.id.listOrderType);
        typeOrder.setText(currentOrder.getType_order());

        TextView orderPrice = (TextView)listItem.findViewById(R.id.listOrderPrice);
        orderPrice.setText(formatNumber.formatNumber((currentOrder.getAmount())));

        TextView nameCustomer = (TextView)listItem.findViewById(R.id.listOrderNameCustomer);
        nameCustomer.setText(currentOrder.getName_customer());

        TextView numberPlate = (TextView)listItem.findViewById(R.id.listOrderPlateNumber);
        numberPlate.setText(currentOrder.getNumber_plate());

        TextView addressOrder = (TextView)listItem.findViewById(R.id.listOrderAddress);
        addressOrder.setText(currentOrder.getAddress());

        TextView statusOrder = (TextView)listItem.findViewById(R.id.listOrderStatusOrder);
        if (currentOrder.getStatus_order().equals("wait"))
            statusOrder.setText(R.string.waitConfirmFromMontir);
        else if (currentOrder.getStatus_order().equals("accept"))
            statusOrder.setText(R.string.confirmFromMontir);
        else if (currentOrder.getStatus_order().equals("cancel"))
            statusOrder.setText(R.string.canceledOrder);
        else if (currentOrder.getStatus_order().equals("process"))
            statusOrder.setText(R.string.processService);
        else if (currentOrder.getStatus_order().equals("done"))
            statusOrder.setText(R.string.serviceDone);
        else if (currentOrder.getStatus_order().equals("end"))
            statusOrder.setText(R.string.serviceDone);

        if (currentOrder.getType_order().equals("Service Rutin")) {
            LinearLayout kerusakanLayout = (LinearLayout)listItem.findViewById(R.id.layoutKerusakanCheckUp);
            kerusakanLayout.setVisibility(View.GONE);

            TextView oliMesin = (TextView)listItem.findViewById(R.id.oliMesinOrder);
            TextView oliGanda = (TextView)listItem.findViewById(R.id.oliGandaOrder);
            oliMesin.setText("Tidak ganti");
            oliGanda.setText("Tidak ganti");
            if (currentOrder.getOli_mesin()) {
                oliMesin.setText("Ganti");
            }
            if (currentOrder.getOli_ganda()) {
                oliGanda.setText("Ganti");
            }
        } else if (currentOrder.getType_order().equals("Check Up")) {
            LinearLayout layoutOliMesin = (LinearLayout)listItem.findViewById(R.id.layoutOliMesin);
            LinearLayout layoutOliGanda = (LinearLayout)listItem.findViewById(R.id.layoutOliGanda);

            layoutOliMesin.setVisibility(View.GONE);
            layoutOliGanda.setVisibility(View.GONE);

            TextView typeKerusakan = (TextView)listItem.findViewById(R.id.kerusakanCheckUpOrder);
            typeKerusakan.setText(currentOrder.getType_checkup());
        }

        TextView detailOrderMotor = (TextView) listItem.findViewById(R.id.detailMotorOrder);
        detailOrderMotor.setText(currentOrder.getBrand()+", "+currentOrder.getType_motor()+", "+currentOrder.getTransmition());

        return listItem;
    }

    @Nullable
    @Override
    public Order getItem(int position) {
        return super.getItem(position);
    }
}
