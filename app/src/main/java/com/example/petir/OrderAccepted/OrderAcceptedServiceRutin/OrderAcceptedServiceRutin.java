package com.example.petir.OrderAccepted.OrderAcceptedServiceRutin;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.petir.Customer;
import com.example.petir.Montir;
import com.example.petir.Order;
import com.example.petir.OrderAccepted.OrderAcceptedCheckup.OrderAcceptedCheckUp;
import com.example.petir.PushNotif;
import com.example.petir.R;
import com.example.petir.Rating;
import com.example.petir.helper.Convertor;
import com.example.petir.helper.FormatNumber;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAcceptedServiceRutin extends AppCompatActivity {


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
        TextView numberHandphone = (TextView) findViewById(R.id.nomorOrderAcceptedServiceRutin);
        getIntentValue();
        dbOrder = FirebaseDatabase.getInstance().getReference("Orders");
        numberHandphone.setText(order.getNo_handphone_customer());
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
        } else if (order.getStatus_order().equals("end")) {
            statusOrder.setText(R.string.serviceDone);
            changeStatusOrder.setVisibility(View.GONE);
            cancelButtonOrder.setVisibility(View.GONE);
        }

        cancelButtonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(OrderAcceptedServiceRutin.this);
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
                        dbOrder.child(order.getId()).child("status_order").setValue("wait");
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
                        getListMontirAndReAssignMontir();
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
        order = gson.fromJson(intent.getExtras().getString("ORDER_SELECTED"),Order.class);
    }
    public void processOrder () {
        changeStatusOrder.setText(R.string.statusDone);
        getValueAgreement();

        changeStatusOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbOrder.child(order.getId()).child("flag_montir_agree").setValue(true);
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

            final DatabaseReference dbMontir = FirebaseDatabase.getInstance().getReference("Montirs").child(order.getMontir().getId());
            dbMontir.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Montir montir = dataSnapshot.getValue(Montir.class);
                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put("wallet",montir.getWallet() + (order.getAmount() - 20000));
                    dbMontir.updateChildren(update);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void getListMontirAndReAssignMontir() {
        final ArrayList<Montir> arrayList = new ArrayList<>();
        DatabaseReference dbMontir = FirebaseDatabase.getInstance().getReference("Montirs");
        final DatabaseReference dbOrders = FirebaseDatabase.getInstance().getReference("Orders");

        final ArrayList<String> idMontir = new ArrayList<>();

        dbOrders.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Date dateBooking = new Date();
                Calendar calendar = Calendar.getInstance();
                Date dateMaxHours = new Date();
                Date dateMinHours = new Date();

                String dateAndTimeBooking = order.getDate() + " " + order.getTime();

                SimpleDateFormat a = new SimpleDateFormat("dd MMM yyyy HH:mm");

                try {
                    dateBooking = a.parse(dateAndTimeBooking);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.setTime(dateBooking);
                calendar.add(Calendar.HOUR,2);
                dateMaxHours = calendar.getTime();
                calendar.setTime(dateBooking);
                calendar.add(Calendar.HOUR,-2);
                dateMinHours = calendar.getTime();


                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    Order order = data.getValue(Order.class);
                    Date date = new Date();
                    String dateAndTime = order.getDate()+" "+order.getTime();
                    try {
                        date = a.parse(dateAndTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(dateMinHours.compareTo(date) < 0
                            && dateMaxHours.compareTo(date) > 0 ) {
                        idMontir.add(order.getMontir().getId());
                        if (order.getStatus_order().equals("done")) {
                            idMontir.remove(order.getMontir().getId());
                        } else if (order.getStatus_order().equals("cancel")) {
                            idMontir.remove(order.getMontir().getId());
                        } else if (order.getStatus_order().equals("end")) {
                            idMontir.remove(order.getMontir().getId());
                        } else if (order.getMontir().getId()
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            idMontir.remove(order.getMontir().getId());
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbMontir.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Location customerLocation = new Location("a");
                com.google.android.gms.maps.model.LatLng latLng = new com.google.android.gms.maps.model.LatLng(order.getLatitude(), order.getLongitude());
                customerLocation.setLatitude(latLng.latitude);
                customerLocation.setLongitude(latLng.longitude);

                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    Montir m = data.getValue(Montir.class);
                    m.setId(data.getKey());

                    android.location.Location montirLocation = new android.location.Location("b");
                    com.google.android.gms.maps.model.LatLng latLngMontir = new com.google.android.gms.maps.model.LatLng(m.getLatitude(),m.getLongitude());
                    montirLocation.setLatitude(latLngMontir.latitude);
                    montirLocation.setLongitude(latLngMontir.longitude);

                    int distance = (int) customerLocation.distanceTo(montirLocation);
                    m.setPassword("");
                    if (distance < 1000  && !idMontir.contains(m.getId())) {
                        arrayList.add(m);
                    }

                }
                Map<String, Object> update = new HashMap<String, Object>();
                update.put("montir", arrayList.get(0));
                dbOrders.child(order.getId()).updateChildren(update);

                PushNotif pushNotif = new PushNotif();
                if (arrayList.get(0).getFcm_token() != null) {
                    try {
                        pushNotif.pushNotiftoMontir(getApplicationContext(),
                                arrayList.get(0).getFcm_token(),
                                "Kamu mendapatkan pesanan");
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference dbCustomer = FirebaseDatabase.getInstance()
                .getReference("Customers")
                .child(order.getCustomer_id());
        dbCustomer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Customer customer = dataSnapshot.getValue(Customer.class);
                PushNotif pushNotif = new PushNotif();
                if (customer.getFcm_token() != null) {
                    try {
                        pushNotif.pushNotiftoMontir(getApplicationContext(),
                                customer.getFcm_token(),
                                "Maaf Pesanaan kamu dibatalkan oleh montir");
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        pushNotif.pushNotiftoMontir(getApplicationContext(),
                                customer.getFcm_token(),
                                "Anda Mendapatkan montir pengganti, silahkan periksa kembali pesanan anda");
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
