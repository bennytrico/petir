package com.example.petir;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.petir.OrderAccepted.OrderAccepted;
import com.example.petir.OrderPending.OrderPending;
import com.example.petir.Wallet.Wallet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.petir.CurrentUser.getCurrentMontirData;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private ActionBarDrawerToggle abdt;

    FirebaseAuth mAuth;
    private Boolean flag = true;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
//            Toast.makeText(getApplicationContext(),"Harus login terlebih dahulu",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
        navigation();
        loadFragment(new OrderPending());
        navigationBottom();
        FirebaseIDService service = new FirebaseIDService();
        service.onTokenRefresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (flag){
            Toast.makeText(getApplicationContext(), "press again to exit", Toast.LENGTH_SHORT).show();
            flag = false;
            final Handler handler = new Handler();

            final Runnable r = new Runnable() {
                public void run() {
                    handler.postDelayed(this, 2000);
                    flag = true;
                }
            };

            handler.postDelayed(r, 1000);
        } else{
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            super.onBackPressed();
        }
    }
    public void navigationBottom(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private boolean loadFragment(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container,fragment).commit();
            return true;
        }
        return false;
    }

    public void navigation(){
        DrawerLayout dl;
        dl = (DrawerLayout) findViewById(R.id.draw_layout);
        abdt = new ActionBarDrawerToggle(this,dl,R.string.Open,R.string.Close);

        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final NavigationView navView = (NavigationView)findViewById(R.id.nav_view);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id == R.id.order){
//                    startActivity(new Intent(getApplicationContext(), OrderPage.class));
                    Toast.makeText(MainActivity.this, "a", Toast.LENGTH_SHORT).show();

                }else if(id == R.id.logout){
                    mAuth.getInstance().signOut();
                    Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    DatabaseReference dbMontir = FirebaseDatabase.getInstance().getReference("Montirs").child(mAuth.getCurrentUser().getUid());
                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put("fcm_token","");
                    dbMontir.updateChildren(update);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (id == R.id.wallet) {
                    Intent intent = new Intent(getApplicationContext(), Wallet.class);
                    getCurrentMontirData();
                    startActivity(intent);
                }

                return true;
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()){
            case R.id.orderPending:
                fragment = new OrderPending();
                break;
            case R.id.orderAccepted:
                fragment = new OrderAccepted();
                break;
        }
        return loadFragment(fragment);
    }
}

