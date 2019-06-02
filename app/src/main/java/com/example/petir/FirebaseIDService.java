package com.example.petir;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

import static com.example.petir.CurrentUser.currentUserID;

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String token) {
        DatabaseReference dbMontir = FirebaseDatabase.getInstance().getReference("Montirs").child(currentUserID);
        Map<String, Object> updateToken = new HashMap<String, Object>();
        updateToken.put("fcm_token",token);
        dbMontir.updateChildren(updateToken);
    }
}
