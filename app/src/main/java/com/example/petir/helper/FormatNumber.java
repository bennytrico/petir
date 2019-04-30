package com.example.petir.helper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FormatNumber {

    private String value;

    public String formatNumber (int value) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        this.value = kursIndonesia.format((value));
        return this.value;
    }
}
