package com.example.petir.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Convertor {

    public Bitmap convertBase64toBitmap(String value) {

        byte[] imageBytes = Base64.decode(value,Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0 ,imageBytes.length);

        return decodedImage;
    }
}
