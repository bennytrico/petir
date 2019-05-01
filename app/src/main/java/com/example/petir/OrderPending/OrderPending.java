package com.example.petir.OrderPending;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.petir.Order;
import com.example.petir.R;
import com.example.petir.adapter.OrderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OrderPending extends Fragment {

    ListView listviewOrder;
    OrderAdapter listViewOrderAdapter;
    ArrayList<Order> orderArrayList = new ArrayList<>();

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
                        r.setId(data.getKey());
                        if (r.getMontir().getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && r.getStatus_order().equals("wait")) {
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
                            assert r != null;
                            showDialogOliGanda(r);
                        }
                    });
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
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_confirmation_order_dialog, null);
        dialog.setContentView(view);

        TextView address = (TextView) dialog.findViewById(R.id.addressOrderDialog);
        address.setText(order.getAddress());

        Button btnConfirm = (Button) dialog.findViewById(R.id.confirmOrderDialog);
        final Button btnCancel = (Button) dialog.findViewById(R.id.cancelOrderDialog);

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> location = null;
        try {
            location = geocoder.getFromLocationName(order.getAddress(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address alamat = location.get(0);
        final Double latitude = alamat.getLatitude();
        final Double longitude = alamat.getLongitude();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String map = "https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude;
                Uri gmmIntentUri = Uri.parse(map);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
//                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
