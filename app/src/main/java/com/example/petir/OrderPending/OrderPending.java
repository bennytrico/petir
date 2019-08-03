package com.example.petir.OrderPending;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.petir.Customer;
import com.example.petir.Montir;
import com.example.petir.Order;
import com.example.petir.PushNotif;
import com.example.petir.R;
import com.example.petir.Rating;
import com.example.petir.adapter.OrderAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.type.LatLng;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderPending extends Fragment {

    ListView listviewOrder;
    OrderAdapter listViewOrderAdapter;
    ArrayList<Order> orderArrayList = new ArrayList<>();
    private Dialog dialog;
    private Address alamat;
    private Double latitude;
    private Double longitude;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order_pending,container,false);

        listviewOrder = (ListView) view.findViewById(R.id.listViewOrderPending);
        DatabaseReference dbOrder = FirebaseDatabase.getInstance().getReference("Orders");
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            dbOrder.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    orderArrayList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Order r = data.getValue(Order.class);
                        if (r != null) {
                            r.setId(data.getKey());
                        }
                        if (r != null && r.getMontir().getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && r.getStatus_order().equals("wait")) {
                            orderArrayList.add(r);
                        }
                    }
                    if (getActivity() != null) {
                        listViewOrderAdapter = new OrderAdapter(
                                getActivity(),
                                0,
                                orderArrayList
                        );
                    }
                    listviewOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Order r = listViewOrderAdapter.getItem(position);
                            if (r != null) {
                                showDialog(r);
                            }
                        }
                    });
                    Collections.reverse(orderArrayList);
                    listviewOrder.setAdapter(listViewOrderAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return view;
    }
    private void showDialog(final Order order) {
        if (getActivity() != null) {
            dialog = new Dialog(getActivity());
        }
        dialog.setCancelable(true);
        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_confirmation_order_dialog, null);
        dialog.setContentView(view);

        TextView address = (TextView) dialog.findViewById(R.id.addressOrderDialog);
        address.setText(order.getAddress());

        TextView viewMap = (TextView) dialog.findViewById(R.id.seeMap);


        Button btnConfirm = (Button) dialog.findViewById(R.id.confirmOrderDialog);
        final Button btnCancel = (Button) dialog.findViewById(R.id.cancelOrderDialog);
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> location;

        try {
            location = geocoder.getFromLocationName(order.getAddress(), 5);
            Geocoder.isPresent();

            if (location.size() > 0) {
                alamat = location.get(0);
                latitude = alamat.getLatitude();
                longitude = alamat.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String map = "https://www.google.com/maps/search/?api=1&query="+order.getAddress();
                Uri gmmIntentUri = Uri.parse(map);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbOrder = FirebaseDatabase.getInstance().getReference().child("Orders").child(order.getId());
                Map<String, Object> update = new HashMap<String, Object>();
                update.put("status_order","accept");
                dbOrder.updateChildren(update);
                DatabaseReference dbRating = FirebaseDatabase.getInstance().getReference("Ratings").child(order.getMontir().getId());
                dbRating.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Rating rtg = dataSnapshot.getValue(Rating.class);
                            DatabaseReference dbRatingUpdate = FirebaseDatabase.getInstance().getReference("Ratings").child(order.getMontir().getId());
                            Integer calculateCountOrder = rtg.getCount_order() + 1;

                            Map<String, Object> updateRating = new HashMap<String, Object>();
                            updateRating.put("count_order",calculateCountOrder);
                            dbRatingUpdate.updateChildren(updateRating);


                        } else {
                            DatabaseReference dbMontir = FirebaseDatabase.getInstance().getReference("Montirs").child(order.getMontir().getId());
                            dbMontir.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Montir m = dataSnapshot.getValue(Montir.class);

                                    Double averageRating = m.getRating();
                                    Rating rating = new Rating(
                                            0.0,
                                            1,
                                            averageRating
                                    );
                                    FirebaseDatabase.getInstance().getReference("Ratings")
                                            .child(order.getMontir().getId())
                                            .setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbOrder = FirebaseDatabase.getInstance().getReference().child("Orders").child(order.getId());
                final DatabaseReference updateCustomer = FirebaseDatabase.getInstance().getReference("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference dbCustomer = FirebaseDatabase.getInstance().getReference().child("Customers").child(order.getCustomer_id());
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

                Map<String, Object> update = new HashMap<String, Object>();
                update.put("status_order","cancel");
                dbOrder.updateChildren(update);
                dialog.dismiss();
                getListMontirAndReAssignMontir(order);
            }
        });
        dialog.show();
    }
    public void getListMontirAndReAssignMontir(final Order order) {
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
                        pushNotif.pushNotiftoMontir(getContext(),
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
                        pushNotif.pushNotiftoMontir(getContext(),
                                customer.getFcm_token(),
                                "Pesanaan kamu ditolak");
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        pushNotif.pushNotiftoMontir(getContext(),
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
