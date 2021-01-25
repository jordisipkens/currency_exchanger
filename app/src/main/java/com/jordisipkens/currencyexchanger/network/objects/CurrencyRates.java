package com.jordisipkens.currencyexchanger.network.objects;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class CurrencyRates {
    @NonNull
    public String base;
    @NonNull
    public HashMap<String, Double> rates;

    public CurrencyRates(@NonNull String base, @NonNull HashMap<String, Double> rates) {
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
    public HashMap<String, Double> getRates() {
        return rates;
    }

    public void setRates(@NonNull HashMap<String, Double> rates) {
        this.rates = rates;
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
