package com.example.petir.OrderPending;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

import java.util.ArrayList;
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
        dbOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderArrayList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    Order r = data.getValue(Order.class);
                    r.setId(data.getKey());
                    if (r.getMontir().getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && r.getStatus_order().equals("wait")) {
                        orderArrayList.add(r);
                    }
                }
                    listViewOrderAdapter = new OrderAdapter(
                            Objects.requireNonNull(getActivity()).getBaseContext(),
                            0,
                            orderArrayList
                    );
                    listviewOrder.setAdapter(listViewOrderAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listviewOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new Gson();
                Order orderSelected = listViewOrderAdapter.getItem(position);
                String orderJson = gson.toJson(orderSelected);
                Intent intent = new Intent(getActivity().getBaseContext(),OrderPendingPage2.class);
                intent.putExtra("ORDER_SELECTED",orderJson);
                startActivity(intent);
            }
        });
        return view;
    }
}
