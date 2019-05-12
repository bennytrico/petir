package com.example.petir.OrderAccepted.OrderAcceptedCheckup;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.petir.Order;
import com.example.petir.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAcceptedCheckUp extends AppCompatActivity {

    private Boolean customerAgree;
    private Boolean montirAgree;
    private Address alamat;
    private Double latitude;
    private Double longitude;

    TextView seeMap;
    TextView address;
    TextView statusOrder;
    Button changeStatusOrder;
    Button cancelButtonOrder;
    Date dateOrder;
    Date dateNow;
    CheckBox checkBoxAll;
    CheckBox checkBoxEngine;
    CheckBox checkBoxBrackingSystem;
    CheckBox checkBoxMechanical;
    CheckBox checkBoxElectricity;
    LinearLayout checkBoxLayout;

    Order order = new Order();
    DatabaseReference dbOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_accepted_check_up);
        seeMap = (TextView) findViewById(R.id.buttonSeeMapOrderAcceptedCheckUp);
        address = (TextView) findViewById(R.id.addressOrderAcceptedCheckUp);
        statusOrder = (TextView) findViewById(R.id.statusOrderAcceptedCheckUp);
        changeStatusOrder = (Button) findViewById(R.id.changeStatusOrderAcceptedCheckUp);
        cancelButtonOrder = (Button) findViewById(R.id.cancelOrderAcceptedCheckUp);
        checkBoxAll = (CheckBox) findViewById(R.id.checkboxAll);
        checkBoxBrackingSystem = (CheckBox) findViewById(R.id.checkboxBrackingSystem);
        checkBoxElectricity = (CheckBox) findViewById(R.id.checkboxElectricity);
        checkBoxMechanical = (CheckBox) findViewById(R.id.checkboxMechanical);
        checkBoxEngine = (CheckBox) findViewById(R.id.checkboxEngine);
        checkBoxLayout = (LinearLayout) findViewById(R.id.layoutCheckBoxOrderAcceptedCheckUp);

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
            checkBoxLayout.setVisibility(View.VISIBLE);
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
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> location = geocoder.getFromLocationName(order.getAddress(), 1);
            alamat = location.get(0);
            latitude = alamat.getLatitude();
            longitude = alamat.getLongitude();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String map = "https://www.google.com/maps/search/?api=1&query="+order.getAddress()+","+latitude+","+longitude;
                Uri gmmIntentUri = Uri.parse(map);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getApplication().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }

    public void getIntentValue () {
        Gson gson = new Gson();
        Intent intent = getIntent();
        order = gson.fromJson(intent.getExtras().getString("ORDER_SELECTED"), Order.class);
    }
    public void processOrder () {
        changeStatusOrder.setText(R.string.statusDone);
        getValueAgreement();
        final Map<String, Object> updated = new HashMap<String, Object>();

        if (order.getCheck_up_list().getAll())
            checkBoxAll.setChecked(true);
        else if (order.getCheck_up_list().getBracking_system())
            checkBoxBrackingSystem.setChecked(true);
        else if (order.getCheck_up_list().getElectrical())
            checkBoxElectricity.setChecked(true);
        else if (order.getCheck_up_list().getEngine())
            checkBoxEngine.setChecked(true);
        else if (order.getCheck_up_list().getMechanical())
            checkBoxMechanical.setChecked(true);

        updated.put("all",order.getCheck_up_list().getAll());
        updated.put("engine",order.getCheck_up_list().getEngine());
        updated.put("mechanical",order.getCheck_up_list().getMechanical());
        updated.put("electrical",order.getCheck_up_list().getElectrical());
        updated.put("bracking_system",order.getCheck_up_list().getBracking_system());
        checkBoxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    updated.put("all",true);
                } else if (!((CheckBox)v).isChecked()) {
                    updated.put("all",false);
                }
            }
        });
        checkBoxEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    updated.put("engine",true);
                } else if (!((CheckBox)v).isChecked()) {
                    updated.put("engine",false);
                }
            }
        });
        checkBoxMechanical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    updated.put("mechanical",true);
                } else if (!((CheckBox)v).isChecked()) {
                    updated.put("mechanical",false);
                }
            }
        });
        checkBoxElectricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    updated.put("electrical",true);
                } else if (!((CheckBox)v).isChecked()) {
                    updated.put("electrical",false);
                }
            }
        });
        checkBoxBrackingSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    updated.put("bracking_system",true);
                } else if (!((CheckBox)v).isChecked()) {
                    updated.put("bracking_system",false);
                }
            }
        });
        final DatabaseReference dborderUpdateCheckUpList = FirebaseDatabase.getInstance().getReference().child("Orders")
                                                            .child(order.getId()).child("check_up_list");
        changeStatusOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbOrder.child(order.getId()).child("flag_customer_agree").setValue(true);
                dborderUpdateCheckUpList.updateChildren(updated);
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
