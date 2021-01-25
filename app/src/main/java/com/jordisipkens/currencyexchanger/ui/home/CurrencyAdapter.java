package com.jordisipkens.currencyexchanger.ui.home;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jordisipkens.currencyexchanger.MyApplication;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

// Simple class to show the symbols corresponding to the currency
public class CurrencyAdapter extends ArrayAdapter<String> {

    public CurrencyAdapter(@NonNull Context context, int resource, List<String> items) {
        super(context, resource, items);
    }

    @Nullable
    @Override
    public String getItem(int position) { // Get currency symbol through currency instance
        Locale locale = MyApplication.getAppContext().getResources().getConfiguration().locale;
        String item = super.getItem(position);
        Currency cInstance = Currency.getInstance(item);

        return cInstance.getDisplayName(locale) + ": " + cInstance.getSymbol();
    }
}
