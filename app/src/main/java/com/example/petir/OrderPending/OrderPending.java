package com.example.petir.OrderPending;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.petir.Order;
import com.example.petir.R;
import com.example.petir.adapter.OrderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
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
                            Log.e("alamat",r.getAddress());
                            if (r != null) {
                                Log.e("alamataaaaaa",r.getAddress());
                                showDialogOliGanda(r);
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
    private void showDialogOliGanda(final Order order) {
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
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbOrder = FirebaseDatabase.getInstance().getReference().child("Orders").child(order.getId());
                Map<String, Object> update = new HashMap<String, Object>();
                update.put("status_order","cancel");
                dbOrder.updateChildren(update);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
