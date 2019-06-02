package com.example.petir;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PushNotif {
    private String SERVER_KEY = "AAAAq-fJnog:APA91bH5BkH0MPMzeuZbEkK41qgRQ8eThF4rVlre5QCeWyMJHfsOS9UpIkMIfOErHpy8yJVZgQ6sLCWWHytoOgV3GPU4r1O5PH4-X4Fpj6gx7I3W4JPEJ6yeC_-LPhT66pKry0Cy5YXV";

    public void pushNotiftoMontir(String DEVICE_TOKEN) throws IOException {
        String title = "Petir";
        String message = "Kamu mendapat pesananan";
//        String pushMessage = "{\"data\":{\"title\":\"" +title + "\"," +
//                "\"message\":\"" +message +"\"}," +
//                "\"to\":\"" + DEVICE_TOKEN +"\"}";

        String push = "\"to\":\"" + DEVICE_TOKEN + "\"," +
                        "\"content_available\":" + true +"," +
                        "\"priority\":\"high\"," +
                        "\"notification\":{" +
                            "\"title\":\"" + title + "\"," +
                            "\"text\":\"" + message + "\"}";
        URL url = new URL("https://fcm.googleapis.com/fcm/send");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(push.getBytes());
    }
}