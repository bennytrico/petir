package com.example.petir.OrderAccepted;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.petir.Order;
import com.example.petir.OrderAccepted.OrderAcceptedCheckup.OrderAcceptedCheckUp;
import com.example.petir.OrderAccepted.OrderAcceptedServiceRutin.OrderAcceptedServiceRutin;
import com.example.petir.R;
import com.example.petir.adapter.OrderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

public class OrderAccepted extends Fragment {

    ListView listviewOrder;
    OrderAdapter listViewOrderAdapter;
    ArrayList<Order> orderArrayList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order_accepted,container,false);

        listviewOrder = (ListView) view.findViewById(R.id.listViewOrderAccepted);

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
                        if (r != null &&
                                r.getMontir().getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) &&
                                !r.getStatus_order().equals("wait") &&
                                !r.getStatus_order().equals("cancel")) {
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
                            Gson gson = new Gson();
                            Order r = listViewOrderAdapter.getItem(position);
                            if (r != null) {
                                String orderJson = gson.toJson(r);
                                if (r.getType_order().equals("Service Rutin")) {
                                    Intent intent = new Intent(getActivity().getBaseContext(), OrderAcceptedServiceRutin.class);
                                    intent.putExtra("ORDER_SELECTED",orderJson);
                                    startActivity(intent);
                                } else if (r.getType_order().equals("Check Up")) {
                                    Intent intent = new Intent(getActivity().getBaseContext(), OrderAcceptedCheckUp.class);
                                    intent.putExtra("ORDER_SELECTED",orderJson);
                                    startActivity(intent);
                                }
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
}
