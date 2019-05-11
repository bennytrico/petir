package com.example.petir.OrderAccepted.OrderAcceptedServiceRutin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.petir.Order;
import com.example.petir.R;
import com.example.petir.helper.Convertor;
import com.example.petir.helper.FormatNumber;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderAcceptedServiceRutin extends AppCompatActivity {


    private Boolean customerAgree;
    private Boolean montirAgree;

    TextView seeMap;
    TextView address;
    TextView statusOrder;
    Button changeStatusOrder;
    Button cancelButtonOrder;
    Date dateOrder;
    Date dateNow;

    Order order = new Order();
    DatabaseReference dbOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_accepted_service_rutin);

        seeMap = (TextView) findViewById(R.id.buttonSeeMapOrderAcceptedServiceRutin);
        address = (TextView) findViewById(R.id.addressOrderAcceptedServiceRutin);
        statusOrder = (TextView) findViewById(R.id.statusOrderAcceptedServiceRutin);
        changeStatusOrder = (Button) findViewById(R.id.changeStatusOrderAcceptedServiceRutin);
        cancelButtonOrder = (Button) findViewById(R.id.cancelOrderAcceptedServiceRutin);
        getIntentValue();
        dbOrder = FirebaseDatabase.getInstance().getReference("Orders");

        if (order.getStatus_order().equals("wait")) {
            statusOrder.setText(R.string.waitConfirmFromMontir);
            changeStatusOrder.setVisibility(View.GONE);
        }else if (order.getStatus_order().equals("accept")) {
            statusOrder.setText(R.string.confirmFromMontir);
            acceptedOrder();
        }else if (order.getStatus_order().equals("cancel")) {
            statusOrder.setText(R.string.canceledOrder);
            changeStatusOrder.setVisibility(View.GONE);
            cancelButtonOrder.setVisibility(View.GONE);
        }else if (order.getStatus_order().equals("process")) {
            statusOrder.setText(R.string.processService);
            processOrder();
        }else if (order.getStatus_order().equals("done")) {
            statusOrder.setText(R.string.serviceDone);
            changeStatusOrder.setVisibility(View.GONE);
            cancelButtonOrder.setVisibility(View.GONE);
        }

        cancelButtonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbOrder.child(order.getId()).child("status_order").setValue("cancel");
                finish();
            }
        });

        address.setText(order.getAddress());
    }

    public void getIntentValue () {
        Gson gson = new Gson();
        Intent intent = getIntent();
        order = gson.fromJson(intent.getExtras().getString("ORDER_SELECTED"),Order.class);
    }
    public void processOrder () {
        changeStatusOrder.setText(R.string.statusDone);
        getValueAgreement();

        changeStatusOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbOrder.child(order.getId()).child("flag_customer_agree").setValue(true);
                finish();
            }
        });
    }
    public void acceptedOrder () {
        changeStatusOrder.setText(R.string.statusOnProgress);
        dateNow = new Date();
        dateOrder = new Date();
        String dateOrderString = order.getDate() + " " + order.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        try {
            dateOrder = dateFormat.parse(dateOrderString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        getValueAgreement();

        if (dateNow.compareTo(dateOrder) < 0) {
            changeStatusOrder.setText("Tombol akan aktif ketika " + dateOrderString);
            changeStatusOrder.setTextColor(getResources().getColor(R.color.black));
            changeStatusOrder.setEnabled(false);
            changeStatusOrder.setBackgroundColor(getResources().getColor(R.color.grey));
        }
        changeStatusOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbOrder.child(order.getId()).child("flag_customer_agree").setValue(true);
                finish();
            }
        });
    }
    public void getValueAgreement() {
        dbOrder.child(order.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Order r = dataSnapshot.getValue(Order.class);
                customerAgree = r.getFlag_customer_agree();
                montirAgree = r.getFlag_montir_agree();
                checkValueAgreement();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void checkValueAgreement () {
        if (order.getStatus_order().equals("accept") && customerAgree && montirAgree) {
            dbOrder.child(order.getId()).child("status_order").setValue("process");
            dbOrder.child(order.getId()).child("flag_customer_agree").setValue(false);
            dbOrder.child(order.getId()).child("flag_montir_agree").setValue(false);
        } else if (order.getStatus_order().equals("process") && customerAgree && montirAgree) {
            dbOrder.child(order.getId()).child("status_order").setValue("done");
            dbOrder.child(order.getId()).child("flag_customer_agree").setValue(false);
            dbOrder.child(order.getId()).child("flag_montir_agree").setValue(false);
        }
    }
}
