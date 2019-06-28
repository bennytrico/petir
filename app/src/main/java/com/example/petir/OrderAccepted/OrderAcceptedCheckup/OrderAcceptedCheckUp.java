package com.example.petir.OrderAccepted.OrderAcceptedCheckup;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.petir.Customer;
import com.example.petir.Order;
import com.example.petir.R;
import com.example.petir.Rating;
import com.google.firebase.auth.FirebaseAuth;
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
    String update;
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
        TextView numberHandphone = (TextView) findViewById(R.id.nomorOrderAcceptedCheckUp);

        getIntentValue();
        numberHandphone.setText(order.getNo_handphone_customer());
        dbOrder = FirebaseDatabase.getInstance().getReference("Orders");

        if (order.getStatus_order().equals("wait")) {
            statusOrder.setText(R.string.waitConfirmFromMontir);
            changeStatusOrder.setVisibility(View.GONE);
            checkBoxLayout.setVisibility(View.GONE);
        }else if (order.getStatus_order().equals("accept")) {
            statusOrder.setText(R.string.confirmFromMontir);
            acceptedOrder();
            checkBoxLayout.setVisibility(View.GONE);
        }else if (order.getStatus_order().equals("cancel")) {
            statusOrder.setText(R.string.canceledOrder);
            changeStatusOrder.setVisibility(View.GONE);
            cancelButtonOrder.setVisibility(View.GONE);
            checkBoxLayout.setVisibility(View.GONE);
        }else if (order.getStatus_order().equals("process")) {
            statusOrder.setText(R.string.processService);
            checkBoxLayout.setVisibility(View.VISIBLE);
            processOrder();
        }else if (order.getStatus_order().equals("done")) {
            statusOrder.setText(R.string.serviceDone);
            changeStatusOrder.setVisibility(View.GONE);
            cancelButtonOrder.setVisibility(View.GONE);
            checkBoxLayout.setVisibility(View.GONE);
        } else if (order.getStatus_order().equals("end")) {
            statusOrder.setText(R.string.serviceDone);
            changeStatusOrder.setVisibility(View.GONE);
            cancelButtonOrder.setVisibility(View.GONE);
            checkBoxLayout.setVisibility(View.GONE);
        }

        cancelButtonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(OrderAcceptedCheckUp.this);
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
                        dbOrder.child(order.getId()).child("status_order").setValue("cancel");
                        final DatabaseReference updateCustomer = FirebaseDatabase.getInstance().getReference("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        DatabaseReference dbCustomer = FirebaseDatabase.getInstance().getReference("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dbCustomer.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Customer c = dataSnapshot.getValue(Customer.class);
                                Integer wallet = c.getWallet();
                                Map<String, Object> update = new HashMap<String, Object>();
                                update.put("wallet", wallet + order.getAmount());
                                updateCustomer.updateChildren(update);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        DatabaseReference dbRating = FirebaseDatabase.getInstance().getReference("Ratings").child(order.getMontir().getId());
                        dbRating.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Rating rtg = dataSnapshot.getValue(Rating.class);
                                    DatabaseReference dbMontirUpdate = FirebaseDatabase.getInstance().getReference("Montirs").child(order.getMontir().getId());
                                    DatabaseReference dbRatingUpdate = FirebaseDatabase.getInstance().getReference("Ratings").child(order.getMontir().getId());

                                    Map<String, Object> updateMontir = new HashMap<String, Object>();
                                    updateMontir.put("rating",rtg.getRating_montir() / rtg.getCount_order());
                                    dbMontirUpdate.updateChildren(updateMontir);

                                    Map<String, Object> updateRating = new HashMap<String, Object>();
                                    updateRating.put("average_rating",rtg.getRating_montir() / rtg.getCount_order());
                                    updateRating.put("rating_montir",rtg.getRating_montir());
                                    dbRatingUpdate.updateChildren(updateRating);
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
        });

        address.setText(order.getAddress());
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> location = geocoder.getFromLocationName(order.getAddress(), 1);
            if (location.size() > 0) {
                alamat = location.get(0);
                latitude = alamat.getLatitude();
                longitude = alamat.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        seeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String map = "https://www.google.com/maps/search/?api=1&query="+order.getAddress();
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
        if (order.getCheck_up_list().contains("All"))
            checkBoxAll.setChecked(true);
        else if (order.getCheck_up_list().contains("Breaking system"))
            checkBoxBrackingSystem.setChecked(true);
        else if (order.getCheck_up_list().contains("Electrical"))
            checkBoxElectricity.setChecked(true);
        else if (order.getCheck_up_list().contains("Engine"))
            checkBoxEngine.setChecked(true);
        else if (order.getCheck_up_list().contains("Mechanical"))
            checkBoxMechanical.setChecked(true);

        checkBoxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    checkBoxBrackingSystem.setChecked(false);
                    checkBoxElectricity.setChecked(false);
                    checkBoxEngine.setChecked(false);
                    checkBoxMechanical.setChecked(false);
                } else if (!((CheckBox)v).isChecked()) {

                }
            }
        });
//        checkBoxEngine.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(((CheckBox)v).isChecked()){
//                    updated.put("engine",true);
//                } else if (!((CheckBox)v).isChecked()) {
//                    updated.put("engine",false);
//                }
//            }
//        });
//        checkBoxMechanical.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(((CheckBox)v).isChecked()){
//                    updated.put("mechanical",true);
//                } else if (!((CheckBox)v).isChecked()) {
//                    updated.put("mechanical",false);
//                }
//            }
//        });
//        checkBoxElectricity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(((CheckBox)v).isChecked()){
//                    updated.put("electrical",true);
//                } else if (!((CheckBox)v).isChecked()) {
//                    updated.put("electrical",false);
//                }
//            }
//        });
//        checkBoxBrackingSystem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(((CheckBox)v).isChecked()){
//                    updated.put("bracking_system",true);
//                } else if (!((CheckBox)v).isChecked()) {
//                    updated.put("bracking_system",false);
//                }
//            }
//        });
        final DatabaseReference dborderUpdateCheckUpList = FirebaseDatabase.getInstance().getReference().child("Orders")
                                                            .child(order.getId());
        changeStatusOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxAll.isChecked())
                    update = "All";
                else if (checkBoxMechanical.isChecked())
                    update += "Mechanical ,";
                else if (checkBoxEngine.isChecked())
                    update += "Engine ,";
                else if (checkBoxElectricity.isChecked())
                    update += "Electrical ,";
                else if (checkBoxBrackingSystem.isChecked())
                    update += "Breaking system ,";

                updated.put("check_up_list",update);
                dbOrder.child(order.getId()).child("flag_montir_agree").setValue(true);
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
                dbOrder.child(order.getId()).child("flag_montir_agree").setValue(true);
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
