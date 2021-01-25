package com.jordisipkens.currencyexchanger.network.objects;

import androidx.annotation.NonNull;

import com.jordisipkens.currencyexchanger.MyApplication;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CurrencyRates {
    @NonNull
    public String base;
    @NonNull
    public HashMap<String, Double> rates;
    @NonNull
    public Date date;

    public CurrencyRates(@NonNull String base, @NonNull HashMap<String, Double> rates) {
        this.base = base;
        this.rates = rates;
        this.date = Calendar.getInstance(MyApplication.getInstance().getResources().getConfiguration().locale).getTime();
    }

    @NonNull
    public String getBase() {
        return base;
    }

    public void setBase(@NonNull String base) {
        this.base = base;
    }

    @NonNull
    public HashMap<String, Double> getRates() {
        return rates;
    }

    public void setRates(@NonNull HashMap<String, Double> rates) {
        this.rates = rates;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public Double calculateResult(@NonNull Double baseAmount, @NonNull String base, @NonNull String given) {
        // Check if base is the same as the base that's selected by the user (In case internet connection is lost while using the application)
        if(this.base.equals(base)) {
            return baseAmount * rates.get(given);
        } else {
            // First calculate the user selected base value back to base currency of the data, then calculate the user selected given currency.
            return (baseAmount / rates.get(base)) * rates.get(given);
        }
    }
}
