package com.jordisipkens.currencyexchanger.network.objects;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class CurrencyRates {
    @NonNull
    public String base;
    @NonNull
    public HashMap<String, String> rates;

    public CurrencyRates(@NonNull String base, @NonNull HashMap<String, String> rates) {
        this.base = base;
        this.rates = rates;
    }

    @NonNull
    public String getBase() {
        return base;
    }

    public void setBase(@NonNull String base) {
        this.base = base;
    }

    @NonNull
    public HashMap<String, String> getRates() {
        return rates;
    }

    public void setRates(@NonNull HashMap<String, String> rates) {
        this.rates = rates;
    }
}
